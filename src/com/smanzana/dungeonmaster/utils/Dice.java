package com.smanzana.dungeonmaster.utils;

import java.util.Random;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Dice extends ValueSpecifier implements DataCompatible {

	private int dieCount;
	private int dieFaces;
	private boolean includeZero;
	
	/**
	 * 
	 * @param count
	 * @param faces
	 * @param includeZero Whether faces includes a side for 0. If so, die roll values
	 *                    lie in range [0,faces). That is, you can never roll 'faces' high.
	 */
	public Dice(int count, int faces, boolean includeZero) {
		this.dieCount = count;
		this.dieFaces = faces;
		this.includeZero = includeZero;
	}
	
	public Dice(int faces) {
		this(1, faces, false);
	}
	
	public int getDieCount() {
		return this.dieCount;
	}
	
	public int getDieFaces() {
		return this.dieFaces;
	}
	
	public boolean includesZero() {
		return includeZero;
	}
	
	public int roll() {
		return roll(new Random());
	}
	
	public int roll(Random rand) {
		int sum = 0;
		for (int i = 0; i < dieCount; i++) {
			sum += rand.nextInt(this.dieFaces) + (this.includeZero ? 0 : 1);
		}
		
		return sum;
	}

	@Override
	public void load(DataNode root) {
		String serial = root.getValue();
		int pos = serial.indexOf('d');
		
		if (pos == -1) {
			this.dieCount = 0;
			this.dieFaces = 0;
			this.includeZero = false;
			return;
		}
		
		this.dieCount = Integer.parseInt(serial.substring(0, pos));
		serial = serial.substring(pos + 1).trim();
		
		pos = serial.indexOf(':');
		if (pos == -1) {
			// does not include 0
			this.dieFaces = Integer.parseInt(serial);
			this.includeZero = false;
		} else {
			this.dieFaces = Integer.parseInt(serial.substring(0, pos));
			this.includeZero = true;
		}
			
	}

	@Override
	public DataNode write(String key) {
		// 1d3, 2d4, 5d5: (includes 0)
		return new DataNode(key, this.dieFaces + "d" + this.dieCount + (this.includeZero ? ":" : ""), null);
	}

	@Override
	public int fetchValue() {
		return this.roll();
	}
	
}
