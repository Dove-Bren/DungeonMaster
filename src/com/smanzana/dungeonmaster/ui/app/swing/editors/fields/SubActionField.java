package com.smanzana.dungeonmaster.ui.app.swing.editors.fields;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.action.subaction.SubAction.DataType;
import com.smanzana.dungeonmaster.action.subaction.SubDamage;
import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.Dice;
import com.smanzana.dungeonmaster.utils.DiceSet;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.dungeonmaster.utils.ValueRange;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;
import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.uiutils.TextUtil;

/**
 * Construct a {@link ValueSpecifier}.<br />
 * Supports {@link ValueConstant}, {@link ValueRange},
 * {@link Dice}, and {@link DiceSet}
 * @author Skyler
 *
 */
public class SubActionField implements ItemListener, EditorField<SubAction>, IEditorOwner {
	
	// Create all fields. Enable and disable based on required maps
	
	private static class EditorWrapper {
		public EditorField<?> field;
		public JComponent comp;
		
		public EditorWrapper(EditorField<?> field) {
			this(field, field.getComponent());
		}
		
		public EditorWrapper(EditorField<?> field, JComponent comp) {
			this.field = field;
			this.comp = comp;
		}
	}
	
	private JPanel wrapper;
	private Map<String, Map<DataType, String>> typeMaps; // holds the data map for each subaction type string
	private Map<DataType, EditorWrapper> typeFields;
	private String currentType;
	private SubAction original;
	private IEditorOwner owner;
	
	public SubActionField() {
		this(null);
	}
	
	public SubActionField(SubAction current) {
		typeMaps = new TreeMap<>();
		typeFields = new EnumMap<>(DataType.class);
		
		if (current == null)
			current = new SubDamage(new Dice(4));
		
		original = current;
		
		JComboBox<String> comboField = new JComboBox<>();
		SubAction act;
		for (String subactionType : SubAction.getRegisteredTypes()) {
			comboField.addItem(subactionType);
			act = SubAction.constructFromType(subactionType);
			typeMaps.put(subactionType, act.getApplicableTypes());
		}
		
		comboField.setEditable(false);
		comboField.setRenderer(new ListCellRenderer<String>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends String> arg0, String arg1, int arg2, boolean arg3,
					boolean arg4) {
				JLabel ret = new JLabel(TextUtil.pretty(arg1));
				
				ret.setOpaque(true);
				if (arg3) {
					ret.setBackground(Color.DARK_GRAY);
					ret.setForeground(Color.WHITE);
				} else {
					ret.setBackground(Color.LIGHT_GRAY);
					ret.setForeground(Color.BLACK);
				}
				
				return ret;
			}
		});
		comboField.addItemListener(this);
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(0, 10)));
		wrapper.add(comboField);
		wrapper.add(Box.createRigidArea(new Dimension(0, 20)));
		
		EditorWrapper editor;
		
		// Amount-hp
		editor = new EditorWrapper(new ValueField(new ValueConstant(0)));
		typeFields.put(DataType.AMOUNT_HP, editor);
		editor.comp.setVisible(false);
		wrapper.add(editor.comp);
		
		// Amount-mp
		editor = new EditorWrapper(new ValueField(new ValueConstant(0)));
		typeFields.put(DataType.AMOUNT_MP, editor);
		editor.comp.setVisible(false);
		wrapper.add(editor.comp);
		
		// Amount-stamina
		editor = new EditorWrapper(new ValueField(new ValueConstant(0)));
		typeFields.put(DataType.AMOUNT_STAMINA, editor);
		editor.comp.setVisible(false);
		wrapper.add(editor.comp);
		
		// Effect
		editor = new EditorWrapper(new EffectField());
		typeFields.put(DataType.EFFECT, editor);
		editor.comp.setVisible(false);
		wrapper.add(editor.comp);
		
		wrapper.add(Box.createRigidArea(new Dimension(0, 10)));

		setObject(current);
	}
	
	private void updateField(String newType) {
		
		for (DataType d : DataType.values()) {
			typeFields.get(d).comp.setVisible(false);
		}
		Map<DataType, String> applicables = typeMaps.get(newType);
		for (DataType d : applicables.keySet()) {
			typeFields.get(d).comp.setVisible(true);
			typeFields.get(d).comp.validate();
			System.out.println("Setting " + typeFields.get(d).comp + " (" + d.name() + ") visible");
		}

		currentType = newType;
		
		wrapper.validate();
		wrapper.repaint();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public SubAction getObject() {
		// Get type. Then based on type, collect fields into DataNode.
		// Then construct subaction based on that
		DataNode node = new DataNode("dummy", null, new LinkedList<>());
		Map<DataType, String> keyMap = typeMaps.get(currentType);
		
		
		for (DataType type : keyMap.keySet()) {
			switch (type) {
			case AMOUNT_HP:
			case AMOUNT_MP:
			case AMOUNT_STAMINA:
				node.addChild(((ValueField) typeFields.get(type).field).getObject().write(keyMap.get(type)));
				break;
			case EFFECT:
				node.addChild(((EffectField) typeFields.get(type).field).getObject().write(keyMap.get(type)));
				break;
			}
		}
		
		
		SubAction sub = SubAction.constructFromType(currentType);
		sub.load(node);
		return sub; 
	}

	@Override
	public void setObject(SubAction obj) {
		String newType;
		if (obj == null) {
			obj = new SubDamage(new ValueConstant(0));
			newType = obj.getClassKey();
		}
		
		// Get mapping of DataType to serialization key
		DataNode node = obj.write("dummy");
		newType = obj.getClassKey();

		Map<DataType, String> map = typeMaps.get(newType);
		for (DataType type : map.keySet()) {
			String subkey = map.get(type);
			// for each type, set value to what's in DataNode at map.get(type)
			DataNode child = node.getChild(subkey);
			if (child == null)
				System.err.println("Failed child lookup for key " + subkey + " of data type " + obj.getClassKey());
			
			switch (type) {
			case AMOUNT_HP:
			case AMOUNT_MP:
			case AMOUNT_STAMINA:
				((ValueField) typeFields.get(type).field).setObject(ValueSpecifier.fromData(child));
				break;
			case EFFECT:
				((EffectField) typeFields.get(type).field).setObject(Effect.fromData(child));
				break;
			}
		}
		
		updateField(newType);
	}

	@Override
	public SubAction getOriginal() {
		return original;
	}

	@Override
	public void setOwner(IEditorOwner owner) {
		this.owner = owner;
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() != ItemEvent.SELECTED)
			return;
		
		String newType = (String) arg0.getItem();
		if (newType == currentType)
			return;
		
		dirty();
		updateField(newType);
	}

	@Override
	public void dirty() {
		if (this.owner != null)
			this.owner.dirty();
	}
}
