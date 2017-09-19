package com.smanzana.dungeonmaster.inventory.item;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.utils.Notable;

public abstract class Item implements Notable {
	
	protected String name;
	protected int value;
	
	private List<String> notes;
	
	public Item(String name, int value)
	{
		this.name= name;
		this.value = value;
		notes = new LinkedList<>();
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public String getName()
	{
		return this.name;
	}
	
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
