package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;

/**
 * Full rest.
 * Includes resetting spent spell slots
 * and fully recovers hp, mp, stamina (if they are enabled)
 * @author Skyler
 *
 */
public class SubRest extends SubAction {
	
	public SubRest() {
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {

		if (MechanicsConfig.instance().getBool(MechanicsKey.USE_HEALTH))
			target.heal(source, target.getMaxHealth());
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.USE_MANA))
			target.restoreMana(source, target.getMaxMana());
			
		if (MechanicsConfig.instance().getBool(MechanicsKey.USE_STAMINA))
			target.restoreStamina(source, target.getMaxStamina());
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.USE_SPELL_SLOTS))
			if (target instanceof Player)
				((Player) target).recoverSpellSlots();
	}	
	
}
