package com.smanzana.dungeonmaster.pawn;

import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Non-player character. 
 * @author Skyler
 *
 */
public class NPC extends Pawn {
	
	public static class NPCOverlay {
		private String race;
		private String name;
		
		public NPCOverlay() {
			;
		}
		
		public NPCOverlay(String race, String name) {
			this.name= name;
			this.race = race;
		}
		
		public NPCOverlay name(String name) {
			this.name = name;
			return this;
		}
		
		public NPCOverlay race(String race) {
			this.race = race;
			return this;
		}
	}

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
	
	public Inventory getInventory() {
		return this.inventory;
	}

	public String getRace() {
		return race;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean damage(Pawn source, int amount) {
		; // do nothing. We're not a Mob
		return false;
	}

	@Override
	public boolean heal(Pawn source, int amount) {
		; // do nothing. Again, not a mob
		return false;
	}
	
	public void applyOverlay(NPCOverlay data) {
		if (data.race != null)
			this.race = data.race;
		
		if (data.name != null)
			this.name = data.name;
	}

}
