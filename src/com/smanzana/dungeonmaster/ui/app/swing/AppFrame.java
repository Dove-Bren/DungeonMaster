package com.smanzana.dungeonmaster.ui.app.swing;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.smanzana.dungeonmaster.ui.app.swing.screens.StartScreen;

/**
 * Main JFrame
 * @author Skyler
 *
 */
public class AppFrame extends JFrame {

	public static ImageIcon createImageIcon(String path) {
        URL imgURL = AppFrame.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, "ImageIcon");
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
	}
	
	private StartScreen startScreen;
	
	public AppFrame() {
		startScreen = new StartScreen();
		this.add(startScreen);
		startScreen.init();
		
	}
}
