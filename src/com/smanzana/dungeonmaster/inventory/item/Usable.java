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
	
	private Action action;

	public Usable(String name, String description, int value, String actionName) {
		super(name, description, value);
		
		// Link action based on name
		if (actionName != null) {
			action = ActionRegistry.instance().lookupAction(actionName);
		} else
			action = null;
	}
	
	@Override
	protected String getClassKey() {
		return "usable";
	}
	
	public void use(Pawn user) {
		action.perform(user);
	}

}
