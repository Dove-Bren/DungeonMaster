package com.smanzana.dungeonmaster.maker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.session.SessionBase;
import com.smanzana.dungeonmaster.session.datums.Datum;

/**
 * Holds a set of configurations to spawn sessions with
 * @author Skyler
 *
 */
public class SessionTemplate extends SessionBase {
	
	private boolean dirty;
	
	public SessionTemplate(File rootDir) {
		super(rootDir, "");
		
		this.load();
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
		this.dirty = false;
	}
	
	/**
	 * Saves template to new root folder.
	 * Does no cleanup of the old root
	 * @param newRoot
	 */
	public void save(File newRoot) {
		this.root = newRoot;
		this.saveConfigs();
		this.saveDatums();
	}
	
	public List<Datum<?>> getDatums() {
		List<Datum<?>> list = new LinkedList<>();
		
		list.add(this.actionDatum);
		list.add(this.classDatum);
		list.add(this.itemDatum);
		list.add(this.npcDatum);
		list.add(this.profileDatum);
		list.add(this.settingDatum);
		list.add(this.spellDatum);
		
		return list;
	}
}
