package com.smanzana.dungeonmaster;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.StepList;

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
		
		//new GameSession(new File("testsession"));
		
		StepList list = new StepList();
		list.addStep(-2, -1);
		list.addStep(2, 0);
		
		for (int i = -5; i <= 5; i++) {
			System.out.print("[" + i + "=>" + list.getValue(i) + "] ");
		}
		System.out.println();
		
		String out = list.write("wrapper").serialize(false, false);
		System.out.println(out);
		
		StepList inList = new StepList();
		inList.load(DataNode.parseSingle(out));

		for (int i = -5; i <= 5; i++) {
			System.out.print("[" + i + "=>" + inList.getValue(i) + "] ");
		}
		System.out.println();
		
		String serial = "-2:-1,2:0,2:1";
		
		StepList list3 = StepList.deserialize(serial);
		for (int i = -5; i <= 5; i++) {
			System.out.print("[" + i + "=>" + list3.getValue(i) + "] ");
		}
	}
		
}

