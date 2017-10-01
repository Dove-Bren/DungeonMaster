package com.smanzana.dungeonmaster.ui.app.swing;

import java.awt.Dimension;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.smanzana.dungeonmaster.ui.app.AppUI;
import com.smanzana.dungeonmaster.ui.app.swing.screens.StartScreen;
import com.smanzana.dungeonmaster.ui.app.swing.screens.TemplateEditorScreen;

/**
 * Main JFrame
 * @author Skyler
 *
 */
public class AppFrame extends JFrame {

	// Path relative to resources/GUI/ directory
	public static ImageIcon createImageIcon(String path) {
        URL imgURL = AppFrame.class.getResource("/resources/GUI/" + path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, "ImageIcon");
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
	}
	
	private static final Map<String, Clip> soundCache = new HashMap<>();
	private static boolean soundEnabled = true;
	
	// path relative to resources/audio/
	public static Clip loadSound(String path) {
		if (!soundEnabled)
			return null;
		
		if (soundCache.containsKey(path))
			return soundCache.get(path);
		
		URL soundURL = AppFrame.class.getResource("/resources/audio/" + path);
        if (soundURL != null) {
            try {
				Clip ret = AudioSystem.getClip();
				ret.open(AudioSystem.getAudioInputStream(soundURL));
				soundCache.put(path, ret);
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Failed to play audio. Sound is now disabled");
				soundEnabled = false;
				return null;
			}
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
	}
	
	private StartScreen startScreen;
	private TemplateEditorScreen templateScreen;
	
	private AppUI ui;
	
	public AppFrame(AppUI ui) {
		this.ui = ui;
		startScreen = new StartScreen(ui);
//		this.add(startScreen);
//		startScreen.init();
		
		templateScreen = new TemplateEditorScreen(ui);
		this.add(templateScreen);
		templateScreen.init();
		
		this.setPreferredSize(new Dimension(1280, 720));
		this.setMinimumSize(new Dimension(500, 400));
		pack();
	}
}
