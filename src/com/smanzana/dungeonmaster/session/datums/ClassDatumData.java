package com.smanzana.dungeonmaster.session.datums;

import java.util.EnumMap;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataNode write(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
