package com.smanzana.dungeonmaster.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Holds a value for each attribute.
 * Also holds general stats, such as health and mana.
 * E.g. everything common to a pawn
 * @author Skyler
 *
 */
public class StatSet implements DataCompatible {

	private int maxHealth;
	private int health;
	private int maxMana;
	private int mana;
	private int maxStamina;
	private int stamina;
	private Map<Attributes, Integer> abilityScores;
	
	public StatSet() {
		health = maxHealth = mana = maxMana = stamina = maxStamina = 1;
		abilityScores = new EnumMap<>(Attributes.class);
		
		for (Attributes attrib : Attributes.values())
			abilityScores.put(attrib, 0);
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getMaxStamina() {
		return maxStamina;
	}

	public void setMaxStamina(int maxStamina) {
		this.maxStamina = maxStamina;
	}

	public int getStamina() {
		return stamina;
	}

	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}
	
	public int getAbilityScore(Attributes attribute) {
		return this.abilityScores.get(attribute);
	}
	
	public void setAbilityScore(Attributes attribute, int score) {
		this.abilityScores.put(attribute, score);
	}
	
	public void addHealth(int amount) {
		this.health += amount;
		if (health < 0)
			health = 0;
		if (health > this.maxHealth)
			this.health = this.maxHealth;
	}
	
	public void addMana(int amount) {
		this.mana += amount;
		if (mana < 0)
			mana = 0;
		if (mana > this.maxMana)
			this.mana = this.maxMana;
	}
	
	public void addStamina(int amount) {
		this.stamina += amount;
		if (stamina < 0)
			stamina = 0;
		if (stamina > this.maxStamina)
			stamina = this.maxStamina;
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		this.abilityScores = new EnumMap<>(Attributes.class);
		health = maxHealth = mana = maxMana = 1;
		
		if (null != (node = root.getChild("health"))) {
			this.health = DataNode.parseInt(node);
		}
		if (null != (node = root.getChild("maxhealth"))) {
			this.maxHealth = DataNode.parseInt(node);
		}
		if (null != (node = root.getChild("mana"))) {
			this.mana = DataNode.parseInt(node);
		}
		if (null != (node = root.getChild("maxmana"))) {
			this.maxMana = DataNode.parseInt(node);
		}
		if (null != (node = root.getChild("stamina"))) {
			this.stamina = DataNode.parseInt(node);
		}
		if (null != (node = root.getChild("maxstamina"))) {
			this.maxStamina = DataNode.parseInt(node);
		}
		
		for (Attributes attr : Attributes.values()) {
			if (null != (node = root.getChild(attr.name()))) {
				this.abilityScores.put(attr, Integer.parseInt(node.getValue()));
			}
		}
		
	}

	@Override
	public DataNode write(String key) {
		List<DataNode> list = new ArrayList<>(Attributes.values().length + 4);
		
		list.add(new DataNode("health", "" + this.health, null));
		list.add(new DataNode("maxhealth", "" + this.maxHealth, null));
		list.add(new DataNode("mana", "" + this.mana, null));
		list.add(new DataNode("maxmana", "" + this.maxMana, null));
		list.add(new DataNode("stamina", "" + this.stamina, null));
		list.add(new DataNode("maxstamina", "" + this.maxStamina, null));
		
		for (Attributes attr : Attributes.values()) {
			list.add(new DataNode(attr.name(), "" + this.abilityScores.get(attr), null));
		}
		
		return new DataNode(key, null, list);
	}
	
}
