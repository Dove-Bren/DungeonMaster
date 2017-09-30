package com.smanzana.dungeonmaster.pawn;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.Inventory.InventoryHook;
import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.session.datums.NPCDatumData;
import com.smanzana.dungeonmaster.session.datums.ProfileDatumData;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

/**
 * Non-player character. 
 * @author Skyler
 *
 */
public class NPC extends Entity {
	
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
	private boolean willTrade;
	
	public NPC() {
		super();
//		this.inventory = new Inventory();
//		this.inventory.setInventoryHook(new InventoryHook() {
//
//			@Override
//			public boolean buy(Item item, Pawn actor) {
//				// We don't have any real reason to say no
//				// Config check already has happened
//				// Add value though
//				inventory.addGold(item.getValue());
//				return true;
//			}
//
//			@Override
//			public boolean steal(Item item, Pawn actor) {
//				return (askDM());
//			}
//			
//		});
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		
		if (null != (node = root.getChild("willtrade"))) {
			this.willTrade = DataNode.parseBool(node);
		}
		
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("willtrade", this.willTrade + "", null));
		
		return base;
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
	
	@Override
	public boolean recoverMana(Pawn source, int amount) {
		;
		return false;
	}
	
	@Override
	public boolean recoverStamina(Pawn source, int amount) {
		;
		return false;
	}
	
	public void applyOverlay(NPCOverlay data) {
		if (data.race != null)
			this.setRace(data.race);
		
		if (data.name != null)
			this.setName(data.name);
		
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
				.name(getName())
				.race(getRace())
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
			npc.setName(prof.getGeneratedName());
			npc.setRace(prof.getRace());
		}
		
		npc.willTrade = data.isWillTrade();
		
		npc.inventory = data.getInventory();
		npc.stats = data.getStats();
		
		return npc;
	}

}
