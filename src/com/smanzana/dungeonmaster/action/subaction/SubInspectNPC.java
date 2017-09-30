package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.pawn.NPC;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.UI;

/**
 * Action prompt for information about an NPC
 * @author Skyler
 *
 */
public class SubInspectNPC extends SubAction {
	
	private static class Factory implements SubActionFactory<SubInspectNPC> {
		@Override
		public SubInspectNPC construct(DataNode data) {
			SubInspectNPC ret = new SubInspectNPC();
			ret.load(data);
			return ret;
		}
	}
	
	protected static void register() {
		SubAction.registerFactory(ClassKey(), new Factory());
	}
	
	private NPC npc;
	
	public SubInspectNPC() {
		;
	}
	
	/**
	 * Use InspectPlayer for players. More specific
	 * @param pawn
	 */
	public SubInspectNPC(NPC npc) {
		this.npc = npc;
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {
		if (source instanceof Player) {
			UI.instance().sendNPC(
					(Player) source,
					npc
					);
		} else {
			System.out.println("Non-player attempted to inspect an NPC. What?");
		}
		
	}

	@Override
	public void load(DataNode root) {
		; // nothing to do
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		// no data to store
		
		return base;
	}

	@Override
	protected String getClassKey() {
		return SubInspectNPC.ClassKey();
	}
	
	protected static String ClassKey() {
		return "internal_inspectnpc";
	}
}
