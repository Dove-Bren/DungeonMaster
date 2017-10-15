package com.smanzana.dungeonmaster.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Convenience de/serialization tool
 * @author Skyler
 *
 */
public class NoteUtil {

	private static final String DELIM = "$";
	
	public static String serializeNotes(List<String> notes) {
		String buf = "";
		for (String s : notes)
			buf += s + DELIM;
		
		return buf;
	}
	
	public static List<String> deserializeNotes(String serial) {
		List<String> list = new LinkedList<String>();
		//serial.trim(); dont. Keep user whitespace
		
		if (serial == null || serial.trim().isEmpty())
			return list;
		
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
	
	/**
	 * Removes _'s and camel-cases stuff
	 * @param raw
	 * @return
	 */
	public static String pretty(String raw) {
		String buf = "";
			
		buf += raw.substring(0, 1);
		raw = raw.substring(1);
		raw = raw.toLowerCase();
		
		int pos;
		while (-1 != (pos = raw.indexOf('_'))) {
			// pos is position of first underscore.
			// copy up to pos into buf. Then copy char after pos as uppercase.
			// then set raw past capital char
			buf += raw.substring(0, pos);
			buf += raw.substring(pos + 1, pos + 2).toUpperCase();
			raw = raw.substring(pos + 2);
		}
		buf += raw;
		
		return buf;
	}
	
}
