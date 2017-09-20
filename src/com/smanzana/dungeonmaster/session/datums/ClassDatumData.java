package com.smanzana.dungeonmaster.session.datums;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.utils.ValueRange;

/**
 * Stores:
 *  - name
 *  - description
 *  - base stats (optional)
 *  - stat growths (optional)
 *  - possible promotions (optional)
 * @author Skyler
 *
 */
public class ClassDatumData implements DatumData {
	
	public static class ClassDatumFactory implements DatumFactory<ClassDatumData> {

		@Override
		public ClassDatumData constructEmptyData() {
			return new ClassDatumData();
		}

		@Override
		public ClassDatumData constructDefaultData() {
			return (ClassDatumData) ClassDatumData.getExampleData();
		}
		
	}
	
	private String name;
	private String description;
	private Map<Attributes, ValueRange> startRanges;
	private Map<Attributes, ValueRange> growthRanges;
	private List<String> promotions;
	
	public ClassDatumData() {
		this.startRanges = new EnumMap<>(Attributes.class);
		this.growthRanges = new EnumMap<>(Attributes.class);
		
		for (Attributes at : Attributes.values()) {
			startRanges.put(at, new ValueRange(0, 0));
			growthRanges.put(at, new ValueRange(0, 0));
		}
		
		this.promotions = new LinkedList<>();
	}
	
	public ClassDatumData(String name, String description) {
		this();
		this.name = name;
		this.description = description;
	}
	
	public ClassDatumData(String name, String description, Map<Attributes, ValueRange> startRanges,
			Map<Attributes, ValueRange> growthRanges, List<String> promotions) {
		this.name = name;
		this.description = description;
		this.startRanges = startRanges;
		this.growthRanges = growthRanges;
		this.promotions = promotions;
		
		// For safety, make sure ranges are filled with enum values
		for (Attributes at : Attributes.values()) {
			if (!this.growthRanges.containsKey(at))
				this.growthRanges.put(at, new ValueRange(0, 0));
			if (!this.startRanges.containsKey(at))
				this.startRanges.put(at, new ValueRange(0, 0));
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<Attributes, ValueRange> getStartRanges() {
		return startRanges;
	}

	public Map<Attributes, ValueRange> getGrowthRanges() {
		return growthRanges;
	}

	public List<String> getPromotions() {
		return promotions;
	}
	
	public void addPromotion(String promo) {
		this.promotions.add(promo);
	}
	
	public void clearPromotions() {
		this.promotions.clear();
	}
	
	public void setStatBase(Attributes attrib, ValueRange range) {
		this.startRanges.put(attrib, range);
	}
	
	public void setStatBase(Attributes attrib, int min, int max) {
		setStatBase(attrib, new ValueRange(min, max));
	}
	
	public void setStatGrowth(Attributes attrib, ValueRange range) {
		this.growthRanges.put(attrib, range);
	}
	
	public void setStatGrowth(Attributes attrib, int min, int max) {
		setStatGrowth(attrib, new ValueRange(min, max));
	}
	
	public ValueRange getBaseRange(Attributes attrib) {
		return this.startRanges.get(attrib);
	}
	
	public ValueRange getGrowthRange(Attributes attrib) {
		return this.growthRanges.get(attrib);
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		
		// name (which is primitive)
		if ((node = root.getChild("name")) != null) {
			this.name = node.getValue();
		}
		
		// description (primitive)
		if ((node = root.getChild("description")) != null) {
			this.description = node.getValue();
		}
		
		// start ranges
		if ((node = root.getChild("stats_base")) != null) {
			// node has 1 child for each attribute which a child that's the range
			DataNode aNode;
			for (Attributes attr : Attributes.values()) {
				aNode = node.getChild(attr.name());
				if (aNode == null)
					continue;
				
				ValueRange range = new ValueRange(0, 0);
				range.load(aNode);
				this.setStatBase(attr, range);
			}
		}
		
		// growth ranges
		if ((node = root.getChild("stats_growth")) != null) {
			// node has 1 child for each attribute which a child that's the range
			DataNode aNode;
			for (Attributes attr : Attributes.values()) {
				aNode = node.getChild(attr.name());
				if (aNode == null)
					continue;
				
				ValueRange range = new ValueRange(0, 0);
				range.load(aNode);
				this.setStatGrowth(attr, range);
			}
		}
		
		//promotions
		this.clearPromotions();
		if ((node = root.getChild("promotions")) != null) {
			for (DataNode child : node.getChildren())
				this.addPromotion(child.getValue());
		}
	}

	@Override
	public DataNode write(String key) {
		List<DataNode> nodes = new ArrayList<>(5);
		DataNode node;
		
		//Name
		node = new DataNode("name", this.name);
		nodes.add(node);
		
		//Description
		node = new DataNode("description", this.description);
		nodes.add(node);
		
		//Base & Growth
		{
			List<DataNode> baseList = new ArrayList<>(Attributes.values().length),
					growthList = new ArrayList<>(Attributes.values().length);
			for (Attributes attr : Attributes.values()) {
				baseList.add(startRanges.get(attr).write(attr.name()));
				growthList.add(growthRanges.get(attr).write(attr.name()));
			}
			
			node = new DataNode("stats_base", null, baseList);
			nodes.add(node);
			node = new DataNode("stats_growth", null, growthList);
			nodes.add(node);
		}
		
		//Promotions
		{
			List<DataNode> list = new ArrayList<>(this.promotions.size());
			for (String promo : this.promotions) {
				list.add(new DataNode("promotion", promo, null));
			}
			
			node = new DataNode("promotions", null, list);
			nodes.add(node);
		}
		
		return new DataNode(key, null, nodes);
	}

	public static DatumData getExampleData() {
		ClassDatumData data = new ClassDatumData("Class-Example", "Just an example class");
		
		data.addPromotion("Class2");
		data.addPromotion("Class3");
		
		data.setStatBase(Attributes.STRENGTH, 5, 10);
		data.setStatBase(Attributes.CHARISMA, 10, 12);
		data.setStatBase(Attributes.DEXTERITY, 5, 10);
		data.setStatBase(Attributes.WISDOM, 5, 10);
		data.setStatBase(Attributes.INTELLIGENCE, 5, 10);
		data.setStatBase(Attributes.CONSTITUTION, 5, 10);
		
		data.setStatGrowth(Attributes.STRENGTH, 5, 10);
		data.setStatGrowth(Attributes.CHARISMA, 10, 12);
		data.setStatGrowth(Attributes.DEXTERITY, 5, 10);
		data.setStatGrowth(Attributes.WISDOM, 5, 10);
		data.setStatGrowth(Attributes.INTELLIGENCE, 5, 10);
		data.setStatGrowth(Attributes.CONSTITUTION, 5, 10);
		
		return data;
	}
	
}
