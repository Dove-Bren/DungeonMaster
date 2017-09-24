package com.smanzana.dungeonmaster.utils;

public class ValueConstant implements ValueSpecifier {

	private int value;
	
	public ValueConstant(int value) {
		this.value = value;
	}
	
	@Override
	public int fetchValue() {
		return this.value;
	}
	
}
