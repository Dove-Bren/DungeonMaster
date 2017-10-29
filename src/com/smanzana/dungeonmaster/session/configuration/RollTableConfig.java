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
public class RollTableConfig extends Config<RollTableKey> {

	private static RollTableConfig instance;
	private Map<RollTableKey, List<String>> commentMap;
		
	public static RollTableConfig instance()
	{
		if (instance == null)
			instance = new RollTableConfig();
		
		return instance;
	}
	
	private RollTableConfig()
	{
		super();
		//setupDefaults();
		commentMap = new EnumMap<>(RollTableKey.class);
		deployComments();
	}
	
	private void pushComment(RollTableKey key, List<String> comments)
	{
		commentMap.get(key).addAll(comments);
	}
	
	private void deployComments()
	{
		for (RollTableKey key : RollTableKey.values()) {
			commentMap.put(key, new LinkedList<>());
			pushComment(key, key.getComments());
		}
	}
	
	protected void setupDefaults()
	{
		// Default D&D5e
		setValue(RollTableKey.BONUS_CHARISMA, "1:-5,3:-4,5:-3,7:-2,9:-1,11:0,13:1,15:2,17:3,19:4,21:5,23:6,25:7,27:8,29:9");
		setValue(RollTableKey.BONUS_CONSTITUTION, "1:-5,3:-4,5:-3,7:-2,9:-1,11:0,13:1,15:2,17:3,19:4,21:5,23:6,25:7,27:8,29:9");
		setValue(RollTableKey.BONUS_DEXTERITY, "1:-5,3:-4,5:-3,7:-2,9:-1,11:0,13:1,15:2,17:3,19:4,21:5,23:6,25:7,27:8,29:9");
		setValue(RollTableKey.BONUS_INTELLIGENCE, "1:-5,3:-4,5:-3,7:-2,9:-1,11:0,13:1,15:2,17:3,19:4,21:5,23:6,25:7,27:8,29:9");
		setValue(RollTableKey.BONUS_STRENGTH, "1:-5,3:-4,5:-3,7:-2,9:-1,11:0,13:1,15:2,17:3,19:4,21:5,23:6,25:7,27:8,29:9");
		setValue(RollTableKey.BONUS_WISDOM, "1:-5,3:-4,5:-3,7:-2,9:-1,11:0,13:1,15:2,17:3,19:4,21:5,23:6,25:7,27:8,29:9");
	}

	@Override
	protected RollTableKey getKey(String serial) {
		try {
			return RollTableKey.valueOf(serial);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected String serializeKey(RollTableKey key) {
		return key.name();
	}

	@Override
	public List<RollTableKey> getKeyList() {
		RollTableKey[] vals = RollTableKey.values();
		List<RollTableKey> list = new ArrayList<>(vals.length);
		for (RollTableKey key : vals)
			list.add(key);
		
		return list;
	}

	@Override
	public List<String> getComments(RollTableKey key) {
		return commentMap.get(key);
	}
	
	private RollTableKey keyFromAttrib(Attributes attrib) {
		for (RollTableKey key : RollTableKey.values()) {
			if (key.getAttribute().equals(attrib))
				return key;
		}
		
		return null;
	}
	
	public int getBonus(Attributes attrib, int score) {
		RollTableKey key = keyFromAttrib(attrib);
		String raw = this.getString(key);
		
		// parse it!
		StepList list = StepList.deserialize(raw);
		return list.getValue(score);
	}
	
	@Override
	public String getDisplayName() {
		return "Roll Tables";
	}

	@Override
	public String getDisplayTooltip() {
		return "Definition of roll bonuses based on stats";
	}
	
}
