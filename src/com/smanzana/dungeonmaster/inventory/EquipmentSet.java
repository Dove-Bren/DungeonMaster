package com.smanzana.dungeonmaster.inventory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.inventory.item.Equipment;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class EquipmentSet implements DataCompatible {

	private Map<Equipment.Slot, Equipment> equips;
	
	public EquipmentSet() {
		equips = new EnumMap<>(Equipment.Slot.class);
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
			}
		}
		
	}

	@Override
	public DataNode write(String key) {
		List<DataNode> list = new ArrayList<>(Equipment.Slot.values().length);
		
		for (Equipment.Slot slot : Equipment.Slot.values()) {
			if (this.equips.containsKey(slot)) {
				list.add(equips.get(slot).write(slot.name()));
			}
		}
		
		return new DataNode(key, null, list);
	}
	
	
	
}
