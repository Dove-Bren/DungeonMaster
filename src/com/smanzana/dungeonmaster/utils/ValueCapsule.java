package com.smanzana.dungeonmaster.utils;

/**
 * Wrapper to int amount, for easily passing modifications through callbacks and event hooks
 * @author Skyler
 *
 */
public class ValueCapsule {

	private int originalAmount;
	private int bonus;
	private float multBonus;
	
	public ValueCapsule(int amount) {
		this.originalAmount = amount;
		this.bonus = 0;
		this.multBonus = 1.0f;
	}
	
	public void addBonus(int bonus) {
		this.bonus += bonus;
	}
	
	public void addMultiplier(float mult) {
		this.multBonus += mult;
	}
	
	public int getFinal() {
		return ((int) ((float) originalAmount * multBonus)) + bonus;
	}
	
}
