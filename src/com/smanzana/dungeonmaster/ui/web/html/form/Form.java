package com.smanzana.dungeonmaster.ui.web.html.form;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.smanzana.dungeonmaster.ui.web.html.HTMLElement;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP;

public class Form extends HTMLElement {

	private String displayName;
	private List<FormInput> elements;
	private String action;
	private String method;
	private String errorID;
	private int refreshInterval; // In seconds. Sends info to server
	
	/**
	 * Same as Form("", "GET")
	 */
	public Form() {
		this("", "GET");
	}
	
	public Form(String action, String method) {
		this(UUID.randomUUID().toString(), action, method);
	}
	
	public Form(String ID, String action, String method) {
		super(ID);
		this.action = action;
		this.method = method;
		this.elements = new LinkedList<>();
		errorID = clean(UUID.randomUUID().toString());
	}
	
	public void setDisplayName(String display) {
		this.displayName = display;
	}

	public String getAction() {
		return action;
	}

	public String getMethod() {
		return method;
	}
	
	public void addInput(FormInput input) {
		elements.add(input);
	}
	
	@Override
	public String asHTML() {
		String ret = "<center>\r\n";
		
		if (displayName != null) {
			ret += "<h2>" + displayName + "</h2>\r\n";
		}
		
		ret += "<span class='form_error' id='" + errorID + "'></span><br />";
				
		ret += "<form id='" + getID() + "' action='" + action + "' method='"
				+ method + "' onsubmit='return _" + getID()+"_submit();" + "'>\r\n";
		
		for (FormInput input : elements) {
			ret += input.asHTML() + "<br />\r\n";
			ret += "<span class='form_label'>" + HTTP.pretty(input.getName()) + "</span><br /><br />\r\n";
			
		}
		
		ret += "<br />" + generateSubmit();
		
		ret += "</form>\r\n</center>";
		return ret;
	}
	
	private String generateSubmit() {
		return "<input type='submit' value='Submit' />";
	}
	
	@Override
	public String getScriptText() {
		String ret = "";
		
		String inputHooks = "";
		String hook;
		boolean first = true;
		for (FormInput input : elements) {
			hook = input.getValidationHook();
			if (hook == null || hook.trim().isEmpty())
				continue;
			
			if (first)
				first = false;
			else
				inputHooks += "else ";
			inputHooks += "if (!" + hook + ") error_elem.innerHTML = 'Error on field: "
					+ HTTP.pretty(input.getName()) + "';\r\n";
			
			// Also add any script pieces it needs before the actual call:
			ret += input.getScriptText() + "\r\n";
		}
		
		if (inputHooks.trim().isEmpty())
			return "";
		
		ret += "function _" + getID() + "_submit() {\r\n";
		ret += "var error_elem = document.getElementById('" + errorID + "');\r\n" + inputHooks + ";\r\n";
		ret += "else error_elem.innerHTML = '';\r\n";
		ret += "return false;\r\n}";
		return ret;
	}
	
	@Override
	public String getStyleText() {
		String ret =  ".form_label {\r\nwidth: 100px;\r\nfont-weight: bold;\r\n}\r\n"
				+ ".form_error {margin-bottom: 15px;\r\ncolor: red;\r\nfont-weight: bold;\r\n}\r\n";
		
		// Form doesn't have any
		for (FormInput input : elements) {
			ret += input.getStyleText() + "\r\n";
		}
		
		return ret;
	}
	
//	// Creates JS async loop of sending feedback to server
//	private String generateFeedback() {
//		
//	}
	
}
