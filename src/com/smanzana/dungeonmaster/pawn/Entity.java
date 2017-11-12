package com.smanzana.dungeonmaster.pawn;

import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.Inventory.InventoryHook;
import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.UI;

public abstract class Entity extends Pawn {

	protected Inventory inventory;
	private String race;
	private String name;
	
	public Entity() {
		this.inventory = new Inventory();
		
		final Entity me = this;
		this.inventory.setInventoryHook(new InventoryHook() {

			@Override
			public boolean buy(Item item, Pawn actor) {
				// We don't have any real reason to say no
				// Config check already has happened
				// Add value though
				inventory.addGold(item.getValue());
				return true;
			}

			@Override
			public boolean steal(Item item, Pawn actor) {
				return (UI.instance().askDM(actor.getName() + " is attempting to steal from " + me.getName()
						+ " (" + item.getName() + "). Allow it?"));
			}
			
		});
	}
	
	@Override
	public void load(DataNode root) {
		this.readBase(root);
		DataNode node;
		
		if (null != (node = root.getChild("race"))) {
			this.race = node.getValue();
		}
		
		if (null != (node = root.getChild("name"))) {
			this.name = node.getValue();
		}
		
		if (null != (node = root.getChild("inventory"))) {
			this.inventory.emptyItems();
			this.inventory.clearEquipment();
			this.inventory.setGold(0);
			this.inventory.load(node);
		}
		
	}

	@Override
	public DataNode write(String key) {
		DataNode base = this.writeBase(key);
		
		base.addChild(new DataNode("race", this.race, null));
		base.addChild(new DataNode("name", this.name, null));
		base.addChild(this.inventory.write("inventory"));
		
		return base;
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}

	public String getRace() {
		return race;
	}

	public String getName() {
		return name;
	}
	
	protected void setRace(String race) {
		this.race = race;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	// If USE_AC, AC from equips. Else damage reduction
	public int getDefenseScore() {
		return this.getInventory().getEquipment().getTotalDefense();
	}
	
	public int getAttackScore() {
		return this.getInventory().getEquipment().getTotalAttack();
	}
	
	// Gets total AC including from equipment.
	// Doesn't check if USE_AC. You must do that.
	public int getAC() {
		int base = super.getAC();
		base += getDefenseScore();
		return base;
	}
	
}
