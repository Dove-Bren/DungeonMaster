package com.smanzana.dungeonmaster;

import java.io.File;

import com.smanzana.dungeonmaster.session.GameSession;

/**
 * D&D Companion Program.
 * Models D&D data
 * @author Skyler
 */
public class DungeonMaster {

	
	public static void main(String[] args) {
		// Two modes:
		// Creation mode (set up configs)
		// DM mode (play a session)
		
		// Creation mode: Create templates
		// DM mode: Spawn a session from a template OR load an existing session
		
		new GameSession(new File("testsession"));
		
	}
		
}

