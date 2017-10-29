package com.smanzana.dungeonmaster.action;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.mechanics.Mechanics;
import com.smanzana.dungeonmaster.pawn.Attributes;
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
			CombatAction ret = new CombatAction(null, null, false, null, TargetType.SELF, null, false);
			ret.load(data);
			return ret;
		}
	}
	
	public static void register() {
		SubAction.registerFactory(ClassKey(), new Factory(), true);
	}
	
	private Attributes savingThrow;
	private boolean useArmor;

	protected CombatAction(String name, String description, boolean beneficial, Attributes contrib, 
			TargetType type, Attributes savingThrow, boolean useArmor) {
		super(name, description, beneficial, contrib, type);
		this.useArmor = useArmor;
		this.savingThrow = savingThrow;
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
	public boolean performSavingThrow(Pawn source, Pawn target) {
		
		// Use saving throw to decide whether it hits
		// handles savingThrow == null and !useArmor
		return Mechanics.checkSavingThrow(getAttribute(), savingThrow, source, target, useArmor);
	}

	@Override
	public String getClassKey() {
		return ClassKey();
	}
	
	protected static String ClassKey() {
		return "combataction";
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		
		if (root.getChild("savingthrow") != null) {
			try {
				savingThrow = Attributes.valueOf(root.getChild("savingthrow").getValue());
			} catch (IllegalArgumentException e) {
				System.out.println("Cannot convert " + root.getChild("savingthrow").getValue() +" into an Attribute");
				savingThrow = null;
			}
		}
		
		useArmor = false;
		if (root.getChild("usearmor") != null) {
			this.useArmor = DataNode.parseBool(root.getChild("usearmor"));
		}
	}
	
	@Override
	public DataNode write(String key) {
		DataNode root = super.write(key);
		
		if (savingThrow != null)
			root.addChild(new DataNode("savingthrow", savingThrow.name(), null));
		root.addChild(new DataNode("usearmor", useArmor + "", null));
		
		return root;
		
	}
	
}
