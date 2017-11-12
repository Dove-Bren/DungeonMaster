package com.smanzana.dungeonmaster.inventory.item;

import java.util.Map;
import java.util.TreeMap;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.ActionRegistry;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.ActionDatumData;
import com.smanzana.dungeonmaster.session.datums.Datum;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.app.swing.screens.TemplateEditorScreen;
import com.smanzana.templateeditor.api.IRuntimeEnumerable;
import com.smanzana.templateeditor.api.ISuperclass;
import com.smanzana.templateeditor.api.annotations.DataLoaderRuntimeEnum;

public class Usable extends Item implements IRuntimeEnumerable<String> {
	
	private static class Factory implements ItemFactory<Usable> {

		@Override
		public Usable construct(DataNode data) {
			Usable ret = new Usable("", "", 0, null);
			ret.load(data);
			return ret;
		}
		
	}
	
	{
		Item.registerType(getClassKey(), new Factory());
	}
	
	@DataLoaderRuntimeEnum
	private String action;
	
	public Usable() {
		this("Usable Item", "", 0, "Example Action");
	}

	public Usable(String name, String description, int value, String actionName) {
		super(name, description, value);
		
		action = actionName;
	}
	
	@Override
	protected String getClassKey() {
		return ClassKey();
	}
	
	protected static String ClassKey() {
		return "usable";
	}
	
	protected static void register() {
		Item.registerType(ClassKey(), new Factory());
	}
	
	public void use(Pawn user) {
		Action fetched = ActionRegistry.instance().lookupAction(action);
		
		if (fetched == null) {
			System.out.println("Failed to lookup action " + action);
		} else {
			fetched.perform(user);
		}
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		
		if (root.getChild("action") != null)
			this.action = root.getChild("action").getValue();
	}
	
	@Override
	public DataNode write(String key) {
		DataNode node = super.write(key);
		
		node.addChild(new DataNode("action", this.action, null));
		
		return node;
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

	@Override
	public ISuperclass cloneObject() {
		return new Usable(name, description, value, action);
	}

}
