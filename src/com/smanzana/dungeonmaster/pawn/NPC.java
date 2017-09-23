package com.smanzana.dungeonmaster.pawn;

import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Non-player character. 
 * @author Skyler
 *
 */
public class NPC extends Pawn {

	private Inventory inventory;
	private String race;
	private String name;
	
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
			this.inventory = new Inventory();
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

	@Override
	public boolean damage(int amount) {
		; // do nothing. We're not a Mob
		return false;
	}

	@Override
	public boolean heal(int amount) {
		; // do nothing. Again, not a mob
		return false;
	}

}
