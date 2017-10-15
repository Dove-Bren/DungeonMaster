package com.smanzana.dungeonmaster.ui.app.swing.editors.fields;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.smanzana.dungeonmaster.session.configuration.Config;
import com.smanzana.dungeonmaster.session.datums.ActionDatumData;
import com.smanzana.dungeonmaster.ui.EditorDisplayable;
import com.smanzana.dungeonmaster.ui.app.UIColor;
import com.smanzana.dungeonmaster.ui.app.swing.AppFrame;
import com.smanzana.dungeonmaster.ui.app.swing.editors.ConfigEditor;
import com.smanzana.dungeonmaster.ui.app.swing.editors.DMEditor;
import com.smanzana.dungeonmaster.ui.app.swing.editors.DatumEditor;

/**
 * List of elements that are selected from another set.
 * Sort of a sub-set selector. You get a list of all possible values.
 * You select elements from there and add them to the working list.
 * @author Skyler
 */
public class GrabListField<T extends EditorDisplayable> implements ActionListener, EditorField {

	public static interface GrabFieldCallback {
		// list of T
		public abstract void setField(List<Object> valueName);
	}
	
	private static class GrabListData implements Serializable {
		private static final long serialVersionUID = 1051923L;
		private static String PREFIX = "_GrabFieldData";
		private String value;
		private boolean from;
		
		public GrabListData(UUID id, boolean from, int[] indices) {
			this.from = from;
			value = PREFIX + id.toString()
				+":";
			boolean first = false;
			for (int i : indices) {
				if (!first)
					first = true;
				else
					value += ",";
				value += i;
			}
		}
		
		public boolean isFrom() {
			return from;
		}
		
		public UUID fetchID() {
			try {
				return UUID.fromString(value.substring(PREFIX.length(), value.indexOf(":")));
			} catch (Exception e) {
				return null;
			}
		}
		
		public int[] fetchIndices() {
			String sub = value.substring(value.indexOf(':') + 1);
			String[] list = sub.split(",");
			int[] indx = new int[list.length];
			for (int i = 0; i < list.length; i++) {
				try {
					indx[i] = Integer.parseInt(list[i]);
				} catch (NumberFormatException e) {
					indx[i] = -1;
				}
			}
			
			return indx;
		}
	}
	
	private static DataFlavor GRABLIST_FLAVOR = null;
	private static boolean DD_ENABLED = true;
	
	private DefaultListModel<T> from;
	private DefaultListModel<T> to;
	private JList<T> fromList;
	private JList<T> toList;
	private JPanel wrapper;
	private GrabFieldCallback hook;
	private UUID uuid;
	
	public GrabListField(String title, GrabFieldCallback hook, List<T> options) {
		this(title, hook, options, null);
	}
	
	/**
	 * Initializes including selected all elements from <i>alreadySelected</i>.
	 * Uses {@link EditorDisplayable#getEditorName} as key value for equals lookups
	 * and uniqueness-check
	 * @param title
	 * @param hook
	 * @param options
	 * @param alreadySelected
	 */
	public GrabListField(String title, GrabFieldCallback hook, List<T> options, List<T> alreadySelected) {
		this.hook = hook;
		this.uuid = UUID.randomUUID();
		
		if (GrabListField.DD_ENABLED && GrabListField.GRABLIST_FLAVOR == null) {
			try {
				GrabListField.GRABLIST_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=" + GrabListData.class.getName());
			} catch (ClassNotFoundException e) {
				System.out.println("Failed lookup of grablist data class. Drag&Drop between grablists is disabled");
				GrabListField.DD_ENABLED = false;
			}
			System.err.println("Lookup on: " + GrabListData.class.getName());
		}
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		from = new DefaultListModel<>();
		fromList = new JList<T>(from);
		fromList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fromList.setLayoutOrientation(JList.VERTICAL);
		fromList.setVisibleRowCount(10);
		fromList.setMaximumSize(fromList.getPreferredScrollableViewportSize());
		fromList.setDragEnabled(GrabListField.DD_ENABLED);
		fromList.setDropMode(DropMode.ON_OR_INSERT);
		UIColor.setColors(fromList, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		fromList.setCellRenderer((list, e, index, isSelected, focus) -> {
			JLabel comp;
			
			comp = new JLabel(e.getEditorName());
			
			Color background;
	        Color foreground;

	        // check if this cell represents the current DnD drop location
	        JList.DropLocation dropLocation = list.getDropLocation();
	        if (dropLocation != null
	                && !dropLocation.isInsert()
	                && dropLocation.getIndex() == index) {

	        	background = Color.BLUE;
	            foreground = Color.WHITE;

	        // check if this cell is selected
	        } else if (isSelected) {
	            background = Color.RED;
	            foreground = Color.WHITE;

	        // unselected, and not the DnD drop location
	        } else {
	            background = Color.WHITE;
	            foreground = Color.BLACK;
	        };

	        comp.setBackground(background);
	        comp.setForeground(foreground);
			
			return comp;
		});
		
		fromList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e, true);
			}
		});
		
		fromList.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 305779L;
		            
		    /**
		     * Items are serialized to strings
		     */
		    public boolean canImport(TransferHandler.TransferSupport info) {
		        // Check for String flavor
		        if (!info.isDataFlavorSupported(GrabListField.GRABLIST_FLAVOR)) {
		            return false;
		        }
		        GrabListData data;
		        try {
					data = (GrabListData) info.getTransferable().getTransferData(GRABLIST_FLAVOR);
				} catch (UnsupportedFlavorException | IOException e) {
					return false;
				}
		        
		        return (uuid.equals(data.fetchID()));
		   }

		    protected Transferable createTransferable(JComponent c) {
		        int[] indices = fromList.getSelectedIndices();

		        final GrabListData data = new GrabListData(uuid, true, indices);
		        
		        return new Transferable() {
					@Override
					public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
						if (arg0.equals(GrabListField.GRABLIST_FLAVOR))
							return data;
						
						throw new UnsupportedFlavorException(arg0);
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[]{GrabListField.GRABLIST_FLAVOR};
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						return arg0.equals(GrabListField.GRABLIST_FLAVOR);
					}
		        };
		    }
		    
		    public int getSourceActions(JComponent c) {
		        return TransferHandler.MOVE;
		    }
		    
		    /**
		     * Perform the actual import.
		     */
		    public boolean importData(TransferHandler.TransferSupport info) {
		        if (!info.isDrop()) {
		            return false;
		        }
		        
		        if (!info.isDataFlavorSupported(GrabListField.GRABLIST_FLAVOR))
		        	return false;
		        GrabListData data;
		        try {
					data = (GrabListData) info.getTransferable().getTransferData(GrabListField.GRABLIST_FLAVOR);
				} catch (UnsupportedFlavorException | IOException e) {
					return false;
				}
		        
		        int pos = ( (JList.DropLocation) info.getDropLocation()).getIndex();
		        int[] indx = data.fetchIndices();
		        
		        if (data.isFrom()) {
		        	// Grab a list of the objects.
		        	// Then remove all the objects. Keep track of insertion point.
		        	// Then insert all the objects
		        	List<T> objs = new LinkedList<>();
		        	for (int i : indx) {
		        		if (i == -1)
		        			continue;
		        		objs.add(from.get(i));
		        	}
		        	for (int i = indx.length - 1; i >= 0; i--) {
		        		if (indx[i] == -1)
		        			continue;
		        		if (indx[i] < pos) // TODO <=?
		        			pos--;
		        		from.remove(indx[i]);
		        	}
		        	
		        	for (T obj : objs) {
		        		from.add(pos++, obj);
		        	}
		        			        	
		        	return true;
		        }
		        

		        transfer(pos, indx, false);
		        
		        return true;
		    }

		    /**
		     * Remove the items moved from the list.
		     */
		    protected void exportDone(JComponent c, Transferable data, int action) {
		    	// Recipient does cleanup. Nothing to do here
		    } 
		});
		
		to = new DefaultListModel<>();
		toList = new JList<>(to);
		toList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		toList.setLayoutOrientation(JList.VERTICAL);
		toList.setVisibleRowCount(10);
		toList.setMaximumSize(toList.getPreferredScrollableViewportSize());
		toList.setDragEnabled(GrabListField.DD_ENABLED);
		toList.setDropMode(DropMode.INSERT);
		UIColor.setColors(toList, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		toList.setCellRenderer((list, e, index, isSelected, focus) -> {
			JLabel comp;
			
			comp = new JLabel(e.getEditorName());
			
			Color background;
	        Color foreground;

	        // check if this cell represents the current DnD drop location
	        JList.DropLocation dropLocation = list.getDropLocation();
	        if (dropLocation != null
	                && !dropLocation.isInsert()
	                && dropLocation.getIndex() == index) {

	        	background = Color.BLUE;
	            foreground = Color.WHITE;

	        // check if this cell is selected
	        } else if (isSelected) {
	            background = Color.RED;
	            foreground = Color.WHITE;

	        // unselected, and not the DnD drop location
	        } else {
	            background = Color.WHITE;
	            foreground = Color.BLACK;
	        };

	        comp.setBackground(background);
	        comp.setForeground(foreground);
			
			return comp;
		});

		toList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e, false);
			}
		});
		
		toList.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1021927;
		            
		    /**
		     * Items are serialized to strings
		     */
		    public boolean canImport(TransferHandler.TransferSupport info) {
		        // Check for String flavor
		        if (!info.isDataFlavorSupported(GrabListField.GRABLIST_FLAVOR)) {
		            return false;
		        }
		        GrabListData data;
		        try {
					data = (GrabListData) info.getTransferable().getTransferData(GRABLIST_FLAVOR);
				} catch (UnsupportedFlavorException | IOException e) {
					return false;
				}
		        
		        return (uuid.equals(data.fetchID()));
		   }

		    protected Transferable createTransferable(JComponent c) {
		        int[] indices = toList.getSelectedIndices();

		        final GrabListData data = new GrabListData(uuid, false, indices);
		        
		        return new Transferable() {
					@Override
					public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
						if (arg0.equals(GrabListField.GRABLIST_FLAVOR))
							return data;
						
						throw new UnsupportedFlavorException(arg0);
					}

					@Override
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[]{GrabListField.GRABLIST_FLAVOR};
					}

					@Override
					public boolean isDataFlavorSupported(DataFlavor arg0) {
						return arg0.equals(GrabListField.GRABLIST_FLAVOR);
					}
		        };
		    }
		    
		    public int getSourceActions(JComponent c) {
		        return TransferHandler.MOVE;
		    }
		    
		    /**
		     * Perform the actual import.
		     */
		    public boolean importData(TransferHandler.TransferSupport info) {
		        if (!info.isDrop()) {
		            return false;
		        }
		        
		        if (!info.isDataFlavorSupported(GrabListField.GRABLIST_FLAVOR))
		        	return false;

		        GrabListData data;
		        try {
					data = (GrabListData) info.getTransferable().getTransferData(GrabListField.GRABLIST_FLAVOR);
				} catch (UnsupportedFlavorException | IOException e) {
					return false;
				}
		        
		        // If !isFrom(), just a re-ordering
		        int pos = ( (JList.DropLocation) info.getDropLocation()).getIndex();
		        int[] indx = data.fetchIndices();
		        
		        if (!data.isFrom()) {
		        	// Grab a list of the objects.
		        	// Then remove all the objects. Keep track of insertion point.
		        	// Then insert all the objects
		        	List<T> objs = new LinkedList<>();
		        	for (int i : indx) {
		        		if (i == -1)
		        			continue;
		        		objs.add(to.get(i));
		        	}
		        	for (int i = indx.length - 1; i >= 0; i--) {
		        		if (indx[i] == -1)
		        			continue;
		        		if (indx[i] < pos)
		        			pos--;
		        		to.remove(indx[i]);
		        	}
		        	
		        	for (T obj : objs) {
		        		to.add(pos++, obj);
		        	}
		        			        	
		        	return true;
		        }
		        
		        // Grab list of indices
		        // Then pull those objects FROM the fromlist in
		        transfer(pos, indx, true);
		        
		        return true;
		    }

		    /**
		     * Remove the items moved from the list.
		     */
		    protected void exportDone(JComponent c, Transferable data, int action) {

//		        if (action == TransferHandler.MOVE) {
//		            for (int i = indices.length - 1; i >= 0; i--) {
//		                from.remove(indices[i]);
//		            }
//		        }
//		        
//		        indices = null;
		    } 
		});
		
		// Add any existing options to the 'to'.
		// Any options that aren't already in the 'to', add to
		// from
		
		List<T> seen = new LinkedList<>();
		if (alreadySelected != null)
			for (T t : alreadySelected) {
				to.addElement(t);
				seen.add(t);
			}
		
		for (T t : options) {
			if (!to.isEmpty()) {
				// check for t in to
				boolean found = false;
				for (T existing : seen)
				if (existing.getEditorName().equals(t.getEditorName())) {
					found = true;
					break;
				}
				if (found)
					continue;
			}
			from.addElement(t);
		}
		
		wrapper.add(new JScrollPane(fromList));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(new JLabel(AppFrame.createImageIcon("icon/r_arrow.png")));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(new JScrollPane(toList));
		
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		wrapper.add(Box.createHorizontalGlue());
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null)
			hook.setField((List<Object>) Collections.list(to.elements()));
	}
	
	public void doMousePressed(MouseEvent e, boolean isFrom) {
		if (e.getClickCount() != 2)
			return;
		
		JList<T> list = (isFrom ? fromList : toList);
		
		// Transfer everything selected
		
		int[] indx = list.getSelectedIndices();
		transfer(-1, indx, isFrom);
	}
	
	private void transfer(int pos, int[] indices, boolean fromTo) {
		//fromTo is true if going from from to to
		//otherways going to to from
		DefaultListModel<T> source, dest;
		if (fromTo) {
			source = from;
			dest = to;
		} else {
			source = to;
			dest = from;
		}
		
		if (pos == -1) {
			// add to end
			pos = dest.size();
		}
		
		for (int i : indices) {
    		if (i == -1)
    			continue;
        	T t = source.elementAt(i);
        	dest.add(pos++, t);
        }
        for (int i = indices.length - 1; i >= 0; i--) {
    		if (i == -1)
    			continue;
        	// Do AFTER we iterate to add
        	// cleanup from end to start
        	source.remove(indices[i]);
        }
        
        actionPerformed(null);
	}
	
}
