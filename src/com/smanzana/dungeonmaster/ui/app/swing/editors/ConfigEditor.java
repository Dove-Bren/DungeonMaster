package com.smanzana.dungeonmaster.ui.app.swing.editors;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.smanzana.dungeonmaster.maker.SessionTemplate;
import com.smanzana.dungeonmaster.session.configuration.CombatBonusConfig;
import com.smanzana.dungeonmaster.session.configuration.Config;
import com.smanzana.dungeonmaster.session.configuration.RollTableConfig;
import com.smanzana.dungeonmaster.ui.app.UIColor;
import com.smanzana.dungeonmaster.ui.app.swing.editors.BoolField.BoolFieldCallback;
import com.smanzana.dungeonmaster.ui.app.swing.editors.DoubleField.DoubleFieldCallback;
import com.smanzana.dungeonmaster.ui.app.swing.editors.IntField.IntFieldCallback;
import com.smanzana.dungeonmaster.ui.app.swing.editors.StepField.StepFieldCallback;
import com.smanzana.dungeonmaster.ui.app.swing.editors.TextField.TextFieldCallback;
import com.smanzana.dungeonmaster.utils.StepList;

public class ConfigEditor extends JScrollPane implements DMEditor {

	private static final long serialVersionUID = -4533006684394006640L;
	private JPanel editor;
	
	// doesn't set as visible
	public ConfigEditor(SessionTemplate template, Config<?> config) {
		super();
		
		editor = new JPanel();
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		//editor.setBackground(Color.YELLOW);
		UIColor.setColors(editor, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
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
				comp = new DoubleField(keyName, new DoubleFieldCallback() {
					@Override
					public void setField(double value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				}, config.getValue(keyName).toString());
				break;
			case INT:
				comp = new IntField(keyName, new IntFieldCallback() {
					@Override
					public void setField(int value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				}, config.getValue(keyName).toString());
				break;
			case STRING:
				// See note about crappiness below.
				if (isStepList(config)) {
					comp = new StepField(keyName, new StepFieldCallback() {
						@Override
						public void setField(String value) {
							config.setValue(keyName, value);
							template.dirty();
						}
					}, StepList.deserialize((String) config.getValue(keyName)));
				} else {
					comp = new TextField(keyName, new TextFieldCallback() {
						@Override
						public void setField(String value) {
							config.setValue(keyName, value);
							template.dirty();
						}
					}, (String) config.getValue(keyName));
				}
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
			UIColor.setColors(comp.getComponent(), UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
			comp.getComponent().setPreferredSize(new Dimension(100, 25));
			editor.add(comp.getComponent());
		}
		
		this.setViewportView(editor);
		this.validate();
	}
	
	private boolean isStepList(Config<?> config) {
		// Crappy. Oh well. Just doing what works :)
		return (config instanceof CombatBonusConfig ||
			    config instanceof RollTableConfig);
	}

	@Override
	public JComponent getComponent() {
		return this;
	}
	
	
	
}
