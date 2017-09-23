package com.smanzana.dungeonmaster.inventory.item;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.battle.effects.Effect;

public abstract class Equipment extends Item {
	
	public enum Slot
	{
		HEAD(true),
		CHEST(true),
		LEGS(true),
		ARMS(true),
		MAIN_HAND(false),
		OFF_HAND(false),
		RING_LEFT(true),
		RING_RIGHT(true),
		BRACELET(true),
		AMULET(true),
		CAPE(true);
		
		private boolean isArmor;
		
		private Slot(boolean isArmor) {
			this.isArmor = isArmor;
		}
		
		public boolean getIsArmor() {
			return this.isArmor;
		}
	}
	
	protected Slot slot;
	protected int durability;
	protected int maxDurability;
	protected List<Effect> effects;
	
	public Equipment() {
		this("", "", 0, Slot.HEAD, 1);
	}
	
	public Equipment(String name, String description, int value, Slot slot, int durability)
	{
		super(name, description, value);
		this.slot = slot;
		this.maxDurability = this.durability = durability;
		this.effects = new LinkedList<Effect>();
	}
	
	public Slot getSlot()
	{
		return this.slot;
	}
	
	public int getDurability()
	{
		return durability;
	}
	
	public int getMaxDurability()
	{
		return maxDurability;
	}
	
	/**
	 * Damages the equipment's durability.
	 * If the damage breaks as a result of this damage, returns true.
	 * @param amount
	 * @return
	 */
	public boolean damageDurability(int amount)
	{
		durability -= amount;

		return durability <= 0;
	}
	
	public boolean damageDurability()
	{
		return damageDurability(1);
	}
	
	public List<Effect> getEffects()
	{
		return effects;
	}
	
	public void addEffect(Effect e)
	{
		if (effects == null)
			effects = new LinkedList<Effect>();
		
		effects.add(e);
	}
	
	public void clearEffects()
	{
		effects.clear();
	}
	
	/**
	 * Performs a basic usage of the equipment.
	 * For armor, this likely means the armor blocked a blow.
	 * Weapons, then, use this when they are used to attack.
	 * Subclasses are encouraged to implement and call this function.
	 * The stub from Equipment only does durability accounting
	 * @return True if the equipment has broken
	 */
	public boolean use()
	{
		// Do durability damage if config says to
		// TODO
		
		// TODO also hook into effects
		
		return this.durability <= 0;
	}
	
}
