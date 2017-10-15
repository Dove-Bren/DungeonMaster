package com.smanzana.dungeonmaster.ui.app.swing.editors;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.ui.app.UIColor;
import com.smanzana.dungeonmaster.ui.app.swing.editors.fields.EditorListField.EditorListEditor;

public class SubActionEditor extends JScrollPane implements EditorListEditor {

	private JPanel editor;
	private SubAction currentObject;
	
	// doesn't set as visible
	public SubActionEditor() {
		super();
		
		editor = new JPanel();
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		UIColor.setColors(editor, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		editor.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		
		
		this.setViewportView(editor);
		this.validate();
	}
	
	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public void setEdittingObject(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetObject() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEditorValid() {
		return true;
	}
	
	
	
}
