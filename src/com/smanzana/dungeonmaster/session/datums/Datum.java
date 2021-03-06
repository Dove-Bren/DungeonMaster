package com.smanzana.dungeonmaster.session.datums;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.utils.Displayable;

/**
 * Like a config, but instead of predefined "key: value" data, holds an arbitrary number of data templates.
 * @author Skyler
 */
public class Datum<T extends DatumData> implements Displayable {
	
	private DatumFactory<T> factory;
	protected List<T> data; // List of all data 'objects'
	protected String childKey;
	protected String displayName;
	protected String tooltip;
	
	private Datum() {
		this.data = new LinkedList<>();
	}
	
	public Datum(String childKey, DatumFactory<T> factory, String displayName, String tooltip) {
		this();
		this.factory = factory;
		this.childKey = childKey;
		this.displayName = displayName;
		this.tooltip = tooltip;
	}
	
	public List<T> getData() {
		return data;
	}
	
	public String getChildKey() {
		return this.childKey;
	}
	
	public void addData(T data) {
		this.data.add(data);
	}
	
	public void removeData(T data) {
		this.data.remove(data);
	}
	
	public void saveToFile(File outFile) throws FileNotFoundException {
		// Convert data list into DataNode object wrapper, then serialize that
		
		if (outFile == null || data == null || data.isEmpty())
			return;
		
		List<DataNode> children = new ArrayList<>(data.size());
		for (T d : data) {
			children.add(d.write(childKey));
		}
		DataNode node = new DataNode("wrapper", null, children);
		String output = node.serialize(true, true);
		
		PrintWriter writer = new PrintWriter(outFile);
		writer.print(output);
		writer.close();
	}
	
	public void loadFromFile(File inFile) throws IOException {
		if (inFile == null)
			return;
		
		if (data != null)
			data.clear();
		else
			data = new LinkedList<>();
		
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		String input = "";
		
		while (reader.ready())
			input += reader.readLine();
		
		reader.close();
		
		Collection<DataNode> nodes = DataNode.parse(input);
		for (DataNode node : nodes) {
			T d = factory.constructEmptyData();
			d.load(node);
			data.add(d);
		}
	}
	
	public void createDefaultFile(File outFile) throws FileNotFoundException {
		List<T> backup = this.data;
		this.data = new ArrayList<>(1);
		this.addData(this.factory.constructDefaultData());
		
		this.saveToFile(outFile);
		this.data = backup;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDisplayTooltip() {
		return tooltip;
	}
	
	public T createEmptyData() {
		return factory.constructEmptyData();
	}
	
}
