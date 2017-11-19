package com.smanzana.dungeonmaster.ui.app.swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.pawn.PlayerClass;
import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.session.SessionBase;
import com.smanzana.dungeonmaster.session.datums.ClassDatumData;
import com.smanzana.dungeonmaster.ui.app.AppUI;
import com.smanzana.dungeonmaster.ui.app.AppUIColor;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.templateeditor.EmbeddedEditor;
import com.smanzana.templateeditor.editor.IEditor;

// Screen for managing creation and delegation of players
public class PlayerManagementScreen extends JPanel implements ActionListener {
	
	public static class PlayerCreationOptions {
		// Map class name to description
		private Map<String, String> classes;
		
		public PlayerCreationOptions(List<String> classes, List<String> descriptions) {
			this.classes = new LinkedHashMap<>();
			for (int i = 0; i < classes.size(); i++) {
				this.classes.put(classes.get(i), descriptions.get(i));
			}
		}
		
		public Map<String, String> getClasses() {
			return classes;
		}
	}
	
	private class CreatedScreen extends JPanel {
		
		public CreatedScreen(PlayerStatus status) {
			super();
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			this.add(Box.createRigidArea(new Dimension(0, 50)));
			JLabel label = new JLabel("New Player Created");
			AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			castSize(label);
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(0, 50)));
			
			label = new JLabel("Connect a client (via webbrowser pointed to " +
					ui.getLocalIP() + " (local) or " + ui.getExternalIP()
					+ " (external) and provide the key: " + status.getKey());
			AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			castSize(label);
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(0, 50)));
			
			label = new JLabel("OR");
			AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			castSize(label);
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(0, 50)));
			
			JButton button = new JButton("Edit Locally");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					edit(status);
				}
			});
			label.add(button);
			this.add(Box.createRigidArea(new Dimension(0, 50)));
			this.add(Box.createVerticalGlue());
		}
	}
	
	private static class PlayerStatusRenderer extends JPanel implements ListCellRenderer<PlayerStatus> {
		
		private static final Border selectedBorder = BorderFactory.createLineBorder(Color.GREEN, 2);
		private static final Border regularBorder = BorderFactory.createLineBorder(Color.BLACK);
		
		private JLabel name;
		private JLabel race;
		private JLabel clazz;
		
		public PlayerStatusRenderer() {
			super();
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			this.add(Box.createRigidArea(new Dimension(0, 5)));
			name = new JLabel();
			name.setFont(name.getFont().deriveFont(Font.BOLD));
			this.add(name);
			this.add(Box.createRigidArea(new Dimension(0, 5)));
			race = new JLabel();
			this.add(race);
			this.add(Box.createRigidArea(new Dimension(0, 5)));
			clazz = new JLabel();
			this.add(clazz);
			this.add(Box.createRigidArea(new Dimension(0, 5)));
			this.add(Box.createVerticalGlue());
			
			setBackground(Color.DARK_GRAY);
		}
		
		@Override
		public Component getListCellRendererComponent(JList<? extends PlayerStatus> arg0, PlayerStatus arg1,
				int arg2, boolean arg3, boolean arg4) {
			
			name.setText(arg1.getClassName());
			race.setText(arg1.getRace());
			clazz.setText(arg1.getClassName());
			
			this.setOpaque(true);
			this.setBorder(arg3 ? selectedBorder : regularBorder);
			
			return this;
		}
	}
	
	private class PlayerStatus {
		private int editKey;
		private Player player;
		
		public PlayerStatus() {
			this(new Player());
		}
		
		public PlayerStatus(Player player) {
			this.player = player;
			editKey = rand.nextInt();
		}
		
		public String getName() {
			return player.getName();
		}
		
		public String getRace() {
			return player.getRace();
		}
		
		public String getClassName() {
			return player.getPlayerClass().getName();
		}
		
		public int getKey() {
			return editKey;
		}
		
		public void update(String name, String race, String background, String className) {
			player.setName(name);
			player.setRace(race);
			player.setBackground(background);
			ClassDatumData classData = session.lookupClass(className);
			if (classData != null)
				player.setPlayerClass(new PlayerClass(classData));
		}
	}
	
	private static final Random rand = new Random();
	// link to UI for screen switching
	private AppUI ui;
	// Current session being managed
	private GameSession session;
	// List of player statuses
	private JList<PlayerStatus> playerList;
	// Model for playerList for easy insertion
	private DefaultListModel<PlayerStatus> playerListModel;
	// Main editor panel
	private JPanel editorPanel;
	// Current editor, if any
	private EmbeddedEditor<?> currentEditor;
	// Nice handy link to creation options
	private PlayerCreationOptions options;
	// Done button, which updates depending on status
	private JButton doneButton;
	
	public PlayerManagementScreen(AppUI ui, GameSession session) {
		super(new BorderLayout());
		this.ui = ui;
		this.session = session;
		List<String> clist = new LinkedList<>(),
					 dlist = new LinkedList<>();
		for (ClassDatumData d : session.getAllClasses()) {
			clist.add(d.getName());
			dlist.add(d.getDescription());
		}
		this.options = new PlayerCreationOptions(clist, dlist);
	}
		
	public void init() {
		editorPanel = new JPanel(new BorderLayout());
		AppUIColor.setColors(editorPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		
		playerListModel = new DefaultListModel<>();
		if (!session.getAllPlayers().isEmpty())
		for (Player player : session.getAllPlayers()) {
			playerListModel.addElement(new PlayerStatus(player));
		}
		playerList = new JList<>(playerListModel);
		AppUIColor.setColors(playerList, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		playerList.setPreferredSize(new Dimension(200, Short.MAX_VALUE));
		castSize(playerList);
		playerList.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		playerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() != 2)
					return;
				
				actionPerformed(new ActionEvent(playerList.getSelectedValue(),
						0, "?select"));
			}
		});
		playerList.setCellRenderer(new PlayerStatusRenderer());
		this.add(playerList, BorderLayout.WEST);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		AppUIColor.setColors(bottomPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		bottomPanel.add(Box.createHorizontalGlue());
		JButton button = new JButton("Create");
		button.setActionCommand("?create");
		button.addActionListener(this);
		bottomPanel.add(button);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		doneButton = new JButton("Done");
		doneButton.setActionCommand("?done");
		doneButton.addActionListener(this);
		bottomPanel.add(doneButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		this.add(bottomPanel, BorderLayout.SOUTH);
		
		updateDoneButton();
	}
	
	private void castSize(JComponent comp) {
		comp.setMaximumSize(comp.getPreferredSize());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		switch (command) {
		case "?select":
		{
			PlayerStatus selected = (PlayerStatus) e.getSource();
			select(selected);
		}
			break;
		case "?done":
			updateDoneButton();
			if (doneButton.isEnabled())
				commitAndReturn();
			break;
		case "?create":
			create();
			break;
		}
	}
	
	private void create() {
		closeEditor();
		PlayerStatus status = new PlayerStatus();
		playerListModel.addElement(status);
		
		showCreateScreen(status);
	}
	
	private void closeEditor() {
		if (currentEditor != null) {
			currentEditor.commit();
			editorPanel.removeAll();
		}
		
		currentEditor = null;
	}
	
	private void showCreateScreen(PlayerStatus status) {
		// Show the 'you made a new one. Tell them this code!' screen
		editorPanel.add(new CreatedScreen(status), BorderLayout.CENTER);
	}
	
	// Gather up player info, store in session, and then return
	// to session screen
	private void commitAndReturn() {
		Oops! Need to change how connections happen:
		Connection should be accepted and given a page
		by the hook. Then that should be submitted
		and filtered, comm created, etc. Missed a step!
	}
	
	private void select(PlayerStatus selected) {
		String display = path;
		int fieldlen = nameField.getColumns();
		if (path.length() > fieldlen) {
			int pos = path.length() - (fieldlen - 3);
			display = "..." + path.substring(pos);
			
			System.out.println("path: [" + path + "]");
			System.out.println("pos: " + pos);
			System.out.println("display: " + display);
		}
		
		nameField.setText(display);
		sessionPath = path;
		
		updateSubmitButton();
	}
	
	private void edit(PlayerStatus status) {
		
	}
	
	private void updateDoneButton() {
		doneButton.setEnabled(true);
		// TODO
	}
}
