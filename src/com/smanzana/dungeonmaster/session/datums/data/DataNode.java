package com.smanzana.dungeonmaster.session.datums.data;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Like a JS object with a name, or a single key:value pair.
 * Has EITHER a string value (primitive) or a list of children. Not both.
 * A node with a string value looks (serialized) like:
 *    <node4>player4</node4>
 * A node with children looks (serialized) like:
 *    <node4>
 *      <child1>...</child1>
 *      ...
 *    </node4>
 * @author Skyler
 *
 */
public class DataNode {

	private String key;
	private String value;
	private Collection<DataNode> children;
	
	/**
	 * Parses serialized collection/nesting of nodes.
	 * Does not work if just passed a primitive value, like "4"
	 * @param serialized
	 * @return
	 */
	public static Collection<DataNode> parse(String serialized) {
		DataNode ret = new DataNode("", serialized);
		
		return ret.children;
	}
	
	/**
	 * Like parse, but returns only the first object.
	 * Still does all the work that parse does.
	 * @param serialized
	 * @return
	 */
	public static DataNode parseSingle(String serialized) {
		Collection<DataNode> nodes = parse(serialized);
		if (nodes != null)
			return nodes.iterator().next();
		
		return null;
	}
	
	/**
	 * Creates a node from a serialized node string.
	 * This is called internally. To represent your data as a DataNode object,
	 * call the {@link DataNode(String key, String value, Collection<DataNode> children)}
	 * constructor.
	 * Input should be ALL text inside encapsulating tags.
	 * Ex: <node>thisisvalue</node> should be passed in "thisisvalue"
	 * @param keyName in above example, "node"
	 * @param serialized
	 */
	public DataNode(String keyName, String serialized) {
		this.children = new LinkedList<DataNode>();
		this.key = keyName;
		serialized = serialized.trim();
		// Check for tags of any sort.
		// If none, primitive
		if (serialized.isEmpty())
			return;
		
		int posOpen = serialized.indexOf("<");
		int posClose = serialized.indexOf(">");
				
		if (posOpen == -1 || posClose == -1) {
			// Primitive
			value = serialized;
			return;
		}

		// if posOpen is not at pos0 we have garbage
		if (posOpen != 0) {
			System.out.println("Found junk before first tag: \"" + serialized.substring(0, Math.min(posOpen + 5, serialized.length())));
			return;
		}
		
		// There exist a tag. Wrap into an object and repeat
		while (posOpen != -1) {
			String openTag = serialized.substring(posOpen, posClose + 1);
			serialized = serialized.substring(posClose + 1).trim();
			
			// look for matching tag
			int closing = serialized.indexOf("</" + openTag.substring(1));
			if (closing == -1) {
				System.out.println("Failed to find matching closing tag for " + openTag);
				return;
			}
			
			// everything inbetween is data for nested node
			String nested = serialized.substring(0, closing);
			DataNode node = new DataNode(openTag.substring(1, openTag.length() -1), nested);
			children.add(node);
			
			serialized = serialized.substring(closing + openTag.length() + 1).trim(); // + 1 for added /
			
			posOpen = serialized.indexOf("<");
			posClose = serialized.indexOf(">");
		}
	}
	
	/**
	 * Includes wrapper tags (from key value)
	 * @param pretty print newlines and tabs?
	 * @return
	 */
	public String serialize(boolean pretty) {
		return serialize(pretty, false, 0);
	}
	
	/**
	 * If headless, prints out only the internals (children/value) of this node
	 * @param pretty
	 * @param headless
	 * @return
	 */
	public String serialize(boolean pretty, boolean headless) {
		return serialize(pretty, headless, 0);
	}
	
	protected String serialize(boolean pretty, boolean headless, int numTabs) {
		String tabs = "";
		if (pretty) {
			for (int i = 0; i < numTabs; i++)
				tabs += "\t";
		}
		String buf = "";
		
		if (!headless)
			buf = tabs + "<" + key + ">";
		
		if (children == null || children.isEmpty()) {
			if (value != null)
				buf += value;
		} else {
			// pretty: add newline here and after each, + tab to tab closing
			if (pretty && !headless) {
				buf += "\n";
			}
			for (DataNode node : children) {
				buf += node.serialize(pretty, false, numTabs + (headless ? 0 : 1));
				if (pretty) {
					buf += "\n";
				}
			}
			if (pretty)
				buf += tabs;
		}
		
		if (!headless)
			buf += "</" + key + ">";
		return buf;
	}
	
	/**
	 * Construct new DataNode object.
	 * Call this to construct a DataNode object representation of your data.
	 * Note: if children is non-null and not empty, value is ignored
	 * @param key
	 * @param value
	 * @param children
	 */
	public DataNode(String key, String value, Collection<DataNode> children) {
		this.key = key;
		
		if (children != null)
			this.children = children;
		else
			this.value = value;
	}
	
	/**
	 * Returns the first child that matches the provided key.
	 * Does not check recursively
	 * @param key
	 * @return
	 */
	public DataNode getChild(String key) {
		DataNode node = null;
		if (children != null && !children.isEmpty()) {
			for (DataNode child : children) {
				if (child.key.equals(key)) {
					node = child;
					break;
				}
			}
		}
		
		return node;
	}
	
	/**
	 * Returns the primitive value of this node.
	 * If the node has children, this is almost certainly null.
	 * Be careful
	 * @return
	 */
	public String getValue() {
		return this.value;
	}
	
	public Collection<DataNode> getChildren() {
		return this.children;
	}
	
}
