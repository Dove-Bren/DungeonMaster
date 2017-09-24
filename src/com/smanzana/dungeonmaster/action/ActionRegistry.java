package com.smanzana.dungeonmaster.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps action names to actions.
 * @author Skyler
 *
 */
public class ActionRegistry {

	private Map<String, Action> map;
	
	private static ActionRegistry instance;
	
	private ActionRegistry() {
		map = new HashMap<>();
	}
	
	public static ActionRegistry instance() {
		if (instance == null)
			instance = new ActionRegistry();
		
		return instance;
	}
	
	public void register(String name, Action action) {
		map.put(name, action);
	}
	
	public Action lookupAction(String name) {
		return map.get(name);
	}
}
