package com.smanzana.dungeonmaster.ui.app;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.smanzana.dungeonmaster.ui.Comm;
import com.smanzana.dungeonmaster.ui.web.WebUI;

/**
 * Connection listening server. Listens for connections and hands
 * new comms off to the registered calback.
 * @author Skyler
 *
 */
public class AppConnectionServer implements Runnable {

	public static interface AppConnectionHook {
		
		/**
		 * Called when a comm connects
		 * @param key the key provided on the matching successful filter
		 * @param newComm
		 */
		public void connect(int key, Comm newComm);
		
		/**
		 * On connection, clients are required to send a connectMessage.
		 * This filter examines the connectMessage and sees whether it
		 * should be accepted as a full Comm.
		 * @param connectMessage
		 * @return 0 to reject. Non-zero as key to pass to matching connect call
		 */
		public int filter(String connectMessage);
		
		/**
		 * Called whenever a connection is received to fetch what
		 * should be served to them.
		 * Further communication should happen over port PORT_LISTEN
		 * @return
		 */
		public String generateConnectionPage();
	}
	
	public static final int DEFAULT_PORT_LISTEN = 8124;
	private static final int HEADER_LEN_MAX = 5000;
	
	private AppConnectionHook hook;
	private ServerSocket listenSocket;
	private ServerSocket webSocket;
	private boolean valid;
	private Boolean running;
	
	public AppConnectionServer(AppConnectionHook hook) {
		this(hook, AppConnectionServer.DEFAULT_PORT_LISTEN);
	}
	
	public AppConnectionServer(AppConnectionHook hook, int port) {
		this.hook = hook;
		running = false;
		try {
			listenSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to listen on port " + port);
			valid = false;
			running = false;
			return;
		}
		try {
			webSocket = new ServerSocket(80);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to bind web port");
			valid = false;
			running = false;
			return;
		}
		
		valid = true;
		
	}
	
	public void run() {
		boolean run = true;
		running = true;
		try {
			listenSocket.setSoTimeout(100);
			webSocket.setSoTimeout(100);
		} catch (SocketException e) {
			e.printStackTrace();
			System.err.println("Could not initialize listenSocket");
			stop();
			shutdown();
			return;
		}
		
		while (run) {

			while (true) {
				Socket connection;
				try {
					connection = listenSocket.accept();
				} catch (SocketTimeoutException e) {
					break;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Exception on listenSocket accept");
					stop();
					shutdown();
					return;
				}
				
				onConnectEx(connection);
			}
			
			while (true) {
				Socket connection;
				try {
					connection = webSocket.accept();
				} catch (SocketTimeoutException e) {
					break;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Exception on webSocket accept");
					stop();
					shutdown();
					return;
				}
				
				onConnect(connection);
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			
			synchronized(running) {
				run = running;
			}
		}
		
		System.out.println("Shutting down connection server");
		shutdown();
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public boolean isRunning() {
		synchronized(running) {
			return running;
		}
	}
	
	public void stop() {
		synchronized(running) {
			running = false;
		}
	}
	
	public void updateHook(AppConnectionHook newHook) {
		this.hook = newHook;
	}
	
	// Closes open listening socket
	private void shutdown() {
		if (listenSocket != null && !listenSocket.isClosed()) 
			try {
				listenSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				listenSocket = null;
			}
		if (webSocket != null && !webSocket.isClosed()) 
			try {
				webSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				webSocket = null;
			}
	}
	
	// Connection over port 80; just checking what it is
	private void onConnect(Socket connection) {
		if (connection == null || !connection.isConnected()
				|| connection.isClosed())
			return;
		
		String page = hook.generateConnectionPage();
		try {
			PrintWriter writer = new PrintWriter(connection.getOutputStream());
			writer.print(page);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to deliver page to connection: "
					+ connection.getInetAddress());
		}
	}
	
	private void onConnectEx(Socket connection) {
		
		// Get connection message;
		String message = null;
		try {
			int originalTimeout = connection.getSoTimeout();
			connection.setSoTimeout(1000);
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			char buf[] = new char[HEADER_LEN_MAX];
			int len;
			
			do {
				if (connection.isClosed())
					break;
				
				try {
				len = reader.read(buf, 0, HEADER_LEN_MAX);
				} catch (SocketTimeoutException e) {
					len = 0;
				}
				if (len == -1) {
					System.out.println("Got -1 from read");
					break;
				}
				
				if (len == 0) {
					System.out.println("got 0 (timeout) from read");
					break;
				}
				
				buffer.append(buf, 0, len);
				
				String result = buffer.toString();
				if (!result.toString().contains("\r\n\r\n")) {
					System.out.println("Got large (> " + HEADER_LEN_MAX + ") response");
					break; // No end of header. It's too big. 
				}
				
				message = result.substring(result.indexOf("\r\n\r\n") + 4);
				
			} while (false);
			
			// reset back to original
			connection.setSoTimeout(originalTimeout);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Encountered socket exception during handshake. Disconnecting");
			try { connection.close(); } catch (Exception ex) {};
			return;
		}
		
		if (message == null) {
			System.err.println("Recieved no message from connecting client. Disconnecting");
			try { connection.close(); } catch (Exception ex) {};
			return;
		}
		
		int key = hook.filter(message);
		if (key == 0) {
			System.err.println("Hook rejecting connection. Disconnecting");
			try { connection.close(); } catch (Exception ex) {};
			return;
		}
		
		hook.connect(key, wrapInComm(connection));
	}
	
	private Comm wrapInComm(Socket connection) {
		return new WebUI(connection);
	}
}
