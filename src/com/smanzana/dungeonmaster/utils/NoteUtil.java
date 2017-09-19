package com.smanzana.dungeonmaster.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Convenience de/serialization tool
 * @author Skyler
 *
 */
public class NoteUtil {

	private static final String DELIM = "$";
	
	public static String serializeNotes(Collection<String> notes) {
		String buf = "";
		for (String s : notes)
			buf += s + DELIM;
		
		return buf;
	}
	
	public static Collection<String> deserializeNotes(String serial) {
		List<String> list = new LinkedList<String>();
		//serial.trim(); dont. Keep user whitespace
		
		int pos = 0;
		String sub;
		while ((pos = serial.indexOf(DELIM)) != -1) {
			// found DELIM
			sub = serial.substring(0, pos);
			serial = serial.substring(pos + 1);
			list.add(sub);
		}
		
		// no more DELIM.
		// if serial is left with non-empty string, add that too (covers the case where there isn't DELIM)
		if (!serial.isEmpty() && !serial.trim().isEmpty())
			list.add(serial);
		
		return list;
	}
	
}
