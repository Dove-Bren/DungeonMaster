package com.smanzana.dungeonmaster.pawn;

import java.util.List;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.ClassDatumData;
import com.smanzana.dungeonmaster.session.datums.SpellDatumData;

/**
 * A character class
 * @author Skyler
 *
 */
public class PlayerClass {
	
	// Leave all the data in the datum object
	private ClassDatumData data;
	
	public PlayerClass(ClassDatumData data) {
		this.data = data;
	}
	
	public String getName() {
		return data.getName();
	}
	
	public String getDescription() {
		return data.getDescription();
	}
	
	public List<String> getPromotions() {
		return data.getPromotions();
	}
	
	/**
	 * Applies progressions to the player as if they leveled up while under this class
	 * This includes ability score progressions (if applicable), updating max health (if applicable),
	 * and awarding spells (if applicable).
	 * @param player
	 */
	public void applyLevel(Player player) {
		if (!MechanicsConfig.instance().getBool(MechanicsKey.USE_LEVELS))
			return;
		
		if (MechanicsConfig.instance().getBool(MechanicsKey.LEVEL_ABILITY_SCORES)) {
			for (Attributes attr : Attributes.values()) {
				player.stats.setAbilityScore(attr,
						player.getAbilityScore(attr) + data.getGrowthRange(attr).fetchValue());
			}
		}
		
		if (null != data.getSpellUnlock(player.getLevel())) {
			SpellDatumData spell = DungeonMaster.getActiveSession().lookupSpell(data.getSpellUnlock(player.getLevel()));
			player.awardSpell(spell.getSpell());
		}
	}
}
