package com.smanzana.dungeonmaster.mechanics;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.ui.UI;

public final class AI {

	public static Pawn selectTarget(Collection<Pawn> targs, boolean isAlly, boolean beneficial) {
		// Ignore non-MOB pawns
		// sort out all that are allied as per beneficial
		// pick random or best based on config
		Collection<Pawn> filter = filterTargets(targs, isAlly, beneficial);
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.ENEMY_TARGET_RANDOM)) {
			int offset = (new Random()).nextInt(filter.size());
			Iterator<Pawn> it = filter.iterator();
			for (int i = 0; i < offset; i++)
				it.next();
			
			return it.next();
		}
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.ENEMY_TARGET_WEAKEST)) {
			int maxScore = Integer.MIN_VALUE;
			Pawn max = null;
			int score = 0;
			for (Pawn pawn : filter) {
				// Get a score for each pawn
				// keep highest and use them
				Mob asMob = null;
				Player asPlayer = null;
				
				if (pawn instanceof Mob)
					asMob = (Mob) pawn;
				if (pawn instanceof Player)
					asPlayer = (Player) pawn;
				
				score = 0;
				if (MechanicsConfig.instance().getBool(MechanicsKey.USE_HEALTH))
					score += Math.sqrt(Math.max(0, pawn.getMaxHealth() - pawn.getHealth()));
				
				if (MechanicsConfig.instance().getBool(MechanicsKey.USE_STAMINA))
					score += .4 * Math.sqrt(Math.max(0, pawn.getMaxStamina() - pawn.getStamina()));

				if (MechanicsConfig.instance().getBool(MechanicsKey.USE_MANA))
					score += .4 * Math.sqrt(Math.max(0, pawn.getMaxMana() - pawn.getMana()));
				
				if (MechanicsConfig.instance().getBool(MechanicsKey.USE_LEVELS))
					if (asPlayer != null)
						score -= asPlayer.getLevel();
				
				if (MechanicsConfig.instance().getBool(MechanicsKey.USE_OFFDEF)) {
					if (asMob != null)
						score -= Mechs.getDefenseScore(asMob);
					else if (asPlayer != null)
						score -= Mechs.getDefenseScore(asPlayer);
				}
				
				if (score > maxScore) {
					maxScore = score;
					max = pawn;
				}
			}
			
			return max;
		}
		
		// else have DM pick
		return UI.instance().selectSingleTarget(null, targs);
	}
	
	public static Collection<Pawn> selectMultiTargets(Collection<Pawn> targs, boolean isAlly, boolean beneficial) {
		// like single, only do MOB pawns
		// Select all that match, though, instead of one
		return filterTargets(targs, isAlly, beneficial);
	}
	
	private static Collection<Pawn> filterTargets(Collection<Pawn> targs, boolean isAlly, boolean beneficial) {
		List<Pawn> results = new LinkedList<>();
		
		for (Pawn potential : targs) {
			if (potential instanceof Player) {
				if (isAlly && beneficial)
					results.add(potential);
			} else if (potential instanceof Mob) {
				Mob mob = (Mob) potential;
				if ( (mob.isAlly() ^ isAlly) == !beneficial)
					results.add(mob);
			} // else continue;
		}
		
		return results;
	}
	
}
