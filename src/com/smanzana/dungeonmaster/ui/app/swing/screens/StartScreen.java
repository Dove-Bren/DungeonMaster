package com.smanzana.dungeonmaster.ui.app.swing.screens;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.dungeonmaster.ui.app.swing.AppSound;
import com.smanzana.dungeonmaster.ui.app.swing.AppSound.Sound;

public class StartScreen extends JPanel {
	
	private boolean onFresh;
	
	// GUI members
	private JPanel buttonPanel;
	private JButton buttonStart;
	private JButton buttonNew;
	private JButton buttonLoad;
	private JButton buttonTemplate;
	private JButton buttonQuit;
	
	public StartScreen() {
		onFresh = true;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}
	
	public void init() {
		
		JLabel title = new JLabel(AppFrame.createImageIcon("title.png"));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		title.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
		
		this.add(Box.createVerticalGlue());
		this.add(Box.createRigidArea(new Dimension(0, 100)));
		this.add(title);
		this.add(Box.createRigidArea(new Dimension(0, 50)));
		this.add(Box.createVerticalGlue());
		this.add(buttonPanel);
		
		buttonStart = new JButton("Start");
		buttonStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressStart();
				getRootPane().setDefaultButton(buttonLoad);
				AppSound.playSound(Sound.CLICK_LIGHT);
			}
		});
		
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 70)));
		buttonPanel.add(buttonStart);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 70)));
		getRootPane().setDefaultButton(buttonStart);
		
		buttonNew = new JButton("New Session");
		buttonNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("New");
				AppSound.playSound(Sound.CLICK_LIGHT);
			}
		});
		
		buttonLoad = new JButton("Load Session");
		buttonLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Load");
			}
		});
		
		buttonTemplate = new JButton("Template Editor");
		buttonTemplate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Template");
			}
		});
		
		buttonQuit = new JButton("Quit");
		buttonQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DungeonMaster.shutdown();
			}
		});
		
		buttonStart.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonLoad.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonNew.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonTemplate.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonQuit.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(Box.createRigidArea(new Dimension(0,50)));
		this.add(Box.createVerticalGlue());

		buttonNew.setVisible(false);
		buttonLoad.setVisible(false);
		buttonTemplate.setVisible(false);
		buttonQuit.setVisible(false);
		
		buttonStart.setPreferredSize(new Dimension(76, 25));
		buttonNew.setPreferredSize(new Dimension(76, 25));
		buttonLoad.setPreferredSize(new Dimension(76, 25));
		buttonTemplate.setPreferredSize(new Dimension(76, 25));
		buttonQuit.setPreferredSize(new Dimension(76, 25));

		buttonPanel.setPreferredSize(new Dimension(76, 165));
		buttonPanel.setMaximumSize(new Dimension(76, 165));
		buttonPanel.validate();
		buttonPanel.setVisible(true);
		this.validate();
		this.setVisible(true);
	}
	
	public void pressStart() {
		if (!onFresh)
			return;
		
		onFresh = false;
		
		buttonStart.setVisible(false);
		buttonStart.validate();
		buttonPanel.removeAll();
		
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		buttonPanel.add(buttonNew);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		buttonPanel.add(buttonLoad);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		buttonPanel.add(buttonTemplate);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		buttonPanel.add(buttonQuit);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		
		buttonNew.setVisible(true);
		buttonLoad.setVisible(true);
		buttonTemplate.setVisible(true);
		buttonQuit.setVisible(true);
		
		this.validate();		
	}
	
}
