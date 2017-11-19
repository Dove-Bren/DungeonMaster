package com.smanzana.dungeonmaster.pawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.Action.TargetType;
import com.smanzana.dungeonmaster.action.subaction.SubInspectPlayer;
import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.configuration.RollTableConfig;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.spell.Spell;
import com.smanzana.dungeonmaster.utils.Dice;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

public class Player extends Entity {
	
	public static class PlayerOverlay {
		private String name;
		private String race;
		private String background;
		//private Inventory inventory;
		private List<Effect> effects;
		private Boolean zombie;
		private Integer xp;
		private Integer maxXP;
		private Integer level;
		private Map<Integer, SpellSlot> spellSlots;
		private List<Spell> spells;
		private PlayerClass playerClass;
		
		public PlayerOverlay() {
			effects = new LinkedList<>();
			spellSlots = new HashMap<>();
			spells = new LinkedList<>();
		}

		public PlayerOverlay(String name, String race, String background, List<Effect> effects, boolean zombie, int xp,
				int maxXP, int level, Map<Integer, SpellSlot> spellSlots, List<Spell> spells, PlayerClass playerClass) {
			this.name = name;
			this.race = race;
			this.background = background;
			this.effects = effects;
			this.zombie = zombie;
			this.xp = xp;
			this.maxXP = maxXP;
			this.level = level;
			this.spellSlots = spellSlots;
			this.spells = spells;
			this.playerClass = playerClass;
		}

		public PlayerOverlay name(String name) {
			this.name = name;
			return this;
		}

		public PlayerOverlay race(String race) {
			this.race = race;
			return this;
		}

		public PlayerOverlay background(String background) {
			this.background = background;
			return this;
		}

		public PlayerOverlay addEffect(Effect effect) {
			this.effects.add(effect);
			return this;
		}

		public PlayerOverlay zombie(boolean zombie) {
			this.zombie = zombie;
			return this;
		}

		public PlayerOverlay xp(int xp) {
			this.xp = xp;
			return this;
		}

		public PlayerOverlay maxxp(int maxXP) {
			this.maxXP = maxXP;
			return this;
		}

		public PlayerOverlay level(int level) {
			this.level = level;
			return this;
		}

		public PlayerOverlay spellSlotsMax(int level, int max) {
			if (!this.spellSlots.containsKey(level))
				this.spellSlots.put(level, new SpellSlot(max));
			else
				this.spellSlots.get(level).total = max;
			return this;
		}

		public PlayerOverlay spellSlotsAvailable(int level, int remaining) {
			if (!this.spellSlots.containsKey(level))
				this.spellSlots.put(level, new SpellSlot(remaining));
			else
				this.spellSlots.get(level).remaining = remaining;
			return this;
		}

		public PlayerOverlay addSpell(Spell spell) {
			this.spells.add(spell);
			return this;
		}

		public PlayerOverlay setPlayerClass(PlayerClass playerClass) {
			this.playerClass = playerClass;
			return this;
		}
		
		
	}
	
	public static class SpellSlot {
		
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
	
	//private String name;
	//private String race;
	private String background;
	//private Inventory inventory;
	private List<Effect> effects;
	private boolean zombie;
	private int xp;
	private int maxXP;
	private int level;
	private Map<Integer, SpellSlot> spellSlots;
	private List<Spell> spells;
	private PlayerClass playerClass;
	private int rawHitDice; // amount of health from hitdice alone. No modifiers.
	
	public Player() {
		super();
		effects = new LinkedList<>();
		this.spellSlots = new HashMap<>();
		this.spells = new LinkedList<>();
		this.zombie = false;
		this.playerClass = null;
		this.xp = 0;
		this.rawHitDice = 0;
	}

	public Player(String name, String race, String background, int maxXP, int level) {
		super();
		setName(name);
		setRace(race);
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
	
	public List<String> getSpells() {
		List<String> names = new ArrayList<>(spells.size());
		for (Spell s : spells)
			names.add(s.getName());
		return names;
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
		this.level++;
		
		if (this.playerClass != null)
			this.playerClass.applyLevel(this);
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.HP_FROM_CONSTITUTION)) {
			int scale = MechanicsConfig.instance().getInt(MechanicsKey.HP_FROM_CONSTITUTION_SCALE);
			if (scale == 0)
				System.out.println("Told to set HP from constitution, but scale set to 0!");
			else
				stats.setMaxHealth(stats.getAbilityScore(Attributes.CONSTITUTION) * scale);
		} else if (MechanicsConfig.instance().getBool(MechanicsKey.HP_FROM_HITDICE)) {
			Dice dice = this.playerClass.getHitDice();
			if (dice == null)
				System.out.println("Told to use hitdice for max health, but hitdice aren't defined (for class " + playerClass.getName() + ")");
			else {
				int max;
				
				// first roll HD and add to raw
				this.rawHitDice += dice.roll();
				
				// now calculate max from raw HD + cons modifier * level
				max = rawHitDice;
				if (MechanicsConfig.instance().getBool(MechanicsKey.HP_FROM_HITDICE_BONUS))
					max += (level * RollTableConfig.instance().getBonus(Attributes.CONSTITUTION, stats.getAbilityScore(Attributes.CONSTITUTION)));
				
				stats.setMaxHealth(max);
			}
		}
		
		stats.setHealth(stats.getMaxHealth());
	}
	
	public void awardXP(int amount) {
		this.xp += amount;
		if (this.xp >= maxXP) {
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

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		this.effects.clear();
		
		if (null != (node = root.getChild("background"))) {
			this.background = node.getValue();
		}
		
		if (null != (node = root.getChild("xp"))) {
			this.xp = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("maxxp"))) {
			this.maxXP = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("rawhitdice"))) {
			this.rawHitDice = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("level"))) {
			this.level = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("zombie"))) {
			this.zombie = DataNode.parseBool(node);
		}
		
		if (null != (node = root.getChild("effects"))) {
			for (DataNode effect : node.getChildren()) {
				this.effects.add(Effect.fromData(effect));
			}
		}
		
		if (null != (node = root.getChild("class"))) {
			this.playerClass = new PlayerClass(DungeonMaster.getActiveSession().lookupClass(node.getValue()));
		}	
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("background", background, null));
		base.addChild(new DataNode("xp", "" + xp, null));
		base.addChild(new DataNode("maxxp", "" + maxXP, null));
		base.addChild(new DataNode("rawhitdice", "" + rawHitDice, null));
		base.addChild(new DataNode("level", "" + level, null));
		base.addChild(new DataNode("zombie", "" + zombie, null));
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
	
	@Override
	public boolean recoverMana(Pawn source, int amount) {
		ValueCapsule capsule = new ValueCapsule(amount);
		Effect.doPreEffects(effects, source, this, capsule);
		
		this.stats.addMana(capsule.getFinal());
		
		Effect.doPostEffects(effects, source, this, capsule);
		
		return this.stats.getMana() <= 0;
	}
	
	@Override
	public boolean recoverStamina(Pawn source, int amount) {
		ValueCapsule capsule = new ValueCapsule(amount);
		Effect.doPreEffects(effects, source, this, capsule);
		
		this.stats.addStamina(capsule.getFinal());
		
		Effect.doPostEffects(effects, source, this, capsule);
		
		return this.stats.getStamina() <= 0;
	}
	
	public void addEffect(Effect effect) {
		this.effects.add(effect);
	}
	
	public void applyOverlay(PlayerOverlay data) {
		
		if (data.name != null)
			this.setName(data.name);
		
		if (data.race != null)
			this.setRace(data.race);
		
		if (data.background != null)
			this.background = data.background;

		if (data.zombie != null)
			this.zombie = data.zombie;

		if (data.xp != null)
			this.xp = data.xp;

		if (data.maxXP != null)
			this.maxXP = data.maxXP;

		if (data.level != null)
			this.level = data.level;

		if (data.playerClass != null)
			this.playerClass = data.playerClass;
		
		if (data.effects != null && !data.effects.isEmpty()) {
			this.effects.clear();
			for (Effect e : data.effects)
				this.effects.add(e);
		}
		
		if (data.spells != null && !data.spells.isEmpty()) {
			this.spells.clear();
			for (Spell s : data.spells)
				this.spells.add(s);
		}
		
		if (data.spellSlots != null && !data.spells.isEmpty()) {
			for (int level : data.spellSlots.keySet()) {
				// overwrite what we have
				this.spellSlots.put(level, data.spellSlots.get(level));
			}
		}
	}

	@Override
	public Collection<Action> getActions(boolean isAdmin, Player player) {
		List<Action> actions = new LinkedList<>();
		Action action;
		
		action = new Action("Inspect", "View this Player's stats and basic info", true, Attributes.INTELLIGENCE, TargetType.TARGET);
		action.addSubAction(new SubInspectPlayer(this));
		actions.add(action);
		
		return actions;
	}
	
	public Collection<Action> getCombatActions() {
		
	}
}
