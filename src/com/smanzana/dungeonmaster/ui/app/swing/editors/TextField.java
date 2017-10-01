package com.smanzana.dungeonmaster.ui.app.swing.editors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class TextField implements ActionListener, EditorField {

	public static interface TextFieldCallback {
		public void setField(String text);
	}
	
	private JTextField textfield;
	private TextFieldCallback hook;
	
	public TextField(TextFieldCallback hook) {
		this(hook, "");
	}
	
	public TextField(TextFieldCallback hook, String startingText) {
		this.hook = hook;
		this.textfield = new JTextField(startingText, 20);
	}
	
	public JTextField getComponent() {
		return textfield;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null)
			hook.setField(textfield.getText());
	}
	
	
	
}
