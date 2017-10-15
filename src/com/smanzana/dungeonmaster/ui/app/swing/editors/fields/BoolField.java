package com.smanzana.dungeonmaster.ui.app.swing.editors.fields;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class BoolField implements ActionListener, EditorField {

	public static interface BoolFieldCallback {
		public void setField(boolean value);
	}
	
	private JRadioButton truefield;
	private JRadioButton falsefield;
	private JPanel wrapper;
	private BoolFieldCallback hook;
	
	public BoolField(String title, BoolFieldCallback hook) {
		this(title, hook, false);
	}
	
	public BoolField(String title, BoolFieldCallback hook, boolean startTrue) {
		this.hook = hook;
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		falsefield = new JRadioButton("False");
		falsefield.addActionListener(this);
		wrapper.add(falsefield);
		wrapper.add(Box.createRigidArea(new Dimension(30, 0)));
		
		truefield = new JRadioButton("True");
		truefield.addActionListener(this);
		wrapper.add(truefield);
		wrapper.add(Box.createHorizontalGlue());
		
		if (startTrue)
			truefield.setSelected(true);
		else
			falsefield.setSelected(true);
		
		ButtonGroup group = new ButtonGroup();
		group.add(falsefield);
		group.add(truefield);
		
		
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null)
			hook.setField(truefield.isSelected());
	}
	
	
	
}
