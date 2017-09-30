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
		
		// actual logic here
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
