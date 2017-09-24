package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;

/**
 * Deals a set amount of damage.
 * @author Skyler
 *
 */
public class SubDamage extends SubAction {

	private ValueSpecifier damage;
	
	public SubDamage(ValueSpecifier amount) {
		this.damage = amount;
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {
		target.damage(source, damage.fetchValue());
	}	
	
}
