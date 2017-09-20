package com.smanzana.dungeonmaster.session.configuration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.utils.StepList;

/**
 * Holds config for mechanics.
 * For example, holds whether normal use of equipment results in durability loss
 * @author Skyler
 *
 */
public class CombatBonusConfig extends Config<CombatBonusKey> {

	private static CombatBonusConfig instance;
	private Map<CombatBonusKey, List<String>> commentMap;
		
	public static CombatBonusConfig instance()
	{
		if (instance == null)
			instance = new CombatBonusConfig();
		
		return instance;
	}
	
	private CombatBonusConfig()
	{
		super();
		//setupDefaults();
		commentMap = new EnumMap<>(CombatBonusKey.class);
		deployComments();
	}
	
	private void pushComment(CombatBonusKey key, List<String> comments)
	{
		commentMap.get(key).addAll(comments);
	}
	
	private void deployComments()
	{
		for (CombatBonusKey key : CombatBonusKey.values()) {
			commentMap.put(key, new LinkedList<>());
			pushComment(key, key.getComments());
		}
	}
	
	protected void setupDefaults()
	{
		// Default 0 until 1:15-18, 2:19-21, 3+
		setValue(CombatBonusKey.BONUS_CHARISMA, "14:0,18:1,21:2");
		setValue(CombatBonusKey.BONUS_CONSTITUTION, "14:0,18:1,21:2");
		setValue(CombatBonusKey.BONUS_DEXTERITY, "14:0,18:1,21:2");
		setValue(CombatBonusKey.BONUS_INTELLIGENCE, "14:0,18:1,21:2");
		setValue(CombatBonusKey.BONUS_STRENGTH, "14:0,18:1,21:2");
		setValue(CombatBonusKey.BONUS_WISDOM, "14:0,18:1,21:2");
	}

	@Override
	protected CombatBonusKey getKey(String serial) {
		try {
			return CombatBonusKey.valueOf(serial);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected String serializeKey(CombatBonusKey key) {
		return key.name();
	}

	@Override
	protected List<CombatBonusKey> getKeyList() {
		CombatBonusKey[] vals = CombatBonusKey.values();
		List<CombatBonusKey> list = new ArrayList<>(vals.length);
		for (CombatBonusKey key : vals)
			list.add(key);
		
		return list;
	}

	@Override
	protected List<String> getComments(CombatBonusKey key) {
		return commentMap.get(key);
	}
	
	private CombatBonusKey keyFromAttrib(Attributes attrib) {
		for (CombatBonusKey key : CombatBonusKey.values()) {
			if (key.getAttribute().equals(attrib))
				return key;
		}
		
		return null;
	}
	
	public int getBonus(Attributes attrib, int score) {
		CombatBonusKey key = keyFromAttrib(attrib);
		String raw = this.getString(key);
		
		// parse it!
		StepList list = StepList.deserialize(raw);
		return list.getValue(score);
	}
	
}
