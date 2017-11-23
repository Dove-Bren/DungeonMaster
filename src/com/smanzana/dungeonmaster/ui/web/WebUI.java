package com.smanzana.dungeonmaster.ui.web;

import java.net.Socket;

import com.smanzana.dungeonmaster.ui.Comm;

public class WebUI extends Comm {

	private Socket connection;
	
	public WebUI(Socket connection) {
		this.connection = connection;
	}
	
	public Socket getConnection() {
		return this.connection;
	}
	
}
