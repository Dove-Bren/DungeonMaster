package com.smanzana.dungeonmaster.inventory.item;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;
import com.smanzana.templateeditor.api.ISuperclass;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;

public class Weapon extends Equipment {

	private static class Factory implements ItemFactory<Weapon> {
		@Override
		public Weapon construct(DataNode data) {
			Weapon wep = new Weapon();
			wep.load(data);
			return wep;
		}
		
	}
	
	@DataLoaderData
	private ValueSpecifier attack;
	@DataLoaderData(name="Is Shield",description="Is this a shield? Shields add their 'attack' value to defense. Shields can only be equiped in the off-hand.")
	private boolean isShield;
	
	public Weapon() {
		this("", "", 0, 1, 0, false);
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
	public Weapon(String name, String description, int value, int durability, int attack, boolean isShield) {
		this(name, description, value, durability, new ValueConstant(attack), isShield);
	}
	
	public Weapon(String name, String description, int value, int durability, ValueSpecifier attack, boolean isShield) {
		super(name, description, value, durability);
		this.attack = attack;
		this.isShield = isShield;
	}
	
	public ValueSpecifier getAttack()
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
			this.attack = ValueSpecifier.fromData(node);
		}
		
		if (null != (node = root.getChild("isshield"))) {
			this.isShield = DataNode.parseBool(node);
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(this.attack.write("attack"));
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
	
	@Override
	protected String getClassKey() {
		return ClassKey();
	}
	
	protected static String ClassKey() {
		return "weapon";
	}
	
	protected static void register() {
		Item.registerType(ClassKey(), new Factory());
	}

	@Override
	public ISuperclass cloneObject() {
		return new Weapon(name, description, value, durability, attack, isShield);
	}
}
