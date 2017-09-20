package com.smanzana.dungeonmaster.session.configuration;

import java.util.LinkedList;
import java.util.List;

public enum MechanicsKey {
	ENABLE_COMBAT_BONUS("Enables combat bonuses.", "Bonuses are defined in CombatBonus.cfg.", "Players get a bonus based off their attributes and the weapon/attack they use"),
	DURABILITY_ENABLED("Enables all other durability options"),
	EQUIPMENT_USE_DURABILITY("In normal use, does equipment lose durability?", "This means when struck, when striking, etc", "If you want to hand-control durabilty, set to false");
	
	private String[] comments;
	
	private MechanicsKey(String ...strings) {
		this.comments = strings;
	}
	
	public List<String> getComments() {
		List<String> list = new LinkedList<String>();
		
		for (String s : comments) {
			list.add(s);
		}
		
		return list;
	}
}
