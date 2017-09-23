package com.smanzana.dungeonmaster.pawn;

import java.util.ArrayList;
import java.util.List;

import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * NPC that is in combat
 * @author Skyler
 *
 */
public class Mob extends NPC {
	
	List<Effect> activeEffects;
	private boolean undead;
	private boolean isAlly;
	
	public Mob(int hp, int mp, boolean ally) {
		super();
		this.stats.setHealth(hp);
		this.stats.setMaxHealth(hp);
		this.stats.setMaxMana(mp);
		this.stats.setMana(mp);
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
	
	@Override
	public boolean damage(int amount) {
		
		// TODO hook into effects (PRE)
		
		this.stats.addHealth(-amount);
		
		// TODO hook into effects (POST);
		
		return this.stats.getHealth() <= 0;
	}

	@Override
	public boolean heal(int amount) {
		
		// TODO hook into effects (PRE)
		
		if (undead)
			damage(amount);
		else
			this.stats.addHealth(amount);
		
		// TODO hook into effects (POST);
		
		return this.stats.getHealth() <= 0;
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		this.activeEffects.clear();
		
		if (null != (node = root.getChild("undead"))) {
			this.undead = (node.getValue().trim().equalsIgnoreCase("true"));
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
		
		base.addChild(new DataNode("undead", this.undead + "", null));
		
		List<DataNode> list = new ArrayList<>(this.activeEffects.size());
		for (Effect ef : this.activeEffects)
			list.add(ef.write("effect"));
		
		base.addChild(new DataNode("effects", null, list));
		
		return base;
	}
	
}
