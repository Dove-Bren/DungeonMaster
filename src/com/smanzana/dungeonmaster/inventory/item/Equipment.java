package com.smanzana.dungeonmaster.inventory.item;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public abstract class Equipment extends Item {
	
	public enum Slot
	{
		HEAD(true),
		CHEST(true),
		LEGS(true),
		ARMS(true),
		MAIN_HAND(false),
		OFF_HAND(false),
		RING_LEFT(true),
		RING_RIGHT(true),
		BRACELET(true),
		AMULET(true),
		CAPE(true);
		
		private boolean isArmor;
		
		private Slot(boolean isArmor) {
			this.isArmor = isArmor;
		}
		
		public boolean getIsArmor() {
			return this.isArmor;
		}
	}
	
	protected Slot slot;
	protected int durability;
	protected int maxDurability;
	protected List<Effect> effects;
	
	protected Map<Attributes, Integer> abilityScoreRequirements;
	protected int levelRequirement;
	
	public Equipment() {
		this("", "", 0, Slot.HEAD, 1);
	}
	
	public Equipment(String name, String description, int value, Slot slot, int durability) {
		super(name, description, value);
		this.slot = slot;
		this.maxDurability = this.durability = durability;
		this.effects = new LinkedList<Effect>();
		this.abilityScoreRequirements = new EnumMap<>(Attributes.class);
		
		for (Attributes atr : Attributes.values())
			this.abilityScoreRequirements.put(atr, 0);
	}
	
	public Slot getSlot() {
		return this.slot;
	}
	
	public int getDurability() {
		return durability;
	}
	
	public int getLevelRequirement() {
		return this.levelRequirement;
	}
	
	public void setLevelRequirement(int value) {
		this.levelRequirement = value;
	}
	
	public int getAbilityScoreRequirement(Attributes attr) {
		return this.abilityScoreRequirements.get(attr);
	}
	
	public void setAbilityScoreRequirement(Attributes attr, int requirement) {
		this.abilityScoreRequirements.put(attr, requirement);
	}
	
	public int getMaxDurability() {
		return maxDurability;
	}
	
	/**
	 * Damages the equipment's durability.
	 * If the damage breaks as a result of this damage, returns true.
	 * @param amount
	 * @return
	 */
	public boolean damageDurability(int amount) {
		durability -= amount;

		return durability <= 0;
	}
	
	public boolean damageDurability() {
		return damageDurability(1);
	}
	
	public List<Effect> getEffects() {
		return effects;
	}
	
	public void addEffect(Effect e) {
		if (effects == null)
			effects = new LinkedList<Effect>();
		
		effects.add(e);
	}
	
	public void clearEffects() {
		effects.clear();
	}
	
	/**
	 * Performs a basic usage of the equipment.
	 * For armor, this likely means the armor blocked a blow.
	 * Weapons, then, use this when they are used to attack.
	 * Subclasses are encouraged to implement and call this function.
	 * The stub from Equipment only does durability accounting
	 * @return True if the equipment has broken
	 */
	public boolean use() {
		// Do durability damage if config says to
		// TODO
		
		// TODO also hook into effects
		
		return this.durability <= 0;
	}
	
	public boolean canEquip(Player player) {
		if (MechanicsConfig.instance().getBool(MechanicsKey.EQUIP_RESTRICT_LEVEL)) {
			if (player.getLevel() < this.levelRequirement)
				return false;
		}
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.EQUIP_RESTRICT_SCORE)) {
			for (Attributes attr : Attributes.values()) {
				if (player.getAbilityScore(attr) < this.getAbilityScoreRequirement(attr))
					return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		this.effects.clear();
		
		if (null != (node = root.getChild("slot"))) {
			try {
				this.slot = Slot.valueOf(node.getValue());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to an equipment slot");
			}
		}
		
		if (null != (node = root.getChild("durability"))) {
			try {
				this.durability = Integer.parseInt(node.getValue());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to a number");
			}
		}
		
		if (null != (node = root.getChild("maxdurability"))) {
			try {
				this.maxDurability = Integer.parseInt(node.getValue());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to a number");
			}
		}
		
		if (null != (node = root.getChild("effects"))) {
			for (DataNode effect : node.getChildren())
				this.effects.add(Effect.fromData(effect));
		}
		
		if (null != (node = root.getChild("abilityrequirements"))) {
			DataNode sub;
			for (Attributes attr : Attributes.values()) {
				if (null != (sub = node.getChild(attr.name()))) {
					try {
						this.abilityScoreRequirements.put(attr, Integer.parseInt(sub.getValue()));
					} catch (Exception e) {
						;
					}
				}
			}
		}
		
		if (null != (node = root.getChild("levelrequirement"))) {
			try {
				this.levelRequirement = Integer.parseInt(node.getValue());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to a number");
			}
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("slot", slot.name(), null));
		base.addChild(new DataNode("durability", this.durability + "", null));
		base.addChild(new DataNode("maxdurability", this.maxDurability + "", null));
		
		List<DataNode> list = new LinkedList<>();
		for (Effect effect : effects)
			list.add(effect.write("effect"));
		base.addChild(new DataNode("effects", null, list));
		
		DataNode reqs = new DataNode("abilityrequirements", null, new LinkedList<>());
		for (Attributes attr : Attributes.values())
			reqs.addChild(new DataNode(attr.name(), abilityScoreRequirements.get(attr) + "", null));

		base.addChild(reqs);
		base.addChild(new DataNode("levelrequirement", levelRequirement + "", null));
		
		return base;
	}	
}
