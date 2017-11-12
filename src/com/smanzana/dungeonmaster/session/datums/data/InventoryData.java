package com.smanzana.dungeonmaster.session.datums.data;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.inventory.EquipmentSet;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.item.Equipment.Slot;
import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.inventory.item.Junk;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;

/**
 * Convenience class. Holds all data needed to make an inventory
 * @author Skyler
 *
 */
public class InventoryData {

	@DataLoaderData
	private int gold;
	@DataLoaderList(templateName="templateItem", factoryName="constructTemplateItem")
	private List<Item> items;
	@DataLoaderData
	private EquipmentSet equips;
	
	protected Item templateItem = new Junk();
	
	public InventoryData() {
		this(0, new LinkedList<>(), new EquipmentSet());
	}
	
	public InventoryData(Inventory inv) {
		this.gold = inv.getGold();
		this.equips = inv.getEquipment();
		this.items = new LinkedList<>();
		for (Item i : inv.getItems())
			this.items.add(i);
	}
	
	public InventoryData(int gold, List<Item> items, EquipmentSet equips) {
		this.gold = gold;
		this.items = items;
		this.equips = equips;
	}
	
	public Inventory toInventory() {
		Inventory inv = new Inventory();
		inv.setGold(gold);
		for (Item i : items)
			inv.addItem(i);
		for (Slot s : Slot.values()) {
			inv.getEquipment().setPiece(s, equips.getPiece(s));
		}
		return inv;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	public void addItem(Item i) {
		this.items.add(i);
	}

	public EquipmentSet getEquips() {
		return equips;
	}

	public void setEquips(EquipmentSet equips) {
		this.equips = equips;
	}
	
	public Item constructTemplateItem() {
		return new Junk();
	}
	
}
