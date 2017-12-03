package com.smanzana.dungeonmaster.ui.web;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.smanzana.dungeonmaster.ui.Comm;
import com.smanzana.dungeonmaster.ui.web.html.HTMLCompatible;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP.HTTPRequest;

public class WebUI extends Comm {

	private Socket connection;
	private Map<String, WebHook> hooks;
	
	public WebUI(Socket connection) {
		this.connection = connection;
		
		hooks = new HashMap<>();
	}
	
	public Socket getSocketConnection() {
		return this.connection;
	}
	
	public boolean sendHTML(HTMLCompatible root) {
		// Clean up any old hooks
		clearHooks();
		
		Map<String, WebHook> rootHooks = root.getWebHooks();
		if (rootHooks != null && !rootHooks.isEmpty()) {
			for (Entry<String, WebHook> row : rootHooks.entrySet()) {
				registerHook(row.getKey(), row.getValue());
			}
		}
		
		return HTTP.sendHTTP(connection, HTTP.generateResponse(HTTP.formatHTML(root)), true);
	}
	
	public void registerHook(String URIKey, WebHook hook) {
		hooks.put(URIKey, hook);
	}
	
	public void clearHooks() {
		hooks.clear();
	}
	
	public boolean doHook(String URIKey, HTTPRequest request, Socket newConnection) {
		if (hooks.isEmpty())
			return false;
		
		for (String key : hooks.keySet())
			if (key.equalsIgnoreCase(URIKey)) {
				this.connection = newConnection;
				hooks.get(key).fire(this, request);
				return true;
			}
		
		return false;
	}
	
}
