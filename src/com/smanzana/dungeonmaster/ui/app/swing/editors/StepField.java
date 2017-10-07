package com.smanzana.dungeonmaster.ui.app.swing.editors;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.dungeonmaster.utils.StepList;

public class StepField implements ActionListener, EditorField {

	public static interface StepFieldCallback {
		public void setField(String value);
	}
	
	private static class RangeSegment extends JPanel implements PropertyChangeListener {
		
		private JLabel min;
		private JFormattedTextField maxField;
		private JFormattedTextField valueField;
		private StepField parent;
		
		public RangeSegment(StepField parent, int min, int max, int value) {
			super(new GridBagLayout());
			setOpaque(false);
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
			
			//this.setBorder(BorderFactory.createLineBorder(Color.black));
			validate();
		}

		public int getMax() {
			return Integer.parseInt(maxField.getText());
		}
		
		public void setMax(int max) {
			maxField.setText(max + "");
		}

		public int getValue() {
			return Integer.parseInt(valueField.getText());
		}
		
		public int getMin() {
			return Integer.parseInt(min.getText());
		}
		
		public void setMin(int min) {
			this.min.setText(min + "");
		}

		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			parent.actionPerformed(null);
		}
		
	}
	
	private JPanel wrapper;
	private JPanel segmentWrapper;
	private StepFieldCallback hook;
	private RangeSegment segments[];
	private int segmentCount = 0;
	private JButton segmentButton;
	
	public StepField(String title, StepFieldCallback hook) {
		this(title, hook, null);
	}
	
	public StepField(String title, StepFieldCallback hook, StepList startingList) {
		this.hook = hook;
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		segmentWrapper = new JPanel();
		segmentWrapper.setLayout(new BoxLayout(segmentWrapper, BoxLayout.LINE_AXIS));
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
		wrapper.add(segmentButton);

		wrapper.validate();
	}
	
	private void addColumn() {
		System.out.println("Add column");
		if (segmentCount >= 15)
			return;
		
		RangeSegment last = segments[segmentCount - 1];
		segments[segmentCount] = new RangeSegment(this, last.getMax() + 1, last.getMax() + 2, last.getValue() + 1);
		segmentWrapper.add(segments[segmentCount++]);//, wrapper.getComponentCount() -3);
		
		if (segmentCount >= 15) {
			segmentButton.setEnabled(false);
		}
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null) {
			; //FIXME TODO
			System.out.println("performed!");
			fromStepList(toStepList());
		}
	}
	
	private StepList toStepList() {
		StepList out = new StepList();
		
		if (segments != null)
		for (RangeSegment seg : segments) {
			if (seg != null)
				out.addStep(seg.getMax(), seg.getValue());
		}
		
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
		
		int lastMax = -100;
		int i = 0;
		for (Integer max : maxs) {
			if (max == null)
				continue;
			int value = in.getValue(max);
			RangeSegment seg = new RangeSegment(this, lastMax + 1, max, value);
			segments[i++] = seg;
			segmentWrapper.add(seg);
			
			lastMax = max;
		}
		
		segmentCount = i;
		wrapper.validate();
	}
}
