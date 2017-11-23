package com.smanzana.dungeonmaster.ui.web.html.form;

import java.util.UUID;

import com.smanzana.dungeonmaster.ui.web.html.HTMLElement;

public class TextInput extends HTMLElement implements FormInput {

	private String startString;
	private String name;
	private boolean vInteger;
	private boolean vNoNumbers;
	private int maxLen; // < 1 is don't check
	private int minLen; // < 0 is don't check
	
	public TextInput(String name, String startingText) {
		this(UUID.randomUUID().toString(), name, startingText);
	}
	
	public TextInput(String ID, String name, String startingText) {
		super(ID);
		this.name = name;
		this.startString = startingText;
		
		maxLen = 0;
		minLen = -1;
	}
	
	/**
	 * Marks this field as requiring only numbers.
	 * This turns of NoNumbers
	 */
	public TextInput integer() {
		vInteger = true;
		vNoNumbers = false;
		return this;
	}
	
	/**
	 * Disallows numbers. Turns of Integer setting
	 */
	public TextInput noNumbers() {
		vNoNumbers = true;
		vInteger = false;
		return this;
	}
	
	/**
	 * Sets min. If < 0 (-1 by default), no minimum
	 * @param min
	 * @return
	 */
	public TextInput min(int min) {
		minLen = min;
		return this;
	}
	
	/**
	 * Sets max. If < 1 (0 by default), no minimum
	 * @param max
	 * @return
	 */
	public TextInput max(int max) {
		maxLen = max;
		return this;
	}
	
	@Override
	public String asHTML() {
		return "<input type='text' name='" + name + "'"
				+ " id='" + getID() + "'"
				+ (startString == null ? "" : " value=' "+ startString + "'")
				+ " />";
	}

	@Override
	public String getScriptText() {
		if (!vInteger && !vNoNumbers && maxLen < 1 && minLen < 0)
			return "";
		
		String ret = "function _" + getID() + "_validate() {\r\n";
		
		ret += "var val = document.getElementById('" + getID() + "');\r\n";
		if (vInteger) {
			ret += "if (isNan(val)) return false;\r\n";
		} else if (vNoNumbers) {
			ret += "if (/^.*\\d.*$/.test(val)) return false;\r\n";
		}
		
		if (maxLen > 0 || minLen > -1) {
			ret += "var len = val.length;\r\n";
			if (maxLen > 0) {
				ret += "if (len > " + maxLen + ") return false;\r\n";
			}
			if (minLen > -1) {
				ret += "if (len < " + minLen + ") return false;\r\n";
			}
		}
		
		ret += "return true;\r\n}";
		return ret;
	}

	@Override
	public String getStyleText() {
		return "";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValidationHook() {
		if (!vInteger && !vNoNumbers && maxLen < 1 && minLen < 0)
			return "";
		
		return "_" + getID() + "_validate()";
	}

}
