package com.smanzana.dungeonmaster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;

/**
 * D&D Companion Program.
 * Models D&D data
 * @author Skyler
 */
public class DungeonMaster {

	private static final String PATH_CONFIG = "config/";
	private static final String PATH_MECHS = "mechanics.cfg";
	
	public static void main(String[] args) {
		loadConfigs();
		
		// do stuff
		
		saveConfigs();
	}
	
	private static void loadConfigs() {
		File configDir = new File(PATH_CONFIG);
		if (!configDir.exists())
			configDir.mkdirs();
		
		File mf = new File(configDir, PATH_MECHS);
		if (mf.exists())
		{
			try {
				MechanicsConfig.instance().readFromFile(mf);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Encountered error while reading mechanics config at " + mf.getAbsolutePath());
			}
		}
		else
		{
			try {
				MechanicsConfig.instance().writeToFile(mf);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Failed to create default mechanics config at " + mf.getAbsolutePath());
			}
		}
	}
	
	private static void saveConfigs() {
		File configDir = new File(PATH_CONFIG);
		if (!configDir.exists())
			configDir.mkdirs();
		
		File mf = new File(configDir, PATH_MECHS);
		try {
			MechanicsConfig.instance().writeToFile(mf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Failed to save out mechanics config file to " + mf.getAbsolutePath());
		}
	}
	
}

