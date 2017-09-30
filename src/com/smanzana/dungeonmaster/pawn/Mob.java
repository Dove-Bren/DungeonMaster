package com.smanzana.dungeonmaster.pawn;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.session.datums.NPCDatumData;
import com.smanzana.dungeonmaster.session.datums.ProfileDatumData;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

/**
 * NPC that is in combat
 * @author Skyler
 *
 */
public class Mob extends NPC {
	
	public static class MobOverlay {
		private Boolean undead;
		private Boolean isAlly;
		private Integer xp;
		private List<Effect> effects;
		
		public MobOverlay() {
			effects = new LinkedList<>();
		}

		public MobOverlay(Boolean undead, Boolean isAlly, Integer xp, List<Effect> effects) {
			this.undead = undead;
			this.isAlly = isAlly;
			this.xp = xp;
			this.effects = effects;
		}
		
		public MobOverlay addEffect(Effect effect) {
			this.effects.add(effect);
			return this;
		}

		public MobOverlay undead(boolean undead) {
			this.undead = undead;
			return this;
		}

		public MobOverlay ally(boolean ally) {
			this.isAlly = ally;
			return this;
		}

		public MobOverlay xp(int xp) {
			this.xp = xp;
			return this;
		}
	}
	
	private List<Effect> activeEffects;
	private boolean undead;
	private boolean isAlly;
	private int xp;
	
	protected Mob() {
		super();
		this.activeEffects = new LinkedList<>();
	}
	
	public Mob(int hp, int mp, int stamina, int xp, boolean ally) {
		this();
		this.stats.setHealth(hp);
		this.stats.setMaxHealth(hp);
		this.stats.setMaxMana(mp);
		this.stats.setMana(mp);
		this.stats.setStamina(stamina);
		this.stats.setMaxStamina(stamina);
		this.xp = xp;
		this.isAlly = ally;
		this.undead = false;
	}
	
	public boolean isUndead() {
		return this.undead;
	}
	
	public void setUndead(boolean undead) {
		this.undead = undead;
	}
	
	public boolean isAlly() {
		return this.isAlly;
	}
	
	public void setAllied(boolean ally) {
		this.isAlly = ally;
	}
	
	public int getXP() {
		return this.xp;
	}
	
	public void setXP(int xp) {
		this.xp = xp;
	}
	
	@Override
	public boolean damage(Pawn source, int amount) {
		
		ValueCapsule capsule = new ValueCapsule(amount);
		Effect.doPreEffects(activeEffects, source, this, capsule);
		
		this.stats.addHealth(-capsule.getFinal());
		
		Effect.doPostEffects(activeEffects, source, this, capsule);
		
		return this.stats.getHealth() <= 0;
	}

	@Override
	public boolean heal(Pawn source, int amount) {
		ValueCapsule capsule = new ValueCapsule(amount);
		Effect.doPreEffects(activeEffects, source, this, capsule);
		
		if (undead)
			damage(source, capsule.getFinal());
		else
			this.stats.addHealth(capsule.getFinal());
		
		Effect.doPostEffects(activeEffects, source, this, capsule);
		
		return this.stats.getHealth() <= 0;
	}
	
	@Override
	public boolean recoverMana(Pawn source, int amount) {
		ValueCapsule capsule = new ValueCapsule(amount);
		Effect.doPreEffects(activeEffects, source, this, capsule);
		
		this.stats.addMana(capsule.getFinal());
		
		Effect.doPostEffects(activeEffects, source, this, capsule);
		
		return this.stats.getMana() <= 0;
	}
	
	@Override
	public boolean recoverStamina(Pawn source, int amount) {
		ValueCapsule capsule = new ValueCapsule(amount);
		Effect.doPreEffects(activeEffects, source, this, capsule);
		
		this.stats.addStamina(capsule.getFinal());
		
		Effect.doPostEffects(activeEffects, source, this, capsule);
		
		return this.stats.getStamina() <= 0;
	}
	
	public void addEffect(Effect effect) {
		this.activeEffects.add(effect);
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		this.activeEffects.clear();
		
		if (null != (node = root.getChild("xp"))) {
			this.xp = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("undead"))) {
			this.undead = DataNode.parseBool(node);
		}
		
		if (null != (node = root.getChild("effects"))) {
			for (DataNode child : node.getChildren()) {
				// each child is an effect
				this.activeEffects.add(Effect.fromData(child));
			}
		}
		
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("xp", this.xp + "", null));
		base.addChild(new DataNode("undead", this.undead + "", null));
		base.addChild(DataNode.serializeAll("effects", "effect", activeEffects));
		
		return base;
	}
	
	public void applyOverlay(MobOverlay data) {
		if (data.isAlly != null)
			this.isAlly = data.isAlly;
		
		if (data.xp != null)
			this.xp = data.xp;

		if (data.undead != null)
			this.undead = data.undead;
		
		if (data.effects != null && !data.effects.isEmpty()) {
			this.activeEffects.clear();
			for (Effect e : data.effects)
				this.activeEffects.add(e);
		}
		
	}
	
	/**
	 * De-escalates this mob into an out-of-combat NPC
	 */
	public NPC toNPC() {
		
		NPC npc = new NPC();
		npc.templateName = templateName;

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
		npc.applyOverlay(po);
		
		npc.applyOverlay((new NPCOverlay())
				.name(this.getName())
				.race(this.getRace())
				.trades(this.willTrade())
				);
		
		return npc;
	}
	
	public static Mob spawn(NPCDatumData data, boolean ally) {
		if (data == null)
			return null;
		
		Mob mob = new Mob();
		mob.templateName = data.getTemplateName();
		mob.dead = false;
		ProfileDatumData prof = DungeonMaster.getActiveSession().lookupProfile(data.getProfileName());
		if (prof != null) {
			mob.setName(prof.getGeneratedName());
			mob.setRace(prof.getRace());
		}
		
		mob.inventory = data.getInventory();
		mob.stats = data.getStats();
		mob.setWillTrade(data.isWillTrade());
		
		mob.isAlly = ally;
		mob.xp = data.getXp();
		
		return mob;
	}
	
}
