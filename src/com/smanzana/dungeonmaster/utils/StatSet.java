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
	private Map<Attributes, Integer> abilityScores;
	
	public StatSet() {
		health = maxHealth = mana = maxMana = 1;
		abilityScores = new EnumMap<>(Attributes.class);
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

	@Override
	public void load(DataNode root) {
		DataNode node;
		this.abilityScores = new EnumMap<>(Attributes.class);
		health = maxHealth = mana = maxMana = 1;
		
		if (null != (node = root.getChild("health"))) {
			this.health = Integer.parseInt(node.getValue());
		}
		if (null != (node = root.getChild("maxhealth"))) {
			this.maxHealth = Integer.parseInt(node.getValue());
		}
		if (null != (node = root.getChild("mana"))) {
			this.mana = Integer.parseInt(node.getValue());
		}
		if (null != (node = root.getChild("maxmana"))) {
			this.maxMana = Integer.parseInt(node.getValue());
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
		list.add(new DataNode("maxmana", "" + this.maxHealth, null));
		
		for (Attributes attr : Attributes.values()) {
			list.add(new DataNode(attr.name(), "" + this.abilityScores.get(attr), null));
		}
		
		return new DataNode(key, null, list);
	}
	
}
