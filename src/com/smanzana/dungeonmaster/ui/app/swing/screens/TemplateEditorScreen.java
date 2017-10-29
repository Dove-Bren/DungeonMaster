package com.smanzana.dungeonmaster.ui.app.swing.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.maker.SessionTemplate;
import com.smanzana.dungeonmaster.session.configuration.CombatBonusConfig;
import com.smanzana.dungeonmaster.session.configuration.Config;
import com.smanzana.dungeonmaster.session.configuration.KeywordConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.RollTableConfig;
import com.smanzana.dungeonmaster.session.datums.Datum;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.ui.app.AppUI;
import com.smanzana.dungeonmaster.ui.app.UIConfState;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.api.ObjectDataLoader;
import com.smanzana.templateeditor.data.SimpleFieldData;
import com.smanzana.templateeditor.editor.EnumMapEditor;
import com.smanzana.templateeditor.editor.IEditor;
import com.smanzana.templateeditor.uiutils.UIColor;

public class TemplateEditorScreen extends JPanel implements ActionListener, IEditorOwner {
	
	private static enum Command {
		// FILE
		NEW("new"),
		OPEN("open"),
		OPENLAST("open last"),
		SAVE("save"),
		SAVEAS("saveas"),
		CLOSE("close"),
		QUIT("quit"),
		MAINMENU("mainmenu"),
		
		// EDIT
		COLORS("colors"),
		
		//
		;
		
		private String command;
		
		private Command(String command) {
			this.command = command;
		}
		
		@Override
		public String toString() {
			return command;
		}
		
		public String getCommand() {
			return command;
		}
		
		public static Command lookup(String command) {
			for (Command cmd : Command.values())
				if (cmd.command.equals(command))
					return cmd;
			
			return null;
		}
	}
	
	private AppUI ui;
	private SessionTemplate currentTemplate;
	
	// GUI members
	private JMenuBar menubar;
	private JTree sourceTree;
	private DefaultTreeModel sourceModel;
	private JScrollPane sourcePanel;
	private JPanel editorPanel;
	private IEditor<?> currentEditor;
	private ObjectDataLoader<?> currentLoader;
	private Map<Command, JMenuItem> menuItems;
	private Object lastEditorObject;
	private boolean dirty;
	
	public TemplateEditorScreen(AppUI UI) {
		super(new BorderLayout());
		currentTemplate = null;
		currentEditor = null;
		lastEditorObject = null;
		menuItems = new EnumMap<>(Command.class);
		this.ui = UI;
		dirty = false;
	}
	
	public void init() {
		
		menubar = new JMenuBar();
		JMenu menu;
		JMenuItem item;
		
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		
		item = new JMenuItem("New");
		item.setMnemonic(KeyEvent.VK_N);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.CTRL_MASK
				));
		item.setActionCommand(Command.NEW.getCommand());
		item.addActionListener(this);
		menu.add(item);
		menuItems.put(Command.NEW, item);
		
		item = new JMenuItem("Open");
		item.setMnemonic(KeyEvent.VK_O);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK
				));
		item.setActionCommand(Command.OPEN.getCommand());
		item.addActionListener(this);
		menu.add(item);
		menuItems.put(Command.OPEN, item);
		
		item = new JMenuItem("Open Last");
		//item.setMnemonic(KeyEvent.VK_L);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK
				));
		item.setActionCommand(Command.OPENLAST.getCommand());
		item.addActionListener(this);
		menu.add(item);
		menuItems.put(Command.OPENLAST, item);
		
		item = new JMenuItem("Save");
		item.setMnemonic(KeyEvent.VK_S);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK
				));
		item.setActionCommand(Command.SAVE.getCommand());
		item.addActionListener(this);
		menu.add(item);
		menuItems.put(Command.SAVE, item);
		
		item = new JMenuItem("Save As");
		item.setMnemonic(KeyEvent.VK_A);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK
				));
		item.setActionCommand(Command.SAVEAS.getCommand());
		item.addActionListener(this);
		menu.add(item);
		menuItems.put(Command.SAVEAS, item);
		
		item = new JMenuItem("Close");
		item.setMnemonic(KeyEvent.VK_C);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_W, ActionEvent.CTRL_MASK
				));
		item.setActionCommand(Command.CLOSE.getCommand());
		item.addActionListener(this);
		menu.add(item);
		menuItems.put(Command.CLOSE, item);
		
		item = new JMenuItem("Main Menu");
		item.setMnemonic(KeyEvent.VK_M);
		item.setActionCommand(Command.MAINMENU.getCommand());
		item.addActionListener(this);
		menu.add(item);
		menuItems.put(Command.MAINMENU, item);
		
		item = new JMenuItem("Quit");
		item.setMnemonic(KeyEvent.VK_Q);
		item.setActionCommand(Command.QUIT.getCommand());
		item.addActionListener(this);
		menu.add(item);
		menuItems.put(Command.QUIT, item);
		
		menubar.add(menu);
		
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		item = UIColor.createMenuItem("Colors");
		item.setMnemonic(KeyEvent.VK_R);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK
				));
		menuItems.put(Command.COLORS, item);
		
		menu.add(item);
		menubar.add(menu);
		
		this.getRootPane().setJMenuBar(menubar);
		
		sourceModel = new DefaultTreeModel(new DefaultMutableTreeNode("template"));
		sourceTree = new JTree(sourceModel);
		//sourceTree.addTreeSelectionListener(this);
		sourceTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e);
			}
		});
		//sourceTree.setBackground(Color.WHITE);
		UIColor.setColors(sourceTree, UIColor.Key.EDITOR_LIST_FOREGROUND, UIColor.Key.EDITOR_LIST_BACKGROUND);
		sourceTree.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = -205095548994473885L;
			private ImageIcon configIcon;
			private ImageIcon datumIcon;
			private ImageIcon datumOpenIcon;
			private ImageIcon dataIcon;
			private boolean fetchedIcons;
			
			@Override
			public Color getBackgroundNonSelectionColor() {
				return null;
			}
			
			public Component getTreeCellRendererComponent(
                    JTree tree,
                    Object value,
                    boolean sel,
                    boolean expanded,
                    boolean leaf,
                    int row,
                    boolean hasFocus) {
			    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			    
			    if (fetchedIcons == false) {
			    	//final int scale = 24;
			    	configIcon = AppFrame.createImageIcon("icon/config.png");
			    	//configIcon = new ImageIcon(configIcon.getImage().getScaledInstance(scale, scale, 0));
			    	datumIcon = AppFrame.createImageIcon("icon/datum.png");
			    	//datumIcon = new ImageIcon(datumIcon.getImage().getScaledInstance(scale, scale, 0));
			    	datumOpenIcon = AppFrame.createImageIcon("icon/datum_open.png");
			    	//datumOpenIcon = new ImageIcon(datumOpenIcon.getImage().getScaledInstance(scale, scale, 0));
			    	dataIcon = AppFrame.createImageIcon("icon/data.png");
			    	//dataIcon = new ImageIcon(dataIcon.getImage().getScaledInstance(scale, scale, 0));
			    	fetchedIcons = true;
			    	
			    	//this.setMinimumSize(new Dimension(scale, scale));
			    	//this.setPreferredSize(new Dimension(scale, scale));
			    }
			    
			    Object obj = null;
			    if (value != null && (value instanceof DefaultMutableTreeNode)) {
			    	obj = ((DefaultMutableTreeNode) value).getUserObject();
			    }
			    
			    if (obj instanceof Config) {
			    	Config<?> conf = (Config<?>) obj;
			        setText(conf.getDisplayName());
			        setToolTipText(conf.getDisplayTooltip());
			        
			        if (configIcon != null)
			    		setIcon(configIcon);
			    } else if (obj instanceof Datum<?>) {
			    	Datum<?> datum = (Datum<?>) obj;
			    	setText(datum.getDisplayName());
			        setToolTipText(datum.getDisplayTooltip());
			        
			        if (datumIcon != null && datumOpenIcon != null)
			    		setIcon(sourceTree.isExpanded(new TreePath(((DefaultMutableTreeNode) value).getPath()))
			    				? datumOpenIcon : datumIcon);
			    } else if (obj instanceof DatumData) {
			    	DatumData data = (DatumData) obj;
			    	setText(data.getDisplayName());
			    	setToolTipText(data.getDisplayTooltip());
			    	
			    	if (dataIcon != null)
			    		setIcon(dataIcon);
			    }
			
			    return this;
			}
		});
		ToolTipManager.sharedInstance().registerComponent(sourceTree);
		sourcePanel = new JScrollPane(sourceTree);
		sourcePanel.setPreferredSize(new Dimension(250, 1000));
		sourcePanel.setMaximumSize(new Dimension(250, 5000));
		sourcePanel.setBorder(BorderFactory.createEtchedBorder());
		//sourcePanel.setBackground(Color.WHITE);
		UIColor.setColors(sourcePanel, UIColor.Key.EDITOR_LIST_FOREGROUND, UIColor.Key.EDITOR_LIST_BACKGROUND);
		this.add(sourcePanel, BorderLayout.LINE_START);		
		
		editorPanel = new JPanel(new BorderLayout());
		//editorPanel.setBackground(Color.CYAN); //// donotcheckin
		UIColor.setColors(editorPanel, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		this.add(editorPanel, BorderLayout.CENTER);
		
		updateTree();
		updateMenu();
		this.validate();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("Got command " + arg0.getActionCommand());
		Command cmd = Command.lookup(arg0.getActionCommand());
		if (cmd == null) {
			System.out.println("Unhandled command: " + arg0.getActionCommand());
			return;
		}
		
		switch (cmd) {
		case NEW:
			{
				if (!clearEditor()) {
					break;
				}
				String name = JOptionPane.showInputDialog(getParent(), "Enter the name of the new template", "New Template", JOptionPane.PLAIN_MESSAGE);
				while (name != null) {
					if (!isValidTemplateName(name))
						name = JOptionPane.showInputDialog(getParent(), "Please enter a valid template name", "New Template", JOptionPane.PLAIN_MESSAGE);
					else if (isValidTemplateDir(name))
						name = JOptionPane.showInputDialog(getParent(), "A template already exists with that name. Enter a different name for the new template", "New Template", JOptionPane.PLAIN_MESSAGE);
					else
						break;
				}
				if (name == null) {
					System.out.println("New template cancelled");
					break;
				}
				
				openTemplate(name);
			}
			break;
		case CLOSE:
			clearEditor();
			break;
		case OPEN:
			{
				if (!clearEditor()) {
					break;
				}
				
				JFileChooser fc = new JFileChooser(new File(DungeonMaster.PATH_TEMPLATES));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(getParent());
				
				File sel = fc.getSelectedFile();
				if (sel != null) {
					if (!sel.exists())
						JOptionPane.showMessageDialog(getParent(), "Could not open the selected template: That folder does not exist", "Error opening template", JOptionPane.PLAIN_MESSAGE);
					else if (!sel.isDirectory())
						JOptionPane.showMessageDialog(getParent(), "Could not open the selected template: That is not a directory", "Error opening template", JOptionPane.PLAIN_MESSAGE);
					else {
						// oh well just make one wherever it is
						openTemplate(sel);
					}
				}
			}
			break;
		case OPENLAST:
			File sel = new File(UIConfState.instance().get(UIConfState.Key.LASTTEMPLATE));
			openTemplate(sel);
			break;
		case MAINMENU:
			if (!clearEditor()) {
				break;
			}
			ui.goMainScreen();
			break;
		case QUIT:
			if (!clearEditor()) {
				break;
			}
			DungeonMaster.shutdown();
			break;
		case SAVE:
			saveEditor();
			currentTemplate.save();
			break;
		case SAVEAS:
			{
				saveEditor();
				JFileChooser fc = new JFileChooser(new File(DungeonMaster.PATH_TEMPLATES));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				fc.showSaveDialog(getParent());
				File loc = fc.getSelectedFile();
				
				if (loc == null) {
					System.out.println("Save-As template cancelled");
					break;
				}
				
				currentTemplate.save(loc);
				updateTree();
			}
			break;
		case COLORS:
			; // Nothing to do; Shouldn't be handled by templateEditor
			break;
		}
		
		updateMenu();
	}
	
	private boolean isValidTemplateName(String name) {
		if (name == null || name.trim().isEmpty())
			return false;
		
		if (name.contains("<") ||
			name.contains(">") ||
			name.contains(":") ||
			name.contains("/") ||
			name.contains("\\") ||
			name.contains("|") ||
			name.contains("*") ||
			name.contains("?") ||
			name.contains("\"")) 
			return false;
		
		return true;
	}
	
	private boolean isValidTemplateDir(String name) {
		File templateDir = new File(DungeonMaster.PATH_TEMPLATES);
		if (!templateDir.exists())
			templateDir.mkdirs();
		
		File target = new File(templateDir, name);
		if (!target.exists())
			return false;
		
		// TODO more
		return true;
	}
	
	// Returns true if editor was successfully cleared.
	// False means something's still open so don't close or open anything new
	private boolean clearEditor() {
		return clearEditor(false);
	}
	
	private boolean clearEditor(boolean force) {
	
		if (currentTemplate == null)
			return true;
		
		saveEditor();
		
		if (force) {
			// Write out a force temp file to let them know next time
			File tmp = new File(DungeonMaster.PATH_TEMPLATES, ".badclose");
			if (!tmp.exists())
				try {
					tmp.createNewFile();
				} catch (IOException e) {
					System.out.println("Could not create .badclose file");
				}
			
			String backupName = currentTemplate.getRoot().getName();
			backupName += ".backup";
			String root = backupName;
			int suffix = 0;
			while (isValidTemplateDir(backupName)) {
				suffix++;
				backupName = root + suffix;
			}
			
			this.currentTemplate.save(new File(DungeonMaster.PATH_TEMPLATES, backupName));
			return true;
		}
		
		if (!currentTemplate.isDirty()) {
			currentTemplate = null;
			clearEditorPanel();
			return true;
		}
		
		// No rush. Ask if they want to save, discard, or cancel
		int selection = JOptionPane.showConfirmDialog(getParent(), "Save changes before closing?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
		if (selection == JOptionPane.CANCEL_OPTION)
			return false;
		if (selection == JOptionPane.YES_OPTION)
			currentTemplate.save();
		
		clearEditorPanel();
		
		return true;
	}
	
	// Also clears out source panel hehe
	private void clearEditorPanel() {
		if (currentEditor != null)
			currentEditor.getComponent().setVisible(false);
		currentEditor = null;
		updateEditor();
		updateTree();
	}
	
	private void saveEditor() {
		// Pulls data back from the editor into our source object
		if (currentEditor == null)
			return;
		
		if (currentLoader == null) {
			// we have to iterate ourselvses. Currently this only means config
			toConfig(currentEditor.fetchData());
		} else {
			this.lastEditorObject = currentLoader.fetchEdittedValue();
		}
	}
	
	// Doesn't care if exists or not; just opens. Commands should
	// handle whether it should or shouldn't
	private boolean openTemplate(String name) {
		File templateDir = new File(DungeonMaster.PATH_TEMPLATES);
		if (!templateDir.exists())
			templateDir.mkdirs();
		
		File target = new File(templateDir, name);
		return openTemplate(target);
	}
	
	private boolean openTemplate(File target) {
		SessionTemplate template;
		try {
			template = new SessionTemplate(target);
		} catch (Exception e) {
			System.out.println("Failed to open template: " + target.getPath());
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(getParent(), "Failed to open template \"" + target.getPath() + "\": "
					+ e.getMessage() + ". Error details can be found in the console.");
			return false;
		}
		
		this.currentTemplate = template;
		
		updateTree();
		
		// testing
		//openEditor(new ConfigEditor(currentTemplate, MechanicsConfig.instance()));
		//testing
		
		UIConfState.instance().set(UIConfState.Key.LASTTEMPLATE, target.getPath());
		
		return true;
	}
	
	// Updates menu items given the current state of our template
	private void updateMenu() {
		// Save & Save as need a template
		menuItems.get(Command.SAVE).setEnabled(currentTemplate != null);
		menuItems.get(Command.SAVEAS).setEnabled(currentTemplate != null);
		menuItems.get(Command.OPENLAST).setEnabled(UIConfState.instance().get(UIConfState.Key.LASTTEMPLATE) != null);
	}
	
	private void updateTree() {
		
		if (currentTemplate == null) {
			sourceTree.setVisible(false);
			sourcePanel.revalidate();
			return;
		}
		
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) sourceModel.getRoot(); 
		root.setUserObject(currentTemplate.getRoot().getName());
		root.removeAllChildren();
		
		addConfigToTree(root, CombatBonusConfig.instance());
		addConfigToTree(root, KeywordConfig.instance());
		addConfigToTree(root, MechanicsConfig.instance());
		addConfigToTree(root, RollTableConfig.instance());
		
		for (Datum<?> datum : currentTemplate.getDatums())
			addDatumToTree(root, datum);
		
		sourceModel.reload();
		sourceTree.validate();
		sourceTree.setVisible(true);
	}
	
	private void addConfigToTree(DefaultMutableTreeNode root, Config<?> config) {
		root.add(new DefaultMutableTreeNode(config));
	}
	
	private void addDatumToTree(DefaultMutableTreeNode root, Datum<?> datum) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(datum);
		for (DatumData data : datum.getData()) {
			node.add(new DefaultMutableTreeNode(data));
		}
		
		root.add(node);
	}
	
	private void updateEditor() {
		if (currentEditor == null) {
			editorPanel.removeAll();
			editorPanel.validate();
			return;
		}
	}
	
	private void openEditor(IEditor<?> editor) {
		if (currentEditor != null) {
			// UH OH //TODO
			currentEditor.getComponent().setVisible(false);
			currentEditor = null;
		}
		
		currentEditor = editor;
		editorPanel.add(editor.getComponent(), BorderLayout.CENTER);
		editor.getComponent().setVisible(true);
		
		editorPanel.validate();
		
	}
	
	public void shutdown() {
		clearEditor(true);
	}

	public void doMousePressed(MouseEvent e) {
		if (e.getClickCount() != 2)
			return;
		TreePath selPath = sourceTree.getPathForLocation(e.getX(), e.getY());
        if (selPath == null)
        	return;
        
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
		
		if (currentTemplate == null)
			return;
		
		if (node == null)
			return;
		
		if (node.getUserObject() == lastEditorObject)
			return;
		
		lastEditorObject = node.getUserObject();
		IEditor<?> editor = null;
		if (lastEditorObject instanceof Config<?>) {
			editor = new EnumMapEditor<>(this, toMap((Config<?>) lastEditorObject));//new ConfigEditor(currentTemplate, (Config<?>) lastEditorObject);
		} else {
			currentLoader = new ObjectDataLoader<>(lastEditorObject);
			editor = IEditor.createTemplateEditor(this, currentLoader);
			
		}
		
		if (editor != null)
			openEditor(editor);
		
	}
	
	private <T extends Enum<T>> Map<T, FieldData> toMap(Config<T> config) {
		Map<T, FieldData> map = new HashMap<>();
		
		// TODO for each enum make a mapping
		for (T key : config.getKeyList()) {
			SimpleFieldData data = null;
			switch (config.getFieldType(key)) {
			case BOOL:
				data = FieldData.simple(config.getBool(key));
				break;
			case DOUBLE:
				data = FieldData.simple(config.getDouble(key));
				break;
			case INT:
				data = FieldData.simple(config.getInt(key));
				break;
			case STRING:
				data = FieldData.simple(config.getString(key));
				break;
			default:
				System.out.println("Missing FieldType key from config reading (TemplateEditorScreen)");
				data = null;
				break;
			}
			
			map.put(key, data);
		}
		
		return map;
	}
	
	private <T extends Enum<T>> Config<T> toConfig(Map<?, FieldData> data) {
		if (lastEditorObject instanceof Config<?>) {
			Map<T, FieldData> castmap = (Map<T, FieldData>) data;
			
			@SuppressWarnings("unchecked")
			Config<T> conf = (Config<T>) lastEditorObject;
			
			for (T key : castmap.keySet()) {
				conf.setValue(key.name(), ( (SimpleFieldData) castmap.get(key)).getValue());
			}
			
			return conf;
			
		} else {
			System.err.println("Found mismatched internal editor object");
			return null;
		}
	}

	@Override
	public void dirty() {
		if (this.currentTemplate != null) {
			this.currentTemplate.dirty();
			this.saveEditor();
		}
	}
	
}
