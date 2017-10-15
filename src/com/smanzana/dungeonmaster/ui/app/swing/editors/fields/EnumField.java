package com.smanzana.dungeonmaster.ui.app.swing.editors.fields;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EnumField<T extends Enum<T>> implements ActionListener, EditorField {

	public static interface EnumFieldCallback {
		public void setField(String valueName);
	}
	
	private JComboBox<String> combo;
	private JPanel wrapper;
	private EnumFieldCallback hook;
	
	public EnumField(String title, EnumFieldCallback hook, T startSelection) {
		this.hook = hook;
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		combo = new JComboBox<>();
		combo.setMinimumSize(new Dimension(50, 20));
		combo.setMaximumSize(new Dimension(300, 20));
		combo.setPreferredSize(new Dimension(100, 20));
		EnumSet.allOf((Class<T>) startSelection.getClass()).forEach((e) -> {
			combo.addItem(pretty(e.name()));
			if (e == startSelection)
				combo.setSelectedIndex(combo.getItemCount() - 1);
		});
		
		combo.addActionListener(this);
		wrapper.add(combo);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		wrapper.add(Box.createHorizontalGlue());
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}
	
	private String pretty(String raw) {
		String buf = "";
			
		buf += raw.substring(0, 1);
		raw = raw.substring(1);
		raw = raw.toLowerCase();
		
		int pos;
		while (-1 != (pos = raw.indexOf('_'))) {
			// pos is position of first underscore.
			// copy up to pos into buf. Then copy char after pos as uppercase.
			// then set raw past capital char
			buf += raw.substring(0, pos);
			buf += raw.substring(pos + 1, pos + 2).toUpperCase();
			raw = raw.substring(pos + 2);
		}
		buf += raw;
		
		return buf;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null)
			hook.setField(combo.getSelectedItem().toString().toUpperCase());
	}
	
	
	
}
