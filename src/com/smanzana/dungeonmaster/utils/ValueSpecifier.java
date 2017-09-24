package com.smanzana.dungeonmaster.utils;

public interface ValueSpecifier {

	/**
	 * Gets a value specified by this specifier.
	 * For Dice or Ranges, this means a random number in the valid set of returns
	 * @return
	 */
	public int fetchValue();
	
}
