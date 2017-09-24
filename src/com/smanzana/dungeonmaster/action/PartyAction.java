package com.smanzana.dungeonmaster.action;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;

/**
 * Action performed by the whole party.
 * This is NOT a combat action. This is NOT an action which
 * does its effects to the whole party in a combat situation.
 * Look at AoE actions for that.
 * Example: Rest at an Inn
 * @author Skyler
 *
 */
public class PartyAction extends Action {

	public PartyAction(String name, String description, TargetType targetType) {
		super(name, description, targetType);
	}
	
	@Override
	public void perform(Pawn source) {
		// Prompt for party agreement
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.ALLOW_PC_CONTROL)) {
			if (!getPartyConfirm())
				return;
		}
		
		super.perform(source);
	}
	
}
