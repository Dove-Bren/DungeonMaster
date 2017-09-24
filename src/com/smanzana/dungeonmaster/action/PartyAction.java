package com.smanzana.dungeonmaster.action;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

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
	
	private static class Factory implements SubActionFactory<PartyAction> {
		@Override
		public PartyAction construct(DataNode data) {
			PartyAction ret = new PartyAction(null, null, TargetType.SELF);
			ret.load(data);
			return ret;
		}
	}
	
	{
		SubAction.registerFactory(getClassKey(), new Factory());
	}

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

	@Override
	protected String getClassKey() {
		return "partyaction";
	}
	
}
