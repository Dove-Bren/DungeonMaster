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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.session.GameSession;
import com.smanzana.dungeonmaster.session.SessionBase;
import com.smanzana.dungeonmaster.ui.app.AppUI;
import com.smanzana.dungeonmaster.ui.app.AppUIColor;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;

// Screen for creating a new session from a template
public class NewScreen extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 4023498297220637890L;
	private static final int SIZE_PATH = 50;

	// link to UI for screen switching
	private AppUI ui;
	// Editable field for typing session name
	private JTextField nameField;
	// Non-editable field displaying template name
	private JTextField templateField;
	// Actual path to template. Not for display
	private String templatePath;
	// List with all template directories we find
	private JList<TemplateDirectory> templateList;
	// Button to open browse dialog
	private JButton templateBrowse;
	// Button to actually make the session
	private JButton createButton;
	
	private static class TemplateDirectoryRenderer extends JLabel implements ListCellRenderer<TemplateDirectory> {
		private static final long serialVersionUID = 5528049648738014428L;
		private static final ImageIcon icon = AppFrame.createImageIcon("icon/datum_open.png");
		@Override
		public Component getListCellRendererComponent(JList<? extends TemplateDirectory> arg0, TemplateDirectory arg1,
				int arg2, boolean arg3, boolean arg4) {
			this.setText(arg1.getDisplay());
			this.setIcon(icon);
			this.setOpaque(true);
			this.setBackground(arg3 ? Color.LIGHT_GRAY : Color.WHITE);
			
			return this;
		}
	}
	
	private static class TemplateDirectory {
		private File file;
		
		public TemplateDirectory(File dir) {
			file = dir;
		}	
		
		public String getDisplay() {
			return file.getName();
		}
	}
	
	public NewScreen(AppUI ui) {
		super(new BorderLayout());
		this.ui = ui;
	}
		
	public void init() {
		JPanel mainPanel = new JPanel();
		AppUIColor.setColors(mainPanel, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_BACKGROUND);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		
		JLabel label;
		label = new JLabel(AppFrame.createImageIcon("title_new.png"));
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		castSize(label);
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 100)));
		
		nameField = new JTextField("Session Name", SIZE_PATH);
		AppUIColor.setColors(nameField, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		nameField.setEditable(true);
		nameField.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateSubmitButton();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				insertUpdate(arg0);
			}
		});
		//nameField.setColumns(SIZE_PATH);
		castSize(nameField);
		mainPanel.add(nameField);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		label = new JLabel("Session Name");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		castSize(label);
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		
		templateField = new JTextField("", SIZE_PATH);
		AppUIColor.setColors(templateField, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		templateField.setEditable(false);
		templateField.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		castSize(templateField);
		mainPanel.add(templateField);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		label = new JLabel("Template");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		castSize(label);
		mainPanel.add(label);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));
		
		createButton = new JButton("Create Session");
		createButton.setEnabled(false);
		createButton.setActionCommand("?create");
		createButton.addActionListener(this);
		createButton.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		//castSize(createButton);
		
		mainPanel.add(createButton);
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
		label = new JLabel("Select Template");
		AppUIColor.setColors(label, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setPreferredSize(new Dimension(
				label.getPreferredSize().width,
				label.getPreferredSize().height + 20
				));
		castSize(label);
		sidePanel.add(label, BorderLayout.NORTH);
		
		DefaultListModel<TemplateDirectory> model = new DefaultListModel<>();
		for (TemplateDirectory dir : listDirectories(new File(DungeonMaster.PATH_TEMPLATES))) {
			model.addElement(dir);
		}
		templateList = new JList<>(model);
		AppUIColor.setColors(templateList, AppUIColor.Key.BASE_FOREGROUND, AppUIColor.Key.BASE_NESTED_BACKGROUND);
		templateList.setPreferredSize(new Dimension(300, Short.MAX_VALUE));
		castSize(templateList);
		templateList.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		templateList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() != 2)
					return;
				
				actionPerformed(new ActionEvent(templateList,
						0, templateList.getSelectedValue().file.getPath()));
			}
		});
		templateList.setCellRenderer(new TemplateDirectoryRenderer());
		sidePanel.add(templateList, BorderLayout.CENTER);
		
		
		templateBrowse = new JButton("Browse");
		templateBrowse.setActionCommand("?browse");
		templateBrowse.addActionListener(this);
		templateBrowse.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		sidePanel.add(templateBrowse, BorderLayout.SOUTH);
		
		sidePanel.add(templateList, BorderLayout.CENTER);
		this.add(sidePanel, BorderLayout.EAST);
	}
	
	private void castSize(JComponent comp) {
		comp.setMaximumSize(comp.getPreferredSize());
	}
	
	private List<TemplateDirectory> listDirectories(File root) {
		List<TemplateDirectory> list = new LinkedList<>();
		
		for (File file : root.listFiles()) {
			if (!file.exists() || !file.isDirectory())
				continue;
			
			if (!isValidDir(file))
				continue;
			
			list.add(new TemplateDirectory(file));
		}
		
		return list;
	}
	
	private boolean isValidDir(File dir) {
		File mechfile = new File(dir, SessionBase.PATH_MECHS);
		return mechfile.exists();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		switch (command) {
		case "?browse":
		{
			JFileChooser fc = new JFileChooser(new File(DungeonMaster.PATH_TEMPLATES));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(getParent());
			
			File sel = fc.getSelectedFile();
			if (sel != null) {
				if (!sel.exists())
					JOptionPane.showMessageDialog(getParent(), "Could not open the selected template: That folder does not exist.", "Error opening template", JOptionPane.PLAIN_MESSAGE);
				else if (!sel.isDirectory())
					JOptionPane.showMessageDialog(getParent(), "Could not open the selected template: That is not a directory.", "Error opening template", JOptionPane.PLAIN_MESSAGE);
				else if (!isValidDir(sel)) {
					JOptionPane.showMessageDialog(getParent(), "Could not open the selected template: The selected directory does not appear to be a valid template.", "Error opening template", JOptionPane.PLAIN_MESSAGE);
				} else {
					// oh well just make one wherever it is
					select(sel.getPath());
				}
			}
		}
			break;
		case "?create":
		{
			File file = new File(templatePath);
			if (!file.exists() || !file.isDirectory()) {
				JOptionPane.showMessageDialog(getParent(), "Could not open the selected template: Select another and try again", "Error opening template", JOptionPane.PLAIN_MESSAGE);
				break;
			}
			
			createFromFile(file);
			return;
		}
		case "?back":
			ui.goMainScreen();
			break;
		default:
			// name is name of the path
			select(command);
			break;
		}
	}
	
	private void select(String path) {
		String display = path;
		int fieldlen = templateField.getColumns();
		if (path.length() > fieldlen) {
			int pos = path.length() - (fieldlen - 3);
			display = "..." + path.substring(pos);
			
			System.out.println("path: [" + path + "]");
			System.out.println("pos: " + pos);
			System.out.println("display: " + display);
		}
		
		templateField.setText(display);
		templatePath = path;
		
		updateSubmitButton();
	}
	
	private void updateSubmitButton() {
		createButton.setEnabled(templatePath != null
				&& !templatePath.trim().isEmpty()
				&& isNameOK(nameField.getText()));
	}
	
	private boolean isNameOK(String name) {
		if (name == null || name.trim().isEmpty())
			return false;
		
		if (name.contains("/") || name.contains("\\") || name.contains(":")
				|| name.contains("*") || name.contains("?") || name.contains("\"")
				|| name.contains("<") || name.contains(">") || name.contains("|"))
			return false;
		
		return true;
	}
	
	// Called when actually selecting and going to create a new template
	private void createFromFile(File file) {
		File outfile = new File(DungeonMaster.PATH_SESSIONS, nameField.getText());
		
		if (outfile.exists()) {
			JOptionPane.showMessageDialog(getParent(), "A session named " + nameField.getText() + " already exists. Select a new name.", "Error creating session", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		GameSession session = new GameSession(file);
		session.save(outfile);
		
		session.addNote("Created from template " + file.getName());
		
		ui.goSessionScreen(session);
	}
	
}
