package com.smanzana.dungeonmaster.inventory.item;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.ActionRegistry;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Usable extends Item {
	
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
	
	private String action;

	public Usable(String name, String description, int value, String actionName) {
		super(name, description, value);
		
		action = actionName;
	}
	
	@Override
	protected String getClassKey() {
		return "usable";
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

}
