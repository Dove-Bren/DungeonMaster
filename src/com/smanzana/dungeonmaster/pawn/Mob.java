package com.smanzana.dungeonmaster.pawn;

import java.util.List;

import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

/**
 * NPC that is in combat
 * @author Skyler
 *
 */
public class Mob extends NPC {
	
	List<Effect> activeEffects;
	private boolean undead;
	private boolean isAlly;
	private int xp;
	
	public Mob(int hp, int mp, int stamina, int xp, boolean ally) {
		super();
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
	
}
