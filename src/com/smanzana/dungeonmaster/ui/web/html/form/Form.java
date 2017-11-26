package com.smanzana.dungeonmaster.ui.web.html.form;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smanzana.dungeonmaster.ui.Comm;
import com.smanzana.dungeonmaster.ui.web.WebHook;
import com.smanzana.dungeonmaster.ui.web.WebUI;
import com.smanzana.dungeonmaster.ui.web.html.AJAX;
import com.smanzana.dungeonmaster.ui.web.html.HTMLElement;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP;
import com.smanzana.dungeonmaster.ui.web.utils.HTTP.HTTPRequest;

public class Form extends HTMLElement {
	
	private class FormHook implements WebHook {
		@Override
		public void fire(WebUI comm, HTTPRequest request) {
			if (hook == null || comm == null || request == null)
				return;
			
			// Parse html body as a map
			String body = request.getBody();
			Map<String, String> data = new HashMap<>();
			String action = null;
			// Should be map between key = value
			while (!body.trim().isEmpty()) {
				int pos = body.indexOf("\r\n");
				String row;
				if (pos == -1)
					row = body;
				else
					row = body.substring(0, pos);
				
				if (action == null) {
					action = row;
				} else {
					pos = row.indexOf("=");
					if (pos == -1)
						continue;
					data.put(row.substring(0, pos).trim(), row.substring(pos+1).trim());
				}
				
				body = body.substring(row.length() + 2);
			}
			
			// Regardless of sync or commit, refresh data
			hook.refreshData(comm, data);
			
			// If commit, mark session as ended
			if (action.equalsIgnoreCase("commit")) {
				
			}
		}
	}
	
	public static interface FormInterface {
		
		/**
		 * When feedback is enabled, this function is called to
		 * deliver the newest data to the registered interface.
		 * @param comm
		 * @param data
		 */
		public void refreshData(Comm fromComm, Map<String, String> data);
		
		/**
		 * When form submit button is pressed, this is called.
		 * The form is closed out (disabled) after this call.
		 * A refreshData call directly precedes this call.
		 * @param fromComm
		 */
		public void commit(Comm fromComm);
	}

	private String displayName;
	private List<FormInput> elements;
	private String action;
	private String method;
	private String errorID;
	private int refreshInterval; // In seconds. Sends info to server
	private FormInterface hook;
	
	/**
	 * Same as Form("", "GET")
	 */
	public Form() {
		this("", "GET");
	}
	
	/**
	 * New form with action and method.
	 * @param action The page to send submitted rsults to. If "", will never be forwarded.
	 * 				 This works well with the FormInterface's commit for embedded
	 * 				 forms
	 * @param method
	 */
	public Form(String action, String method) {
		this(UUID.randomUUID().toString(), action, method);
	}
	
	public Form(String ID, String action, String method) {
		super(ID);
		this.action = action;
		this.method = method;
		this.elements = new LinkedList<>();
		hook = null;
		refreshInterval = 0;
		errorID = clean(UUID.randomUUID().toString());
		
		if (action == null)
			action = "";
	}
	
	public void setDisplayName(String display) {
		this.displayName = display;
	}
	
	public void addFeedback(int intervalSeconds, FormInterface hook) {
		this.hook = hook;
		this.refreshInterval = intervalSeconds;
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
		
		ret += "<form id='" + getID() + "' action='" + action + "' method='"
				+ method + "' onsubmit='return _" + getID()+"_submit();" + "'>\r\n";
		
		for (FormInput input : elements) {
			ret += input.asHTML() + "<br />\r\n";
			ret += "<span class='form_label'>" + HTTP.pretty(input.getName()) + "</span><br /><br />\r\n";
			
		}
		
		ret += "<br />" + generateSubmit();
		
		ret += "<br /><br /><span class='form_error' id='" + errorID + "'></span><br />";
		
		ret += "<p><img id='sync_icon' class='sync_active' src='/images/sync_active.png' /></p>\r\n</form>\r\n</center>";
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
		ret += "var error_elem = document.getElementById('" + errorID + "');\r\n" + inputHooks + "\r\n";
		ret += "else error_elem.innerHTML = '';\r\n";
		
		if (action.isEmpty()) {
			// Trigger commit hook with AJAX
			ret += "\r\n";
			ret += "return false;\r\n}\r\n";	
		} else {
			ret += "return true;\r\n}\r\n";
		}
		
		if (refreshInterval > 0 && this.hook != null) {
			ret += generateFeedback() + "\r\n";
		}
		
		return ret;
	}
	
	@Override
	public String getStyleText() {
		String ret =  ".form_label {\r\nwidth: 100px;\r\nfont-weight: bold;\r\n}\r\n"
				+ ".form_error {margin-bottom: 15px;\r\ncolor: red;\r\nfont-weight: bold;\r\n}\r\n"
				+ ".sync_inactive {\r\nposition: absolute;\r\nright: 20px;\r\nbottom: 20px;\r\n}\r\n"
				+ "@keyframes frames_sync_active1 {0%{width: 24px; height: auto;} 50%{transform:rotateZ(180deg); width: 32px;} 100%{transform:rotateZ(360deg); width: 24px;}}\r\n"
				+ "@keyframes frames_sync_active2 {0%{width: 24px; height: auto;} 50%{transform:rotateZ(180deg); width: 32px;} 100%{transform:rotateZ(360deg); width: 24px;}}\r\n"
				+ ".sync_active {\r\nposition: absolute;\r\nright: 20px;\r\nbottom: 20px;\r\nanimation: frames_sync_active1 2s linear forwards;\r\n}\r\n"
				+ ".sync_active2 {\r\nposition: absolute;\r\nright: 20px;\r\nbottom: 20px;\r\nanimation: frames_sync_active2 2s linear forwards;\r\n}\r\n";
		
		// Form doesn't have any
		for (FormInput input : elements) {
			ret += input.getStyleText() + "\r\n";
		}
		
		return ret;
	}
	
	// Creates JS async loop of sending feedback to server
	private String generateFeedback() {
		String ids = "";
		boolean first = true;
		for (FormInput input : elements) {
			if (first) first = false;
			else ids += ", ";
			
			ids += "'" + input.getID() + "' ";
			
		}
		return ""
				+ "function connection_uptime() {\r\n"
				+ "var elem = document.getElementById('sync_icon');\r\n"
				+ "if (elem.className == 'sync_active') elem.className = 'sync_active2';\r\n"
				+ "else elem.className = 'sync_active';\r\n"
				+ "elem.src = 'images/sync_active.png';\r\n}\r\n"
				+ "function connection_downtime() {\r\n"
				+ "var elem = document.getElementById('sync_icon');\r\n"
				+ "elem.className = 'sync_inactive';\r\nelem.src = 'images/sync_dead.png';\r\n}\r\n"
				+ "function _" + getID() + "_feedback() {\r\n"
				+ "var content = 'sync\\r\\n'\r\n;\r\n"
				+ "var ids = [" + ids + "];\r\n"
				+ "var i;"
				+ "for (i = 0; i < ids.length; i++) {\r\n"
				+ "  var elem = document.getElementById(ids[i]);\r\n"
				+ "  content = content + elem.name + ' = ' + elem.value + '\\r\\n';\r\n"
				+ "}\r\n"
				+ "var timehandle = setTimeout(connection_downtime, 1000);\r\n"
				+ AJAX.generate("hook_" + getID() + "", true, true, "content", ("function() {\r\n"
						+ "clearTimeout(timehandle);\r\n"
						+ "if (this.readyState == 4 && this.status == 200) {\r\n"
						+ "setTimeout(_" + getID() + "_feedback, "
						+ refreshInterval * 1000 + ");\r\n"
						+ "connection_uptime();\r\n"
						+ "}\r\n"
						+ "}"))
				+ "}\r\n"
				+ "setTimeout(_" + getID() + "_feedback, "
				+ refreshInterval * 1000 + ");\r\n";
				
	}
	
	@Override
	public Map<String, WebHook> getWebHooks() {
		Map<String, WebHook> map = new HashMap<>();
		
		map.put("hook_" + getID(), new FormHook());
		
		return map;
	}
	
}
