package com.smanzana.dungeonmaster.ui.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.smanzana.dungeonmaster.ui.Comm;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.dungeonmaster.ui.web.WebUI;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP.HTTPRequest;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP.HTTPResponse;

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
		
		/**
		 * Called when filter rejected a connection.
		 * @return
		 */
		public String generateRejectionPage();
		
		/**
		 * When a connection requests a non-root page, it usually means
		 * They are trying to connect to a Java hook.
		 * @param URI
		 * @param request
		 * @return Response to send to client. If
		 * null, generates a rejection response and sends it.
		 */
		public HTTPResponse doHook(String URI, HTTPRequest request);
	}
	
	public static final int DEFAULT_PORT_LISTEN = 8124;
	
	private AppConnectionHook hook;
	private ServerSocket listenSocket;
	private ServerSocket webSocket;
	private boolean valid;
	private Boolean running;
	private Map<String, ImageIcon> imageMap;
	
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
		imageMap = defaultImages();
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
		HTTP.sendHTTP(connection, 
				HTTP.generateResponse(hook.generateConnectionPage()), true);
	}
	
	private void onConnectEx(Socket connection) {
		
		// Get connection message;
		HTTPRequest request;
		try {
			int originalTimeout = connection.getSoTimeout();
			connection.setSoTimeout(1000);
			
			request = HTTP.readHTTPRequest(connection);
			
			// reset back to original
			connection.setSoTimeout(originalTimeout);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Encountered socket exception during handshake. Disconnecting");
			try { connection.close(); } catch (Exception ex) {};
			return;
		}
		
		if (request == null) {
			System.err.println("Recieved no message from connecting client. Disconnecting");
			try { connection.close(); } catch (Exception ex) {};
			return;
		}
		
		System.out.println("URI requested: " + request.getHeader().getURI().trim());
		String uri = request.getHeader().getURI().trim();
		if (uri.equals("/")) {
			int key = hook.filter(request.getBody());
			if (key == 0) {
				System.err.println("Hook rejecting connection. Disconnecting");
				
				try {
					HTTP.generateResponse(hook.generateRejectionPage())
						.write(connection.getOutputStream());
					connection.getOutputStream().close();
				} catch (IOException e) { }
				return;
			}
			
			hook.connect(key, wrapInComm(connection));
		}
		else {
			// It's something for our hooks?
			uri = stripPath(uri);
			HTTPResponse response = hook.doHook(uri, request);
			
			if (response == null) {
				response = HTTP.generateResponse(404, "ERROR", "");
			}
				
			
			try { 
				HTTP.sendHTTP(connection, response);
				connection.close(); 
			} catch (Exception ex) {};
			return;
		}
	}
	
	private Comm wrapInComm(Socket connection) {
		return new WebUI(connection);
	}
	
	private String stripPath(String raw) {
		if (raw.charAt(0) == '/')
			raw = raw.substring(1);
		return raw;
	}
	
	private static Map<String, ImageIcon> defaultImages() {
		Map<String, ImageIcon> map = new HashMap<>();
		
		map.put("sync_dead.png", AppFrame.createImageIcon("icon/sync_dead.png"));
		map.put("sync_active.png", AppFrame.createImageIcon("icon/sync_active.png"));
		
		return map;
	}
}
