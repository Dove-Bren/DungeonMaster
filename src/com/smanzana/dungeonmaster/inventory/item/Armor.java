package com.smanzana.dungeonmaster.inventory.item;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Armor extends Equipment {

	private int defense;
	
	public Armor() {
		this("", "", 0, Slot.HEAD, 1, 0);
	}
	
	public Armor(String name, String description, int value, Slot slot, int durability, int defense) {
		super(name, description, value, slot, durability);
		this.defense = defense;
	}
	
	public int getDefense()
	{
		return defense;
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		
		if (null != (node = root.getChild("defense"))) {
			this.defense = DataNode.parseInt(node);
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("defense", this.defense + "", null));
		
		return base;
	}
	
	@Override
	public boolean fitsSlot(Slot slot) {
		switch (slot) {
		case HEAD:
		case CHEST:
		case LEGS:
		case ARMS:
		case RING_LEFT:
		case RING_RIGHT:
		case BRACELET:
		case AMULET:
		case CAPE:
			return true;
		
		default:
			return false;
		}
	}

}
