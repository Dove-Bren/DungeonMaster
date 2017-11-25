package com.smanzana.dungeonmaster.ui.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.pawn.Player.SpellSlot;
import com.smanzana.dungeonmaster.session.datums.SpellDatumData;
import com.smanzana.templateeditor.api.IRuntimeEnumerable;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;
import com.smanzana.templateeditor.api.annotations.DataLoaderRuntimeEnum;

/**
 * Data clone of a player.
 * We can't pass around an actual player object. That would be
 * incredible unsafe!
 * @author Skyler
 *
 */
public class PlayerView implements IRuntimeEnumerable<String> {
	
	//@DataLoaderName
	private String name;
	//@DataLoaderData
	private String race;
	//@DataLoaderData
	private String background;
	// If you want inventory, use the proper call
	@DataLoaderData
	private boolean zombie;
	@DataLoaderData
	private int xp;
	@DataLoaderData
	private int maxxp;
	@DataLoaderData
	private int level;
	@DataLoaderRuntimeEnum
	private List<String> spells;
	@DataLoaderList(factoryName="spellSlotFactory", templateName = "templateSpellSlot")
	private Map<Integer, SpellSlot> spellSlots;
	
	protected static final SpellSlot templateSpellSlot = spellSlotFactory();
	private static SpellSlot spellSlotFactory() {
		return new SpellSlot(3, 3);
	}
	
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
		
		int max = Math.max(10, source.getMaxSlotLevel());
		this.spellSlots = new LinkedHashMap<>();
		for (int i = 0; i < max; i++) {
			this.spellSlots.put(i, new SpellSlot(source.getTotalSlots(i), source.getRemainingSlots(i)));
		}
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
		this.spellSlots = new HashMap<>();
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

	public PlayerView setName(String name) {
		this.name = name;
		return this;
	}

	public PlayerView setRace(String race) {
		this.race = race;
		return this;
	}

	public PlayerView setBackground(String background) {
		this.background = background;
		return this;
	}

	public PlayerView setZombie(boolean zombie) {
		this.zombie = zombie;
		return this;
	}

	public PlayerView setXp(int xp) {
		this.xp = xp;
		return this;
	}

	public PlayerView setMaxxp(int maxxp) {
		this.maxxp = maxxp;
		return this;
	}

	public PlayerView setLevel(int level) {
		this.level = level;
		return this;
	}

	public PlayerView setSpells(List<String> spells) {
		this.spells = spells;
		return this;
	}

	public PlayerView setSpellSlots(Map<Integer, SpellSlot> spellSlots) {
		this.spellSlots = spellSlots;
		return this;
	}

	public PlayerView setClassName(String className) {
		this.className = className;
		return this;
	}

	public PlayerView setClassDesc(String classDesc) {
		this.classDesc = classDesc;
		return this;
	}

	@Override
	public Map<String, String> fetchValidValues(String key) {
		Map<String, String> map = new LinkedHashMap<>();
		
		for (SpellDatumData data : DungeonMaster.getActiveSession().getAllSpells()) {
			map.put(data.getDisplayName(), data.getDisplayName());
		}
		
		return map;
	}
	
}
