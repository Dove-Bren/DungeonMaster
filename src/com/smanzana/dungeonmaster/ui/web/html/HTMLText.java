package com.smanzana.dungeonmaster.ui.web.html;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.smanzana.dungeonmaster.ui.web.WebHook;

/**
 * Nice and boring centered text. Can have children elements. Can be nested
 * in others.
 * Converts to a <p>, not a div.
 * @author Skyler
 *
 */
public class HTMLText extends HTMLContainer {

	private String text;
	
	public HTMLText(String text) {
		this(text, UUID.randomUUID().toString());
	}
	
	public HTMLText(String text, String ID) {
		super(ID);
		this.text = text;
	}
	
	public HTMLText text(String newText) {
		this.text = newText;
		return this;
	}
	
	public String getText() {
		return this.text;
	}

	@Override
	protected String getHTMLString() {
		String buf = "<p class='htmltext'>" + text + "</p>\r\n";
		
		buf += this.getChildHTML();
		
		return buf;
	}

	@Override
	public String getScriptText() {
		String buf = "";
		
		buf += this.getChildScriptText();
		
		return buf;
	}

	@Override
	public String getStyleText() {
		String buf = "p.htmltext {\r\ntext-align: center;\r\n}\r\n";
		
		buf += this.getChildStyleText();
		
		return buf;
	}

	@Override
	public Map<String, WebHook> getWebHooks() {
		Map<String, WebHook> hooks = new HashMap<>();
		
		for (HTMLCompatible child : this.getChildren()) {
			hooks.putAll(child.getWebHooks());
		}
		
		return hooks;
	}
	
}
