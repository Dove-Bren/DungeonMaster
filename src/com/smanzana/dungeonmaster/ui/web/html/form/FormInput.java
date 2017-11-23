package com.smanzana.dungeonmaster.ui.web.html.form;

import com.smanzana.dungeonmaster.ui.web.html.HTMLCompatible;

public interface FormInput extends HTMLCompatible {

	/**
	 * Returns the name of this input
	 * @return
	 */
	public String getName();
	
	/**
	 * Return a call to a function to perform validation.
	 * This is optional. Returning null or emptystr works to
	 * skip validation for this element. Otherwise, returns should
	 * be in the form "myfunc()" (NO SEMICOLON!)
	 * @return
	 */
	public String getValidationHook();
	
}
