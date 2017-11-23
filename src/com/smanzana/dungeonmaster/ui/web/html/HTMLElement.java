package com.smanzana.dungeonmaster.ui.web.html;

public abstract class HTMLElement implements HTMLCompatible {

	protected String ID;
	
	protected HTMLElement(String ID) {
		this.ID = clean(ID);
	}
	
	@Override
	public String getID() {
		return this.ID;
	}
	
	public static String clean(String raw) {
		raw = raw.replace('-', '_');
		raw = raw.replace(' ', '_');
		return raw;
	}
}
