package com.smanzana.dungeonmaster.ui.web.html;

public abstract class HTMLElement implements HTMLCompatible {

	protected String ID;
	protected String href;
	
	protected HTMLElement(String ID) {
		this.ID = clean(ID);
		this.href = null;
	}
	
	public void href(String href) {
		this.href = href;
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
	
	protected abstract String getHTMLString();
	
	@Override
	public final String asHTML() {
		if (href == null)
			return getHTMLString();
		
		return "<a href='" + href + "'>\r\n" + getHTMLString() + "</a>\r\n";
	}
}
