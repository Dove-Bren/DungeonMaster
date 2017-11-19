package com.smanzana.dungeonmaster.spell;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;

public class Spell implements DataCompatible {

	@DataLoaderName
	private String name;
	@DataLoaderDescription
	private String description;
	@DataLoaderData
	private int manaCost;
	@DataLoaderData
	private int level; // spell level, as in spell slot level
	@DataLoaderList(templateName="subactionTemplate")
	private List<SubAction> subactions;
	
	protected static final SubAction subactionTemplate = SubAction.templateSubaction;
	
	public Spell(String name, String description, int manaCost, int level) {
		this.name = name;
		this.description = description;
		this.manaCost = manaCost;
		this.level = level;
		
		subactions = new LinkedList<>();
	}
	
	public void addSubaction(SubAction subaction) {
		subactions.add(subaction);
	}
	
	public void clearSubactions() {
		subactions.clear();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getManaCost() {
		return manaCost;
	}

	public int getLevel() {
		return level;
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
		
		if (null != (node = root.getChild("manacost"))) {
			manaCost = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("level"))) {
			level = DataNode.parseInt(node);
		}
		
		subactions.clear();
		if (null != (node = root.getChild("subactions"))) {
			for (DataNode child : node.getChildren())
				subactions.add(SubAction.fromData(child));
		}
	}

	@Override
	public DataNode write(String key) {
		DataNode node = new DataNode(key, null, new LinkedList<>());
		
		node.addChild(new DataNode("name", this.name, null));
		node.addChild(new DataNode("description", this.description, null));
		node.addChild(new DataNode("manacost", this.manaCost + "", null));
		node.addChild(new DataNode("level", this.level + "", null));
		node.addChild(DataNode.serializeAll("subactions", "subaction", subactions));
		
		return node;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Spell) {
			Spell other = (Spell) o;
			return (other.name.equals(name) &&
					other.description.equals(description) &&
					other.level == level &&
					other.manaCost == manaCost
					);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode()
				+ 7 * description.hashCode()
				+ 23 * level
				+ 37 * manaCost;
	}
}
