package com.smanzana.dungeonmaster.pawn;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.Inventory.InventoryHook;
import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.session.datums.NPCDatumData;
import com.smanzana.dungeonmaster.session.datums.ProfileDatumData;
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
		private Boolean willTrade;
		
		public NPCOverlay() {
			;
		}
		
		public NPCOverlay(String race, String name, boolean willTrade) {
			this.name= name;
			this.race = race;
			this.willTrade = willTrade;
		}
		
		public NPCOverlay name(String name) {
			this.name = name;
			return this;
		}
		
		public NPCOverlay race(String race) {
			this.race = race;
			return this;
		}
		
		public NPCOverlay trades(boolean willTrade) {
			this.willTrade = willTrade;
			return this;
		}
	}

	protected String templateName; // set when spawned from data, for lookup
	protected Inventory inventory;
	private String race;
	private String name;
	private boolean willTrade;
	
	public NPC() {
		super();
		this.inventory = new Inventory();
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
				return (askDM());
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
		
		if (null != (node = root.getChild("willtrade"))) {
			this.willTrade = DataNode.parseBool(node);
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
		base.addChild(new DataNode("willtrade", this.willTrade + "", null));
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
	
	public boolean willTrade() {
		return willTrade;
	}
	
	public void setWillTrade(boolean willTrade) {
		this.willTrade = willTrade;
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
		
		if (data.willTrade != null)
			this.willTrade = data.willTrade;
	}
	
	public Mob toMob(boolean ally) {
		
		Mob mob = new Mob();
		mob.templateName = templateName;

		PawnOverlay po = (new PawnOverlay())
				.dead(dead)
				.hp(getHealth())
				.maxhp(getMaxHealth())
				.mp(getMana())
				.maxmp(getMana())
				.stamina(getStamina())
				.maxstamina(getMaxStamina())
				.killable(canDie);
		for (Attributes attr : Attributes.values())
			po.score(attr, stats.getAbilityScore(attr));
		mob.applyOverlay(po);
		
		mob.applyOverlay((new NPCOverlay())
				.name(name)
				.race(race)
				.trades(willTrade)
				);
		
		// Look up NPCDatumData and create Mob with this.* + datum data missing pieces
		if (templateName == null) {
			// Prompt DM for XP value
		} else {
			NPCDatumData data = DungeonMaster.getActiveSession().lookupNPC(templateName);
			if (data != null) {
				mob.setXP(data.getXp());
			}
		}
		
		mob.setAllied(ally);
		
		return mob;
	}
	
	public static NPC spawn(NPCDatumData data) {
		if (data == null)
			return null;
		
		NPC npc = new NPC();
		npc.templateName = data.getTemplateName();
		npc.dead = false;
		ProfileDatumData prof = DungeonMaster.getActiveSession().lookupProfile(data.getProfileName());
		if (prof != null) {
			npc.name = prof.getGeneratedName();
			npc.race = prof.getRace();
		}
		
		npc.willTrade = data.isWillTrade();
		
		npc.inventory = data.getInventory();
		npc.stats = data.getStats();
		
		return npc;
	}

}
