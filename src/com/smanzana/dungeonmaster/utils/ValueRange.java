package com.smanzana.dungeonmaster.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Holds a range of potential values
 * @author Skyler
 *
 */
public class ValueRange implements DataCompatible {

	private int min;
	private int range;
	
	public ValueRange(int min, int max) {
		this.min = min;
		this.range = max - min;
	}
	
	public int getMin() {
		return this.min;
	}
	
	public int getMax() {
		return this.min + this.range;
	}
	
	public int getRange() {
		return this.range;
	}
	
	public int getRandom(Random rand) {
		return this.min + rand.nextInt(this.range);
	}
	
	public int getRandom() {
		return this.getRandom(new Random());
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		boolean foundNew = false;
		
		// get min
		if ((node = root.getChild("min")) != null) {
			this.min = Integer.parseInt(node.getValue());
			foundNew = true;
		}
		
		// get range
		if ((node = root.getChild("range")) != null) {
			this.range = Integer.parseInt(node.getValue());
			foundNew = true;
		}
		
		if (!foundNew) {
			// try to parse min,range or min-max
			if (root.getValue() != null && !root.getValue().trim().isEmpty()) {
				int pos = root.getValue().indexOf(',');
				if (pos != -1) {
					this.min = Integer.parseInt(root.getValue().substring(0, pos));
					this.range = Integer.parseInt(root.getValue().substring(pos + 1));
					return;
				} 
				pos = root.getValue().indexOf('-');
				if (pos != -1) {
					this.min = Integer.parseInt(root.getValue().substring(0, pos));
					this.range = Integer.parseInt(root.getValue().substring(pos + 1)) - min;
					return;
				}
			}
		}
	}

	@Override
	public DataNode write(String key) {
		List<DataNode> nodes = new ArrayList<>(2);
		nodes.add(new DataNode("min", "" + this.min));
		nodes.add(new DataNode("range", "" + this.range));
		
		return new DataNode(key, null, nodes);
	}
	
	
}
