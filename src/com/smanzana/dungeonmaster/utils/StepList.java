package com.smanzana.dungeonmaster.utils;

import java.util.HashMap;
import java.util.Map;

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
public class StepList {
	
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
	
	public int getValue(int input) {
		if (map.isEmpty())
			return 0;
		
		if (input > this.max) {
			return this.large;
		}
		
		// step through find first max that's higher return that value
		// if none higher, large value
		for (Integer key : map.keySet()) {
			if (key >= input) {
				// found first higher one
				return map.get(key);
			}
		}
		
		// If you made it outside loop, there's a bug. nonetheless return large
		// so it's hard to notice
		return large;
	}
}
