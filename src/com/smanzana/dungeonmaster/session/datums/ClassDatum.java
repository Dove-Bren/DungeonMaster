package com.smanzana.dungeonmaster.session.datums;

public class ClassDatum extends Datum<ClassDatumData> {

	private static final String KEY_CLASS = "class";
	
	public ClassDatum() {
		super(KEY_CLASS);
	}
	
	@Override
	protected ClassDatumData constructEmptyData() {
		return new ClassDatumData();
	}

	@Override
	protected ClassDatumData constructDefaultData() {
		return (ClassDatumData) ClassDatumData.getExampleData();
	}
	
}
