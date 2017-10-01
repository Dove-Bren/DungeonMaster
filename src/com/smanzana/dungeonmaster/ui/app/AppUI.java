package com.smanzana.dungeonmaster.ui.app;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;

public class AppUI {
	
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
}
