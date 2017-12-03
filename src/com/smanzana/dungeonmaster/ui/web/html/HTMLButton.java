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
public class HTMLButton extends HTMLContainer {

	private String label;
	private WebHook hook;
	
	public HTMLButton(String text) {
		this(text, UUID.randomUUID().toString());
	}
	
	public HTMLButton(String text, String ID) {
		super(ID);
		this.label = text;
	}
	
	public void setHook(WebHook hook) {
		this.hook = hook;
	}
	
	@Override
	protected String getHTMLString() {
		String buf = "<button class='htmlbutton'>" + label + "</button>\r\n";
		
		buf += this.getChildHTML();
		
		return buf;
	}

	@Override
	public String getScriptText() {
		String buf = "";
		
		buf += this.getChildScriptText();
		
		if (hook != null) {
			buf += "function _button_" + getID() + " {\r\n"
					+ AJAX.generate("hook_button_" + this.getID(), false,
							true, null, null)
					+ "}\r\n";
		}
		
		return buf;
	}

	@Override
	public String getStyleText() {
		String buf = "p.htmlbutton {\r\ntext-align: center;\r\n}\r\n";
		
		buf += this.getChildStyleText();
		
		return buf;
	}

	@Override
	public Map<String, WebHook> getWebHooks() {
		Map<String, WebHook> hooks = new HashMap<>();
				
		for (HTMLCompatible child : this.getChildren()) {
			hooks.putAll(child.getWebHooks());
		}
		
		if (hook != null) {
			hooks.put("hook_button_" + this.getID(), hook);
		}
		
		return hooks;
	}
}
