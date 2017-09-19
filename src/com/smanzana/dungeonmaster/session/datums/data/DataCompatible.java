package com.smanzana.dungeonmaster.session.datums.data;

public interface DataCompatible {
	
	/**
	 * Parse provided data tree.
	 * Root provided has no key. It represent the templated wrapper object.
	 * Consider:
	 * <class>
	 *   <param1>4</param1>
	 *   <param2>
	 *     <param3>5</param3>
	 *   </param2>
	 * </class>
	 * 
	 * root in this case is the 'class' object.
	 * @param root
	 */
	public void load(DataNode root);
	
	/**
	 * Represent this data as a DataNode object.
	 * @param key The key to use for the created object
	 * @return DataNode object with key = passed in key, value set or children set
	 */
	public DataNode write(String key);
	
}
