package com.smanzana.dungeonmaster.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Race + info to create randomly-generated name
 * @author Skyler
 */
public class CharacterProfile implements DataCompatible {

	public static class NameSet implements DataCompatible {
		private List<String> set;
		
		public NameSet() {
			this.set = new LinkedList<>();
		}
		
		public void addName(String name) {
			this.set.add(name);
		}
		
		public String getName() {
			return this.getName(new Random());
		}
		
		public String getName(Random rand) {
			return set.get(rand.nextInt(set.size()));
		}

		@Override
		public void load(DataNode root) {
			String buf = root.getValue();
			this.set.clear();
			
			for (String tok : buf.split(":")) {
				set.add(tok.trim());
			}
		}

		@Override
		public DataNode write(String key) {
			String buf = "";
			for (String s : set)
				buf += s + ":";
			
			return new DataNode(key, buf, null);
		}
		
		protected String serialize() {
			return write("dummy").getValue();
		}
		
		protected static NameSet deserialize(String serial) {
			DataNode node = new DataNode("dummy", serial, null);
			NameSet set = new NameSet();
			
			set.load(node);
			return set;
		}
		
		
	}
	
	private String race;
	private NameSet first;
	private List<NameSet> additional;
	
	public CharacterProfile() {
		this.additional = new LinkedList<>();
	}
	
	public CharacterProfile(String race) {
		this();
		this.race = race;
	}
	
	public String getGeneratedName() {
		return this.getGeneratedName(new Random());
	}
	
	public String getGeneratedName(Random rand) {
		String buf = first.getName(rand);
		
		for (NameSet set : additional) {
			if (rand.nextBoolean())
				buf += set.getName(rand);
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
		String serial = root.getValue();
		additional.clear();
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

	@Override
	public DataNode write(String key) {
		String buf = "";
		buf += "{" + first.serialize() + "}";
		for (NameSet set : additional) {
			buf += " {" + set.serialize() + "}";
		}
		
		return new DataNode(key, buf);
	}
	
}
