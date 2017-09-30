package com.smanzana.dungeonmaster.session.datums;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.setting.Setting;

/**
 * Stores:
 *  - title
 *  - description
 *  - playeractions
 *  - adminactions
 * @author Skyler
 *
 */
public class SettingDatumData implements DatumData {
	
	public static class SettingDatumFactory implements DatumFactory<SettingDatumData> {

		@Override
		public SettingDatumData constructEmptyData() {
			return new SettingDatumData();
		}

		@Override
		public SettingDatumData constructDefaultData() {
			return (SettingDatumData) SettingDatumData.getExampleData();
		}
		
	}
	
	private Setting setting;
	
	public SettingDatumData() {
		;
	}
	
	public SettingDatumData(Setting setting) {
		this();
		this.setting = setting;
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	public void setSetting(Setting setting) {
		this.setting = setting;
	}

	@Override
	public void load(DataNode root) {
		this.setting = new Setting();
		setting.load(root);
	}

	@Override
	public DataNode write(String key) {
		return setting.write(key);
	}

	public static DatumData getExampleData() {
		SettingDatumData data = new SettingDatumData();
		
		Setting example = new Setting("Cavern", "A deep, dark cavern that may be home to more than rats...");
		
		example.addPlayerAction("Yell");
		example.addPlayerAction("Unsafe Rest");
		
		example.addAdminAction("Cave-In");
		example.addAdminAction("Healing Fireflies");
		
		data.setSetting(example);
		
		return data;
	}
	
}
