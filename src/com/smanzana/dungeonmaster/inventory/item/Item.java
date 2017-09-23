package com.smanzana.dungeonmaster.inventory.item;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.Notable;
import com.smanzana.dungeonmaster.utils.NoteUtil;

public abstract class Item implements Notable, DataCompatible {
	
	protected String name;
	protected String description;
	protected int value;
	
	private List<String> notes;
	
	public Item(String name, String desc, int value) {
		this.name = name;
		this.value = value;
		this.description = desc;
		notes = new LinkedList<>();
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
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
	
	@Override
	public void load(DataNode root) {
		DataNode node;
		this.notes.clear();
		
		if (null != (node = root.getChild("name"))) {
			this.name = node.getValue();
		}
		
		if (null != (node = root.getChild("description"))) {
			this.description = node.getValue();
		}
		
		if (null != (node = root.getChild("value"))) {
			try {
				this.value = Integer.parseInt(node.getValue());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to an int");
			}
		}
		
		if (null != (node = root.getChild("notes"))) {
			this.notes = NoteUtil.deserializeNotes(node.getValue());
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		List<DataNode> list = new LinkedList<>();
		
		list.add(new DataNode("name", name, null));
		list.add(new DataNode("description", description, null));
		list.add(new DataNode("value", value + "", null));
		list.add(new DataNode("notes", NoteUtil.serializeNotes(notes), null));
		
		return new DataNode(key, null, list);
	}
	
}
