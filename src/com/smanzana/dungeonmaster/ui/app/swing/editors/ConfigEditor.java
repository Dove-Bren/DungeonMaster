package com.smanzana.dungeonmaster.ui.app.swing.editors;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.smanzana.dungeonmaster.maker.SessionTemplate;
import com.smanzana.dungeonmaster.session.configuration.Config;
import com.smanzana.dungeonmaster.ui.app.swing.editors.BoolField.BoolFieldCallback;
import com.smanzana.dungeonmaster.ui.app.swing.editors.DoubleField.DoubleFieldCallback;
import com.smanzana.dungeonmaster.ui.app.swing.editors.IntField.IntFieldCallback;
import com.smanzana.dungeonmaster.ui.app.swing.editors.TextField.TextFieldCallback;

public class ConfigEditor extends JScrollPane implements DMEditor {

	private static final long serialVersionUID = -4533006684394006640L;
	private JPanel editor;
	
	// doesn't set as visible
	public ConfigEditor(SessionTemplate template, Config<?> config) {
		super();
		
		editor = new JPanel();
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		editor.setBackground(Color.YELLOW);
		editor.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		EditorField comp;
		for (String keyName : config.getKeyNames()) {
			comp = null;
			switch (config.getFieldType(keyName)) {
			case BOOL:
				comp = new BoolField(keyName, new BoolFieldCallback() {
					@Override
					public void setField(boolean value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				}, (Boolean) config.getValue(keyName));
				break;
			case DOUBLE:
				comp = new DoubleField(new DoubleFieldCallback() {
					@Override
					public void setField(double value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				});
				break;
			case INT:
				comp = new IntField(new IntFieldCallback() {
					@Override
					public void setField(int value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				});
				break;
			case STRING:
				comp = new TextField(new TextFieldCallback() {
					@Override
					public void setField(String value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				});
				break;
			}
			
			if (comp == null)
				continue;
			
			if (config.getComments(keyName) != null) {
				String buf = "";
				for (String line : config.getComments(keyName)) {
					if (!buf.isEmpty())
						buf += "\n";
					buf += line;
				}
				comp.getComponent().setToolTipText(buf);
			}
			System.out.println("Adding text box");
			comp.getComponent().setBackground(Color.GREEN);
			comp.getComponent().setPreferredSize(new Dimension(100, 25));
			editor.add(comp.getComponent());
		}
		
		this.setViewportView(editor);
		this.validate();
	}

	@Override
	public JComponent getComponent() {
		this.setBackground(Color.RED);
		return this;
	}
	
	
	
}
