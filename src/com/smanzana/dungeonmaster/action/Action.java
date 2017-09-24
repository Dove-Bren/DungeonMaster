package com.smanzana.dungeonmaster.action;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.pawn.Pawn;

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

	public static enum TargetType {
		SELF,
		TARGET,
		PARTY,
		MULTI,
	}
	
	private String name;
	private String description;
	private List<SubAction> subActions;
	private TargetType targetType;
	
	protected Action(String name, String description, TargetType type) {
		this.subActions = new LinkedList<>();
		this.name = name;
		this.description = description;
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
	
	// TODO protected abstract IMG getIcon();
	
	/**
	 * Perform action.
	 * If performed by DM, source will be null.
	 * @param source Pawn performing the action.
	 */
	public void perform(Pawn source) {
		// Figure out who to apply to based on targetType
		switch (targetType) {
		case SELF:
			apply(source, source);
			break;
		case TARGET:
			apply(source, selectTarget());
			break;
		case PARTY:
			for (Pawn pc : DungeonMaster.getActiveSession().getParty())
				apply(source, pc);
			break;
		case MULTI:
			for (Pawn targ : selectMultiTargets())
				apply(source, targ);
			break;
		}
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
	
}
