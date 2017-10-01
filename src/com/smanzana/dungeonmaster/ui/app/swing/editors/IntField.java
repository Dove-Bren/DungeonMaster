package com.smanzana.dungeonmaster.ui.app.swing.editors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;

public class IntField implements ActionListener, EditorField {

	public static interface IntFieldCallback {
		public void setField(int value);
	}
	
	private JFormattedTextField textfield;
	private IntFieldCallback hook;
	
	public IntField(IntFieldCallback hook) {
		this(hook, "");
	}
	
	public IntField(IntFieldCallback hook, String startingText) {
		this.hook = hook;
		this.textfield = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.textfield.setText(startingText);
	}
	
	public JFormattedTextField getComponent() {
		return textfield;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null)
			hook.setField(Integer.parseInt(textfield.getText()));
	}
	
	
	
}
