package com.smanzana.dungeonmaster.ui;

import java.util.Collection;
import java.util.List;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.ui.common.InventoryView;
import com.smanzana.dungeonmaster.ui.common.MessageBox;
import com.smanzana.dungeonmaster.ui.common.NPCView;
import com.smanzana.dungeonmaster.ui.common.PlayerView;
import com.smanzana.dungeonmaster.ui.common.TargetView;

/**
 * Communication endpoint between UI and a player.
 * This can be to a DM or an actual player
 * @author Skyler
 *
 */
public abstract class Comm {

	/**
	 * Main processing loop.
	 * Fetch input. Do what you do to map it back to a callback.
	 * This is called on UI thread. It should not block.
	 */
	public abstract void processInput();
	
	/**
	 * Shut down the comm.
	 * This usually means send 'Sorry! We're closed!' to the client.
	 * For APP client, also involves shutting down application
	 */
	public abstract void shutdown();
	
	/**
	 * Show a message box.
	 * Call callback with the option that was selected, as per messagebox
	 * @param msg
	 * @param callback
	 */
	public abstract void showMessageBox(MessageBox msg, UICallback callback);
	
	public abstract void clearScreen();
	
	public abstract void showInventory(InventoryView inv);
	
	public abstract void showPlayer(PlayerView player);
	
	public abstract void showNPC(NPCView pawn);
	
	public abstract void setActions(List<Action> actions);
	
	public abstract void showTargetSelect(Collection<TargetView<?>> targets, boolean multi, UICallback callback);
	
	// Creation key used to associate (and validate) comm with character slot
	public abstract void showPlayerCreation(int creationKey, PlayerCreationOptions opts);
	
	// DM only actions
	// Sending to player results in no op
	
	public abstract void promptRoll(Attributes suggestedAttrib, PlayerView player, String desc);
	public abstract void promptRoll(Attributes suggestedAttrib, NPCView npc, String desc);
	
	
	
}
