package com.smanzana.dungeonmaster.ui.app.swing.editors.fields;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.smanzana.dungeonmaster.utils.Dice;
import com.smanzana.dungeonmaster.utils.DiceSet;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.dungeonmaster.utils.ValueRange;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;
import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.BoolField;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.GenericListField;
import com.smanzana.templateeditor.editor.fields.IntField;
import com.smanzana.templateeditor.uiutils.TextUtil;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * Construct a {@link ValueSpecifier}.<br />
 * Supports {@link ValueConstant}, {@link ValueRange},
 * {@link Dice}, and {@link DiceSet}
 * @author Skyler
 *
 */
public class ValueField implements ItemListener, EditorField<ValueSpecifier>, IEditorOwner {
	
	private static enum Type {
		CONSTANT,
		RANGE,
		DICE,
		DICESET
	}
	
	private static abstract class ValueEditor<T extends ValueSpecifier> extends JPanel {
		private static final long serialVersionUID = 1L;

		public abstract T getValueSpecifier();
		
		protected ValueEditor() {
			super();
			color();
		}
		
		protected void color() {
			color(this);
		}
		
		protected void color(JComponent comp) {
			UIColor.setColors(comp, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		}
	}
	
	private static class ConstantField extends ValueEditor<ValueConstant> {
		
		private static final long serialVersionUID = 3128237228839503771L;
		private IntField field;
		
		public ConstantField(IEditorOwner owner, int startingVal) {
			super();

			field = new IntField(startingVal);
			field.setOwner(owner);
			JLabel label = new JLabel("Constant Value");
			UIColor.setColors(label, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(Box.createRigidArea(new Dimension(10, 0)));
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(20, 0)));
			color(field.getComponent());
			this.add(field.getComponent());
			this.add(Box.createRigidArea(new Dimension(10, 0)));
		}
		
		public int getValue() {
			return field.getObject();
		}
		
		public void setValue(int value) {
			field.setObject(value);
		}

		@Override
		public ValueConstant getValueSpecifier() {
			return new ValueConstant(getValue());
		}
	}
	
	private static class RangeField extends ValueEditor<ValueRange> implements IEditorOwner {
		
		private static final long serialVersionUID = 312823727939503771L;
		private IntField fieldMin;
		private IntField fieldMax;
		
		public RangeField(IEditorOwner owner, int startingMin, int startingMax) {
			super();

			fieldMin = new IntField(startingMin);
			fieldMax = new IntField(startingMax);
			fieldMin.setOwner(owner);
			fieldMax.setOwner(owner);
			JLabel label = new JLabel("Value Range: ");
			color(label);
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(Box.createRigidArea(new Dimension(10, 0)));
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(20, 0)));
			color(fieldMin.getComponent());
			this.add(fieldMin.getComponent());
			this.add(Box.createRigidArea(new Dimension(10, 0)));
			label = new JLabel(" - ");
			color(label);
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(10, 0)));
			color(fieldMax.getComponent());
			this.add(fieldMax.getComponent());
			this.add(Box.createRigidArea(new Dimension(10, 0)));

		}
		
		public int getMin() {
			return fieldMin.getObject();
		}
		
		public int getMax() {
			return fieldMax.getObject();
		}
		
		public void setValues(int min, int max) {
			fieldMin.setObject(min);
			fieldMax.setObject(max);
		}

		@Override
		public void dirty() {
			int min = getMin(),
					max = getMax();
			if (min > max) {
				fieldMin.setObject(max);
				fieldMax.setObject(min);
			} else if (min == max) {
				fieldMax.setObject(min + 1);
			}
		}

		@Override
		public ValueRange getValueSpecifier() {
			return new ValueRange(getMin(), getMax());
		}
	}
	
	private static class DiceField extends ValueEditor<Dice> implements IEditorOwner {
		
		private static final long serialVersionUID = 31246464939673771L;
		private IntField fieldNum;
		private IntField fieldFaces;
		private BoolField fieldZero;
		
		public DiceField(IEditorOwner owner, int startingNum, int startingFaces, boolean includeZero) {
			super();

			fieldNum = new IntField(startingNum);
			fieldNum.setOwner(owner);
			fieldFaces = new IntField(startingFaces);
			fieldFaces.setOwner(owner);
			fieldZero = new BoolField(includeZero);
			fieldZero.setOwner(owner);
			JLabel label = new JLabel("Dice: ");
			color(label);
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(Box.createRigidArea(new Dimension(10, 0)));
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(20, 0)));
			color(fieldNum.getComponent());
			this.add(fieldNum.getComponent());
			label = new JLabel("d");
			color(label);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setBorder(null);
			this.add(label);
			color(fieldFaces.getComponent());
			this.add(fieldFaces.getComponent());
			this.add(Box.createRigidArea(new Dimension(30, 0)));
			label = new JLabel("0-bounded: ");
			color(label);
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(5, 0)));
			color(fieldZero.getComponent());
			this.add(fieldZero.getComponent());
			this.add(Box.createRigidArea(new Dimension(10, 0)));
			this.add(Box.createHorizontalGlue());

		}
		
		public int getNum() {
			return fieldNum.getObject();
		}
		
		public int getFaces() {
			return fieldFaces.getObject();
		}
		
		public boolean getZero() {
			return fieldZero.getObject();
		}
		
		public void setValues(int num, int faces, boolean zero) {
			fieldNum.setObject(num);
			fieldFaces.setObject(faces);
			fieldZero.setObject(zero);
		}

		@Override
		public void dirty() {
			int num = getNum(),
				faces = getFaces();
			
			if (num < 1)
				fieldNum.setObject(1);
			if (faces < 2)
				fieldFaces.setObject(2);
		}

		@Override
		public Dice getValueSpecifier() {
			return new Dice(getNum(), getFaces(), getZero());
		}
	}
	
	private static class DiceSetField extends ValueEditor<DiceSet> {
		
		private static final long serialVersionUID = -1107297129151127038L;

		private static class DiceSetFieldData extends FieldData {
			private DiceField field;
			private IEditorOwner cloneOwner;
			
			public DiceSetFieldData(IEditorOwner owner, DiceField field) {
				this.field = field;
				cloneOwner = owner;
			}
			
			@Override
			public FieldData clone() {
				return new DiceSetFieldData(cloneOwner, new DiceField(
						cloneOwner,
						field.getNum(),
						field.getFaces(),
						field.getZero()
						));
			}

			@Override
			public EditorField<?> constructField() {
				return new EditorField<DiceField>() {

					@Override
					public JComponent getComponent() {
						return field;
					}

					@Override
					public DiceField getObject() {
						return null;
					}

					@Override
					public void setObject(DiceField obj) {
						;
					}

					@Override
					public DiceField getOriginal() {
						return field;
					}

					@Override
					public void setOwner(IEditorOwner owner) {
						;
					}
				};
			}

			@Override
			public void fillFromField(EditorField<?> field) {
				;
			}
		}
		
		private GenericListField<DiceSetFieldData> list;
		
		public DiceSetField(IEditorOwner owner, Dice template, List<Dice> inputList) {
			super();

			List<DiceSetFieldData> dataList = new ArrayList<>(inputList.size());
			for (Dice f : inputList)
				dataList.add(new DiceSetFieldData(owner, new DiceField(owner, f.getDieCount(), f.getDieFaces(), f.includesZero())));
			list = new GenericListField<>(new DiceSetFieldData(owner, new DiceField(owner, template.getDieCount(), template.getDieFaces(), template.includesZero())), dataList);
			list.setOwner(owner);
			color(list.getComponent());
			JLabel label = new JLabel("Dice Set: ");
			color(label);
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(Box.createRigidArea(new Dimension(10, 0)));
			this.add(label);
			this.add(Box.createRigidArea(new Dimension(20, 0)));
			this.add(list.getComponent());
			this.add(Box.createRigidArea(new Dimension(10, 0)));

		}
		
		public List<Dice> getDice() {
			List<DiceSetFieldData> data = list.getObject();
			List<Dice> out = new ArrayList<>(data.size());
			for (DiceSetFieldData d : data)
				out.add(d.field.getValueSpecifier());
			return out;
		}
		
		public void setValues(IEditorOwner owner, List<Dice> newDice) {
			List<DiceSetFieldData> dataList = new ArrayList<>(newDice.size());
			for (Dice f : newDice)
				dataList.add(new DiceSetFieldData(owner, new DiceField(owner, f.getDieCount(), f.getDieFaces(), f.includesZero())));
			
			list.setObject(dataList);
		}

		@Override
		public DiceSet getValueSpecifier() {
			DiceSet set = new DiceSet();
			for (Dice f : getDice()) {
				set.addDice(f);
			}
			return set;
		}
	}
	
	private JPanel wrapper;
	private Map<Type, ValueEditor<?>> typeFields; // holds the panel belonging to each type
	private Type currentType;
	private ValueSpecifier original;
	private IEditorOwner owner;
	
	public ValueField() {
		this(null);
	}
	
	public ValueField(ValueSpecifier current) {
		typeFields = new EnumMap<>(Type.class);
		
		if (current == null)
			current = new ValueConstant(0);
		
		original = current;
		
		JComboBox<Type> comboField = new JComboBox<>();
		for (Type t : Type.values())
		comboField.addItem(t);
		
		comboField.setEditable(false);
		comboField.setRenderer(new ListCellRenderer<Type>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends Type> arg0, Type arg1, int arg2, boolean arg3,
					boolean arg4) {
				JLabel ret = new JLabel(TextUtil.pretty(arg1.name()));
				
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
		comboField.setMaximumSize(new Dimension(Short.MAX_VALUE, comboField.getPreferredSize().height));
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
		UIColor.setColors(wrapper, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		wrapper.add(Box.createRigidArea(new Dimension(0, 10)));
		JLabel label = new JLabel("Value Type");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(.5f);
		UIColor.setColors(label, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		wrapper.add(label);
		wrapper.add(comboField);
		wrapper.add(Box.createRigidArea(new Dimension(0, 20)));
		
		ValueEditor<?> editor;
		// Constant
		editor = new ConstantField(this, 0);
		typeFields.put(Type.CONSTANT, editor);
		editor.setVisible(false);
		wrapper.add(editor);
		
		// Range
		editor = new RangeField(this, 0, 1);
		typeFields.put(Type.RANGE, editor);
		editor.setVisible(false);
		wrapper.add(editor);
		
		// Dice
		editor = new DiceField(this, 1, 6, false);
		typeFields.put(Type.DICE, editor);
		editor.setVisible(false);
		wrapper.add(editor);
		
		// DiceSet
		List<Dice> diceFieldList = new LinkedList<>();
		Dice diceFieldTemplate = new Dice(1, 6, false);;
		diceFieldList.add(new Dice(1, 6, false));
		diceFieldList.add(new Dice(2, 4, false));
		editor = new DiceSetField(this, diceFieldTemplate, diceFieldList);
		typeFields.put(Type.DICESET, editor);
		editor.setVisible(false);
		wrapper.add(editor);
		
		wrapper.add(Box.createRigidArea(new Dimension(0, 10)));

		setObject(current);
		comboField.setSelectedItem(currentType);
	}
	
	private void updateField(Type newType) {
		
		if (currentType != null)
			typeFields.get(currentType).setVisible(false);
		typeFields.get(newType).setVisible(true);

		currentType = newType;
		
		wrapper.validate();
		wrapper.repaint();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public ValueSpecifier getObject() {
		return typeFields.get(currentType).getValueSpecifier();
	}

	@Override
	public void setObject(ValueSpecifier obj) {
		Type newType;
		if (obj == null) {
			obj = new ValueConstant(0);
			newType = Type.CONSTANT;
		}
		
		// deduce type
		if (obj instanceof ValueConstant) {
			newType = Type.CONSTANT;
			ConstantField field = (ConstantField) typeFields.get(newType);
			field.setValue(obj.fetchValue());
		} else if (obj instanceof ValueRange) {
			newType = Type.RANGE;
			RangeField field = (RangeField) typeFields.get(newType);
			ValueRange o = (ValueRange) obj;
			field.setValues(o.getMin(), o.getMax());
		} else if (obj instanceof Dice) {
			newType = Type.DICE;
			DiceField field = (DiceField) typeFields.get(newType);
			Dice o = (Dice) obj;
			field.setValues(o.getDieCount(), o.getDieFaces(), o.includesZero());
		} else if (obj instanceof DiceSet) {
			newType = Type.DICESET;
			DiceSetField field = (DiceSetField) typeFields.get(newType);
			DiceSet o = (DiceSet) obj;
			field.setValues(this, o.getDice());
		} else {
			System.err.println("Missing switch case in ValueField!");
			obj = new ValueConstant(0);
			newType = Type.CONSTANT;
		}
		
		updateField(newType);
	}

	@Override
	public ValueSpecifier getOriginal() {
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
		
		Type newType = (Type) arg0.getItem();
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
