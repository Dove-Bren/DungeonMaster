package com.smanzana.dungeonmaster.pawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.Notable;
import com.smanzana.dungeonmaster.utils.NoteUtil;
import com.smanzana.dungeonmaster.utils.StatSet;

/**
 * Base entity class.
 * A Pawn can be a player character, an NPC, and enemy, etc
 * @author Skyler
 *
 */
public abstract class Pawn implements Notable, DataCompatible {
	
	private boolean canDie;
	protected boolean dead;
	
	private StatSet stats;
	
	private List<String> notes;
		
	/**
	 * Creates generic Pawn that cannot die.
	 */
	public Pawn()
	{
		dead = false;
		canDie = false;
		notes = new LinkedList<>();
		this.stats = new StatSet();
	}
	
	/**
	 * Creates a pawn with the given max attributes.
	 * @param maxHealth
	 * @param maxMana
	 */
	public Pawn(int maxHealth, int maxMana)
	{
		this();
		canDie = true;
		stats.setHealth(maxHealth);
		stats.setMaxHealth(maxHealth);
		stats.setMana(maxMana);
		stats.setMaxMana(maxMana);
	}
	
	public boolean getCanDie()
	{
		return canDie;
	}
	
	/**
	 * Damages the pawn by the given amount.
	 * Checks health and, if they can die and did, returns true.
	 * @param amount
	 * @return true when the entity dies.
	 */
	public abstract boolean damage(int amount);
	
	/**
	 * Heals conventually. This could be interpreted as damage to undead.
	 * @param amount
	 * @return true if the pawn dies
	 */
	public abstract boolean heal(int amount);
	
	@Override
	public void addNote(String note) {
		notes.add(note);
	}
	
	@Override
	public void clearNotes() {
		notes.clear();
	}
	
	@Override
	public Collection<String> getNotes() {
		return notes;
	}
	
	/**
	 * Creates a DataNode and writes in base objects.
	 * Suggested do this before adding subclass objects
	 * @param key
	 * @return
	 */
	protected DataNode writeBase(String key) {
		List<DataNode> list = new ArrayList<>(4);
		
		list.add(new DataNode("killable", this.canDie + "", null));
		list.add(new DataNode("dead", this.dead + "", null));
		list.add(this.stats.write("stats"));
		list.add(new DataNode("notes", NoteUtil.serializeNotes(notes), null));
		
		return new DataNode(key, null, list);
	}
	
	protected void readBase(DataNode root) {
		DataNode node;
		this.canDie = false;
		this.dead = false;
		
		if (null != (node = root.getChild("killable"))) {
			this.canDie = node.getValue().trim().equalsIgnoreCase("true");
		}
		
		if (null != (node = root.getChild("dead"))) {
			this.canDie = node.getValue().trim().equalsIgnoreCase("true");
		}
		
		if (null != (node = root.getChild("stats"))) {
			this.stats.load(node);
		}
		
		if (null != (node = root.getChild("notes"))) {
			this.notes = NoteUtil.deserializeNotes(node.getValue());
		}
	}
}
