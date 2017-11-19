package com.smanzana.dungeonmaster.ui.app;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
		 * @param newComm
		 */
		public void connect(Comm newComm);
		
		/**
		 * On connection, clients are required to send a connectMessage.
		 * This filter examines the connectMessage and sees whether it
		 * should be accepted as a full Comm.
		 * @param connectMessage
		 * @return true if accepted. False otherwise
		 */
		public boolean filter(String connectMessage);
	}
	
	private static final int PORT_LISTEN = 15251;
	private static final int HEADER_LEN_MAX = 50;
	
	private AppConnectionHook hook;
	private ServerSocket listenSocket;
	private boolean valid;
	private Boolean running;
	
	public AppConnectionServer(AppConnectionHook hook) {
		this(hook, PORT_LISTEN);
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
		
		valid = true;
		
	}
	
	public void run() {
		boolean run = true;
		try {
			listenSocket.setSoTimeout(100);
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
	}
	
	private void onConnect(Socket connection) {
		
		// Get connection message;
		String message = null;
		try {
			int originalTimeout = connection.getSoTimeout();
			connection.setSoTimeout(1000);
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			char buf[] = new char[50];
			int len;
			while (true) {
				len = reader.read(buf, 0, 50);
				if (len == -1)
					break;
				
				buffer.append(buf, 0, len);
				if (buffer.length() > HEADER_LEN_MAX)
					throw new IOException("Header length (" + buffer.length() + ")"
							+ " is larger than the max (" + HEADER_LEN_MAX + ")");
			}
			
			// reset back to original
			connection.setSoTimeout(originalTimeout);
			message = buffer.toString();
			
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
		
		if (!hook.filter(message)) {
			System.err.println("Hook rejecting connection. Disconnecting");
			try { connection.close(); } catch (Exception ex) {};
			return;
		}
		
		hook.connect(wrapInComm(connection));
	}
	
	private Comm wrapInComm(Socket connection) {
		return new WebUI(connection);
	}
}
