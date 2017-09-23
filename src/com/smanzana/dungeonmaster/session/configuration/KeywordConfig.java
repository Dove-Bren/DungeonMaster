package com.smanzana.dungeonmaster.session.configuration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Remapping of keywords.
 * Cool customization.
 * @author Skyler
 *
 */
public class KeywordConfig extends Config<KeywordKey> {

	private static KeywordConfig instance;
	private Map<KeywordKey, List<String>> commentMap;
		
	public static KeywordConfig instance()
	{
		if (instance == null)
			instance = new KeywordConfig();
		
		return instance;
	}
	
	private KeywordConfig()
	{
		super();
		//setupDefaults();
		commentMap = new EnumMap<>(KeywordKey.class);
		deployComments();
	}
	
	private void pushComment(KeywordKey key, List<String> comments)
	{
		commentMap.get(key).addAll(comments);
	}
	
	private void deployComments()
	{
		for (KeywordKey key : KeywordKey.values()) {
			commentMap.put(key, new LinkedList<>());
			pushComment(key, key.getComments());
		}
	}
	
	protected void setupDefaults()
	{
		for (KeywordKey key : KeywordKey.values()) {
			this.setValue(key, key.getDefault());
		}
	}

	@Override
	protected KeywordKey getKey(String serial) {
		try {
			return KeywordKey.valueOf(serial);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected String serializeKey(KeywordKey key) {
		return key.name();
	}

	@Override
	protected List<KeywordKey> getKeyList() {
		KeywordKey[] vals = KeywordKey.values();
		List<KeywordKey> list = new ArrayList<>(vals.length);
		for (KeywordKey key : vals)
			list.add(key);
		
		return list;
	}

	@Override
	protected List<String> getComments(KeywordKey key) {
		return commentMap.get(key);
	}
	
	public String getKeyword(KeywordKey key) {
		return this.getString(key);
	}
	
}
