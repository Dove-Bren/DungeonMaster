package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;

/**
 * Attacks with contents of main and off hands, if they are weapons
 * @author Skyler
 *
 */
public class SubMeleeAttack extends SubAction {

	private ValueSpecifier damage;
	
	public SubMeleeAttack(ValueSpecifier amount) {
		this.damage = amount;
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {
		target.damage(source, damage.fetchValue());
	}	
	
}
