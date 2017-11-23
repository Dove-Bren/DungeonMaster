package com.smanzana.dungeonmaster.ui.web.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.smanzana.dungeonmaster.ui.web.WebUI;
import com.smanzana.dungeonmaster.ui.web.html.HTMLCompatible;

// Static class with nice utility functions for senting HTTP requests
public class HTTP {

	public static final int HEADER_LEN_MAX = 5000;

	public static String generateHeader(boolean useGet, String URI, String host, int contentLen) {
		return (useGet ? "GET" : "POST") + " " + URI + " HTTP/1.1\r\n"
				+ "Host: " + host + "\r\nUser-Agent: DungeonMaster/0.1\r\n"
						+ "Accept: text/plain, text/html;q=0.5\r\n"
						+ "Accept-Language: en-US,en;q=0.5\r\n"
						+ (contentLen > 0 ? "Content-Length: " + contentLen + "\r\n" : "")
						+ (contentLen > 0 ? "Content-Type: text/plain\r\n" : "")
						+ "\r\n";
	}
	
	public static String generateResponseHeader() {
			return "HTTP/1.1 200 OK\r\n"
					+ "Content-Type: text/html\r\n"
					+ "r\n\r\n";
	}
	
	public static String readHTTPResponse(Socket connection) throws IOException {
		String response = null;
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
			
			response = result.substring(result.indexOf("\r\n\r\n") + 4);
			
		} while (false);
		
		return response;
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
	
	public static boolean sendHTTP(WebUI connection, String message) {
		return sendHTTP(connection.getConnection(), message);
	}
	
	public static String formatHTML(HTMLCompatible root) {
		String ret = "<html>\r\n<head>\r\n";
		
		ret += "<style>\r\n" + root.getStyleText() + "</style>\r\n";
		ret += "<script language='javascript' type='text/javascript'>\r\n"
				+ root.getScriptText() + "</script>\r\n";
		
		ret += "</head>\r\n<body>\r\n";
		
		ret += root.asHTML();
		
		ret += "\r\n</body>\r\n</html>\r\n";
		return ret;
	}
	
}
