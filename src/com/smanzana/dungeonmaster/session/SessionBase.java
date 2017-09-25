package com.smanzana.dungeonmaster.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.ActionRegistry;
import com.smanzana.dungeonmaster.session.configuration.CombatBonusConfig;
import com.smanzana.dungeonmaster.session.configuration.Config;
import com.smanzana.dungeonmaster.session.configuration.KeywordConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.RollTableConfig;
import com.smanzana.dungeonmaster.session.datums.ActionDatumData;
import com.smanzana.dungeonmaster.session.datums.ClassDatumData;
import com.smanzana.dungeonmaster.session.datums.Datum;
import com.smanzana.dungeonmaster.session.datums.ProfileDatumData;
import com.smanzana.dungeonmaster.session.datums.SettingDatumData;
import com.smanzana.dungeonmaster.session.datums.SpellDatumData;

public abstract class SessionBase {
	
	// Config paths
	private static final String PATH_MECHS = "mechanics.cfg";
	private static final String PATH_COMBONUS = "combatbonuses.cfg";
	private static final String PATH_ROLLTABLE = "rolltables.cfg";
	private static final String PATH_KEYWORDS = "keywords.cfg";
	
	// Datum paths
	private static final String PATH_CLASS = "class.dat";
	private static final String PATH_PROFILE = "profiles.dat";
	private static final String PATH_ACTIONS = "actions.dat";
	private static final String PATH_SPELLS = "spells.dat";
	private static final String PATH_SETTING = "setting.dat";
	
	// Datums
	protected Datum<ClassDatumData> classDatum;
	protected Datum<ProfileDatumData> profileDatum;
	protected Datum<ActionDatumData> actionDatum;
	protected Datum<SpellDatumData> spellDatum;
	protected Datum<SettingDatumData> settingDatum;
	
	protected File root;
	protected String configDir;
	
	protected SessionBase(File root, String configDir) {
		this.root = root;
		this.configDir = configDir;
		
		this.classDatum = new Datum<ClassDatumData>("class", new ClassDatumData.ClassDatumFactory());
		this.profileDatum = new Datum<ProfileDatumData>("profile", new ProfileDatumData.ProfileDatumFactory());
		this.actionDatum = new Datum<>("action", new ActionDatumData.ActionDatumFactory());
		this.spellDatum = new Datum<>("spell", new SpellDatumData.SpellDatumFactory());
		this.settingDatum = new Datum<>("setting", new SettingDatumData.SettingDatumFactory());
	}
	
	public ClassDatumData lookupClass(String className) {
		for (ClassDatumData data : classDatum.getData()) {
			if (data.getName().equals(className))
				return data;
		}
		
		return null;
	}
	
	public ProfileDatumData lookupProfile(String profileName) {
		for (ProfileDatumData profile : profileDatum.getData()) {
			if (profile.getProfileName().equals(profileName))
				return profile;
		}
		
		return null;
	}
	
	public SpellDatumData lookupSpell(String spellName) {
		for (SpellDatumData data : spellDatum.getData()) {
			if (data.getSpell().getName().equals(spellName))
				return data;
		}
		
		return null;
	}
	
	public Action lookupAction(String actionName) {
		return ActionRegistry.instance().lookupAction(actionName);
	}
	
	public File getRoot() {
		return root;
	}
	
	public String getConfigDir() {
		return configDir;
	}
	
	/**
	 * Loads configs and datums from the appropriate subdirs of root
	 */
	protected void load() {
		loadConfigs();
		loadDatums();
	}
	
	private void loadDatum(File configDir, String path, Datum<?> datum) {
		File f = new File(configDir, path);

		if (!f.exists()) {
			try {
				datum.createDefaultFile(f);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			datum.loadFromFile(f);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Encountered error while reading data file at " + f.getAbsolutePath());
		}
	}
	
	protected void loadDatums() {
		File configDir = new File(root, this.configDir);
		if (!configDir.exists())
			configDir.mkdirs();
		
		loadDatum(configDir, PATH_CLASS, this.classDatum);
		loadDatum(configDir, PATH_PROFILE, this.profileDatum);
		loadDatum(configDir, PATH_ACTIONS, this.actionDatum);
		loadDatum(configDir, PATH_SPELLS, this.spellDatum);
		loadDatum(configDir, PATH_SETTING, this.settingDatum);
	}
	
	private void loadConfig(File configDir, String path, Config<?> instance) {
		File f = new File(configDir, path);
		if (f.exists())
		{
			try {
				instance.readFromFile(f);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Encountered error while reading config at " + f.getAbsolutePath());
			}
		}
		else
		{
			try {
				instance.writeToFile(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Failed to create default mechanics config at " + f.getAbsolutePath());
			}
		}
	}
	
	protected void loadConfigs() {
		File configDir = new File(root, this.configDir);
		if (!configDir.exists())
			configDir.mkdirs();
		
		loadConfig(configDir, PATH_MECHS, MechanicsConfig.instance());
		loadConfig(configDir, PATH_COMBONUS, CombatBonusConfig.instance());
		loadConfig(configDir, PATH_ROLLTABLE, RollTableConfig.instance());
		loadConfig(configDir, PATH_KEYWORDS, KeywordConfig.instance());
	}
	
	protected void save() {
		saveConfigs();
		saveDatums();
	}
	
	private void saveDatum(File configDir, String path, Datum<?> datum) {
		File f = new File(configDir, path);
		try {
			datum.saveToFile(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Failed to save data file to " + f.getAbsolutePath());
		}
	}
	
	protected void saveDatums() {
		File configDir = new File(root, this.configDir);
		if (!configDir.exists())
			configDir.mkdirs();
		
		saveDatum(configDir, PATH_CLASS, this.classDatum);
		saveDatum(configDir, PATH_PROFILE, this.profileDatum);
		saveDatum(configDir, PATH_ACTIONS, this.actionDatum);
		saveDatum(configDir, PATH_SPELLS, this.spellDatum);
		saveDatum(configDir, PATH_SETTING, this.settingDatum);
	}
	
	private void saveConfig(File configDir, String path, Config<?> instance) {
		File f = new File(configDir, path);
		try {
			instance.writeToFile(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Failed to save out config file to " + f.getAbsolutePath());
		}
	}
	
	protected void saveConfigs() {
		File configDir = new File(root, this.configDir);
		if (!configDir.exists())
			configDir.mkdirs();
		
		saveConfig(configDir, PATH_MECHS, MechanicsConfig.instance());
		saveConfig(configDir, PATH_COMBONUS, CombatBonusConfig.instance());
		saveConfig(configDir, PATH_ROLLTABLE, RollTableConfig.instance());
		saveConfig(configDir, PATH_KEYWORDS, KeywordConfig.instance());
	}
}
