package com.smanzana.dungeonmaster.ui.common;

import java.util.HashMap;
import java.util.Map;

import com.smanzana.dungeonmaster.inventory.EquipmentSet;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.item.Item;

/**
 * Can't pass around a pointer to data kept by main thread.
 * Instead, create a clone with only the data we need for clients.
 * @author Skyler
 *
 */
public class InventoryView {
	
	public static class ItemView {
		
		private Item clone;
		
		public ItemView(Item source) {
			this.clone = source.clone();
		}
		
		/**
		 * Not meant to be used outside of UI code.
		 * Do not call.
		 * @return
		 */
		public Item getClone() {
			return clone;
		}
	}
	
	public static class EquipmentSetView {
		
		private EquipmentSet clone;
		
		public EquipmentSetView(EquipmentSet source) {
			this.clone = source.clone();
		}
		
		/**
		 * Only meant to be called from UI thread.
		 * Do not call.
		 * @return
		 */
		public EquipmentSet getClone() {
			return clone;
		}
	}

	private int gold;
	private Map<Integer, ItemView> clones;
	private EquipmentSetView equips;
	
	
	public InventoryView(Inventory source) {
		// Instead of being lazy and cloning, we'll actually pull out data
		this.gold = source.getGold();
		this.equips = new EquipmentSetView(source.getEquipment());
		this.clones = new HashMap<>();
		for (Integer key : source.getItemMap().keySet()) {
			clones.put(key, new ItemView(source.getItemMap().get(key)));
		}
	}
	
	public int getGold() {
		return gold;
	}
	
	public EquipmentSetView getEquipment() {
		return equips;
	}
	
	public Map<Integer, ItemView> getItems() {
		return clones;
	}
}
