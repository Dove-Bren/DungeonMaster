package com.smanzana.dungeonmaster.mechanics;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.pawn.Entity;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.configuration.KeywordConfig;
import com.smanzana.dungeonmaster.session.configuration.KeywordKey;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.configuration.RollTableConfig;
import com.smanzana.dungeonmaster.ui.UI;
import com.smanzana.dungeonmaster.utils.Dice;

public final class Mechanics {
	
	/**
	 * Used internally. Generic roll.<br />
	 * Performs an auto roll or asks the DM depending on config.
	 * When automatically rolling, uses pawn stats + attribute bonus table.
	 * If either attrib or pawn is null, no rolltable lookup is performed
	 * #spoiler when showing to user, they don't actually roll. It's already been done at that point.
	 * @param description
	 * @param attrib
	 * @param pawn
	 * @return
	 */
	public static int performRoll(String description, Attributes attrib, Pawn pawn) {
		// if config.auto_roll
		// use attrib to find out bonus (if on)
		// return roll + bonus
		if (MechanicsConfig.instance().getBool(MechanicsKey.AUTO_SAVINGTHROW)) {
			// Roll ourselves
			int roll = (new Dice(1, 20, false)).fetchValue();
			int bonus = 0;
			int total = roll + bonus;
			String attribName = "";
			if (attrib != null && pawn != null) {
				attribName = attrib.name();
				bonus = RollTableConfig.instance().getBonus(attrib, pawn.getAbilityScore(attrib));
			}
			
			// If player and config has show, show them roll
			UI.instance().showRoll(pawn, attribName, roll, bonus, description);
			
			DungeonMaster.getActiveSession().broadcastLog( (pawn == null ? "DM" : pawn.getName()) +
					" rolled a " + total + " (" + roll + " + " + bonus + ")");
			return total;
		} else {
			return UI.instance().getDMRoll(pawn, attrib, description);
		}
	}
	
	/**
	 * Uses config to determine if the described action succeeds or is resisted.
	 * Ideally you call this before you damage equipment, etc.<br />
	 * Several cases are handled here:<br />
	 * <h3>Only target and resisting supplied</h3>
	 * <p>
	 * Regular saving throw is performed. Roll + bonus is performed and sent to DM to evaluate.
	 * </p>
	 * <h3>Target and Swinger details, no useArmor</h3>
	 * <p>
	 * If not operating with USE_CONTESTED_COMBAT, handled like "<i>Only target and resisting supplied</i>" from above.<br />
	 * Otherwise, both the swinger and defender roll with bonuses. Highest roller wins.
	 * </p>
	 * <h3>All details & useArmor</h3>
	 * <p>
	 * <b>USE_AC</b>:<br />
	 * The aggressor rolls. If provided an attribute, bonuses are applied. This roll is then checked against
	 * the defenders total AC. <br />
	 * <b>USE_CONTESTED_COMBAT</b>:<br />
	 * Handled just like when useArmor is false.<br />
	 * <b>Neither</b>:<br />
	 * No check is made. Always succeeds.
	 * </p>
	 * 
	 * @param contributing attribute used for roll bonuses that applies to attacker's roll
	 * @param resisting saving throw attribute. Applies bonus to defender's roll
	 * @param swinger
	 * @param target
	 * @param useArmor whether to ALSO factor in armor (if applicable)
	 * @return true if it hits. False if it misses.
	 */
	public static boolean checkSavingThrow(Attributes contributing, Attributes resisting, Pawn swinger, Pawn target, boolean useArmor) {
		if (target == null && swinger == null)
			return false;
		
		// use_contested only if !USE_AC && USE_CONTESTED
		boolean useContested = (!MechanicsConfig.instance().getBool(MechanicsKey.USE_AC)
				&& MechanicsConfig.instance().getBool(MechanicsKey.USE_CONTESTED_COMBAT));
		
		// if !USE_AC and !USE_CONTESTED, auto pass
		if (!useContested && !MechanicsConfig.instance().getBool(MechanicsKey.USE_AC)) {
			return true;
		}
		
		if (swinger == null || target == null
				|| (!useArmor && !useContested)) {
			// Environmental check. Use swinger or target
			Pawn real = (swinger == null ? target : swinger);
			Attributes attr = (swinger == null ? resisting : contributing);
			int roll = performRoll(KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROW), attr, real);
			return UI.instance().askDM(real.getName() + " rolled " + roll + ". Did they  pass "
					+ KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROW) + "?");
		}
		
		//both swinger and target passed.
		//also not (!useArmor && !USE_CONTESTED)
		if (useContested) {
			int attackerRoll, defenderRoll;
			
			attackerRoll = performRoll("Attacker " + KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROW) + " roll", contributing, swinger);
			defenderRoll = performRoll("Defender " + KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROW) + " roll", resisting, target);
			
			DungeonMaster.getActiveSession().broadcastLog(KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROW)
					+ (attackerRoll >= defenderRoll ? "failed" : "succeeded")
					+ " (A: " + attackerRoll + " | D: " + defenderRoll + ")");
			
			return attackerRoll >= defenderRoll;
		}
		
		//If we're here, must be useArmor && USE_AC		
		if (!MechanicsConfig.instance().getBool(MechanicsKey.USE_OFFDEF)) {
			//doesn't mean anything if OFFDEF is off
			return true;
		}
		
		int attackerRoll;
		
		attackerRoll = performRoll("Attacker " + KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROW) + " roll", contributing, swinger);
		
		DungeonMaster.getActiveSession().broadcastLog(KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROW)
				+ (attackerRoll >= target.getAC() ? "failed" : "succeeded")
				+ " (A: " + attackerRoll + " | D: " + target.getAC() + ")");
		
		return attackerRoll >= target.getAC();
	}

	/**
	 * Takes final damage from damage die rolls + effects and transforms it as per
	 * the configured mechanics. This includes adjusting for defense.
	 * @param target
	 * @param damage
	 * @return The modified damage.
	 */
	public static int AdjustDamage(Pawn target, int damage) {
		// if !USE_AC, subtract defense value from damage
		// else just return it
		if (!MechanicsConfig.instance().getBool(MechanicsKey.USE_AC)) {
			return damage - (target instanceof Entity ? ((Entity) target).getDefenseScore() : 0);
		}
		
		return damage;
	}
	
}
