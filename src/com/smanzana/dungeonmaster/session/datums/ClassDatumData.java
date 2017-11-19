package com.smanzana.dungeonmaster.session.datums;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.ui.app.swing.screens.TemplateEditorScreen;
import com.smanzana.dungeonmaster.utils.Dice;
import com.smanzana.dungeonmaster.utils.ValueRange;
import com.smanzana.templateeditor.api.IRuntimeEnumerable;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;
import com.smanzana.templateeditor.api.annotations.DataLoaderRuntimeEnum;

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
public class ClassDatumData implements DatumData, IRuntimeEnumerable<String> {
	
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
	
	@DataLoaderName
	private String name;
	@DataLoaderDescription
	private String description;
	@DataLoaderData
	@DataLoaderList(templateName="valueTemplate",factoryName="constructTemplateValue")
	private Map<Attributes, ValueRange> startRanges;
	@DataLoaderData
	@DataLoaderList(templateName="valueTemplate",factoryName="constructTemplateValue")
	private Map<Attributes, ValueRange> growthRanges;
	@DataLoaderData
	private Map<Integer, String> spellUnlocks;
	@DataLoaderRuntimeEnum
	private List<String> promotions;
	@DataLoaderData
	private Dice hitdice;
	
	protected static ValueRange valueTemplate = constructTemplateValue();
	protected static ValueRange constructTemplateValue() {
		return new ValueRange(0, 1);
	}
	
	public ClassDatumData() {
		this.startRanges = new EnumMap<>(Attributes.class);
		this.growthRanges = new EnumMap<>(Attributes.class);
		this.spellUnlocks = new HashMap<>();
		
		clearStatBases();
		clearStatGrowths();
		
		this.promotions = new LinkedList<>();
		this.hitdice = new Dice(1, 1, false);

		for (int i = 0; i < 11; i++)
			spellUnlocks.put(i, null);
	}
	
	public ClassDatumData(String name, String description) {
		this();
		this.name = name;
		this.description = description;
	}
	
	public ClassDatumData(String name, String description, Map<Attributes, ValueRange> startRanges,
			Map<Attributes, ValueRange> growthRanges, Map<Integer, String> spellUnlocks, List<String> promotions, Dice hitdice) {
		this(name, description);
		this.startRanges = startRanges;
		this.growthRanges = growthRanges;
		this.spellUnlocks = spellUnlocks;
		this.promotions = promotions;
		this.hitdice = hitdice;
		
		// For safety, make sure ranges are filled with enum values
		for (Attributes at : Attributes.values()) {
			if (!this.growthRanges.containsKey(at))
				this.growthRanges.put(at, new ValueRange(0, 0));
			if (!this.startRanges.containsKey(at))
				this.startRanges.put(at, new ValueRange(0, 0));
		}

		for (int i = 0; i < 11; i++)
			spellUnlocks.put(i, null);
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
	
	public void clearStatBases() {
		this.startRanges.clear();
		for (Attributes attr : Attributes.values())
			this.startRanges.put(attr, new ValueRange(0, 0));
	}
	
	public void clearStatGrowths() {
		this.growthRanges.clear();
		for (Attributes attr : Attributes.values())
			this.growthRanges.put(attr, new ValueRange(0, 0));
	}
	
	public ValueRange getGrowthRange(Attributes attrib) {
		return this.growthRanges.get(attrib);
	}
	
	public void clearSpellUnlocks() {
		spellUnlocks.clear();
	}
	
	public void addSpellUnlock(int level, String spell) {
		spellUnlocks.put(level, spell);
	}
	
	public String getSpellUnlock(int level) {
		return spellUnlocks.get(level);
	}
	
	public Dice getHitDice() {
		return this.hitdice;
	}
	
	/**
	 * Die used to roll for additional hp per level
	 * @param hitdice
	 */
	public void setHitDice(Dice hitdice) {
		this.hitdice = hitdice;
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
		this.clearStatBases();
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
		this.clearStatGrowths();
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
		
		spellUnlocks.clear();
		for (int i = 0; i < 11; i++)
			spellUnlocks.put(i, null);
		if ((node = root.getChild("spell_unlocks")) != null) {
			for (DataNode child : node.getChildren()) {
				// have level, spell name
				
				spellUnlocks.put(
						DataNode.parseInt(child.getChild("level")),
						child.getChild("spell").getValue());
			}
		}
		
		//promotions
		this.clearPromotions();
		if ((node = root.getChild("promotions")) != null) {
			for (DataNode child : node.getChildren())
				this.addPromotion(child.getValue());
		}

		if ((node = root.getChild("hitdice")) != null) {
			this.hitdice = new Dice(1,1, false);
			hitdice.load(node);
		}
	}

	@Override
	public DataNode write(String key) {
		List<DataNode> nodes = new ArrayList<>(5);
		
		nodes.add(new DataNode("name", this.name));
		nodes.add(new DataNode("description", this.description));
		
		List<DataNode> baseList = new ArrayList<>(Attributes.values().length),
				growthList = new ArrayList<>(Attributes.values().length);
		for (Attributes attr : Attributes.values()) {
			if (startRanges.get(attr) != null)
				baseList.add(startRanges.get(attr).write(attr.name()));
			if (growthRanges.get(attr) != null)
				growthList.add(growthRanges.get(attr).write(attr.name()));
		}
		
		nodes.add(new DataNode("stats_base", null, baseList));
		nodes.add(new DataNode("stats_growth", null, growthList));
		
		//Promotions
		{
			List<DataNode> list = new ArrayList<>(this.promotions.size());
			for (String promo : this.promotions) {
				list.add(new DataNode("promotion", promo, null));
			}
			
			nodes.add(new DataNode("promotions", null, list));
		}
		
		if (hitdice != null)
			nodes.add(hitdice.write("hitdice"));
		
		DataNode spells = new DataNode("spell_unlocks", null, new LinkedList<>());
		for (Integer k : spellUnlocks.keySet()) {
			if (spellUnlocks.get(k) != null) {
				DataNode spell = new DataNode(k + "", null, new LinkedList<>());
				
				spell.addChild(new DataNode("level", k + "", null));
				spell.addChild(new DataNode("spell", spellUnlocks.get(k), null));
				
				spells.addChild(spell);
			}
				
		}
		nodes.add(spells);
		
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
		
		data.addSpellUnlock(5, "Fireball");
		data.addSpellUnlock(10, "Fireball II");
		
		data.setHitDice(new Dice(1,12,false));
		
		return data;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getDisplayTooltip() {
		return getDescription();
	}

	@Override
	public Map<String, String> fetchValidValues(String key) {
		Datum<ClassDatumData> datum = TemplateEditorScreen.instance()
				.getCurrentTemplate().getClassDatum();
		
		Map<String, String> values = new TreeMap<>();
		
		for (ClassDatumData d : datum.getData()) {
			if (d == this)
				continue;
			values.put(d.getName(), d.getName());
		}
		
		return values;
	}
	
}
