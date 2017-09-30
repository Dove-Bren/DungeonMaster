package com.smanzana.dungeonmaster.action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.mechanics.AI;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.configuration.KeywordConfig;
import com.smanzana.dungeonmaster.session.configuration.KeywordKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.UI;

/**
 * Generic Action.
 * Actions are basically a list of effects that are triggered together.
 * More or less, actions are scripts of game effects.
 * When configured correctly, actions can be selected and dispatched by users in relevant circumstances.
 * The entirety of a PC's experience is through actions and whatever the DM does manually
 * Example: Rest at In => take money, top of hp,mana,stamina, reset spell counters, etc
 * @author Skyler
 *
 * TODO Make user-defined actions through script?
 * This would mean making this serializable and SubAction serializable.
 * Building action from serial would just mean adding subactions
 * Building subaction from serial would mean parsing script of sorts. This is harder (and maybe not needed?)
 */
public class Action extends SubAction {
	
	private static class Factory implements SubActionFactory<Action> {
		@Override
		public Action construct(DataNode data) {
			Action ret = new Action();
			ret.load(data);
			return ret;
		}
	}
	
	public static void register() {
		SubAction.registerFactory(ClassKey(), new Factory());
	}

	public static enum TargetType {
		SELF,
		TARGET,
		PARTY,
		MULTI,
	}
	
	private String name;
	private String description;
	private boolean beneficial;
	private List<SubAction> subActions;
	private TargetType targetType;
	private Attributes attribute; // Contributing attribue
	
	private Action() {
		this.subActions = new LinkedList<>();
	}
	
	public Action(String name, String description, boolean beneficial, Attributes bonusAttrib, TargetType type) {
		this.subActions = new LinkedList<>();
		this.name = name;
		this.description = description;
		this.beneficial = beneficial;
		this.attribute = bonusAttrib;
		this.targetType = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public TargetType getTargetType() {
		return targetType;
	}
	
	public Action addSubAction(SubAction sub) {
		subActions.add(sub);
		return this;
	}
	
	public Action clearSubActions() {
		subActions.clear();
		return this;
	}
	
	public Action setSubActions(SubAction ...actions) {
		subActions.clear();
		for (SubAction action : actions)
			subActions.add(action);
		
		return this;
	}
	
	public Attributes getAttribute() {
		return attribute;
	}
	
	// TODO protected abstract IMG getIcon();
	
	/**
	 * Perform action.
	 * If performed by DM, source will be null.
	 * @param source Pawn performing the action.
	 */
	public void perform(Pawn source) {
		// Figure out who to apply to based on targetType
		Pawn target;
		switch (targetType) {
		case SELF:
			// Skip saving throw
			apply(source, source);
			break;
		case TARGET:
			target = selectTarget(source, this.isBeneficial());
			if (performSavingThrow(source, target))
				apply(source, target);
			else
				System.out.println(target.getName() + " " + KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROWVERB)
						+ " " + source.getName() + "'s " + getName());
			break;
		case PARTY:
			for (Pawn pc : DungeonMaster.getActiveSession().getParty())
				apply(source, pc);
			break;
		case MULTI:
			for (Pawn targ : selectMultiTargets(source, this.isBeneficial())) {
				if (performSavingThrow(source, targ))
					apply(source, targ);
				else
					System.out.println(targ.getName() + " " + KeywordConfig.instance().getKeyword(KeywordKey.SAVINGTHROWVERB)
							+ " " + source.getName() + "'s " + getName());
			}
			break;
		}
	}
	
	// Subclasses that DO have saving throws should override
	protected boolean performSavingThrow(Pawn source, Pawn target) {
		return true; // Regular actions have no saving throw
	}
	
	private Pawn selectTarget(Pawn source, boolean beneficial) {
		// Is player?
		// If so, try to ask that player
		// Otherwise fall back to AI
		
		if (source instanceof Player) {
			Pawn targ = UI.instance().selectSingleTarget((Player) source, DungeonMaster.getActiveSession().getAllActivePawns());
			if (targ != null) {
				return targ;
			} // else

			// Failed for one reason or another
			// Fall through to AI code
		}
		
		// Do AI resolution
		boolean ally = false;
		
		if (source instanceof Mob)
			ally = ((Mob) source).isAlly();
		else if (source instanceof Player)
			ally = true;
		
		return AI.selectTarget(DungeonMaster.getActiveSession().getAllActivePawns(), ally, beneficial);
	}
	
	private Collection<Pawn> selectMultiTargets(Pawn source, boolean beneficial) {
		
		if (source instanceof Player) {
			Collection<Pawn> targs = UI.instance().selectTargets((Player) source, DungeonMaster.getActiveSession().getAllActivePawns());
			if (targs != null && !targs.isEmpty()) {
				return targs;
			} // else

			// Failed for one reason or another
			// Fall through to AI code
		}
		
		boolean ally = false;
		
		if (source instanceof Mob)
			ally = ((Mob) source).isAlly();
		else if (source instanceof Player)
			ally = true;
		
		return AI.selectMultiTargets(DungeonMaster.getActiveSession().getAllActivePawns(), ally, beneficial);
	}
	
	public boolean isBeneficial() {
		return beneficial;
	}
	
	/**
	 * @param target
	 */
	@Override
	public void apply(Pawn source, Pawn target) {
		// Apply subactions
		for (SubAction action : subActions)
			action.apply(source, target);
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		
		if (null != (node = root.getChild("name"))) {
			name = node.getValue();
		}
		
		if (null != (node = root.getChild("description"))) {
			description = node.getValue();
		}
		
		if (null != (node = root.getChild("beneficial"))) {
			beneficial = DataNode.parseBool(node);
		}
		
		this.subActions.clear();
		if (null != (node = root.getChild("subactions"))) {
			for (DataNode child : node.getChildren())
				subActions.add(SubAction.fromData(child));
		}
		
		if (null != (node = root.getChild("targettype"))) {
			try {
				targetType = TargetType.valueOf(node.getValue());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.out.println("Cannot convert " + node.getValue() + " to a target type");
			}
		}
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("name", name, null));
		base.addChild(new DataNode("description", description, null));
		base.addChild(new DataNode("beneficial", beneficial + "", null));
		base.addChild(DataNode.serializeAll("subactions", "subaction", subActions));
		base.addChild(new DataNode("targettype", targetType.name(), null));
		
		return base;
	}

	@Override
	protected String getClassKey() {
		return ClassKey();
	}
	
	protected static String ClassKey() {
		return "action";
	}
	
}
