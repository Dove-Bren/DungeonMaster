package com.smanzana.dungeonmaster.ui.web.html;

/**
 * AJAX helper class
 * @author Skyler
 *
 */
public class AJAX {

	public static String generate(String url, boolean post, boolean async,
			String payload, String onstatechange) {
		return "var req;\r\n"
				+ "if (window.XMLHttpRequest) {req = new XMLHttpRequest();}\r\n"
				+ "else {req = new ActiveXObject('Microsoft.XMLHTTP');}\r\n"
				+ "req.open('" + (post ? "POST" : "GET") + "', '" + url + "', " + (async ? "true" : "false") + ");\r\n"
				+ (post ? "req.setRequestHeader('Content-Type', 'text/plain');\r\n" : "")
				+ (onstatechange != null ? "req.onreadystatechange = " + onstatechange + ";\r\n" : "")
				+ "req.send(" + (payload != null ? payload : "") + ");\r\n";
	}
	
}
