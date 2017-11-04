package com.smanzana.dungeonmaster.session.datums;

import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.inventory.item.Junk;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;

/**
 * Stores:
 *  - name
 *  - description
 *  - value
 *  - type {junk,usable,weapon,armor}
 *  - additional based on type
 * @author Skyler
 *
 */
public class ItemDatumData implements DatumData {
	
	public static class ItemDatumFactory implements DatumFactory<ItemDatumData> {

		@Override
		public ItemDatumData constructEmptyData() {
			return new ItemDatumData();
		}

		@Override
		public ItemDatumData constructDefaultData() {
			return (ItemDatumData) ItemDatumData.getExampleData();
		}
		
	}
	
	//@DataLoaderData(expand=true)
	@DataLoaderData
	private Item item;
	
	public ItemDatumData() {
		
	}
	
	public ItemDatumData(Item item) {
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

	@Override
	public void load(DataNode root) {
		item = Item.fromData(root);
	}

	@Override
	public DataNode write(String key) {
		return this.item.write(key);
	}

	public static DatumData getExampleData() {
		// Need to actually make DND5E stuff? D:
		Item item = new Junk("Gold Dust", "Small pile of gold dust", 10);
		return new ItemDatumData(item); 
	}

	@Override
	public String getDisplayName() {
		if (item == null)
			return "";
		return item.getName();
	}

	@Override
	public String getDisplayTooltip() {
		if (item == null)
			return null;
		return item.getDescription();
	}
	
}
