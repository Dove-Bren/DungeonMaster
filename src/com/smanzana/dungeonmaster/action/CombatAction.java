package com.smanzana.dungeonmaster.action;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.UI;

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
	
	public static void register() {
		SubAction.registerFactory(ClassKey(), new Factory());
	}

	protected CombatAction(String name, String description, TargetType type) {
		super(name, description, type);
	}

	@Override
	public void perform(Pawn source) {
		// Get target/s
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.ALLOW_PC_CONTROL) &&
				MechanicsConfig.instance().getBool(MechanicsKey.CONFIRM_PC_ACTION)) {
			if (!UI.instance().askDM(source.getName() + " is using combat action " + this.getName()
					+ ". Please approve. (To turn off, turn of CONFIRM_PC_ACTION in the mechanics config)"))
				return;
		}
		
		super.perform(source);
	}

	@Override
	protected String getClassKey() {
		return ClassKey();
	}
	
	protected static String ClassKey() {
		return "combataction";
	}
	
}
