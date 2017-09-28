package com.smanzana.dungeonmaster.ui.common;

import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.NPC;

/**
 * Data clone of an NPC.
 * @author Skyler
 *
 */
public class NPCView {
	
	private String name;
	private String race;
	// If you want inventory, use the proper call
	private boolean undead; // Regular NPCs will set this 0
	private int xp; // Regular NPCs will set this 0
	private boolean willTrade;
	
	public NPCView(NPC source) {
		this.name = source.getName();
		this.race = source.getRace();
		this.willTrade = source.willTrade();
		xp = 0;
		undead = false;
		
		if (source instanceof Mob) {
			xp = ((Mob) source).getXP();
			undead = ((Mob) source).isUndead();
		}
	}

	public String getName() {
		return name;
	}

	public String getRace() {
		return race;
	}

	public boolean isUndead() {
		return undead;
	}

	public int getXp() {
		return xp;
	}

	public boolean isWillTrade() {
		return willTrade;
	}
}
