package com.smanzana.dungeonmaster.setting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.Interactable;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.Notable;
import com.smanzana.dungeonmaster.utils.NoteUtil;

/**
 * A place/location. THAT kind of setting.
 * @author Skyler
 *
 */
public class Setting implements Notable, DataCompatible, Interactable {

	private String title;
	private String description;
	private List<String> notes;
	private List<Action> playerActions;
	private List<Action> adminActions;
	
	public Setting() {
		this.notes = new LinkedList<>();
		this.playerActions = new LinkedList<>();
		this.adminActions = new LinkedList<>();
	}
	
	public Setting(String title, String description) {
		this();
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addPlayerAction(Action action) {
		this.playerActions.add(action);
	}
	
	public void addAdminAction(Action action) {
		this.adminActions.add(action);
	}
	
	@Override
	public Collection<Action> getActions(boolean admin) {
		if (!admin)
			return this.playerActions;
		
		List<Action> actions = new ArrayList<>(playerActions.size() + adminActions.size());
		actions.addAll(adminActions);
		actions.addAll(playerActions);
		
		return actions;
	}
	
	@Override
	public void addNote(String note) {
		notes.add(note);
	}
	
	@Override
	public void clearNotes() {
		notes.clear();
	}
	
	@Override
	public Collection<String> getNotes() {
		return notes;
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		
		if (null != (node = root.getChild("title"))) {
			this.title = node.getValue();
		}
		
		if (null != (node = root.getChild("description"))) {
			this.description = node.getValue();
		}
		
		if (null != (node = root.getChild("notes"))) {
			this.notes = NoteUtil.deserializeNotes(node.getValue());
		}
		
		playerActions.clear();
		if (null != (node = root.getChild("playeractions"))) {
			for (DataNode child : node.getChildren()) {
				playerActions.add((Action) Action.fromData(child));
			}
		}
		
		adminActions.clear();
		if (null != (node = root.getChild("adminactions"))) {
			for (DataNode child : node.getChildren()) {
				adminActions.add((Action) Action.fromData(child));
			}
		}
	}

	@Override
	public DataNode write(String key) {
		DataNode base = new DataNode(key, null, new LinkedList<>());
		
		base.addChild(new DataNode("title", title, null));
		base.addChild(new DataNode("description", description, null));
		base.addChild(new DataNode("notes", NoteUtil.serializeNotes(notes), null));
		base.addChild(DataNode.serializeAll("playeractions", "action", playerActions));
		base.addChild(DataNode.serializeAll("adminactions", "action", adminActions));
		
		return base;
	}
		
}
