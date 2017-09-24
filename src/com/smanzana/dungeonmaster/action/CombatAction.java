package com.smanzana.dungeonmaster.action;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Action performed only in combat
 * @author Skyler
 *
 */
public class CombatAction extends Action {
	
	private static class Factory implements SubActionFactory<CombatAction> {
		@Override
		public CombatAction construct(DataNode data) {
			CombatAction ret = new CombatAction(null, null, TargetType.SELF);
			ret.load(data);
			return ret;
		}
	}
	
	{
		SubAction.registerFactory(getClassKey(), new Factory());
	}

	protected CombatAction(String name, String description, TargetType type) {
		super(name, description, type);
	}

	@Override
	public void perform(Pawn source) {
		// Get target/s
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.ALLOW_PC_CONTROL) &&
				MechanicsConfig.instance().getBool(MechanicsKey.CONFIRM_PC_ACTION)) {
			if (!getDMApproval())
				return;
		}
		
		super.perform(source);
	}

	@Override
	protected String getClassKey() {
		return "combataction";
	}
	
}
