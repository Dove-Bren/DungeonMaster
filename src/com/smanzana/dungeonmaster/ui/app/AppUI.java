package com.smanzana.dungeonmaster.ui.app;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.ui.Comm;
import com.smanzana.dungeonmaster.ui.UICallback;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.dungeonmaster.ui.app.swing.screens.PlayerManagementScreen.PlayerCreationOptions;
import com.smanzana.dungeonmaster.ui.common.InventoryView;
import com.smanzana.dungeonmaster.ui.common.MessageBox;
import com.smanzana.dungeonmaster.ui.common.NPCView;
import com.smanzana.dungeonmaster.ui.common.PlayerView;
import com.smanzana.dungeonmaster.ui.common.TargetView;
import com.smanzana.dungeonmaster.ui.web.utils.HTTPHeaders;
import com.smanzana.templateeditor.uiutils.UIColor;

public class AppUI extends Comm {
	
	private static File FILE_COLOR = new File(DungeonMaster.PATH_COLORS);
	private static File FILE_UISTATE = new File(DungeonMaster.PATH_UISTATE);
	
	private AppFrame guiFrame;
	
	public AppUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		if (FILE_COLOR != null && FILE_COLOR.exists())
		try {
			UIColor.loadFromFile(FILE_COLOR);
		} catch (IOException e) {
			System.out.println("Failed to load DM Scheme file.");
		}
		
		if (FILE_UISTATE != null && FILE_UISTATE.exists())
		try {
			UIConfState.loadFromFile(FILE_UISTATE);
		} catch (IOException e) {
			System.out.println("Failed to load DM State file.");
		}
		
		
		guiFrame = new AppFrame(this);
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
		try {
			UIColor.saveToFile(FILE_COLOR);
		} catch (FileNotFoundException e) {
			System.out.println("Failed to save to file " + FILE_COLOR + ": FNF");
		}
		try {
			UIConfState.saveToFile(FILE_UISTATE);
		} catch (FileNotFoundException e) {
			;
		}
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
		guiFrame.setScreen(AppFrame.Screen.TEMPLATE);
	}
	
	public void goLoadSession() {
		guiFrame.setScreen(AppFrame.Screen.LOAD);
	}
	
	public void goNewSession() {
		guiFrame.setScreen(AppFrame.Screen.NEW);
	}
	
	public void goGameScreen() {
		System.out.println("Going to Game Screen");
	}
	
	// Screen before actual play
	// Where players can join and PCs get made
	public void goSessionScreen(GameSession session) {
		System.out.println("Going to Session Screen");
	}
	
	public void goCharacterManagement(GameSession returnSession) {
		guiFrame.setCurrentSession(returnSession);
		guiFrame.setScreen(AppFrame.Screen.PLAYMANAGE);
	}
	
	public void goMainScreen() {
		guiFrame.setScreen(AppFrame.Screen.START);
	}

	@Override
	public void showPlayerCreation(int creationKey, PlayerCreationOptions opts) {
		// TODO Auto-generated method stub
		
	}
	
	public static String getLocalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}
	
	private static String externalIPCache = null;
	
	public static String getExternalIP() {
		if (externalIPCache != null)
			return externalIPCache;
		
		Socket s = null;
		try {
			s = new Socket("api.ipify.org", 80);
			s.setSoTimeout(500);
			PrintWriter writer = new PrintWriter(s.getOutputStream());
			writer.print(HTTPHeaders.generateHeader(true, "/", "api.ipify.org", 0));
			writer.flush();
		} catch (UnknownHostException e) {
			; // continue to try again
			System.err.println("Unknown host: ipecho");
		} catch (IOException e) {
			System.err.println("Could not lookup ipecho for remote address");
			try {s.close();} catch (Exception ex) {};
		}
		
		if (s != null) {
			try {
				externalIPCache = HTTPHeaders.readHTTPResponse(s);
				s.close();
				if (externalIPCache != null)
					return externalIPCache;
			} catch (SocketTimeoutException e) {
				System.err.println("Connection to ipecho timed out");
				try {s.close();} catch (Exception ex) {};
			} catch (IOException e) {
				System.err.println("Read error while fetching remove address");
				try {s.close();} catch (Exception ex) {};
			}
		}
		
		// Try next service: ipify.org
		s = null;
		try {
			s = new Socket("ipecho.net", 80);
			s.setSoTimeout(1500);
			PrintWriter writer = new PrintWriter(s.getOutputStream());
			writer.print(HTTPHeaders.generateHeader(true, "/plain", "ipecho.net", 0));
			writer.flush();
		} catch (UnknownHostException e) {
			; // continue to try again
			System.err.println("Unknown host: ipify");
		} catch (IOException e) {
			System.err.println("Could not lookup ipify for remote address");
			try {s.close();} catch (Exception ex) {};
		}
		
		if (s != null) {
			try {
				externalIPCache = HTTPHeaders.readHTTPResponse(s);
				
				s.close();
				if (externalIPCache != null)
					return externalIPCache;
			} catch (SocketTimeoutException e) {
				System.err.println("Connection to ipify timed out");
				try {s.close();} catch (Exception ex) {};
			} catch (IOException e) {
				System.err.println("Read error while fetching remove address");
				try {s.close();} catch (Exception ex) {};
			}
		}
		
		return "127.0.0.1";
	}
}
