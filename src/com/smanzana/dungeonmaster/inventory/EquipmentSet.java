package com.smanzana.dungeonmaster.inventory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.inventory.item.Armor;
import com.smanzana.dungeonmaster.inventory.item.Equipment;
import com.smanzana.dungeonmaster.inventory.item.Weapon;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;

public class EquipmentSet implements DataCompatible, Cloneable {

	@DataLoaderData
	@DataLoaderList(templateName="templateEquipment", factoryName="constructTemplate")
	private Map<Equipment.Slot, Equipment> equips;
	
	protected static Equipment templateEquipment = constructTemplate();
	private static Equipment constructTemplate() {
		return (Equipment) Equipment.empty.clone();
	}
	
	public EquipmentSet() {
		equips = new EnumMap<>(Equipment.Slot.class);
		for (Equipment.Slot slot : Equipment.Slot.values()) {
			equips.put(slot, null);
		}
	}
	
	public void setPiece(Equipment.Slot slot, Equipment item) {
		equips.put(slot, item);
	}
	
	public Equipment getPiece(Equipment.Slot slot) {
		return equips.get(slot);
	}

	@Override
	public void load(DataNode root) {
		this.equips.clear();
		DataNode node;

		for (Equipment.Slot slot : Equipment.Slot.values()) {
			if (null != (node = root.getChild(slot.name()))) {
				equips.put(slot, Equipment.fromData(node));
			} else
				equips.put(slot, null);
		}
		
	}

	@Override
	public DataNode write(String key) {
		List<DataNode> list = new ArrayList<>(Equipment.Slot.values().length);
		
		for (Equipment.Slot slot : Equipment.Slot.values()) {
			if (this.equips.containsKey(slot)) {
				Equipment e = equips.get(slot);
				if (e != null)
				list.add(e.write(slot.name()));
			}
		}
		
		return new DataNode(key, null, list);
	}
	
	public int getTotalDefense() {
		// Sum equipment INCLUDING offhand (if it's a shield)
		int sum = 0;
		for (Equipment.Slot slot : Equipment.Slot.values()) {
			if (slot == Equipment.Slot.MAIN_HAND)
				continue;
			if (slot == Equipment.Slot.OFF_HAND) {
				if (null != getPiece(slot) && ( (Weapon) getPiece(slot)).isShield())
					sum += ((Weapon) getPiece(slot)).getAttack().fetchValue();
			} else if (getPiece(slot) != null)
				sum += ((Armor) getPiece(slot)).getDefense();
		}
		
		return sum;
	}
	
	public int getTotalAttack() {
		// mainhand + offhand (if not shield)
		int sum = 0;
		Equipment equip;
		
		equip = getPiece(Equipment.Slot.MAIN_HAND); 
		if (equip != null)
			sum += ((Weapon) equip).getAttack().fetchValue();
		
		equip = getPiece(Equipment.Slot.OFF_HAND);
		if (equip != null) {
			if (!((Weapon) equip).isShield())
				sum += ((Weapon) equip).getAttack().fetchValue();
		}
		
		return sum;
	}
	
	public EquipmentSet clone() {
		DataNode serial = this.write("dummy");
		EquipmentSet out = new EquipmentSet();
		out.load(serial);
		
		return out;
	}
	
}
