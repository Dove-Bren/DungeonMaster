package com.smanzana.dungeonmaster.session;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.utils.Notable;

public class GameSession extends SessionBase implements Notable {
	
	private static final String PATH_CONFIG = "data/";
	
	private List<String> notes;
		
	public GameSession(File root) {
		super(root, PATH_CONFIG);
		notes = new LinkedList<>();
		
		// just for testing
		load();
		// just for testing
		
		// Nothing special; Session does not begin until 'run' is called
	}
	
	/**
	 * Runs session
	 * Blocks until it has finished.
	 * Sessions are responsible for loading and saving all their own data
	 */
	public void run() {
		load();
		
		// Run the game
		// Detect end
		
		save();
	}

	
	@Override
	public void addNote(String note) {
		notes.add(note);
	}
	
	@Override
	public void clearNotes() {
		notes.clear();
	}
	
	@Override
	public Collection<String> getNotes() {
		return notes;
	}
	
}
