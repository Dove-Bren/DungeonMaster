package com.smanzana.dungeonmaster.ui.app;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.ui.Comm;
import com.smanzana.dungeonmaster.ui.UICallback;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.dungeonmaster.ui.common.InventoryView;
import com.smanzana.dungeonmaster.ui.common.MessageBox;
import com.smanzana.dungeonmaster.ui.common.NPCView;
import com.smanzana.dungeonmaster.ui.common.PlayerView;
import com.smanzana.dungeonmaster.ui.common.TargetView;

public class AppUI extends Comm {
	
	private AppFrame guiFrame;
	
	public AppUI() {
		guiFrame = new AppFrame();
		guiFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		guiFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				;
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				;
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				DungeonMaster.shutdown();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				;
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				;
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				;
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				;
			}
		});
		
		guiFrame.setVisible(true);
	}
	
	public void shutdown() {
		if (guiFrame == null)
			return;
		
		guiFrame.setVisible(false);
		//guiFrame.dispose();
		guiFrame = null;
	}

	@Override
	public void processInput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMessageBox(MessageBox msg, UICallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showInventory(InventoryView inv) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showPlayer(PlayerView player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNPC(NPCView pawn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActions(List<Action> actions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showTargetSelect(Collection<TargetView<?>> targets, boolean multi, UICallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void promptRoll(Attributes suggestedAttrib, PlayerView player, String desc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void promptRoll(Attributes suggestedAttrib, NPCView npc, String desc) {
		// TODO Auto-generated method stub
		
	}
	
	///////////////////////////////////////////  
    //          UI Control Methods           //
    ///////////////////////////////////////////
	public void goTemplateEditor() {
		
	}
	
	public void goLoadSession() {
		
	}
	
	public void goNewSession() {
		
	}
	
	public void goGameScreen() {
		
	}
	
	// Screen before actual play
	// Where players can join and PCs get made
	public void goSessionScreen(GameSession session) {
		
	}
	
	public void goCharacterCreation(GameSession returnSession) {
		
	}
	
	
}
