package com.smanzana.dungeonmaster.session.datums;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.utils.ValueRange;

/**
 * Stores:
 *  - name
 *  - description
 *  - list of subactions
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
	
	private String name;
	private String description;
	private List<SubAction> subactions;
	
	public ActionDatumData() {
		this.subactions = new LinkedList<>();
	}
	
	public ActionDatumData(String name, String description) {
		this();
		this.name = name;
		this.description = description;
	}
	
	public ActionDatumData(String name, String description, List<SubAction> subactions) {
		this.name = name;
		this.description = description;
		this.subactions = subactions;
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
		
		node.addChild(new DataNode("name", this.name));
		node.addChild(new DataNode("description", this.description));
		node.addChild(DataNode.serializeAll("subactions", "subaction", subactions));
		
		return new DataNode(key, null, nodes);
	}

	public static DatumData getExampleData() {
		ActionDatumData data = new ActionDatumData("Class-Example", "Just an example class");
		
		data.addPromotion("Class2");
		data.addPromotion("Class3");
		
		data.setStatBase(Attributes.STRENGTH, 5, 10);
		data.setStatBase(Attributes.CHARISMA, 10, 12);
		data.setStatBase(Attributes.DEXTERITY, 5, 10);
		data.setStatBase(Attributes.WISDOM, 5, 10);
		data.setStatBase(Attributes.INTELLIGENCE, 5, 10);
		data.setStatBase(Attributes.CONSTITUTION, 5, 10);
		
		data.setStatGrowth(Attributes.STRENGTH, 5, 10);
		data.setStatGrowth(Attributes.CHARISMA, 10, 12);
		data.setStatGrowth(Attributes.DEXTERITY, 5, 10);
		data.setStatGrowth(Attributes.WISDOM, 5, 10);
		data.setStatGrowth(Attributes.INTELLIGENCE, 5, 10);
		data.setStatGrowth(Attributes.CONSTITUTION, 5, 10);
		
		return data;
	}
	
}
