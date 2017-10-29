package com.smanzana.dungeonmaster.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.app.swing.editors.fields.StepField;
import com.smanzana.templateeditor.api.ICustomData;
import com.smanzana.templateeditor.editor.fields.EditorField;

/**
 * Called 'step list', but more accurately described as a map between range maxs and bonuses.
 * Holds a value for every integer on the number line. For example, a range list could transform an integer
 * into -1, 0, and 1, where (-inf to 10] => -1, [11 to 43] => 0, [44 to inf) => 1
 * While the entire range of ints is covered for input, output can be anything. Instead of -1, 0, and 1 above, we could
 * represent the same mapping but to -4, 55, and 0. 
 * 
 * <b>Serialized:</b>
 * Example: 5:-1,10:0,15:1 means      (-inf,5] = 1, [6, 10] = 0, [11, 15] = 1, [16, inf) = 2 (1 more than last number)
 * Also supports creating holes at the end by repeating last max with n-1 of desired bonus.
 * Example: 5:-1,10:0,15:1,15:3 means (-inf,5] = 1, [6, 10] = 0, [11, 15] = 1, [16, inf) = 4 (1 more than last number)
 * @author Skyler
 *
 */
public class StepList implements DataCompatible, ICustomData {
	
	/**
	 * Uses max of range as key.
	 */
	private Map<Integer, Integer> map;
	private int large; // returned when query higher of all ranges
	private int max; // cache for convenience
	
	/**
	 * Constructs a new StepList.
	 */
	public StepList() {
		this.map = new HashMap<>();
		this.large = 0;
		this.max = Integer.MIN_VALUE;
	}
	
	public void addStep(int max, int value) {
		addStep(max, value, true);
	}
	
	/**
	 * 
	 * @param max
	 * @param value
	 * @param force Overwrite existing range if it exists.
	 *              probs what you want except when defining end gaps
	 */
	public void addStep(int max, int value, boolean force) {
		// if already exists, potentially create gap
		if (!force && max == this.max && map.containsKey(max)) {
			// Already exists in map and is the max.
			// Just adjust large value
			this.large = value + 1;
			return;
		}
		
		this.map.put(max, value);
		if (max > this.max) {
			this.max = max;
			this.large = value + 1;
		}
	}
	
	/**
	 * Sets value returned when input is larger than largest max
	 * Overwritten each time addStep is called
	 * @param max
	 */
	public void setHigh(int max) {
		this.large = max;
	}
	
	/**
	 * Value given when input is higher than current maximum
	 * @return
	 */
	public int getHigh() {
		return this.large;
	}
	
	public int getValue(int input) {
		if (map.isEmpty())
			return 0;
		
		if (input > this.max) {
			return this.large;
		}
		
		// step through find first max that's higher return that value
		// if none higher, large value
		
		// We have a hash map. Iterator doesn't mean next > cur.
		// Have to keep info about closest fit
		int closeKey = this.max;
		int closeValue = map.get(this.max);
		for (Integer key : map.keySet()) {
			if (key >= input) {
				// found one that is higher
				// if smaller than previous closeKey, use this instead
				if (key < closeKey) {
					closeKey = key;
					closeValue = map.get(key);
				}
			}
		}
		
		return closeValue;
	}

	@Override
	public void load(DataNode root) {
		String serial = root.getValue();
		if (serial == null || serial.trim().isEmpty())
			return;
		
		this.max = Integer.MIN_VALUE;
		this.large = 0;
		
		serial = serial.trim();
		int pos, end;
		int max, value;
		String eval;
		while (!serial.trim().isEmpty()) {
			pos = serial.indexOf(':');
			// each loop body means there's another mapping
			// if "," exists in string, trim to that. Otherwise take whole string
			end = serial.indexOf(',');
			if (end == -1) {
				eval = serial;
				serial = "";
			} else {
				eval = serial.substring(0, end);
				if (serial.length() == end + 1) // comma at end of string
					serial = "";
				else // string after comma
					serial = serial.substring(end + 1);
			}
			
			try {
				max = Integer.parseInt(eval.substring(0, pos));
				value = Integer.parseInt(eval.substring(pos + 1));
				this.addStep(max, value, false); //allow gaps
				if (max > this.max) {
					this.max = max;
					this.large = value + 1;
				}
			} catch (NumberFormatException e) {
				System.out.println("Failed to parse range step: \"" + eval + "\"");
			}
			
			serial.trim();
		}
	}

	@Override
	public DataNode write(String key) {
		String serial = "";
		
		if (!this.map.isEmpty()) {
			List<Integer> keys = new ArrayList<>(map.keySet());
			Collections.sort(keys); // so that we write it out better-looking
			for (Integer k : keys) {
				serial += k + ":" + map.get(k) + ",";
				// cleanup trailing comma in deserialization. Who cares?
			}
		}
		serial += this.max + ":" + (large - 1);
		
		return new DataNode(key, serial, null);
	}
	
	public String serialize() {
		// convenience for things that don't work with DataNode
		DataNode node = this.write("dummy");
		return node.getValue();
	}
	
	/**
	 * Do not confuse this with the {@link load} function.
	 * It does not work with DataNodes. It expects a raw step string.
	 * @param serial
	 * @return
	 */
	public static StepList deserialize(String serial) {
		DataNode dummy = new DataNode("dummy", serial, null);
		StepList list = new StepList();
		list.load(dummy);
		
		return list;
	}
	
	public Collection<Integer> getMaxs() {
		return map.keySet();
	}
	
	public Collection<Integer> getValues() {
		return map.values();
	}

	@Override
	public EditorField<?> getField() {
		return new StepField(this);
	}

	@Override
	public ICustomData fillFromField(EditorField<?> field) {
		return ((StepField) field).getObject();
	}
}
