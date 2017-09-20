package com.smanzana.dungeonmaster.session.datums;

import com.smanzana.dungeonmaster.session.datums.data.DatumData;

public interface DatumFactory<T extends DatumData> {
	
	/**
	 * Construct a blank instance of T.
	 * This is later used to load a piece of data via the load method
	 * @return
	 */
	public T constructEmptyData();
	
	/**
	 * Construct and return a piece of example data.
	 * This is used when the datum does not exist on disk and
	 * an example is being written out
	 * @return
	 */
	public T constructDefaultData();
	
}
