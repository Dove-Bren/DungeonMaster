package com.smanzana.dungeonmaster.spell;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Spell implements DataCompatible {

	private String name;
	private String description;
	private int manaCost;
	private int level; // spell level, as in spell slot level
	private List<SubAction> subactions;
	
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
	
}
