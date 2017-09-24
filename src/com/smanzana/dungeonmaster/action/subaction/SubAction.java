package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.pawn.Pawn;

/**
 * An actual piece of "DO SOMETHING".
 * Sub actions make changes.
 * Actions are made up of subactions
 * @author Skyler
 *
 */
public abstract class SubAction {
	
	public abstract void apply(Pawn target);
	
}
