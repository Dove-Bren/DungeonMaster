package com.smanzana.dungeonmaster.ui.common;

import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.NPC;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;

/**
 * Wraps a Pawn to be a potential target
 * @author Skyler
 *
 */
public class TargetView<T extends Pawn> {

	private T target;
	
	private String name;
	private String type;
	//private ICON icon;
	
	public TargetView(T target) {
		this.name = target.getName();
		if (target instanceof Player)
			type = "Player";
		else if (target instanceof Mob) {
			if (((Mob) target).isAlly()) {
				type = "Mob - Allied";
			} else {
				type = "Mob - Enemy";
			}
		} else if (target instanceof NPC) {
			type = "NPC";
		} else
			type = "";
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	/**
	 * Don't call unless you know what you're doing.
	 * Returns the pointer to the actual pawn. Should not be modified
	 * off the main thread.
	 * @return
	 */
	public T getTarget() {
		return target;
	}
	
}
