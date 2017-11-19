package com.smanzana.dungeonmaster.ui.app;

import java.io.IOException;
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
		public void onConnect(Comm newComm);
	}
	
	private static final int PORT_LISTEN = 15251;
	
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
				
				hook.onConnect(wrapInComm(connection));
			}
			
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
	
	private Comm wrapInComm(Socket connection) {
		return new WebUI(connection);
	}
}
