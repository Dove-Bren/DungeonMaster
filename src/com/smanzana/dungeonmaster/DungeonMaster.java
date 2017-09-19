package com.smanzana.dungeonmaster;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueRange;

/**
 * D&D Companion Program.
 * Models D&D data
 * @author Skyler
 */
public class DungeonMaster {

	
	public static void main(String[] args) {
		// Two modes:
		// Creation mode (set up configs)
		// DM mode (play a session)
		
		// Creation mode: Create templates
		// DM mode: Spawn a session from a template OR load an existing session
		
		ValueRange range = new ValueRange(1, 5);
		DataNode node = range.write("range");
		
		System.out.println(node.serialize(true, true));
	}
		
}

