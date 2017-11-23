package com.smanzana.dungeonmaster.ui.web.html;

public abstract class HTMLElement implements HTMLCompatible {

	protected String ID;
	
	protected HTMLElement(String ID) {
		this.ID = ID;
	}
	
	@Override
	public String getID() {
		return this.ID;
	}
}
