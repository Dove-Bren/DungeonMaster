package com.smanzana.dungeonmaster.ui.web.html.form;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smanzana.dungeonmaster.ui.web.WebHook;
import com.smanzana.dungeonmaster.ui.web.html.HTMLElement;

public class SwitchboxInput extends HTMLElement implements FormInput {

	private List<String> elems;
	private String name;
	private String starting;

	public SwitchboxInput(String name, List<String> options) {
		this(UUID.randomUUID().toString(), name, options, null);
	}
	
	public SwitchboxInput(String name, List<String> options, String starting) {
		this(UUID.randomUUID().toString(), name, options, starting);
	}
	
	public SwitchboxInput(String ID, String name, List<String> options, String starting) {
		super(ID);
		this.name = name;
		this.elems = options;
		this.starting = starting;
	}
	
	@Override
	protected String getHTMLString() {
		String ret =  "<select name='" + name + "' id='" + getID() + "'>\r\n";
		
		for (String elem : elems) {
			ret += "<option value='" + elem + "'"
					+ (elem.equals(starting) ? " selected" : "")
					+ ">" + elem + "</option>\r\n";
		}
		
		ret += "</select>\r\n";
		return ret;
	}

	@Override
	public String getScriptText() {
		return "";
	}

	@Override
	public String getStyleText() {
		return "";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValidationHook() {
		return "";
	}

	@Override
	public Map<String, WebHook> getWebHooks() {
		return null;
	}

}
