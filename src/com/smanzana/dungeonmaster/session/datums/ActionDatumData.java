package com.smanzana.dungeonmaster.session.datums;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.Action.TargetType;
import com.smanzana.dungeonmaster.action.PartyAction;
import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.action.subaction.SubDamage;
import com.smanzana.dungeonmaster.action.subaction.SubRest;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;

/**
 * Stores:
 *  - name
 *  - description
 *  - list of subactions
 *  -
 * @author Skyler
 *
 */
public class ActionDatumData implements DatumData {
	
	public static class ActionDatumFactory implements DatumFactory<ActionDatumData> {

		@Override
		public ActionDatumData constructEmptyData() {
			return new ActionDatumData();
		}

		@Override
		public ActionDatumData constructDefaultData() {
			return (ActionDatumData) ActionDatumData.getExampleData();
		}
		
	}
	
	@DataLoaderName
	private String name;
	@DataLoaderDescription
	private String description;
	@DataLoaderData
	private boolean beneficial;
	@DataLoaderData
	private boolean isParty; // is a party action
	@DataLoaderData
	private String partyPrompt;
	@DataLoaderData
	private TargetType targetType;
	@DataLoaderList(templateName="templateSubaction")
	private List<SubAction> subactions;
	@DataLoaderData
	private Attributes attribute;
	
	protected static final SubAction templateSubaction = SubAction.templateSubaction;
	
	public ActionDatumData() {
		this.subactions = new LinkedList<>();
		this.subactions.add(new SubDamage(new ValueConstant(4)));
		
		targetType = TargetType.SELF;
		attribute = Attributes.STRENGTH;
	}
	
	public ActionDatumData(String name, String description, boolean beneficial, Attributes attribute, TargetType targetType) {
		this();
		this.name = name;
		this.description = description;
		this.beneficial = beneficial;
		this.targetType = targetType;
		this.attribute = attribute;
		isParty = false;
	}
	
	public ActionDatumData(String name, String description, boolean beneficial, Attributes attribute,  TargetType targetType,
			boolean isParty, String partyPrompt, List<SubAction> subactions) {
		this.name = name;
		this.description = description;
		this.subactions = subactions;
		this.beneficial = beneficial;
		this.targetType = targetType;
		this.isParty = isParty;
		this.partyPrompt = partyPrompt;
		this.attribute = attribute;
	}
	
	public Attributes getAttribute() {
		return attribute;
	}

	public void setAttribute(Attributes attribute) {
		this.attribute = attribute;
	}

	public String getPartyPrompt() {
		return partyPrompt;
	}

	public void setPartyPrompt(String partyPrompt) {
		this.partyPrompt = partyPrompt;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
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

	public List<SubAction> getSubactions() {
		return subactions;
	}
	
	public void addSubaction(SubAction sub) {
		this.subactions.add(sub);
	}
	
	public void clearSubactions() {
		this.subactions.clear();
	}
	
	public void setBeneficial(boolean bene) {
		this.beneficial = bene;
	}
	
	public boolean getBeneficial() {
		return this.beneficial;
	}
	
	public void setIsParty(boolean isParty) {
		this.isParty = isParty;
	}
	
	public boolean isParty() {
		return isParty;
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		
		// name (which is primitive)
		if ((node = root.getChild("name")) != null) {
			this.name = node.getValue();
		}
		
		// description (primitive)
		if ((node = root.getChild("description")) != null) {
			this.description = node.getValue();
		}
		
		// beneficial (primitive)
		if ((node = root.getChild("beneficial")) != null) {
			this.beneficial = DataNode.parseBool(node);
		}
		
		// isParty (primitive)
		if ((node = root.getChild("isparty")) != null) {
			this.isParty = DataNode.parseBool(node);
		}
		
		// targettype
		if ((node = root.getChild("targettype")) != null) {
			try {
				this.targetType = TargetType.valueOf(node.getValue());
			} catch (Exception e) {
				System.out.println("Cannot convert \"" + node.getValue() + "\" into an action target type");
				this.targetType = TargetType.SELF;
			}
		}
		
		// attribute
		if ((node = root.getChild("attribute")) != null) {
			try {
				this.attribute = Attributes.valueOf(node.getValue());
			} catch (Exception e) {
				System.out.println("Cannot convert " + node.getValue() + " into an attribute");
				this.attribute = Attributes.STRENGTH;
			}
		}
		
		if ((node = root.getChild("partyprompt")) != null) {
			this.partyPrompt = node.getValue();
		}
		
		//subactions
		this.clearSubactions();
		if ((node = root.getChild("subactions")) != null) {
			for (DataNode child : node.getChildren())
				this.addSubaction(SubAction.fromData(child));
		}
	}

	@Override
	public DataNode write(String key) {
		DataNode node = new DataNode(key, null, new LinkedList<>());
		
		node.addChild(new DataNode("name", this.name, null));
		node.addChild(new DataNode("description", this.description, null));
		node.addChild(new DataNode("beneficial", this.beneficial + "", null));
		node.addChild(new DataNode("isparty", this.isParty + "", null));
		node.addChild(new DataNode("partyprompt", (partyPrompt == null ? "" : partyPrompt) + "", null));
		node.addChild(new DataNode("targettype", (targetType == null ? "" : targetType.name()), null));
		node.addChild(new DataNode("attribute", (attribute == null ? "" : attribute.name()), null));
		node.addChild(DataNode.serializeAll("subactions", "subaction", subactions));
		
		return node;
	}

	public static DatumData getExampleData() {
		ActionDatumData data = new ActionDatumData("Rest At Inn",
				"Purchase a room and sleep, recovering health and spell slots.", true, Attributes.CHARISMA, TargetType.SELF);

		data.setIsParty(true);
		data.setPartyPrompt("Gather at the inn for a night of rest?");
		data.addSubaction(new SubRest());
		
		return data;
	}
	
	public Action toAction() {
		Action action;
		if (isParty()) {
			action = new PartyAction(getName(), getDescription(), getBeneficial(), getAttribute(), getPartyPrompt(), getTargetType()); 
		} else {
			action = new Action(getName(), getDescription(), getBeneficial(), getAttribute(), getTargetType());
		}
		
		for (SubAction sub : this.getSubactions()) {
			action.addSubAction(sub);
		}
		
		return action;
	}
	
	@Override
	public String getDisplayName() {
		return getName();
	}
	
	@Override
	public String getDisplayTooltip() {
		return getDescription();
	}
}
