package com.smanzana.dungeonmaster.session.datums;

import com.smanzana.dungeonmaster.action.subaction.SubDamage;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.session.datums.data.DatumData;
import com.smanzana.dungeonmaster.spell.Spell;
import com.smanzana.dungeonmaster.utils.Dice;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;

/**
 * Stores:
 *  - name
 *  - description
 *  - list of subactions
 * @author Skyler
 *
 */
public class SpellDatumData implements DatumData {
	
	public static class SpellDatumFactory implements DatumFactory<SpellDatumData> {

		@Override
		public SpellDatumData constructEmptyData() {
			return new SpellDatumData();
		}

		@Override
		public SpellDatumData constructDefaultData() {
			return (SpellDatumData) SpellDatumData.getExampleData();
		}
		
	}
	
	@DataLoaderData(expand=true)
	private Spell spell;
	
	public SpellDatumData() {
		spell = new Spell("Sample Spell", "", 5, 1);
	}
	
	public SpellDatumData(Spell spell) {
		this.spell = spell;
	}

	public Spell getSpell() {
		return spell;
	}

	@Override
	public void load(DataNode root) {
		this.spell = new Spell("", "", 0, 0);
		spell.load(root);
	}

	@Override
	public DataNode write(String key) {
		return this.spell.write(key);
	}

	public static DatumData getExampleData() {
		// Need to actually make DND5E stuff? D:
		Spell spell = new Spell("Flare", "Fires a small ball of fire at the target dealing 2d4 damage", 0, 1);
		
		spell.addSubaction(new SubDamage(new Dice(2, 4, false)));
		
		return new SpellDatumData(spell);
	}

	@Override
	public String getDisplayName() {
		if (spell == null)
			return "";
		return spell.getName();
	}

	@Override
	public String getDisplayTooltip() {
		if (spell == null)
			return null;
		return spell.getDescription();
	}
	
}
