package com.smanzana.dungeonmaster.ui.app.swing.editors;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DoubleField implements ActionListener, EditorField {

	public static interface DoubleFieldCallback {
		public void setField(double value);
	}
	
	private JPanel wrapper;
	private JFormattedTextField textfield;
	private DoubleFieldCallback hook;
	
	public DoubleField(String title, DoubleFieldCallback hook) {
		this(title, hook, "");
	}
	
	public DoubleField(String title, DoubleFieldCallback hook, String startingText) {
		this.hook = hook;
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		this.textfield = new JFormattedTextField(NumberFormat.getNumberInstance());
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
			hook.setField(Double.parseDouble(textfield.getText()));
	}
	
	
	
}
