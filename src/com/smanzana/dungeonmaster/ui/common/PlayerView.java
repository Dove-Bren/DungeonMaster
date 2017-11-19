package com.smanzana.dungeonmaster.ui.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.pawn.Player.SpellSlot;

/**
 * Data clone of a player.
 * We can't pass around an actual player object. That would be
 * incredible unsafe!
 * @author Skyler
 *
 */
public class PlayerView {
	
	private String name;
	private String race;
	private String background;
	// If you want inventory, use the proper call
	private boolean zombie;
	private int xp;
	private int maxxp;
	private int level;
	private List<String> spells;
	private Map<Integer, SpellSlot> spellSlots;
	private String className;
	private String classDesc;
	
	public PlayerView(Player source) {
		this.name = source.getName();
		this.race = source.getRace();
		this.background = source.getBackground();
		this.zombie = source.isZombie();
		this.xp = source.getCurrentXP();
		this.maxxp = source.getCurrentMaxXP();
		this.level = source.getLevel();
		this.spells = new ArrayList<>(source.getSpells());
		this.className = (source.getPlayerClass() == null ? "None" : source.getPlayerClass().getName());
		this.classDesc = (source.getPlayerClass() == null ? "" : source.getPlayerClass().getDescription());
	}
	
	public PlayerView() {
		this.name = "";
		this.race = "";
		this.background = "";
		this.zombie = false;
		this.xp = 0;
		this.maxxp = 1;
		this.level = 1;
		this.spells = new LinkedList<>();
		this.className = "None";
		this.classDesc = "";
	}

	public String getName() {
		return name;
	}

	public String getRace() {
		return race;
	}

	public String getBackground() {
		return background;
	}

	public boolean isZombie() {
		return zombie;
	}

	public int getXp() {
		return xp;
	}

	public int getMaxxp() {
		return maxxp;
	}

	public int getLevel() {
		return level;
	}

	public List<String> getSpells() {
		return spells;
	}

	public Map<Integer, SpellSlot> getSpellSlots() {
		return spellSlots;
	}

	public String getClassName() {
		return className;
	}

	public String getClassDesc() {
		return classDesc;
	}
	
}
