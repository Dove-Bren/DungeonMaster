package com.smanzana.dungeonmaster;

import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.ui.UI;
import com.smanzana.dungeonmaster.ui.app.AppUI;
import com.smanzana.dungeonmaster.ui.app.swing.AppSound;

/**
 * D&D Companion Program.
 * Models D&D data
 * @author Skyler
 */
public class DungeonMaster {

	public static final String PATH_TEMPLATES = "templates";
	public static final String PATH_SESSIONS = "session";
	
	private static GameSession activeSession = null;
	private static Thread UIThread = null;
	private static Boolean receivedShutdown = false; // Shut down the whole program
	private static Boolean receivedClose = false; // Shut down running session & UI
	private static Boolean sessionLock = false; // locked for access; true when session active
	
	// Program Client members
	private static AppUI hostUI;
	
	public static void main(String[] args) {
		// Two modes:
		// Creation mode (set up configs)
		// DM mode (play a session)
		
		// Creation mode: Create templates
		// DM mode: Spawn a session from a template OR load an existing session
		
		AppSound.preloadSounds();
		
		hostUI = new AppUI(); // Kicks off program GUI & client
		
		// Wait for a session to run (or shutdown)
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				;
			}
			
			synchronized(receivedShutdown) {
				if (receivedShutdown) {
					shutdown();
					return;
				}
			}
			
			GameSession next = null;
			synchronized(sessionLock) {
				if (activeSession != null) {
					sessionLock = true;
					next = activeSession;
				}
			}
			
			if (next != null) {
				runSessionInternal(next);
				synchronized(sessionLock) {
					sessionLock = false;
				}
			}
		}
		
//		activeSession = new GameSession(new File("testsession"));
//		runSession(activeSession);
	}
	
	public static GameSession getActiveSession() {
		return activeSession;
	}
	
	public static void runSession(GameSession session) {
		synchronized(sessionLock) {
			if (sessionLock || activeSession != null) {
				System.out.println("Error! Already have an active session!");
				return;
			}
			activeSession = session;
		}
		
	}
	
	private static void runSessionInternal(GameSession session) {
		synchronized(receivedClose) {
			receivedClose = false;
		}
		launchSession(session);
		
		try {
			session.run();
		} catch (RuntimeException e) {
			System.out.println("Uncaught Exception:");
			e.printStackTrace();
		}
		
		closeSession();
	}
	
	/**
	 * Launches a session.
	 * This includes performing any initialization calls that are necessary
	 * @param session
	 */
	private static void launchSession(GameSession session) {
		UIThread = new Thread(UI.instance(), "UI Thread");
		UI.instance().setDMComm(hostUI);
		UIThread.start();
	}
	
	public static void shutdown() {
		synchronized(receivedShutdown) {
			if (receivedShutdown) {
				System.out.println("Already received shutdown. Ignoring repeated shutdown");
				return;
			}
			receivedShutdown = true;
		}
		closeSession();
		hostUI.shutdown();
		System.exit(0);
		
	}
	
	public static void closeSession() {
		boolean dirty = false;
		synchronized(receivedClose) {
			if (receivedClose) {
				System.out.println("Already received close. Ignoring repeated session shutdown");
				return;
			}
			receivedClose = true;
		}
		
		if (UIThread != null) {
			System.out.println("Requesting UI Thread shutdown...");
			UI.instance().halt();
			try {
				UIThread.join();
			} catch (InterruptedException e) {
				;
			}
			
			if (UIThread.isAlive()) {
				System.out.println("UI Thread failed to shut down properly.");
				dirty = true;
			} else {
				System.out.println("UI Thread shutdown successfully");
			}
		}
		
		if (activeSession != null) {
			System.out.println("Shutting down Game Thread...");
			activeSession.shutdown();
			
			activeSession = null;
		}
		
		if (dirty) {
			System.out.println("Program shutdown incorrectly. Shutting down");
			shutdown();
		} else {
			System.out.println("Session successfully closed.");
		}
		return;
	}
		
}

