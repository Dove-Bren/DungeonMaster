package com.smanzana.dungeonmaster.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;

public abstract class SessionBase {
	
	private static final String PATH_MECHS = "mechanics.cfg";
	
	protected File root;
	protected String configDir;
	
	protected SessionBase(File root, String configDir) {
		this.root = root;
		this.configDir = configDir;
	}
	
	public File getRoot() {
		return root;
	}
	
	public String getConfigDir() {
		return configDir;
	}
	
	protected void loadConfigs() {
		File configDir = new File(root, this.configDir);
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
	
	protected void saveConfigs() {
		File configDir = new File(root, this.configDir);
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
