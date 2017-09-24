package com.smanzana.dungeonmaster.session.datums;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.utils.NameSet;

/**
 * Stores:
 *  - race
 *  - Name Sets
 * @author Skyler
 *
 */
public class ProfileDatumData implements DatumData {
	
	public static class ProfileDatumFactory implements DatumFactory<ProfileDatumData> {

		@Override
		public ProfileDatumData constructEmptyData() {
			return new ProfileDatumData();
		}

		@Override
		public ProfileDatumData constructDefaultData() {
			return (ProfileDatumData) ProfileDatumData.getExampleData();
		}
		
	}
	
	private String profileName;
	private String race;
	private NameSet first;
	private List<NameSet> additional;
	
	public ProfileDatumData() {
		this.additional = new LinkedList<>();
	}
	
	public ProfileDatumData(String profileName, String race) {
		this();
		this.profileName = profileName;
		this.race = race;
	}
	
	public String getProfileName() {
		return this.profileName;
	}
	
	public String getGeneratedName() {
		return this.getGeneratedName(new Random());
	}
	
	public String getGeneratedName(Random rand) {
		String buf = first.getName(rand);
		
		for (NameSet set : additional) {
			if (rand.nextBoolean())
				buf += " " + set.getName(rand);
		}
		
		return buf;
	}
	
	public void addNameSet(NameSet set) {
		this.additional.add(set);
	}
	
	public void setRequiredSet(NameSet set) {
		this.first = set;
	}
	
	public String getRace() {
		return this.race;
	}

	@Override
	public void load(DataNode root) {
		additional.clear();
		race = "";
		
		DataNode node;

		if ((node = root.getChild("profilename")) != null) {
			this.profileName = node.getValue();
		}
		
		if ((node = root.getChild("race")) != null) {
			this.race = node.getValue();
		}
		
		if ((node = root.getChild("names")) != null) {
			String serial = node.getValue();
			boolean fflag = false;
			
			for (String sub : serial.split("[{}]")) {
				if (sub.trim().isEmpty())
					continue;
				sub = sub.trim();
				if (!fflag) {
					fflag = true;
					this.first = NameSet.deserialize(sub);
					continue;
				}
				
				this.additional.add(NameSet.deserialize(sub));
			}
		}
		
		
	}

	@Override
	public DataNode write(String key) {
		String buf = "";
		buf += "{" + first.serialize() + "}";
		for (NameSet set : additional) {
			buf += " {" + set.serialize() + "}";
		}
		
		List<DataNode> children = new ArrayList<>(3);
		children.add(new DataNode("profilename", this.profileName, null));
		children.add(new DataNode("race", this.race, null));
		children.add(new DataNode("names", buf, null));
		
		return new DataNode(key, null, children);
	}

	public static DatumData getExampleData() {
		ProfileDatumData data = new ProfileDatumData();
		
		List<DataNode> list = new ArrayList<>(3);
		list.add(new DataNode("profilename", "example", null));
		list.add(new DataNode("race", "Example Race", null));
		list.add(new DataNode("names", "{Rgathr:Vratt:Bryton:Lanctifer} {The Brave:Nostrilstrong:Groverton}", null));
		
		data.load(new DataNode("dummy", null, list));
		
		return data;
	}
	
}
