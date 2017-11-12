package com.smanzana.dungeonmaster.inventory.item;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.templateeditor.api.ISuperclass;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;

public class Armor extends Equipment {

	private static class Factory implements ItemFactory<Armor> {
		@Override
		public Armor construct(DataNode data) {
			Armor armor = new Armor();
			armor.load(data);
			return armor;
		}
		
	}
	
	@DataLoaderData
	private int defense;
	@DataLoaderData
	private Slot slot;
	
	public Armor() {
		this("", "", 0, Slot.HEAD, 1, 0);
	}
	
	public Armor(String name, String description, int value, Slot slot, int durability, int defense) {
		super(name, description, value, durability);
		this.defense = defense;
		this.slot = slot;
	}
	
	public int getDefense()
	{
		return defense;
	}
	
	public Slot getSlot() {
		return this.slot;
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		
		if (null != (node = root.getChild("defense"))) {
			this.defense = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("slot"))) {
			try {
				this.slot = Slot.valueOf(node.getValue().toUpperCase());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to an equipment slot");
			}
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("slot", slot.name(), null));
		base.addChild(new DataNode("defense", this.defense + "", null));
		
		return base;
	}
	
	@Override
	protected String getClassKey() {
		return ClassKey();
	}
	
	protected static String ClassKey() {
		return "armor";
	}
	
	protected static void register() {
		Item.registerType(ClassKey(), new Factory());
	}

	@Override
	public boolean fitsSlot(Slot slot) {
		return slot == this.slot;
	}

	@Override
	public ISuperclass cloneObject() {
		return new Armor(name, description, value, slot, durability, defense);
	}
}
