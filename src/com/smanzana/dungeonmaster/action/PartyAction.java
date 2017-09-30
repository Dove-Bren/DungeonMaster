package com.smanzana.dungeonmaster.action;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.UI;

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
			PartyAction ret = new PartyAction(null, null, true, "", TargetType.SELF);
			ret.load(data);
			return ret;
		}
	}
	
	public static void register() {
		SubAction.registerFactory(ClassKey(), new Factory());
	}
	
	private String prompt;

	public PartyAction(String name, String description, boolean beneficial, String prompt, TargetType targetType) {
		super(name, description, beneficial, targetType);
		this.prompt = prompt;
	}
	
	@Override
	public void perform(Pawn source) {
		// Prompt for party agreement
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.ALLOW_PC_CONTROL)) {
			if (!UI.instance().askParty(this.prompt))
				return;
		}
		
		super.perform(source);
	}

	@Override
	protected String getClassKey() {
		return ClassKey();
	}
	
	protected static String ClassKey() {
		return "partyaction";
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		DataNode node;
		
		if (null != (node = root.getChild("prompt"))) {
			prompt = node.getValue();
		}
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("prompt", prompt, null));
		
		return base;
	}
	
}
