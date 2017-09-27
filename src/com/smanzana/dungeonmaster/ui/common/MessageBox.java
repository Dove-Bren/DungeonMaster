package com.smanzana.dungeonmaster.ui.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a message box to the user.
 * This includes a message and all possible options
 * The message box returns the text of what was pressed
 * as a return
 * @author Skyler
 *
 */
public class MessageBox {

	private String message;
	private List<String> options;
	
	public MessageBox(String message, List<String> options) {
		this.message = message;
		this.options = options;
	}
	
	public MessageBox(String message, String ... options) {
		this.message = message;
		this.options = new ArrayList<>(options.length + 1);
		for (String s : options)
			this.options.add(s);
	}
	
	public String getMessage() {
		return message;
	}
	
	public List<String> getOptions() {
		return options;
	}
	
}
