package com.smanzana.dungeonmaster.ui.app.swing.editors.fields;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smanzana.dungeonmaster.utils.NoteUtil;

public class EnumField<T extends Enum<T>> implements ActionListener, EditorField {

	public static interface EnumFieldCallback {
		public void setField(String valueName);
	}
	
	private JComboBox<String> combo;
	private JPanel wrapper;
	private EnumFieldCallback hook;
	private Map<T, String> prettyMap;
	
	@SuppressWarnings("unchecked")
	public EnumField(String title, EnumFieldCallback hook, T startSelection) {
		this.hook = hook;
		this.prettyMap = new HashMap<>();
		
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
			String pretty = NoteUtil.pretty(e.name());
			prettyMap.put(e, pretty);
			combo.addItem(pretty);
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null)
			hook.setField(lookup(combo.getSelectedItem().toString()).name()); // nullptr if lookup fails.
			// but it shouldn't fail.
	}
	
	private T lookup(String pretty) {
		for (T t : prettyMap.keySet())
			if (prettyMap.get(t).equals(pretty))
				return t;
		
		return null;
	}
	
}
