package com.smanzana.dungeonmaster.ui.app.swing.editors;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;

import com.smanzana.dungeonmaster.session.configuration.Config;
import com.smanzana.dungeonmaster.ui.app.swing.editors.IntField.IntFieldCallback;
import com.smanzana.dungeonmaster.ui.app.swing.editors.TextField.TextFieldCallback;

public class ConfigEditor extends JScrollPane {

	// doesn't set as visible
	public ConfigEditor(Config<?> config) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		EditorField comp;
		for (String keyName : config.getKeyNames()) {
			comp = null;
			switch (config.getFieldType(keyName)) {
			case BOOL:
				break;
			case DOUBLE:
				break;
			case INT:
				comp = new IntField(new IntFieldCallback() {
					@Override
					public void setField(int value) {
						config.setValue(keyName, value);
					}
				});
				break;
			case STRING:
				comp = new TextField(new TextFieldCallback() {
					@Override
					public void setField(String value) {
						config.setValue(keyName, value);
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
			this.add(comp.getComponent());
		}
		
		
		this.validate();
	}
	
	
	
}
