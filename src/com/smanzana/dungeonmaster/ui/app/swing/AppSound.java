package com.smanzana.dungeonmaster.ui.app.swing;

import javax.sound.sampled.Clip;

public class AppSound {

	public static enum Sound {
		CLICK_LIGHT("click_light.wav"),
		CLICK_HEAVY("click_heavy.wav"),
		CLICK_SLIDE("click_slide.wav");
		
		private String path;
		
		private Sound(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
	}
	
	public static void preloadSounds() {
		System.out.print("Preloading audio... ");
		for (Sound sound : Sound.values())
			AppFrame.loadSound(sound.getPath());
		System.out.println("done");
	}
	
	public static void playSound(Sound sound) {
		Clip clip = AppFrame.loadSound(sound.getPath());
		if (clip == null)
			return;
		clip.setFramePosition(0);
		
		clip.start();
	}
	
}
