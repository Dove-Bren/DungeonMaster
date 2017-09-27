package com.smanzana.dungeonmaster.ui;

/**
 * UI callback.
 * Actually used on UI AND main thread as generic callback.
 * UI uses to internally keep track of what to do once it gets replies back from clients
 * Main thread uses it to handle what happens when UI returns a response
 * @author Skyler
 *
 */
public interface UICallback {

	public void callback(String serialResponse);
	
}
