package com.smanzana.dungeonmaster.ui;

import com.smanzana.dungeonmaster.ui.common.InventoryView;
import com.smanzana.dungeonmaster.ui.common.MessageBox;
import com.smanzana.dungeonmaster.ui.common.PlayerView;

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
	
}
