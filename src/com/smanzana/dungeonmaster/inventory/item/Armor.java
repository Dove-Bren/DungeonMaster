package com.smanzana.dungeonmaster.inventory.item;

public class Armor extends Equipment {

	private int defense;
	
	public Armor(String name, int value, Slot slot, int durability, int defense) {
		super(name, value, slot, durability);
		this.defense = defense;
	}
	
	public int getDefense()
	{
		return defense;
	}

}
