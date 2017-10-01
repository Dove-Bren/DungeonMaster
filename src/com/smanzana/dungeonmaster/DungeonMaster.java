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

	private static GameSession activeSession = null;
	private static Thread UIThread = null;
	private static Boolean receivedShutdown = false; // Prevent multiple-processing
	
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
		
//		activeSession = new GameSession(new File("testsession"));
//		runSession(activeSession);
	}
	
	public static GameSession getActiveSession() {
		return activeSession;
	}
	
	public static void runSession(GameSession session) {
		if (activeSession != null) {
			System.out.println("Error! Already have an active session!");
			return;
		}
		activeSession = session;
		launchSession(session);
		
		try {
			session.run();
		} catch (RuntimeException e) {
			System.out.println("Uncaught Exception:");
			e.printStackTrace();
		}
		
		shutdown();
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
		
		if (UIThread != null) {
			System.out.println("Requesting UI Thread shutdown (waiting up to 10 seconds)...");
			UI.instance().halt();
			try {
				UIThread.join(1000 * 10);
			} catch (InterruptedException e) {
				;
			}
			
			if (UIThread.isAlive()) {
				System.out.println("UI Thread failed to shut down properly.");
			} else {
				System.out.println("UI Thread shutdown successfully");
			}
		}
		
		if (activeSession != null) {
			System.out.println("Shutting down Game Thread...");
			activeSession.shutdown();
			
			activeSession = null;
		}
		
		System.exit(0);
		return;
	}
		
}

