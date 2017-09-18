package com.smanzana.dungeonmaster.inventory.item;

public abstract class Item {
	
	protected String name;
	protected int value;
	
	public Item(String name, int value)
	{
		this.name= name;
		this.value = value;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public String getName()
	{
		return this.name;
	}
	
}
