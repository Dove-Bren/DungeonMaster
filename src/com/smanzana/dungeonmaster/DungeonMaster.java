package com.smanzana.dungeonmaster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.smanzana.dungeonmaster.session.datums.ClassDatum;

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
		
		ClassDatum datum = new ClassDatum();
		
		try {
			datum.loadFromFile(new File("example1234.ex"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			datum.saveToFile(new File("outagainfile.ex"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}

