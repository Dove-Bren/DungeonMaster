package com.smanzana.dungeonmaster.utils;

import java.util.Collection;

/**
 * Can store notes
 * @author Skyler
 *
 */
public interface Notable {

	public void addNote(String note);
	public void clearNotes();
	public Collection<String> getNotes();
	
}
