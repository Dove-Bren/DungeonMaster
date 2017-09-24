package com.smanzana.dungeonmaster.inventory.item;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Weapon extends Equipment {

	private int attack;
	private boolean isShield;
	
	public Weapon() {
		this("", "", 0, Slot.OFF_HAND, 1, 0, false);
	}
	
	/**
	 * If this weapon is a shield, attack is used instead as defense bonus
	 * @param name
	 * @param description
	 * @param value
	 * @param slot
	 * @param durability
	 * @param attack
	 * @param isShield
	 */
	public Weapon(String name, String description, int value, Slot slot, int durability, int attack, boolean isShield) {
		super(name, description, value, slot, durability);
		this.attack = attack;
		this.isShield = isShield;
	}
	
	public int getAttack()
	{
		return attack;
	}
	
	public boolean isShield() {
		return this.isShield;
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		
		if (null != (node = root.getChild("attack"))) {
			this.attack = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("isshield"))) {
			this.isShield = DataNode.parseBool(node);
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("attack", this.attack + "", null));
		base.addChild(new DataNode("isshield", this.isShield + "", null));
		
		return base;
	}
	
	@Override
	public boolean fitsSlot(Slot slot) {
		switch (slot) {
		case MAIN_HAND:
			if (this.isShield)
				return false;
			// fall through
		case OFF_HAND:
			return true;
		
		default:
			return false;
		}
	}

}
