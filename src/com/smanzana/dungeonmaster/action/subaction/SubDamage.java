package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
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
			SubDamage ret = new SubDamage(null);
			ret.load(data);
			return ret;
		}
	}
	
	{
		SubAction.registerFactory(getClassKey(), new Factory());
	}

	private ValueSpecifier damage;
	
	public SubDamage(ValueSpecifier amount) {
		this.damage = amount;
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
	protected String getClassKey() {
		return "damage";
	}	
	
}
