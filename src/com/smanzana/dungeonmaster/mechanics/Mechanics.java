package com.smanzana.dungeonmaster.mechanics;

import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.pawn.Pawn;

public final class Mechanics {
	
	// Used internally. Generic roll
	public static int performRoll(Attributes attrib, Pawn pawn) {
		// if config.auto_roll
		// use attrib to find out bonus (if on)
		// return roll + bonus
	}
	
	/**
	 * Uses config to determine if the described attack connects or misses.
	 * Ideally you call this before you damage equipment, etc.
	 * @param contributing attribute used for roll bonuses
	 *        applies to saving throw AND AC check (if applicable)
	 * @param resisting saving throw attribute. If null, skips regular saving throw (only makes sense
	 * when useArmor is true)
	 * @param swinger
	 * @param target
	 * @param useArmor whether to ALSO check armor (if applicable)
	 * @return true if it hits. False if it misses.
	 */
	public static boolean checkSavingThrow(Attributes contributing, Attributes resisting, Pawn swinger, Pawn target, boolean useArmor) {
		// if config.useAC can miss (if so, return false)
		// if useAC, roll with attrib and compare to defense score. Miss/Swing based on that
		
	}

	/**
	 * Takes final damage from damage die rolls + effects and transforms it as per
	 * the configured mechanics. This includes adjusting for defense.
	 * @param target
	 * @param damage
	 * @return The modified damage.
	 */
	public static int AdjustDamage(Pawn target, int damage) {
		
	}
	
}
