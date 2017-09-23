package com.smanzana.dungeonmaster.session.configuration;

import java.util.LinkedList;
import java.util.List;

public enum KeywordKey {
	STRENGTH("Physical Power"),
	CONSTITUTION("Physical Fortitude. Can affect max health"),
	INTELLIGENCE("Mental Power. Usually used with magic offense. Symbolizes how smart someone is."),
	WISDOM("Mental Fortitude. Usually affects max mana. A more profound and hard to obtain intelligence."),
	CHARISMA("Like-ability. Usually affects persuasion."),
	DEXTERITY("Reflexes and Coordination. Usually affects ranged combat."),
	HP("Health points.", "HP"),
	MP("Mana (or special power) points. Spent using spells."),
	XP("Gained through combat and actions. Used to level up."),
	LEVEL("Character's overall progression."),
	HEAD("Equipment Slot. Headgear.", "Headgear"),
	CHEST("Equipment Slot. Main body piece.", "Chestpiece"),
	LEGS("Equipment Slot. Covers the legs.", "Leggings"),
	ARMS("Equipment Slot. Covers the arms."),
	MAIN_HAND("Equipment Slot. Character's main hand.", "Main Hand"),
	OFF_HAND("Equipment Slot. Character's off hand.", "Off Hand"),
	RING_LEFT("Equipment Slot. Small accessory.", "Left Ring"),
	RING_RIGHT("Equipment Slot. Additional small accessory.", "Right Ring"),
	BRACELET("Equipment Slot. Accessory placed on one's wrist."),
	AMULET("Equipment Slot. Accessory worn around the neck."),
	CAPE("Equipment Slot. Usually decorational or magic."),
	GOLD("Currency name."),
	DEFENSE("Ability to resist physical attacks."),
	ATTACK("Ability to attack physically."),
	;
	
	private String def;
	private String comment;
	
	private KeywordKey(String comment) {
		this(comment, null);
	}
	
	private KeywordKey(String comment, String def) {
		this.comment = comment;
		if (def == null) {
			//construct one from enum name
			def = this.name();
			def = def.substring(0, 1).toUpperCase() + def.substring(1).toLowerCase();
		}
		
		this.def = def;
	}
	
	public List<String> getComments() {
		List<String> list = new LinkedList<String>();
		list.add(comment);
		
		return list;
	}
	
	public String getDefault() {
		return def;
	}
}
