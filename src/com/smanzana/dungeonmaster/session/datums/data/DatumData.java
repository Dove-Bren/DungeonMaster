package com.smanzana.dungeonmaster.session.datums.data;

/**
 * Data template that populates a Datum
 * @author Skyler
 *
 */
public interface DatumData {
	
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
	 * @return
	 */
	public DataNode write();
	
}
