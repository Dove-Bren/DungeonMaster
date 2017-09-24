package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;

/**
 * Cast a spell.
 * Does slot cleanup work
 * @author Skyler
 *
 */
public class SubApplyEffect extends SubAction {

	private Effect effect;
	
	public SubApplyEffect(Effect effect) {
		this.effect = effect;
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {
		if (target instanceof Mob)
			((Mob) target).addEffect(effect.clone());
		else if (target instanceof Player)
			((Player) target).addEffect(effect.clone());
	}
	
}
