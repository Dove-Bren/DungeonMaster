package com.smanzana.dungeonmaster.session.datums;

import java.util.LinkedList;

import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.item.Junk;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.utils.StatSet;
import com.smanzana.dungeonmaster.utils.StepList;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;

/**
 * NPC (npc, or mob, really) template
 * Stores:
 *  - template name
 *  - profile name
 *  - stats
 *  - inventory
 *  - xp (only used if combatted and killed)
 *  - willtrade (only when not in combat)
 * @author Skyler
 *
 */
public class NPCDatumData implements DatumData {
	
	public static class NPCDatumFactory implements DatumFactory<NPCDatumData> {

		@Override
		public NPCDatumData constructEmptyData() {
			return new NPCDatumData();
		}

		@Override
		public NPCDatumData constructDefaultData() {
			return (NPCDatumData) NPCDatumData.getExampleData();
		}
		
	}
	
	@DataLoaderName
	private String templateName;
	@DataLoaderData
	private String profileName;
	@DataLoaderData
	private StatSet stats;
	@DataLoaderData
	private Inventory inventory;
	@DataLoaderData
	private int xp;
	@DataLoaderData
	private boolean willTrade;
	@DataLoaderData
	private ValueSpecifier love;
	@DataLoaderData
	private StepList loveSteps;
	
	public NPCDatumData() {
		this.inventory = new Inventory();
		this.stats = new StatSet();
		
		// remove
		love = new ValueConstant(5);
		loveSteps = new StepList();
		loveSteps.addStep(5, 1);
		loveSteps.addStep(10, 2);
	}
	
	public NPCDatumData(String templateName, String profileName) {
		this();
		this.templateName = templateName;
		this.profileName = profileName;
	}
	
	public NPCDatumData(String templateName, String profileName, StatSet stats, Inventory inventory, int xp,
			boolean willTrade) {
		super();
		this.templateName = templateName;
		this.profileName = profileName;
		this.stats = stats;
		this.inventory = inventory;
		this.xp = xp;
		this.willTrade = willTrade;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

	public boolean isWillTrade() {
		return willTrade;
	}

	public void setWillTrade(boolean willTrade) {
		this.willTrade = willTrade;
	}

	public StatSet getStats() {
		return stats;
	}

	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		
		if ((node = root.getChild("templatename")) != null) {
			this.templateName = node.getValue();
		}
		
		if ((node = root.getChild("profilename")) != null) {
			this.profileName = node.getValue();
		}
		
		if ((node = root.getChild("xp")) != null) {
			this.xp = DataNode.parseInt(node);
		}
		
		if ((node = root.getChild("inventory")) != null) {
			this.inventory = new Inventory();
			this.inventory.load(node);
		}
		
		if ((node = root.getChild("stats")) != null) {
			this.stats = new StatSet();
			stats.load(node);
		}
		
	}

	@Override
	public DataNode write(String key) {
		DataNode node = new DataNode(key, null, new LinkedList<>());
		
		node.addChild(new DataNode("templatename", templateName, null));
		node.addChild(new DataNode("profilename", profileName, null));
		node.addChild(new DataNode("xp", xp + "", null));
		node.addChild(this.inventory.write("inventory"));
		node.addChild(this.stats.write("stats"));
		
		return node;
	}

	public static DatumData getExampleData() {
		NPCDatumData data = new NPCDatumData("merchant-dwarf-1", "profilename");
		
		data.willTrade = true;
		data.xp = 0;
		data.stats.setMaxHealth(40);
		data.stats.setHealth(40);
		for (Attributes attr : Attributes.values())
			data.stats.setAbilityScore(attr, 12);
		
		data.stats.setAbilityScore(Attributes.STRENGTH, 16);
		data.stats.setAbilityScore(Attributes.CONSTITUTION, 18);
		
		data.inventory.addGold(400);
		data.inventory.addItem(new Junk("Gold Dust", "Small pile of gold dust", 10));
		
		return data;
	}

	@Override
	public String getDisplayName() {
		return getTemplateName();
	}

	@Override
	public String getDisplayTooltip() {
		return "Template NPC";
	}
	
}
