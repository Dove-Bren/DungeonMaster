package com.smanzana.dungeonmaster.maker;

import java.io.File;

import com.smanzana.dungeonmaster.session.SessionBase;

/**
 * Holds a set of configurations to spawn sessions with
 * @author Skyler
 *
 */
public class SessionTemplate extends SessionBase {
	
	private boolean dirty;
	
	public SessionTemplate(File rootDir) {
		super(rootDir, "");
		
		this.loadConfigs();
		dirty = false;
	}
	
	public void dirty() {
		this.dirty = true;
	}
	
	public boolean isDirty() {
		return this.dirty;
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
