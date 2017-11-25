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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.pawn.Player.PlayerOverlay;
import com.smanzana.dungeonmaster.pawn.PlayerClass;
import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.session.datums.ClassDatumData;
import com.smanzana.dungeonmaster.ui.Comm;
import com.smanzana.dungeonmaster.ui.app.AppConnectionServer;
import com.smanzana.dungeonmaster.ui.app.AppConnectionServer.AppConnectionHook;
import com.smanzana.dungeonmaster.ui.app.AppUI;
import com.smanzana.dungeonmaster.ui.app.AppUIColor;
import com.smanzana.dungeonmaster.ui.web.WebUI;
import com.smanzana.dungeonmaster.ui.web.html.form.Form;
import com.smanzana.dungeonmaster.ui.web.html.form.Form.FormInterface;
import com.smanzana.dungeonmaster.ui.web.html.form.SwitchboxInput;
import com.smanzana.dungeonmaster.ui.web.html.form.TextInput;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP.HTTPRequest;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP.HTTPResponse;

// Screen for managing creation and delegation of players
public class PlayerManagementScreen extends JPanel implements ActionListener,
	AppConnectionHook {
	
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
		
		private static final long serialVersionUID = -3932990466722712041L;

		public CreatedScreen(PlayerStatus status) {
			super();
			AppUIColor.setColors(this, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
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
			
			label = new JLabel("Connect a web browser (like Firefox of Chrome) to " +
					AppUI.getLocalIP() + " (local) or " + AppUI.getExternalIP()
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
			button.setAlignmentX(Box.CENTER_ALIGNMENT);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					select(status);
				}
			});
			this.add(button);
			this.add(Box.createRigidArea(new Dimension(0, 50)));
			this.add(Box.createVerticalGlue());
		}
	}
	
	private class StartScreen extends JPanel {
		
		private static final long serialVersionUID = -393434354687412041L;

		public StartScreen() {
			super();
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			this.add(Box.createVerticalGlue());
			JLabel label = new JLabel("To get started, add a player below or"
					+ " select one from the left.");
			AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			castSize(label);
			this.add(label);
			this.add(Box.createVerticalGlue());
		}
	}
	
	private static class PlayerStatusRenderer extends JPanel implements ListCellRenderer<PlayerStatus> {
		
		private static final long serialVersionUID = 4181005398572157166L;
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
			this.setPreferredSize(new Dimension(Short.MAX_VALUE, 75));
			
			setBackground(Color.DARK_GRAY);
		}
		
		@Override
		public Component getListCellRendererComponent(JList<? extends PlayerStatus> arg0, PlayerStatus arg1,
				int arg2, boolean arg3, boolean arg4) {
			
			name.setText(arg1.getName());
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
		private boolean taken;
		
		public PlayerStatus() {
			this(new Player());
		}
		
		public PlayerStatus(Player player) {
			this.player = player;
			// TODO make sure unique
			editKey = rand.nextInt(5000);
			this.taken = false;
		}
		
		public boolean isTaken() {
			return taken;
		}

		public void setTaken(boolean taken) {
			this.taken = taken;
		}

		public String getName() {
			return player.getName();
		}
		
		public String getRace() {
			return player.getRace();
		}
		
		public String getClassName() {
			PlayerClass c = player.getPlayerClass();
			if (c == null)
				return "None";
			
			return c.getName();
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
	private PlayerCreationScreen editScreen;
	// Current playerstatus being editted, if any
	private PlayerStatus currentEdittingStatus;
	// Nice handy link to creation options
	private PlayerCreationOptions options;
	// Done button, which updates depending on status
	private JButton doneButton;
	// Map of current people editting statuses
	private Map<PlayerStatus, Comm> comms;
	
	private AppConnectionServer server;
	private Thread serverThread;
	
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
		comms = new HashMap<>();
		this.options = new PlayerCreationOptions(clist, dlist);
		this.server = new AppConnectionServer(this);
		serverThread = new Thread(this.server);
		serverThread.start();
	}
		
	public void init() {
		DungeonMaster.setGameSession(session);
		editorPanel = new JPanel(new BorderLayout());
		AppUIColor.setColors(editorPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		
		JPanel screen = new StartScreen();
		AppUIColor.setColors(screen, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		editorPanel.add(screen, BorderLayout.CENTER);
		this.add(editorPanel, BorderLayout.CENTER);
		
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
				
				if (playerList.getSelectedValue() == null)
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
		if (editScreen != null) {
			updateCurrentStatus();
			editScreen.setVisible(false);
			editorPanel.removeAll();
			editorPanel.validate();
		}
		
		editScreen = null;
		currentEdittingStatus = null;
	}
	
	private void showCreateScreen(PlayerStatus status) {
		// Show the 'you made a new one. Tell them this code!' screen
		editorPanel.add(new CreatedScreen(status), BorderLayout.CENTER);
	}
	
	// Gather up player info, store in session, and then return
	// to session screen
	private void commitAndReturn() {
		
	}
	
	private void select(PlayerStatus selected) {
		edit(selected, selected.isTaken());
		updateDoneButton();
	}
	
	private void edit(PlayerStatus status, boolean locked) {
		closeEditor();
		
		editScreen = new PlayerCreationScreen(null, null, this, status.player);
		editScreen.init();
		editorPanel.removeAll();
		editorPanel.add(editScreen, BorderLayout.CENTER);
		editScreen.setVisible(true);
		editorPanel.validate();
		currentEdittingStatus = status;
	}
	
	private void clientEdit(Comm newComm, PlayerStatus status) {
		System.out.println("Client editting!");
		Form form = new Form("/", "GET");
		form.addInput(new TextInput("name", "Name")
				.noNumbers().min(2).max(20).cols(20));
		form.addInput(new TextInput("race", "Race")
				.noNumbers().min(2).max(10).cols(20));
		form.addInput(new TextInput("background", "Background")
				.noNumbers().min(20).max(1000)
				.rows(5).cols(100));
		form.addInput(new SwitchboxInput("class", new ArrayList<String>(options.classes.keySet())));
		form.setDisplayName("Character Creation");
		form.addFeedback(5, new FormInterface() {
			@Override
			public void refreshData(Map<String, String> data) {
				String val;
				
				val = data.get("name");
				if (val != null)
					status.player.setName(val);

				val = data.get("race");
				if (val != null)
					status.player.setRace(val);

				val = data.get("background");
				if (val != null)
					status.player.setBackground(val);
				
				val = data.get("class");
				if (val != null && session.lookupClass(val) != null)
					status.player.setPlayerClass(new PlayerClass(session.lookupClass(val)));
				
				playerList.repaint();
			}
		});
		
		if (((WebUI) newComm).sendHTML(form))
			comms.put(status, newComm);
		
	}
	
	private void updateDoneButton() {
		doneButton.setEnabled(true);
		// TODO
	}
	
	public void updateCurrentStatus() {
		// Update status as part of an update loop
		editScreen.commit();
		currentEdittingStatus.player.applyOverlay(new PlayerOverlay(
				editScreen.getPlayer()));
		currentEdittingStatus.player.getInventory().replaceWith(
				editScreen.getInventory().toInventory()
				);
		playerList.repaint();
	}

	@Override
	public void connect(int key, Comm newComm) {
		Enumeration<PlayerStatus> it = playerListModel.elements();
		while (it.hasMoreElements()) {
			PlayerStatus status = it.nextElement();
			if (status.isTaken() && status.editKey == key) {
				clientEdit(newComm, status);
				return;
			}
		}
		
		System.err.println("Got an invalid key for a comm!");
		newComm.shutdown();
	}

	@Override
	public int filter(String connectMessage) {
		// "key: KEY"
		if (connectMessage == null || connectMessage.trim().isEmpty()
				|| connectMessage.indexOf('=') == -1)
			return 0;
		
		int foundKey;
		try {
			foundKey = Integer.parseInt(connectMessage.substring(connectMessage.indexOf('=')+1));
		} catch (NumberFormatException e) {
			return 0;
		}
		
		Enumeration<PlayerStatus> it = playerListModel.elements();
		while (it.hasMoreElements()) {
			PlayerStatus status = it.nextElement();
			if (!status.isTaken() && status.editKey == foundKey) {
				status.setTaken(true);
				return foundKey;
			}
		}
		
		return 0;
	}

	@Override
	public String generateConnectionPage() {
		return "<html><head><style>body {    background-color: #" + HTTP.getRGBWord(AppUIColor.peek(AppUIColor.Key.BASE_BACKGROUND)) + ";    color: #" + HTTP.getRGBWord(AppUIColor.peek(AppUIColor.Key.BASE_FOREGROUND)) + ";}h1 {    font-family: Helvetica, Serif, Sans-Serif;    margin-top: 50px;    text-align: center;}h2 {    font-family: Helvetica, Serif, Sans-Serif;    margin-top: 60px;    text-align: center;}.titlebar {color: #" + HTTP.getRGBWord(AppUIColor.peek(AppUIColor.Key.BASE_FOREGROUND)) + ";width: 400px;}p {    width: 500px;    margin-bottom: 50px;    margin-top: 30px;    border: 1px solid white;}#screen_loading {    position: absolute;    top: 0px;    bottom: 0px;    left: 0px;    right: 0px;    z-index: 10000000;    display: none;    background-color: #333333;}#number_error {    text-align: center;    margin-top: 2px;    color: red;    display: none;}</style><script language='JavaScript' type='text/javascript'>function submitKey(key) {    var elem = document.getElementById('form_key');    if (!elem)        return false;    var val = Number(elem.value);    if (isNaN(val)) {        setError('Not a number');        return false;    }            document.getElementById('screen_loading').style.display = 'block';        document.getElementById('form').action =        'http://' + window.location.hostname + ':" + AppConnectionServer.DEFAULT_PORT_LISTEN + "/';        return true;}function setError(error) {    var elem = document.getElementById('number_error');    elem.style.display = 'block';    elem.innerHTML = error;}</script></head><body>    <div id='screen_loading'>        <h2>Loading...</h2>    </div>    <h1>QuestManager</h1><hr class='titlebar' />    <h2>Player Creation</h2>    <center><p>    &nbsp;&nbsp;&nbsp;&nbsp;To get started, get a player template key from the DM. This is the key that's given as soon as    a player is created.    </p></center>        <center>        <form id='form' method='post' action=''>            <input id = 'form_key' type='text' name='key' /><br />            <span id='number_error'></span></br />            <button onclick='submitKey();'>Submit</button></center>                    </form>    </center></body></html>";
	}
	
	@Override
	public String generateRejectionPage() {
		return "<html><head><style>body {    background-color: #" + HTTP.getRGBWord(AppUIColor.peek(AppUIColor.Key.BASE_BACKGROUND)) + ";    color: #" + HTTP.getRGBWord(AppUIColor.peek(AppUIColor.Key.BASE_FOREGROUND)) + ";}h1 {    font-family: Helvetica, Serif, Sans-Serif;    margin-top: 50px;    text-align: center;}h2 {    font-family: Helvetica, Serif, Sans-Serif;    margin-top: 30px;    text-align: center;}p {    width: 500px;    margin-bottom: 50px;    margin-top: 30px;    border: 1px solid white;}#screen_loading {    position: absolute;    top: 0px;    bottom: 0px;    left: 0px;    right: 0px;    z-index: 10000000;    display: none;    background-color: #333333;}#number_error {    text-align: center;    margin-top: 2px;    color: red;    display: none;}</style><script language='JavaScript' type='text/javascript'>function setAction() {    document.getElementById('form').action =        'http://' + window.location.hostname + '/';    return true;}</script></head><body>    <h2>Invalid ID</h2>    <center><p>    &nbsp;&nbsp;&nbsp;&nbsp;That ID is not valid. Check with the DM and try again.</p></center>        <center>        <form id='form' method='get' action=''>            <button onclick='setAction();'>Back</button></center>                    </form>    </center></body></html>";
	}

	@Override
	public HTTPResponse doHook(String URI, HTTPRequest request) {
		for (Comm comm : comms.values()) {
			if (comm instanceof WebUI) {
				if (((WebUI) comm).doHook(URI, request))
					return HTTP.generateBlankResponse();
			}
		}
		
		return null;
	}
}
