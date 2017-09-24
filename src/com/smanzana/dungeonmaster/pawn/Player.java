package com.smanzana.dungeonmaster.pawn;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.spell.Spell;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

public class Player extends Pawn {
	
	private static class SpellSlot {
		
		private int total;
		private int remaining;
		
		public SpellSlot(int total) {
			this.remaining = this.total = total;
		}
		
		public int getTotal() {
			return this.total;
		}
		
		public int getRemaining() {
			return this.remaining;
		}
		
		public void decrement() {
			remaining--;
			if (remaining < 0)
				remaining = 0;
		}
		
		public void refresh() {
			remaining = total;
		}
	}
	
	private String name;
	private String race;
	private String background;
	private Inventory inventory;
	private List<Effect> effects;
	private boolean zombie;
	private int xp;
	private int maxXP;
	private int level;
	private Map<Integer, SpellSlot> spellSlots;
	private List<Spell> spells;
	private PlayerClass playerClass;
	
	public Player() {
		effects = new LinkedList<>();
		inventory = new Inventory();
		this.spellSlots = new HashMap<>();
		this.spells = new LinkedList<>();
		this.zombie = false;
		this.playerClass = null;
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
	
	public PlayerClass getPlayerClass() {
		return playerClass;
	}
	
	public void setPlayerClass(PlayerClass clazz) {
		playerClass = clazz;
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public void addToInventory(Item item) {
		inventory.addItem(item);
	}
	
	public int getTotalSlots(int slotLevel) {
		if (!spellSlots.containsKey(slotLevel))
			return 0;
		
		return spellSlots.get(slotLevel).getTotal();
	}
	
	public int getRemainingSlots(int slotLevel) {
		if (!spellSlots.containsKey(slotLevel))
			return 0;
		
		return spellSlots.get(slotLevel).getRemaining();
	}
	
	public void recoverSpellSlots() {
		for (Integer level : spellSlots.keySet()) {
			spellSlots.get(level).refresh();
		}
	}
	
	public void setSpellSlots(int level, int count) {
		spellSlots.put(level, new SpellSlot(count));
	}
	
	public void clearSpells() {
		spells.clear();
	}
	
	public void awardSpell(Spell spell) {
		if (spells.contains(spell))
			return;
		spells.add(spell);
	}
	
	/**
	 * Tries to use tightest-fitting spell slot.
	 * @param level
	 * @return true if slot found and consumed. False is none are available
	 * Always returns false if config doesn't use slots
	 */
	public boolean consumeSpellSlot(int level) {
		if (!MechanicsConfig.instance().getBool(MechanicsKey.USE_SPELL_SLOTS))
			return false;
		
		// must have spell slot of right size
		if (getRemainingSlots(level) > 0) {
			spellSlots.get(level).decrement();
			return true;
		}
		
		if (!MechanicsConfig.instance().getBool(MechanicsKey.USE_SPELL_SLOTS_LARGER))
			return false; // don't get to try and find a bigger one
		
		int minLevel = Integer.MAX_VALUE; // min level with available slot (> level)
		for (Integer l : spellSlots.keySet()) {
			if (l < level)
				continue;
			
			if (l > minLevel)
				continue;
			
			if (getRemainingSlots(l) > 0) {
				minLevel = l;
			}
		}
		
		if (minLevel == Integer.MAX_VALUE)
			return false;
		
		spellSlots.get(minLevel).decrement();
		return true;
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
		
		if (null != (node = root.getChild("class"))) {
			this.playerClass = new PlayerClass(getSession().lookupClass(node.getValue()));
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
		base.addChild(new DataNode("class", playerClass.getName(), null));
		
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
