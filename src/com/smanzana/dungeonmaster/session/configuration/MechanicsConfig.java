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
		//setupDefaults();
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
		setValue(MechanicsKey.DURABILITY_ENABLED, Boolean.TRUE);
		setValue(MechanicsKey.EQUIPMENT_USE_DURABILITY, Boolean.TRUE);
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
	protected List<MechanicsKey> getKeyList() {
		MechanicsKey[] vals = MechanicsKey.values();
		List<MechanicsKey> list = new ArrayList<>(vals.length);
		for (MechanicsKey key : vals)
			list.add(key);
		
		return list;
	}

	@Override
	protected List<String> getComments(MechanicsKey key) {
		return commentMap.get(key);
	}
	
}
