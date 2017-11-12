package com.smanzana.dungeonmaster.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;

public class NameSet implements DataCompatible {

	@DataLoaderList(templateName="templateString")
	private List<String> set;
	
	protected static String templateString = "";
	
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
	
	public String serialize() {
		return write("dummy").getValue();
	}
	
	public static NameSet deserialize(String serial) {
		DataNode node = new DataNode("dummy", serial, null);
		NameSet set = new NameSet();
		
		set.load(node);
		return set;
	}
	
}
