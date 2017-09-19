package com.smanzana.dungeonmaster.maker;

import java.io.File;

import com.smanzana.dungeonmaster.session.SessionBase;

/**
 * Holds a set of configurations to spawn sessions with
 * @author Skyler
 *
 */
public class SessionTemplate extends SessionBase {
	
	public SessionTemplate(File rootDir) {
		super(rootDir, "");
		
		this.loadConfigs();
	}
	
	/**
	 * Saves template out to current root
	 */
	public void save() {
		save(this.root);
	}
	
	/**
	 * Saves template to new root folder.
	 * Does no cleanup of the old root
	 * @param newRoot
	 */
	public void save(File newRoot) {
		this.root = newRoot;
		this.saveConfigs();
	}
}
