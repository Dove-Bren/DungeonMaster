package com.smanzana.dungeonmaster.inventory.item;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;

public abstract class Equipment extends Item {
	
//	private static class Factory implements ItemFactory<Equipment> {
//
//		@Override
//		public Equipment construct(DataNode data) {
//			return Equipment.fromData(data);
//		}
//		
//	}
//	
//	{
//		Item.registerType(getClassKey(), new Factory());
//	}
	
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
	
	@DataLoaderData
	protected Slot slot;
	@DataLoaderData
	protected int durability;
	@DataLoaderData
	protected int maxDurability;
	@DataLoaderList(templateName = "templateEffect")
	protected List<Effect> effects;
	protected Effect templateEffect = Effect.templateEffect;
	@DataLoaderData
	protected Map<Attributes, Integer> abilityScoreRequirements;
	@DataLoaderData
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
	
//	Things look like they're working for subclass stuff.
//	Except that items (equipment specifically) is broken down weird.
//	Need to keep either figuring that out OR simplifying it.
//	Also usable takes a string action. It would be cool to have a tag
//	that said this was supposed to be the name of something else.
//	Also a 'verifier' method to check if a value is valid! That's cool!
	
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
	 * The stub from Equipment only does durability accounting and effect triggering
	 * @param user who's using the equipment
	 * @return True if the equipment has broken
	 */
	public boolean use(Pawn user) {
		if (MechanicsConfig.instance().getBool(MechanicsKey.DURABILITY_ENABLED)
				&& MechanicsConfig.instance().getBool(MechanicsKey.EQUIPMENT_USE_DURABILITY))
			this.damageDurability();
		
		Effect.doOnUseEffects(effects, user, null, null);
		
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
				this.slot = Slot.valueOf(node.getValue().toUpperCase());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.out.println("Failed to convert " + node.getValue() + " to an equipment slot");
			}
		}
		
		if (null != (node = root.getChild("durability"))) {
			this.durability = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("maxdurability"))) {
			this.maxDurability = DataNode.parseInt(node);
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
			this.levelRequirement = DataNode.parseInt(node);
		}
		
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("slot", slot.name(), null));
		base.addChild(new DataNode("durability", this.durability + "", null));
		base.addChild(new DataNode("maxdurability", this.maxDurability + "", null));
		base.addChild(DataNode.serializeAll("effects", "effect", effects));
		
		DataNode reqs = new DataNode("abilityrequirements", null, new LinkedList<>());
		for (Attributes attr : Attributes.values())
			reqs.addChild(new DataNode(attr.name(), abilityScoreRequirements.get(attr) + "", null));

		base.addChild(reqs);
		base.addChild(new DataNode("levelrequirement", levelRequirement + "", null));
		
		return base;
	}	
	
//	@Override
//	protected String getClassKey() {
//		return ClassKey();
//	}
//	
//	protected static String ClassKey() {
//		return "equipment";
//	}
//	
//	protected static void register() {
//		Item.registerType(ClassKey(), new Factory());
//	}
	
	public static Equipment fromData(DataNode node) {
		// use slot to determine if weapon or armor
		if (null == node.getChild("slot"))
			return null;
		
		Slot slot = Slot.valueOf(node.getValue().toUpperCase());
		Equipment equip = (slot.getIsArmor() ? new Armor() : new Weapon());
		equip.load(node);
		
		return equip;
	}
	
	public boolean fitsSlot(Slot slot) {
		if (slot == Slot.MAIN_HAND || slot == Slot.OFF_HAND)
			return this.slot == Slot.MAIN_HAND || this.slot == Slot.OFF_HAND;
		
		return (this.slot == slot);
	}
	
	public abstract boolean verifySlot(Slot slot);
}
