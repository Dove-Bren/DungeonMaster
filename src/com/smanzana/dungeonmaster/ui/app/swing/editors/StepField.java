package com.smanzana.dungeonmaster.ui.app.swing.editors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.dungeonmaster.utils.StepList;

public class StepField implements ActionListener, EditorField, PropertyChangeListener {

	public static interface StepFieldCallback {
		public void setField(String value);
	}
	
	private static class RangeSegment extends JPanel implements PropertyChangeListener {
		
		private static final long serialVersionUID = -6013376481894020315L;
		private static final Color COLOR_DEFAULT = new Color(186, 186, 186);
		private static final Color COLOR_PRESSED = new Color(186, 73, 73);
		private static final Color COLOR_HOVER = new Color(186, 203, 203);
		
		private JLabel min;
		private JFormattedTextField maxField;
		private JFormattedTextField valueField;
		private StepField parent;
		private boolean criticalClick = false;
		
		public RangeSegment(StepField parent, int min, int max, int value) {
			super(new GridBagLayout());
			this.setPreferredSize(new Dimension(80, 80));
			this.setMaximumSize(new Dimension(80, 80));
			this.setBackground(COLOR_DEFAULT);
			this.parent = parent;
			
			this.min = new JLabel(min + "");
			
			maxField = new JFormattedTextField(NumberFormat.getIntegerInstance());
			maxField.setText(max + "");
			maxField.setColumns(2);
			maxField.addPropertyChangeListener("value", this);
			
			valueField = new JFormattedTextField(NumberFormat.getIntegerInstance());
			valueField.setText(value + "");
			valueField.setColumns(2);
			valueField.addPropertyChangeListener("value", this);
			
			GridBagConstraints cons = new GridBagConstraints();
			cons.fill = GridBagConstraints.HORIZONTAL;

			add(this.min, cons);
			cons.gridx = 1;
			cons.gridy = 0;
			JLabel label = new JLabel("to");
			label.setHorizontalAlignment(JLabel.CENTER);
			add(label, cons);
			cons.gridx = 2;
			cons.gridy = 0;
			add(maxField, cons);
			
			// want to put in center
			cons.gridx = 1;
			cons.gridy = 1;
			label = new JLabel(AppFrame.createImageIcon("icon/d_arrow.png"));
			label.setHorizontalAlignment(JLabel.CENTER);
			add(label, cons);
			
			cons.gridx = 1;
			cons.gridy = 2; // might need to be -1 + 1 y
			add(valueField, cons);
			final RangeSegment me = this;
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						setBackground(COLOR_PRESSED);
						criticalClick = true;
					}
					else {
						criticalClick = false;
					}
					repaint();
				}
				
				public void mouseEntered(MouseEvent e) {
					setBackground(COLOR_HOVER);
				}
				
				public void mouseExited(MouseEvent e) {
					setBackground(COLOR_DEFAULT);
				}
				
				public void mouseReleased(MouseEvent e) {
					if (criticalClick)
					if (e.getX() >= 0 && e.getX() <= getWidth()
					&&  e.getY() >= 0 && e.getY() <= getHeight()) {
						parent.deleteSegment(me);
					}
					
					criticalClick = false;
				}
			});
			
			//this.setBorder(BorderFactory.createLineBorder(Color.black));
			validate();
		}

		public int getMax() {
			return Integer.parseInt(maxField.getText());
		}

		public int getValue() {
			return Integer.parseInt(valueField.getText());
		}

		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			if (arg0.getNewValue() == null || arg0.getNewValue().toString().trim().isEmpty()) {
				((JFormattedTextField) arg0.getSource()).setValue((Long) 0L);
			}

			parent.actionPerformed(null);
		}
		
	}
	
	private JPanel wrapper;
	private JPanel segmentWrapper;
	private StepFieldCallback hook;
	private RangeSegment segments[];
	private int segmentCount = 0;
	private JButton segmentButton;
	private JFormattedTextField largeField; // holds high value
	
	public StepField(String title, StepFieldCallback hook) {
		this(title, hook, null);
	}
	
	public StepField(String title, StepFieldCallback hook, StepList startingList) {
		this.hook = hook;
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		wrapper.add(Box.createHorizontalGlue());
		
		segmentWrapper = new JPanel();
		segmentWrapper.setLayout(new BoxLayout(segmentWrapper, BoxLayout.LINE_AXIS));
		segmentWrapper.setOpaque(false);
		segmentWrapper.add(Box.createHorizontalGlue());
		wrapper.add(segmentWrapper);
		
		if (startingList == null) {
			startingList = new StepList();
			startingList.addStep(5, 1);
			startingList.addStep(10, 2);
		}
		
		fromStepList(startingList);

		wrapper.add(Box.createHorizontalGlue());
		
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		segmentButton = new JButton("Add Column");
		segmentButton.addActionListener((arg0) -> {
			addColumn();
		});
		
		JPanel buttonHighPanel = new JPanel();
		buttonHighPanel.setLayout(new BoxLayout(buttonHighPanel, BoxLayout.PAGE_AXIS));
		buttonHighPanel.setOpaque(false);
		
		buttonHighPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		buttonHighPanel.add(Box.createVerticalGlue());
		
		largeField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		largeField.setMinimumSize(new Dimension(20, 20));
		largeField.setPreferredSize(largeField.getMinimumSize());
		largeField.setMaximumSize(largeField.getPreferredSize());
		largeField.setValue((Long) (long) startingList.getHigh());
		largeField.setColumns(2);
		largeField.addPropertyChangeListener("value", this);
		largeField.setAlignmentX(0.5f);
		largeField.setHorizontalAlignment(JTextField.CENTER);
		buttonHighPanel.add(largeField);
		
		buttonHighPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		buttonHighPanel.add(Box.createVerticalGlue());
		segmentButton.setAlignmentX(0.5f);
		buttonHighPanel.add(segmentButton);
		buttonHighPanel.add(Box.createVerticalGlue());
		buttonHighPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		
		wrapper.add(buttonHighPanel);
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));

		wrapper.validate();
	}
	
	private void addColumn() {
		System.out.println("Add column");
		if (segmentCount >= 15)
			return;
		
		RangeSegment last = segments[segmentCount - 1];
		segments[segmentCount] = new RangeSegment(this, last.getMax() + 1, last.getMax() + 2, last.getValue() + 1);
		segmentWrapper.add(segments[segmentCount++]);//, wrapper.getComponentCount() -3);
		segmentWrapper.add(Box.createHorizontalGlue());
		
		update();
	}
	
	private void update() {
		StepList steps = toStepList();
		fromStepList(steps);
		
		if (this.hook != null)
			this.hook.setField(steps.serialize());
		
		segmentButton.setEnabled(segmentCount < 15);		
		wrapper.validate();
	}
	
	protected void deleteSegment(RangeSegment segment) {
		if (segmentCount != 1) {
			for (int index = 0; index < segments.length; index++) {
				if (segments[index] == segment) {
					segments[index] = null;
					break;
				}
			}
		}
		actionPerformed(null);
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null) {
			; //FIXME TODO
			System.out.println("performed!");
			update();
		}
	}
	
	private StepList toStepList() {
		StepList out = new StepList();
		
		if (segments != null)
		for (RangeSegment seg : segments) {
			if (seg != null)
				out.addStep(seg.getMax(), seg.getValue());
		}
		
		out.setHigh(((Long) largeField.getValue()).intValue());
		
		return out;
	}
	
	/**
	 * Overwrites all internal data with the data from the given steplist
	 * @param in
	 */
	private void fromStepList(StepList in) {
		List<Integer> maxs = new ArrayList<>(in.getMaxs());
		
		if (segments != null)
		for (RangeSegment seg : segments) {
			if (seg != null)
				seg.setVisible(false);
		}
		
		segments = new RangeSegment[Math.max(15, maxs.size())];
		segmentWrapper.removeAll();
		Collections.sort(maxs);
		segmentWrapper.add(Box.createHorizontalGlue());
		
		int lastMax = -100;
		int i = 0;
		for (Integer max : maxs) {
			if (max == null)
				continue;
			int value = in.getValue(max);
			RangeSegment seg = new RangeSegment(this, lastMax + 1, max, value);
			segments[i++] = seg;
			segmentWrapper.add(seg);
			segmentWrapper.add(Box.createHorizontalGlue());
			
			lastMax = max;
		}
		
		segmentCount = i;
		wrapper.validate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// 'largeField' changed.
		if (arg0.getNewValue() == null || arg0.getNewValue().toString().trim().isEmpty()) {
			largeField.setValue((Long) 0L);
		}

		update();
	}
}
