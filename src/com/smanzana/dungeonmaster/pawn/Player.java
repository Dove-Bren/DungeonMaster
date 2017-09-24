package com.smanzana.dungeonmaster.pawn;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

public class Player extends Pawn {
	
	private String name;
	private String race;
	private String background;
	private Inventory inventory;
	private List<Effect> effects;
	private boolean zombie;
	private int xp;
	private int maxXP;
	private int level;
	
	public Player() {
		effects = new LinkedList<>();
		inventory = new Inventory();
		this.zombie = false;
		this.xp = 0;
	}

	public Player(String name, String race, String background, int maxXP, int level) {
		super();
		this.name = name;
		this.race = race;
		this.background = background;
		this.maxXP = maxXP;
		this.level = level;
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public void addToInventory(Item item) {
		inventory.addItem(item);
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void levelup() {
		// TODO hooks!
		// TODO do things based on config
	}
	
	public void awardXP(int amount) {
		this.xp += amount;
		if (this.xp > maxXP) {
			this.xp -= maxXP;
			this.levelup();
		}
	}
	
	public int getCurrentXP() {
		return this.xp;
	}
	
	public int getCurrentMaxXP() {
		return this.maxXP;
	}
	
	public boolean isZombie() {
		return zombie;
	}
	
	public void setZombie(boolean zombie) {
		this.zombie = zombie;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		this.effects.clear();
		
		if (null != (node = root.getChild("name"))) {
			this.name = node.getValue();
		}
		
		if (null != (node = root.getChild("race"))) {
			this.race = node.getValue();
		}
		
		if (null != (node = root.getChild("background"))) {
			this.background = node.getValue();
		}
		
		if (null != (node = root.getChild("xp"))) {
			this.xp = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("maxxp"))) {
			this.maxXP = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("level"))) {
			this.level = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("zombie"))) {
			this.zombie = DataNode.parseBool(node);
		}
		
		if (null != (node = root.getChild("inventory"))) {
			this.inventory.load(node);
		}
		
		if (null != (node = root.getChild("effects"))) {
			for (DataNode effect : node.getChildren()) {
				this.effects.add(Effect.fromData(effect));
			}
		}		
	}

	@Override
	public DataNode write(String key) {
		DataNode base = this.writeBase(key);
		
		base.addChild(new DataNode("name", name, null));
		base.addChild(new DataNode("race", race, null));
		base.addChild(new DataNode("background", background, null));
		base.addChild(new DataNode("xp", "" + xp, null));
		base.addChild(new DataNode("maxxp", "" + maxXP, null));
		base.addChild(new DataNode("level", "" + level, null));
		base.addChild(new DataNode("zombie", "" + zombie, null));
		base.addChild(inventory.write("inventory"));
		base.addChild(DataNode.serializeAll("effects", "effect", effects));
		
		return base;
	}
	
	@Override
	public boolean damage(Pawn source, int amount) {
		
		ValueCapsule capsule = new ValueCapsule(amount);
		Effect.doPreEffects(effects, source, this, capsule);
		
		this.stats.addHealth(-capsule.getFinal());
		
		Effect.doPostEffects(effects, source, this, capsule);
		
		return this.stats.getHealth() <= 0;
	}

	@Override
	public boolean heal(Pawn source, int amount) {
		ValueCapsule capsule = new ValueCapsule(amount);
		Effect.doPreEffects(effects, source, this, capsule);
		
		if (zombie)
			damage(source, capsule.getFinal());
		else
			this.stats.addHealth(capsule.getFinal());
		
		Effect.doPostEffects(effects, source, this, capsule);
		
		return this.stats.getHealth() <= 0;
	}
	
	public void addEffect(Effect effect) {
		this.effects.add(effect);
	}
		
}
