package com.smanzana.dungeonmaster.ui.web.html.form;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.smanzana.dungeonmaster.ui.web.html.HTMLElement;

public class Form extends HTMLElement {

	private List<FormInput> elements;
	private String action;
	private String method;
	
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
		String ret =  "<form id='" + getID() + "' action='" + action + "' method='"
				+ method + "'>\r\n";
		
		for (FormInput input : elements) {
			ret += input.asHTML() + "\r\n";
		}
		
		ret += generateSubmit();
		
		ret += "</form>";
		return ret;
	}
	
	private String generateSubmit() {
		return "<button onclick='_" + getID()+"_submit()" + "'>Submit</button>";
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
			
			if (!first) {
				inputHooks += "&& ";
				first = true;
			}
			inputHooks += hook + " ";
			
			// Also add any script pieces it needs before the actual call:
			ret += input.getScriptText() + "\r\n";
		}
		
		if (inputHooks.trim().isEmpty())
			return "";
		
		ret += "function _" + getID() + "_submit() {\r\nreturn ";
		ret += inputHooks + ";\r\n";
		
		ret += "}";
		return ret;
	}
	
	@Override
	public String getStyleText() {
		String ret =  "";
		
		// Form doesn't have any
		for (FormInput input : elements) {
			ret += input.getStyleText() + "\r\n";
		}
		
		return ret;
	}
	
}
