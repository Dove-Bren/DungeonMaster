package com.smanzana.dungeonmaster;

import java.io.File;

import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.ui.UI;

/**
 * D&D Companion Program.
 * Models D&D data
 * @author Skyler
 */
public class DungeonMaster {

	private static GameSession activeSession = null;
	private static Thread UIThread = null;
	private static boolean receivedShutdown = false; // Prevent multiple-processing
	
	public static void main(String[] args) {
		// Two modes:
		// Creation mode (set up configs)
		// DM mode (play a session)
		
		// Creation mode: Create templates
		// DM mode: Spawn a session from a template OR load an existing session
		
		activeSession = new GameSession(new File("testsession"));
	}
	
	public static GameSession getActiveSession() {
		return activeSession;
	}
	
	private static void runSession(GameSession session) {
		launchSession(session);
		session.run();
	}
	
	/**
	 * Launches a session.
	 * This includes performing any initialization calls that are necessary
	 * @param session
	 */
	private static void launchSession(GameSession session) {
		UIThread = new Thread(UI.instance());
		UIThread.start();
	}
	
	public static void shutdown() {
		if (receivedShutdown) {
			System.out.println("Already received shutdown. Ignoring repeated shutdown");
			return;
		}
		
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
		
		return;
	}
		
}

