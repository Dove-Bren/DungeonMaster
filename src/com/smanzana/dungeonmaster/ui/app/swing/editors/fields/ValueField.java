package com.smanzana.dungeonmaster.ui.app.swing.editors.fields;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smanzana.dungeonmaster.utils.Dice;
import com.smanzana.dungeonmaster.utils.DiceSet;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.dungeonmaster.utils.ValueRange;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.IntField;
import com.smanzana.templateeditor.editor.fields.IntField.IntFieldCallback;

/**
 * Construct a {@link ValueSpecifier}.<br />
 * Supports {@link ValueConstant}, {@link ValueRange},
 * {@link Dice}, and {@link DiceSet}
 * @author Skyler
 *
 */
public class ValueField implements ActionListener, EditorField {

	public static interface ValueFieldCallback {
		public void setField(ValueSpecifier value);
	}
	
	private static enum Type {
		CONSTANT,
		RANGE,
		DICE,
		DICESET
	}
	
	private JPanel wrapper;
	private ValueFieldCallback hook;
	private Map<Type, JComponent> typeFields; // holds the panel belonging to each type
	
	public ValueField(String title, ValueFieldCallback hook) {
		this(title, hook, null);
	}
	
	public ValueField(String title, ValueFieldCallback hook, ValueSpecifier current) {
		this.hook = hook;
		typeFields = new EnumMap<>(Type.class);
		
		if (current == null)
			current = new ValueConstant(0);
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		JPanel typePanel;
		
		// Constant
		typePanel = (new IntField("", new IntFieldCallback() {
			@Override
			public void setField(int value) {
				((ValueConstant) getCurrentValue()).setValue(value);
			}
		}, current instanceof ValueConstant ? current.fetchValue() + "" : "0").getComponent());
		wrapper.add(typePanel);
		
		
		
		
		
		
		wrapper.add(Box.createHorizontalGlue());

		wrapper.validate();
	}
	
	private ValueSpecifier getCurrentValue() {
		
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null)
			hook.setField(Integer.parseInt(textfield.getText()));
	}
	
	
	
}
