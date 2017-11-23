package com.smanzana.dungeonmaster.ui.web;

import com.smanzana.dungeonmaster.ui.web.utils.HTTP.HTTPRequest;

public interface WebHook {

	/**
	 * Generic firing function. Provides the comm the hook was registered to
	 * and teh entire request object for custom parsing
	 * @param comm
	 * @param request
	 */
	public void fire(WebUI comm, HTTPRequest request);
	
}
