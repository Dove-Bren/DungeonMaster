package com.smanzana.dungeonmaster.setting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.ActionRegistry;
import com.smanzana.dungeonmaster.action.Interactable;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.datums.ActionDatumData;
import com.smanzana.dungeonmaster.session.datums.Datum;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.app.swing.screens.TemplateEditorScreen;
import com.smanzana.dungeonmaster.utils.Notable;
import com.smanzana.dungeonmaster.utils.NoteUtil;
import com.smanzana.templateeditor.api.IRuntimeEnumerable;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;
import com.smanzana.templateeditor.api.annotations.DataLoaderRuntimeEnum;

/**
 * A place/location. THAT kind of setting.
 * @author Skyler
 *
 */
public class Setting implements Notable, DataCompatible, Interactable, IRuntimeEnumerable<String> {

	@DataLoaderName
	private String title;
	@DataLoaderDescription
	private String description;
	
	private List<String> notes;
	
	@DataLoaderRuntimeEnum("player")
	@DataLoaderData(name="Player Actions",description="Actions players can take while in this setting")
	private List<String> playerActions;
	@DataLoaderRuntimeEnum("admin")
	@DataLoaderData(name="Admin Actions",description="Actions only the DM can take while in this setting")
	private List<String> adminActions;
	
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
		this.playerActions.add(action.getName());
	}
	
	public void addPlayerAction(String actionName) {
		this.playerActions.add(actionName);
	}
	
	public void addAdminAction(Action action) {
		this.adminActions.add(action.getName());
	}
	
	public void addAdminAction(String actionName) {
		this.adminActions.add(actionName);
	}
	
	private List<Action> fetchActions(List<String> names) {
		List<Action> actions = new LinkedList<>();
		
		for (String name : names) {
			Action action = ActionRegistry.instance().lookupAction(name);
			if (action != null)
				actions.add(action);
			else
				System.out.println("Could not resolve setting action \"" + name + "\" to a registered action. "
						+ "Actions must be defined in actions.dat");
		}
		
		return actions;
	}
	
	@Override
	public Collection<Action> getActions(boolean admin, Player player) {
		if (!admin)
			return fetchActions(this.playerActions);
		
		List<Action> actions = new ArrayList<>(playerActions.size() + adminActions.size());
		actions.addAll(fetchActions(adminActions));
		actions.addAll(fetchActions(playerActions));
		
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
				playerActions.add(child.getValue());
			}
		}
		
		adminActions.clear();
		if (null != (node = root.getChild("adminactions"))) {
			for (DataNode child : node.getChildren()) {
				adminActions.add(child.getValue());
			}
		}
	}

	@Override
	public DataNode write(String key) {
		DataNode base = new DataNode(key, null, new LinkedList<>());
		
		base.addChild(new DataNode("title", title, null));
		base.addChild(new DataNode("description", description, null));
		base.addChild(new DataNode("notes", NoteUtil.serializeNotes(notes), null));
		base.addChild(DataNode.serializeAllStrings("playeractions", "action", playerActions));
		base.addChild(DataNode.serializeAllStrings("adminactions", "action", adminActions));
		
		return base;
	}

	@Override
	public Map<String, String> fetchValidValues(String key) {
		Datum<ActionDatumData> datum = TemplateEditorScreen.instance()
				.getCurrentTemplate().getActionDatum();
		
		Map<String, String> values = new TreeMap<>();
		
		for (ActionDatumData d : datum.getData()) {
			values.put(d.getName(), d.getName());
		}
		
		return values;
	}
		
}
