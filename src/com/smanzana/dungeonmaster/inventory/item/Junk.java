package com.smanzana.dungeonmaster.inventory.item;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Junk extends Item {

	private static class Factory implements ItemFactory<Junk> {
		@Override
		public Junk construct(DataNode data) {
			Junk junk = new Junk();
			junk.load(data);
			return junk;
		}
		
	}
	
	{
		Item.registerType(getClassKey(), new Factory());
	}
	
	@Override
	protected String getClassKey() {
		return "junk";
	}
	
	

}