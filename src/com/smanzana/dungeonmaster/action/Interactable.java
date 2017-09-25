package com.smanzana.dungeonmaster.action;

import java.util.Collection;

/**
 * Something that can offer a list of actions to a player
 * @author Skyler
 *
 */
public interface Interactable {

	public Collection<Action> getActions(boolean isAdmin);
	
}
