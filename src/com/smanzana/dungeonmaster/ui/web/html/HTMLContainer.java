package com.smanzana.dungeonmaster.ui.web.html;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.ui.web.WebHook;

/**
 * Supports some number of nested children
 * @author Skyler
 *
 */
public abstract class HTMLContainer extends HTMLElement {

	protected List<HTMLCompatible> children;
	
	protected HTMLContainer(String ID) {
		super(ID);
		children = new LinkedList<>();
	}
	
	public void addChild(HTMLCompatible child) {
		children.add(child);
	}
	
	public List<HTMLCompatible> getChildren() {
		return children;
	}
	
	public void clearChildren() {
		children.clear();
	}
	
	protected String getChildHTML() {
		String buf = "";
		
		for (HTMLCompatible child : this.getChildren()) {
			buf += child.asHTML();
		}
		
		return buf;
	}
	
	public String getChildScriptText() {
		String buf = "";
		
		for (HTMLCompatible child : this.getChildren()) {
			buf += child.getScriptText();
		}
		
		return buf;
	}

	public String getChildStyleText() {
		String buf = "";
		
		for (HTMLCompatible child : this.getChildren()) {
			buf += child.getStyleText();
		}
		
		return buf;
	}

	public Map<String, WebHook> getChildWebHooks() {
		Map<String, WebHook> hooks = new HashMap<>();
		
		for (HTMLCompatible child : this.getChildren()) {
			hooks.putAll(child.getWebHooks());
		}
		
		return hooks;
	}
	
}
