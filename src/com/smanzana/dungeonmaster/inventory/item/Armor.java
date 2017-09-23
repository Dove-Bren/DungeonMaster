package com.smanzana.dungeonmaster.inventory.item;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Armor extends Equipment {

	private int defense;
	
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
			try {
				this.defense = Integer.parseInt(node.getValue());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to a number");
			}
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("defense", this.defense + "", null));
		
		return base;
	}

}
