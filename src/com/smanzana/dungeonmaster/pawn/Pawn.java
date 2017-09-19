package com.smanzana.dungeonmaster.pawn;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.utils.Notable;

/**
 * Base entity class.
 * A Pawn can be a player character, an NPC, and enemy, etc
 * @author Skyler
 *
 */
public abstract class Pawn implements Notable {
	
	private boolean canDie;
	protected boolean dead;
	protected int maxHealth;
	protected int health;
	protected int maxMana;
	protected int mana;
	
	private List<String> notes;
	
	/**
	 * Creates a pawn with the given max attributes.
	 * @param maxHealth
	 * @param maxMana
	 */
	public Pawn(int maxHealth, int maxMana)
	{
		dead = false;
		canDie = true;
		this.health = this.maxHealth = maxHealth;
		this.mana = this.maxMana = maxMana;
		notes = new LinkedList<>();
	}
	
	/**
	 * Creates generic Pawn that cannot die.
	 */
	public Pawn()
	{
		dead = false;
		canDie = false;
		this.health = this.maxHealth = 1;
		this.mana = this.maxMana = 1;
		notes = new LinkedList<>();
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
}
