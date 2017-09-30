package com.smanzana.dungeonmaster.action;

import java.util.Collection;

import com.smanzana.dungeonmaster.pawn.Player;

/**
 * Something that can offer a list of actions to a player
 * @author Skyler
 *
 */
public interface Interactable {

	/**
	 * Gets all actions for the given player.
	 * @param isAdmin
	 * @param player
	 * @return
	 */
	public Collection<Action> getActions(boolean isAdmin, Player player);
	
}
