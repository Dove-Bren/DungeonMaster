package com.smanzana.dungeonmaster.session.datums.data;

import java.util.Comparator;

import com.smanzana.dungeonmaster.utils.Displayable;

/**
 * Data template that populates a Datum
 * @author Skyler
 *
 */
public interface DatumData extends DataCompatible, Displayable {

	public static Comparator<DatumData> comparator = new Comparator<DatumData>() {
		@Override
		public int compare(DatumData o1, DatumData o2) {
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
	};
	
}
