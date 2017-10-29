package com.smanzana.dungeonmaster.utils;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.app.swing.editors.fields.ValueField;
import com.smanzana.templateeditor.api.ICustomData;
import com.smanzana.templateeditor.editor.fields.EditorField;

public abstract class ValueSpecifier implements DataCompatible, ICustomData {

	/**
	 * Gets a value specified by this specifier.
	 * For Dice or Ranges, this means a random number in the valid set of returns
	 * @return
	 */
	public abstract int fetchValue();
	
	public static ValueSpecifier fromData(DataNode data) {
		// Crappy but oh well. Bite me
		ValueSpecifier ret;
		if (data.getValue() != null) {
			String serial = data.getValue();
			// if has d's, it's dice or a diceset
			if (serial.contains("d")) {
				if (serial.contains(","))
					ret = new DiceSet();
				else
					ret = new Dice(1);
			} else if (serial.contains("-") || serial.contains(",")) {
				// formats 2 and 3 of value range
				ret = new ValueRange(1, 2);
			} else
				ret = new ValueConstant(0);
		} else {
			// only implementation we have right now that has children is value range
			ret = new ValueRange(1, 2);
		}
		
		ret.load(data);
		return ret;
	}
	
	/**
	 * Construct an editor field to represent this field.
	 * The editor field should be initialized to the current state of this piece of data.
	 * @return An editor to be displayed to represent and edit this field
	 */
	@Override
	public EditorField<?> getField() {
		return new ValueField(this);
	}
	
	/**
	 * Pull data out from the given field to update this piece of data.
	 * The passed field will be the same as produced with a corresponding call to {@link #getField()}.
	 * @param field
	 */
	@Override
	public ValueSpecifier fillFromField(EditorField<?> field) {
		return ((ValueField) field).getObject();
	}
	
}
