package com.smanzana.dungeonmaster.ui.app.swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.session.datums.ClassDatumData;
import com.smanzana.dungeonmaster.session.datums.data.InventoryData;
import com.smanzana.dungeonmaster.ui.app.AppUI;
import com.smanzana.dungeonmaster.ui.app.AppUIColor;
import com.smanzana.dungeonmaster.ui.common.PlayerView;
import com.smanzana.templateeditor.EmbeddedEditor;
import com.smanzana.templateeditor.IEditorOwner;

/**
 *  Screen for creating a player including summary details and inventroy.
 * In standalone, displayed. In server mode, embedded in PlayerManagementScreen
 */
public class PlayerCreationScreen extends JPanel implements IEditorOwner,
	ActionListener {
	
	private static final long serialVersionUID = -7751194547382314908L;
	// link to UI for screen switching (null if embedded)
	private AppUI ui;
	// Link to active session for class data & stuff(TM)
	private GameSession session;
	// If not top-level, the screen that owns us (for updates)
	private PlayerManagementScreen parentScreen;
	// When embedded, basic info can be locked as a client is editting it
	private Boolean locked;
	// Actual link to player to edit. May be locked (@locked)
	private PlayerView player;
	// Link to the inventory to edit
	private InventoryData inventory;
	// Lockable elements
	private JLabel lockLabel;
	private JTextField nameField;
	private JTextField raceField;
	private JTextArea backgroundField;
	private JComboBox<String> classField;
	// Editor links
	private EmbeddedEditor<PlayerView> playerEditor;
	private EmbeddedEditor<InventoryData> inventoryEditor;
	private JPanel topPanel; // Houses non-editor fields that can be locked
	
	public PlayerCreationScreen(AppUI ui, GameSession session,
			PlayerManagementScreen parent, Player player) {
		super(new BorderLayout());
		this.ui = ui;
		this.player = new PlayerView(player);
		this.inventory = new InventoryData(player.getInventory());
		this.session = session;
		locked = null;
		if (session != null)
			DungeonMaster.setGameSession(session);
		this.parentScreen = parent;
	}
		
	public void init() {
		GameSession session = this.session;
		if (session == null) {
			session = DungeonMaster.getActiveSession();
		}
		JPanel mainPanel = new JPanel();
		AppUIColor.setColors(mainPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		
		JLabel label;
		label = new JLabel("Player Details");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		castSize(label);
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		topPanel = new JPanel();
		AppUIColor.setColors(topPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
		topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		lockLabel = new JLabel("Unlocked");
		lockLabel.setHorizontalAlignment(JLabel.RIGHT);
		lockLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
		AppUIColor.setColors(lockLabel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		//castSize(lockLabel);
		topPanel.add(lockLabel);
		topPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		
		nameField = new JTextField(player.getName());
		AppUIColor.setColors(nameField, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		nameField.setColumns(15);
		castSize(nameField);
		nameField.setAlignmentX(Box.CENTER_ALIGNMENT);
		topPanel.add(nameField);
		label = new JLabel("Name");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		label.setAlignmentX(Box.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		topPanel.add(label);
		topPanel.add(Box.createRigidArea(new Dimension(0, 25)));

		raceField = new JTextField(player.getRace());
		AppUIColor.setColors(raceField, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		raceField.setColumns(15);
		castSize(raceField);
		raceField.setAlignmentX(Box.CENTER_ALIGNMENT);
		topPanel.add(raceField);
		label = new JLabel("Race");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		label.setAlignmentX(Box.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		topPanel.add(label);
		topPanel.add(Box.createRigidArea(new Dimension(0, 25)));

		backgroundField = new JTextArea(player.getBackground(), 5, 50);
		AppUIColor.setColors(backgroundField, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		backgroundField.setLineWrap(true);
		backgroundField.setWrapStyleWord(true);
		castSize(backgroundField);
		backgroundField.setAlignmentX(Box.CENTER_ALIGNMENT);
		JScrollPane secret = new JScrollPane(backgroundField);
		castSize(secret);
		topPanel.add(secret);
		label = new JLabel("Background");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		label.setAlignmentX(Box.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		topPanel.add(label);
		topPanel.add(Box.createRigidArea(new Dimension(0, 25)));

		classField = new JComboBox<String>();
		for (ClassDatumData s : session.getAllClasses()) {
			classField.addItem(s.getName());
		}
		classField.setSelectedItem(player.getClassName());
		AppUIColor.setColors(classField, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		castSize(classField);
		classField.setAlignmentX(Box.CENTER_ALIGNMENT);
		topPanel.add(classField);
		label = new JLabel("Class");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		label.setAlignmentX(Box.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		topPanel.add(label);
		topPanel.add(Box.createRigidArea(new Dimension(0, 25)));
		topPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		mainPanel.add(topPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		label = new JLabel("Player Stats");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		label.setAlignmentX(Box.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		castSize(label);
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel playerPanel = new JPanel(new BorderLayout());
		AppUIColor.setColors(playerPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		playerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		playerEditor = new EmbeddedEditor<PlayerView>(player, this);
		playerPanel.add(playerEditor.getEditorPanel(), BorderLayout.CENTER);
		
		mainPanel.add(playerPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		label = new JLabel("Inventory");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		label.setAlignmentX(Box.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		castSize(label);
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		JPanel inventoryPanel = new JPanel(new BorderLayout());
		AppUIColor.setColors(inventoryPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		inventoryPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		inventoryEditor = new EmbeddedEditor<InventoryData>(inventory, this);
		inventoryPanel.add(inventoryEditor.getEditorPanel(), BorderLayout.CENTER);
		
		mainPanel.add(inventoryPanel);
		
		if (ui != null) {
			// Need a submit button
			JButton button = new JButton("Submit");
			button.addActionListener(this);
			button.setAlignmentX(Box.CENTER_ALIGNMENT);
			mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
			mainPanel.add(button);
		}
		
		mainPanel.add(Box.createVerticalGlue());
		
		JScrollPane pane = new JScrollPane(mainPanel);
		this.add(pane, BorderLayout.CENTER);
	}
	
	private void castSize(JComponent comp) {
		comp.setMaximumSize(comp.getPreferredSize());
	}
	
	public void commit() {
		commitFields();
		if (playerEditor != null)
			player = playerEditor.commit();
		if (inventoryEditor != null)
			inventory = inventoryEditor.commit();
	}
	
	public void updateLock(boolean locked) {
		if (this.locked != null && locked == this.locked)
			return;
		
		this.locked = locked;
		String key = "";
		if (parentScreen != null) {
			key = " - " + parentScreen.getCurrentEditKey();
		}
		if (locked) {
			// Lock fields
			commitFields();
			lockLabel.setText("Locked" + key);
			lockLabel.setForeground(new Color(50, 0, 10));
		} else {
			// Becoming unlocked
			refreshFields();
			lockLabel.setText("Unlocked" + key);
			lockLabel.setForeground(
					AppUIColor.peek(AppUIColor.Key.BASE_FOREGROUND));
		}
		nameField.setEnabled(!locked);
		raceField.setEnabled(!locked);
		backgroundField.setEnabled(!locked);
		classField.setEnabled(!locked);
	}
	
	private void commitFields() {
		if (locked == null || !locked) {
			player.setName(nameField.getText());
			player.setRace(raceField.getText());
			player.setBackground(backgroundField.getText());
			
			String name = (String) classField.getSelectedItem();
			player.setClassName(name);
			
			player.setClassDesc(
					DungeonMaster.getActiveSession().lookupClass(name).getDescription());
		}
	}
	
	/**
	 * Draws info from playerview into our managed fields.
	 * Does not work when not locked
	 */
	public void refreshFields() {
		if (locked) {
			nameField.setText(player.getName());
			raceField.setText(player.getRace());
			backgroundField.setText(player.getBackground());
			classField.setSelectedItem(player.getClassName());
			topPanel.repaint();
		}
	}
	
	public PlayerView getPlayer() {
		return player;
	}
	
	public InventoryData getInventory() {
		return inventory;
	}

	@Override
	public void dirty() {
		System.out.println("Committing");
		commit();
		commitFields();
		if (parentScreen != null)
			parentScreen.updateCurrentStatus();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Only thing we listen to is the submit button
		System.out.println("Submit edit!");
		System.out.println("Unimplemented");
	}
}
