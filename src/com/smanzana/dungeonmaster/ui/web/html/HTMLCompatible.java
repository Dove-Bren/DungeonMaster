package com.smanzana.dungeonmaster.ui.web.html;

import java.util.Map;

import com.smanzana.dungeonmaster.ui.web.WebHook;

public interface HTMLCompatible {

	/**
	 * Return this element (including all children) formatted as HTML
	 * @return
	 */
	public String asHTML();
	
	/**
	 * Return any text you'd like to be stuck up in the script tag in the head
	 * @return
	 */
	public String getScriptText();
	
	/**
	 * Any style information that should be stored in the head can be returned here
	 * @return
	 */
	public String getStyleText();

	/**
	 * Return a unique ID to reference this element by.
	 * IDs must be unique. If you're confident, return a string with
	 * some unique prefixes. If you're not confident, generate a new UUID
	 * and return that.
	 * @return
	 */
	public String getID();
	
	/**
	 * Returns a mapping between URIKey and webhooks this element would register.
	 * Returning null or an empty map work to refuse any registration
	 * @return
	 */
	public Map<String, WebHook> getWebHooks();
	
}
