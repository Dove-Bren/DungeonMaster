package com.smanzana.dungeonmaster.utils;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class ValueConstant implements ValueSpecifier {

	private int value;
	
	public ValueConstant(int value) {
		this.value = value;
	}
	
	@Override
	public int fetchValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public void load(DataNode root) {
		this.value = DataNode.parseInt(root);
	}

	@Override
	public DataNode write(String key) {
		return new DataNode(key, value + "", null);
	}
	
}
