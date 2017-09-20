package com.smanzana.dungeonmaster.session.configuration;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.pawn.Attributes;

public enum CombatBonusKey {
	BONUS_STRENGTH(Attributes.STRENGTH, "Range of bonuses given to strength checks/rolls.", "This is map between MAXs and their bonuses", "For example, \"11: -1, 13: 0\"", "This translates to \"-inf to 11 gives -1; 12 to 13 gives 0; anything higher than 13 gives 1\"", "To specify a gap, repeat the last entry. Example: \"11: -1, 13: 0, 13: 1\"", "This means \"-inf to 11 is -1; 12 to 13 is 0; 14+ is 2 (1 + last number found)\""),
	BONUS_DEXTERITY(Attributes.DEXTERITY, "Range of bonuses for dexterity", "Ranges specified as described for strength"),
	BONUS_WISDOM(Attributes.WISDOM, "Range of bonuses for wisdom", "Ranges specified as described for strength"),
	BONUS_INTELLIGENCE(Attributes.INTELLIGENCE, "Range of bonuses for intelligence", "Ranges specified as described for strength"),
	BONUS_CONSTITUTION(Attributes.CONSTITUTION, "Range of bonuses for constitution", "Ranges specified as described for strength"),
	BONUS_CHARISMA(Attributes.CHARISMA, "Range of bonuses for charisma", "Ranges specified as described for strength");
	
	private String[] comments;
	private Attributes attribute;
	
	private CombatBonusKey(Attributes attrib, String ...strings) {
		this.comments = strings;
		this.attribute = attrib;
	}
	
	public List<String> getComments() {
		List<String> list = new LinkedList<String>();
		
		for (String s : comments) {
			list.add(s);
		}
		
		return list;
	}
	
	public Attributes getAttribute() {
		return this.attribute;
	}
}
