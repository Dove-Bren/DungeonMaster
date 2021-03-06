package com.smanzana.dungeonmaster.action.subaction;

import java.util.Map;
import java.util.TreeMap;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;

/**
 * Deals a set amount of damage.
 * @author Skyler
 *
 */
public class SubDamage extends SubAction {

	private static class Factory implements SubActionFactory<SubDamage> {
		@Override
		public SubDamage construct(DataNode data) {
			SubDamage ret = new SubDamage(new ValueConstant(0));
			ret.load(data);
			return ret;
		}
	}
	
	protected static void register() {
		SubAction.registerFactory(ClassKey(), new Factory(), false);
	}

	private ValueSpecifier damage;
	
	public SubDamage(ValueSpecifier amount) {
		this.damage = amount;
	}
	
	public ValueSpecifier getDamage() {
		return damage;
	}

	public void setDamage(ValueSpecifier damage) {
		this.damage = damage;
	}

	@Override
	public void apply(Pawn source, Pawn target) {
		target.damage(source, damage.fetchValue());
	}

	@Override
	public void load(DataNode root) {
		if (root.getChild("amount") != null)
			damage = ValueSpecifier.fromData(root.getChild("amount"));
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(damage.write("amount"));
		
		return base;
	}

	@Override
	public String getClassKey() {
		return SubDamage.ClassKey();
	}
	
	protected static String ClassKey() {
		return "damage";
	}

	@Override
	public String getDisplayName() {
		return "Damage";
	}

	@Override
	public String getDisplayTooltip() {
		return "Deal raw damage";
	}

	@Override
	public Map<DataType, String> getApplicableTypes() {
		Map<DataType, String> map = new TreeMap<>();
		map.put(DataType.AMOUNT_HP, "amount");
		return map;
	}
	
}
