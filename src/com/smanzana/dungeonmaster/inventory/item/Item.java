package com.smanzana.dungeonmaster.inventory.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.Notable;
import com.smanzana.dungeonmaster.utils.NoteUtil;
import com.smanzana.templateeditor.api.ISuperclass;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;

public abstract class Item implements Notable, DataCompatible, Cloneable, ISuperclass {
	
	protected static interface ItemFactory<T extends Item> {
		
		public T construct(DataNode data);
		
	}
	
	private static Map<String, ItemFactory<?>> factories = null;
	
	private static void init() {
		factories = new HashMap<>();
		
		Junk.register();
		Usable.register();
		Weapon.register();
		Armor.register();
	}
	
	protected static void registerType(String typeKey, ItemFactory<?> factory) {
		factories.put(typeKey, factory);
	}
	
	public static Item fromData(DataNode node) {
		String key = null;
		
		if (node == null)
			return null;
		
		if (factories == null)
			init();
		
		if (node.getChild("type") != null) {
			key = node.getChild("type").getValue();
		}
		
		if (key == null || !factories.containsKey(key))
			return null;
		
		return factories.get(key).construct(node);
	}
	
	@DataLoaderName
	protected String name;
	@DataLoaderDescription
	protected String description;
	@DataLoaderData
	protected int value;
	
	private List<String> notes;
	
	public Item() {
		notes = new LinkedList<>();
	}
	
	public Item(String name, String desc, int value) {
		this();
		this.name = name;
		this.value = value;
		this.description = desc;
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
			this.value = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("notes"))) {
			this.notes = NoteUtil.deserializeNotes(node.getValue());
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		List<DataNode> list = new LinkedList<>();
		
		list.add(new DataNode("type", getClassKey(), null));
		list.add(new DataNode("name", name, null));
		list.add(new DataNode("description", description, null));
		list.add(new DataNode("value", value + "", null));
		list.add(new DataNode("notes", NoteUtil.serializeNotes(notes), null));
		
		return new DataNode(key, null, list);
	}
	
	protected abstract String getClassKey();
	
	@Override
	public Item clone() {
		DataNode serial = this.write("dummy");
		Item out = Item.fromData(serial);
		
		return out;
	}
	
	@Override
	public List<ISuperclass> getChildTypes() {
		List<ISuperclass> list = new LinkedList<>();
		
		list.add(new Junk());
		list.add(new Armor());
		list.add(new Weapon());
		list.add(new Usable());
		
		return list;
	}
	
	@Override
	public String getChildName(ISuperclass child) {
		return ((Item) child).getClassKey();
	}
	
}
