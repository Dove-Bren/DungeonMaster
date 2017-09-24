package com.smanzana.dungeonmaster.utils;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public interface ValueSpecifier extends DataCompatible {

	/**
	 * Gets a value specified by this specifier.
	 * For Dice or Ranges, this means a random number in the valid set of returns
	 * @return
	 */
	public int fetchValue();
	
	public static ValueSpecifier fromData(DataNode data) {
		// Crappy but oh well. Bite me
		ValueSpecifier ret;
		if (data.getValue() != null) {
			String serial = data.getValue();
			// if has d's, it's dice or a diceset
			if (serial.contains("d")) {
				if (serial.contains(","))
					ret = new DiceSet();
				else
					ret = new Dice(1);
			} else if (serial.contains("-") || serial.contains(",")) {
				// formats 2 and 3 of value range
				ret = new ValueRange(1, 2);
			} else
				ret = new ValueConstant(0);
		} else {
			// only implementation we have right now that has children is value range
			ret = new ValueRange(1, 2);
		}
		
		ret.load(data);
		return ret;
	}
	
}
