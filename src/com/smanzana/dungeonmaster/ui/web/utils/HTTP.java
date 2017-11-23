package com.smanzana.dungeonmaster.ui.web.utils;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.smanzana.dungeonmaster.ui.app.AppUIColor;
import com.smanzana.dungeonmaster.ui.web.html.HTMLCompatible;

// Static class with nice utility functions for sending HTTP requests
public class HTTP {
	
	public static class HTTPRequestHeader {
		
		private boolean useGet;
		private String URI;
		private String host;
		private int contentLen;
		
		private HTTPRequestHeader() {
			useGet = false;
			URI = "";
			host = "";
			contentLen = 0;
		}
		
		public HTTPRequestHeader setGet(boolean get) {
			useGet = get;
			return this;
		}
		
		public HTTPRequestHeader setURI(String URI) {
			this.URI = URI;
			return this;
		}
		
		public HTTPRequestHeader setHost(String host) {
			this.host = host;
			return this;
		}
		
		public HTTPRequestHeader setContentLen(int len) {
			this.contentLen = len;
			return this;
		}
		
		public String asText() {
			return toString();
		}
		
		public boolean isUseGet() {
			return useGet;
		}

		public String getURI() {
			return URI;
		}

		public String getHost() {
			return host;
		}

		public int getContentLen() {
			return contentLen;
		}

		@Override
		public String toString() {
			return (useGet ? "GET" : "POST") + " " + URI + " HTTP/1.1\r\n"
					+ "Host: " + host + "\r\nUser-Agent: DungeonMaster/0.1\r\n"
							+ "Accept: text/plain, text/html;q=0.5\r\n"
							+ "Accept-Language: en-US,en;q=0.5\r\n"
							+ (contentLen > 0 ? "Content-Length: " + contentLen + "\r\n" : "")
							+ (contentLen > 0 ? "Content-Type: text/plain\r\n" : "")
							+ "\r\n";
		}
	}
	
	public static class HTTPRequest {
		private HTTPRequestHeader header;
		private String body;
		
		private HTTPRequest() {
			header = new HTTPRequestHeader();
			body = "";
		}
		
		public String asText() {
			return toString();
		}
		
		public HTTPRequestHeader getHeader() {
			return header;
		}
		
		public String getBody() {
			return body;
		}
		
		public HTTPRequest setHeader(HTTPRequestHeader header) {
			this.header = header;
			return this;
		}
		
		public HTTPRequest setBody(String body) {
			this.body = body;
			return this;
		}
		
		@Override
		public String toString() {
			return header.asText() + body;
		}
	}

	public static final int HEADER_LEN_MAX = 5000;

	public static HTTPRequestHeader generateRequestHeader(boolean useGet, String URI, String host, int contentLen) {
		return new HTTPRequestHeader()
				.setGet(useGet)
				.setURI(URI)
				.setHost(host)
				.setContentLen(contentLen);
	}
	
	public static HTTPRequest generateRequest(boolean useGet, String URI, String host, String content) {
		HTTPRequest req = new HTTPRequest()
				.setHeader(generateRequestHeader(useGet, URI, host, 
						(content == null || content.trim().isEmpty() ? 0 : content.length())));
		
		if (content != null && !content.trim().isEmpty())
			req.setBody(content);
		
		return req;
	}
	
	public static String generateResponseHeader() {
		return generateResponseHeader(200, "OK");
	}
	
	public static String generateResponseHeader(int code, String desc) {
			return "HTTP/1.1 " + code + " " + desc + "\r\n"
					+ "Content-Type: text/html\r\n"
					+ "r\n\r\n";
	}
	
	public static String trimHTTPHeader(String raw) {
		if (!raw.contains("\r\n\r\n")) {
			return raw; 
		}
		
		return raw.substring(raw.indexOf("\r\n\r\n") + 4);
	}
	
	public static HTTPRequest readHTTPRequest(Socket connection) throws IOException {
		HTTPRequest request = null;
		InputStreamReader reader = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		char buf[] = new char[HEADER_LEN_MAX];
		int len;
		
		do {
			if (connection.isClosed())
				break;
			
			try {
			len = reader.read(buf, 0, HEADER_LEN_MAX);
			} catch (SocketTimeoutException e) {
				len = 0;
			}
			if (len == -1) {
				System.err.println("Got -1 from read");
				break;
			}
			
			if (len == 0) {
				System.err.println("got 0 (timeout) from read");
				break;
			}
			
			buffer.append(buf, 0, len);
			
			String result = buffer.toString();
			if (!result.toString().contains("\r\n\r\n")) {
				System.err.println("Got large (> " + HEADER_LEN_MAX + ") response");
				break; // No end of header. It's too big. 
			}
			
			request = parseHTTPRequest(result);
			
		} while (false);
		
		return request;
	}
	
	public static boolean sendHTTP(Socket connection, String message) {
		if (connection == null || !connection.isConnected()
				|| connection.isClosed())
			return false;
		
		try {
			PrintWriter writer = new PrintWriter(connection.getOutputStream());
			writer.print(generateResponseHeader());
			writer.print(message);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to deliver page to connection: "
					+ connection.getInetAddress());
			return false;
		}
		
		return true;
	}
	
//	public static boolean sendHTTP(WebUI connection, String message) {
//		return sendHTTP(connection.getConnection(), message);
//	}
	
	public static String formatHTML(HTMLCompatible root) {
		String ret = "<html>\r\n<head>\r\n";
		
		ret += "<style>\r\nbody {\r\nbackground-color: #" + getRGBWord(AppUIColor.peek(AppUIColor.Key.BASE_BACKGROUND)) + ";\r\ncolor: #" + getRGBWord(AppUIColor.peek(AppUIColor.Key.BASE_FOREGROUND)) + ";\r\n}\r\n" 
		
				+ root.getStyleText() + "</style>\r\n";
		ret += "<script language='javascript' type='text/javascript'>\r\n"
				+ root.getScriptText() + "</script>\r\n";
		
		ret += "</head>\r\n<body>\r\n";
		
		ret += root.asHTML();
		
		ret += "\r\n</body>\r\n</html>\r\n";
		return ret;
	}
	
	/**
	 * Capitalizes the first letter. Converts _ and camel-casing to spaces
	 * @param raw
	 * @return
	 */
	public static String pretty(String raw) {
		int len = raw.length();
		
		StringBuffer buf = new StringBuffer(raw);
		buf.setCharAt(0, Character.toUpperCase(raw.charAt(0)));
		for (int i = 1; i < len; i++) {
			if (Character.isUpperCase(buf.charAt(i))) {
				// camel case! insert space
				buf.insert(i, ' ');
				i += 1;
				continue;
			}
			
			if (buf.charAt(i) == '_') {
				// snake case! Replace with space!
				buf.setCharAt(i, ' ');
				continue;
			}
		}
		
		return buf.toString();
	}
	
	public static String getRGBWord(Color color) {
		return String.format("%2x%2x%2x",
				color.getRed(),
				color.getGreen(),
				color.getBlue());
	}
	
	// Also works on responses!
	private static HTTPRequest parseHTTPRequest(String raw) {
		if (raw == null || raw.trim().isEmpty() || raw.indexOf("\r\n") == -1)
			return null;
		
		String requestLine = raw.substring(0, raw.indexOf("\r\n")); // First line
		HTTPRequestHeader header = new HTTPRequestHeader();
		String[] tokens = requestLine.split(" ");
		if (tokens.length != 3)
			return null;
		
		// We only support POST and simple GET
		header.setGet(!tokens[0].trim().equalsIgnoreCase("POST"));
		header.setURI(breakURI(tokens[1]));
		// Discard HTTP version
		// Fish for content-length
		int pos;
		pos = raw.indexOf("Content-Length:");
		if (pos != -1) {
			String len = raw.substring(pos + 15, raw.indexOf("\r\n", pos + 15)).trim();
			try {
				header.setContentLen(Integer.parseInt(len));
			} catch (NumberFormatException e) {
				header.setContentLen(0);
				System.err.println("Failed to parse content-length from ["
						+ len + "]");
			}
		}
		
		HTTPRequest request = new HTTPRequest().setHeader(header);
		raw = trimHTTPHeader(raw);
		request.setBody(raw.substring(0, header.contentLen));
		
		return request;
	}
	
	// Removes any get parameters
	private static String breakURI(String raw) {
		if (raw.indexOf("&") == -1)
			return raw;
		
		return raw.substring(0, raw.indexOf("&"));
	}
}
