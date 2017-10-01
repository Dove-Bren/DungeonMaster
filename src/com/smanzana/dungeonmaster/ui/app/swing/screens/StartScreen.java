package com.smanzana.dungeonmaster.ui.app.swing.screens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;

public class StartScreen extends JPanel {
	
	private boolean onFresh;
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
		
		this.add(Box.createVerticalGlue());
		this.add(new JLabel(AppFrame.createImageIcon("resources/title.png")));
		this.add(Box.createRigidArea(new Dimension(0, 50)));
		this.add(Box.createVerticalGlue());
		
		buttonStart = new JButton("Start");
		buttonStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressStart();
				getRootPane().setDefaultButton(buttonLoad);
			}
		});
		this.add(buttonStart);
		getRootPane().setDefaultButton(buttonStart);
		
		buttonNew = new JButton("New Session");
		buttonNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("New");
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
				System.out.println("Quit");
			}
		});
		
		this.add(buttonNew);
		this.add(buttonLoad);
		this.add(buttonTemplate);
		this.add(buttonQuit);

		buttonNew.setVisible(false);
		buttonLoad.setVisible(false);
		buttonTemplate.setVisible(false);
		buttonQuit.setVisible(false);
		
		this.validate();
		this.setVisible(true);
	}
	
	public void pressStart() {
		if (!onFresh)
			return;
		
		onFresh = false;
		buttonStart.setVisible(false);
		buttonNew.setVisible(true);
		buttonLoad.setVisible(true);
		buttonTemplate.setVisible(true);
		buttonQuit.setVisible(true);
		
		this.validate();		
	}
	
}
