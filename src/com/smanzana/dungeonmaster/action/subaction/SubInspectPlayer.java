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
 * Action prompt for information about a Player
 * @author Skyler
 *
 */
public class SubInspectPlayer extends SubAction {
	
	private static class Factory implements SubActionFactory<SubInspectPlayer> {
		@Override
		public SubInspectPlayer construct(DataNode data) {
			SubInspectPlayer ret = new SubInspectPlayer();
			ret.load(data);
			return ret;
		}
	}
	
	protected static void register() {
		SubAction.registerFactory(ClassKey(), new Factory());
	}
	
	private Player player;
	
	public SubInspectPlayer() {
		;
	}
	
	public SubInspectPlayer(Player player) {
		this.player = player;
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {
		if (source instanceof Player) {
			UI.instance().sendPlayer(
					(Player) source,
					player
					);
		} else {
			System.out.println("Non-player attempted to inspect a Player. What?");
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
		return SubInspectPlayer.ClassKey();
	}
	
	protected static String ClassKey() {
		return "internal_inspectplayer";
	}
}
