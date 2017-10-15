package com.smanzana.dungeonmaster.ui.app.swing.editors.fields;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextField implements ActionListener, EditorField {

	public static interface TextFieldCallback {
		public void setField(String text);
	}
	
	private JTextField textfield;
	private TextFieldCallback hook;
	private JPanel wrapper;
	
	public TextField(String title, TextFieldCallback hook) {
		this(title, hook, "");
	}
	
	public TextField(String title, TextFieldCallback hook, String startingText) {
		this.hook = hook;
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		this.textfield = new JTextField(startingText, 20);
		textfield.addActionListener(this);
		wrapper.add(textfield);
		wrapper.add(Box.createHorizontalGlue());

		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null)
			hook.setField(textfield.getText());
	}
	
	
	
}
