package com.smanzana.dungeonmaster.session.configuration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Holds config for mechanics.
 * For example, holds whether normal use of equipment results in durability loss
 * @author Skyler
 *
 */
public class MechanicsConfig extends Config<MechanicsKey> {

	private static MechanicsConfig instance;
	private Map<MechanicsKey, List<String>> commentMap;
		
	public static MechanicsConfig instance()
	{
		if (instance == null)
			instance = new MechanicsConfig();
		
		return instance;
	}
	
	private MechanicsConfig()
	{
		super();
		commentMap = new EnumMap<>(MechanicsKey.class);
		deployComments();
	}
	
	private void pushComment(MechanicsKey key, List<String> comments)
	{
		commentMap.get(key).addAll(comments);
	}
	
	private void deployComments()
	{
		for (MechanicsKey key : MechanicsKey.values()) {
			commentMap.put(key, new LinkedList<>());
			pushComment(key, key.getComments());
		}
	}
	
	protected void setupDefaults()
	{
		for (MechanicsKey key : MechanicsKey.values())
			setValue(key, Boolean.FALSE);

		// D&D 5E
		setValue(MechanicsKey.ALLOW_PC_CONTROL, Boolean.TRUE);
		
		setValue(MechanicsKey.USE_ABILITY_SCORES, Boolean.TRUE);
		setValue(MechanicsKey.USE_HEALTH, Boolean.TRUE);
		setValue(MechanicsKey.USE_OFFDEF, Boolean.TRUE);
		setValue(MechanicsKey.USE_AC, Boolean.TRUE);

		setValue(MechanicsKey.USE_LEVELS, Boolean.TRUE);
		setValue(MechanicsKey.USE_LEVELS, Boolean.TRUE);
		setValue(MechanicsKey.HP_FROM_CONSTITUTION, Boolean.TRUE);

		setValue(MechanicsKey.USE_COMBAT_ACTIONS, Boolean.TRUE);
		setValue(MechanicsKey.USE_DAMAGE_CALC, Boolean.TRUE);
		setValue(MechanicsKey.USE_ENEMY_ACTIONS, Boolean.TRUE);
		setValue(MechanicsKey.ENEMY_TARGET_RANDOM, Boolean.TRUE);
		setValue(MechanicsKey.DROP_LOOT, Boolean.TRUE);
		setValue(MechanicsKey.USE_INITIATIVE, Boolean.TRUE);

		setValue(MechanicsKey.USE_ACTIONS, Boolean.TRUE);
		setValue(MechanicsKey.AUTO_ACTIONS, Boolean.TRUE);

		setValue(MechanicsKey.ITEMS_DROPABLE, Boolean.TRUE);
		setValue(MechanicsKey.ITEMS_TRADING, Boolean.TRUE);
		setValue(MechanicsKey.ITEMS_SELLABLE, Boolean.TRUE);
		setValue(MechanicsKey.EQUIP_RESTRICT_SCORE, Boolean.TRUE);
	}

	@Override
	protected MechanicsKey getKey(String serial) {
		try {
			return MechanicsKey.valueOf(serial);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected String serializeKey(MechanicsKey key) {
		return key.name();
	}

	@Override
	public List<MechanicsKey> getKeyList() {
		MechanicsKey[] vals = MechanicsKey.values();
		List<MechanicsKey> list = new ArrayList<>(vals.length);
		for (MechanicsKey key : vals)
			list.add(key);
		
		return list;
	}

	@Override
	public List<String> getComments(MechanicsKey key) {
		return commentMap.get(key);
	}
	
	@Override
	public String getDisplayName() {
		return "Mechanics";
	}

	@Override
	public String getDisplayTooltip() {
		return "Collection of settings that dictate how the game is played";
	}
	
}
