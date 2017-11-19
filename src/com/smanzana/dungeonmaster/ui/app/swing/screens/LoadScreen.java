package com.smanzana.dungeonmaster.ui.app.swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

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

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.session.SessionBase;
import com.smanzana.dungeonmaster.ui.app.AppUI;
import com.smanzana.dungeonmaster.ui.app.AppUIColor;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;

// Screen for loading an existing session
public class LoadScreen extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -5663285800053540191L;

	private static final int SIZE_PATH = 50;

	// link to UI for screen switching
	private AppUI ui;
	// Editable field for typing session name
	private JTextField nameField;
	// Current path that will be loaded
	private String sessionPath;
	// List with all template directories we find
	private JList<SessionDirectory> sessionList;
	// Button to actually load
	private JButton loadButton;
	
	private static class TemplateDirectoryRenderer extends JLabel implements ListCellRenderer<SessionDirectory> {
		private static final long serialVersionUID = 4721972206780176566L;
		private static final ImageIcon icon = AppFrame.createImageIcon("icon/datum_open.png");
		@Override
		public Component getListCellRendererComponent(JList<? extends SessionDirectory> arg0, SessionDirectory arg1,
				int arg2, boolean arg3, boolean arg4) {
			this.setText(arg1.getDisplay());
			this.setIcon(icon);
			this.setOpaque(true);
			this.setBackground(arg3 ? Color.LIGHT_GRAY : Color.WHITE);
			
			return this;
		}
	}
	
	private static class SessionDirectory {
		private File file;
		
		public SessionDirectory(File dir) {
			file = dir;
		}	
		
		public String getDisplay() {
			return file.getName();
		}
	}
	
	public LoadScreen(AppUI ui) {
		super(new BorderLayout());
		this.ui = ui;
	}
		
	public void init() {
		JPanel mainPanel = new JPanel();
		AppUIColor.setColors(mainPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		
		JLabel label;
		label = new JLabel(AppFrame.createImageIcon("title_load.png"));
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		castSize(label);
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 100)));
		
		nameField = new JTextField("", SIZE_PATH);
		nameField.setEditable(false);
		nameField.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		AppUIColor.setColors(nameField, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		castSize(nameField);
		mainPanel.add(nameField);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		label = new JLabel("Session Name");
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		castSize(label);
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		
		loadButton = new JButton("Load Session");
		loadButton.setEnabled(false);
		loadButton.setActionCommand("?load");
		loadButton.addActionListener(this);
		loadButton.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		mainPanel.add(loadButton);
		mainPanel.add(Box.createVerticalGlue());
		
		JButton back = new JButton("Back");
		back.setActionCommand("?back");
		back.addActionListener(this);
		back.setEnabled(true);
		back.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		mainPanel.add(back);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		
		this.add(mainPanel, BorderLayout.CENTER);
		
		JPanel sidePanel = new JPanel(new BorderLayout());
		AppUIColor.setColors(sidePanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		sidePanel.setBorder(BorderFactory.createEtchedBorder());
		label = new JLabel("Select Session");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setPreferredSize(new Dimension(
				label.getPreferredSize().width,
				label.getPreferredSize().height + 20
				));
		castSize(label);
		sidePanel.add(label, BorderLayout.NORTH);
		
		DefaultListModel<SessionDirectory> model = new DefaultListModel<>();
		for (SessionDirectory dir : listDirectories(new File(DungeonMaster.PATH_SESSIONS))) {
			model.addElement(dir);
		}
		sessionList = new JList<>(model);
		AppUIColor.setColors(sessionList, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		sessionList.setPreferredSize(new Dimension(300, Short.MAX_VALUE));
		castSize(sessionList);
		sessionList.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		sessionList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() != 2)
					return;
				
				actionPerformed(new ActionEvent(sessionList,
						0, sessionList.getSelectedValue().file.getPath()));
			}
		});
		sessionList.setCellRenderer(new TemplateDirectoryRenderer());
		sidePanel.add(sessionList, BorderLayout.CENTER);
		
		
		sidePanel.add(sessionList, BorderLayout.CENTER);
		this.add(sidePanel, BorderLayout.WEST);
	}
	
	private void castSize(JComponent comp) {
		comp.setMaximumSize(comp.getPreferredSize());
	}
	
	private List<SessionDirectory> listDirectories(File root) {
		List<SessionDirectory> list = new LinkedList<>();
		
		for (File file : root.listFiles()) {
			if (!file.exists() || !file.isDirectory())
				continue;
			
			if (!isValidDir(file))
				continue;
			
			list.add(new SessionDirectory(file));
		}
		
		return list;
	}
	
	private boolean isValidDir(File dir) {
		File mechfile = new File(new File(dir, GameSession.PATH_CONFIG), SessionBase.PATH_MECHS);
		return mechfile.exists();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		switch (command) {
		case "?load":
		{
			File file = new File(sessionPath);
			if (!file.exists() || !file.isDirectory()) {
				JOptionPane.showMessageDialog(getParent(), "Could not open the selected session: Select another and try again", "Error opening session", JOptionPane.PLAIN_MESSAGE);
				break;
			}
			
			loadFromFile(file);
			return;
		}
		case "?back":
			ui.goMainScreen();
			break;
		default:
			select(command);
			break;
		}
	}
	
	private void select(String path) {
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
	
	private void updateSubmitButton() {
		loadButton.setEnabled(sessionPath != null
				&& !sessionPath.trim().isEmpty());
	}
	
	// Called when actually selecting and going to create a new template
	private void loadFromFile(File file) {
		if (!file.exists()) {
			JOptionPane.showMessageDialog(getParent(), "Could not find the session " + nameField.getText() + ".", "Error loading session", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		GameSession session = new GameSession(file);
		
		ui.goSessionScreen(session);
	}
	
}
