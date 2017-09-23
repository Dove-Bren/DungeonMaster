package com.smanzana.dungeonmaster.inventory;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.inventory.item.Equipment;
import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Inventory implements DataCompatible {

	private int gold;
	private List<Item> heldItems;
	private EquipmentSet equipment;
	
	public Inventory() {
		heldItems = new LinkedList<>();
		gold = 0;
		equipment = new EquipmentSet();
	}
	
	public void setGold(int newAmount) {
		this.gold = newAmount;
	}
	
	public void addGold(int amount) {
		this.gold += amount;
		if (gold < 0)
			gold = 0;
	}
	
	public int getGold() {
		return this.gold;
	}
	
	public List<Item> getItems() {
		return this.heldItems;
	}
	
	public EquipmentSet getEquipment() {
		return this.equipment;
	}
	
	public void addItem(Item item) {
		this.heldItems.add(item);
	}
	
	public void emptyItems() {
		this.heldItems.clear();
	}
	
	public void clearEquipment() {
		for (Equipment.Slot slot : Equipment.Slot.values()) {
			equipment.setPiece(slot, null);
		}
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		heldItems.clear();
		
		if (null != (node = root.getChild("gold"))) {
			this.gold = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("equipment"))) {
			this.equipment.load(node);
		}
		
		if (null != (node = root.getChild("items"))) {
			for (DataNode child : node.getChildren()) {
				heldItems.add(Item.fromData(child));
			}
		}
		
	}

	@Override
	public DataNode write(String key) {
		List<DataNode> list = new LinkedList<>();
		
		list.add(new DataNode("gold", this.gold + "", null));
		list.add(this.equipment.write("equipment"));
		list.add(DataNode.serializeAll("items", "item", heldItems));
		
		return new DataNode(key, null, list);
	}	
}
