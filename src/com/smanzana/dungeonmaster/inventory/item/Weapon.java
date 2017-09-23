package com.smanzana.dungeonmaster.inventory.item;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Weapon extends Equipment {

	private int attack;
	
	public Weapon() {
		this("", "", 0, Slot.OFF_HAND, 1, 0);
	}
	
	public Weapon(String name, String description, int value, Slot slot, int durability, int attack) {
		super(name, description, value, slot, durability);
		this.attack = attack;
	}
	
	public int getAttack()
	{
		return attack;
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		
		if (null != (node = root.getChild("attack"))) {
			try {
				this.attack = Integer.parseInt(node.getValue());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to a number");
			}
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("attack", this.attack + "", null));
		
		return base;
	}

}
