package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Cast a spell.
 * Does slot cleanup work
 * @author Skyler
 *
 */
public class SubApplyEffect extends SubAction {

	private static class Factory implements SubActionFactory<SubApplyEffect> {
		@Override
		public SubApplyEffect construct(DataNode data) {
			SubApplyEffect ret = new SubApplyEffect(null);
			ret.load(data);
			return ret;
		}
	}
	
	{
		SubAction.registerFactory(getClassKey(), new Factory());
	}
	
	private Effect effect;
	
	public SubApplyEffect(Effect effect) {
		this.effect = effect;
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {
		if (target instanceof Mob)
			((Mob) target).addEffect(effect.clone());
		else if (target instanceof Player)
			((Player) target).addEffect(effect.clone());
	}

	@Override
	public void load(DataNode root) {
		if (root.getChild("effect") != null)
			effect = Effect.fromData(root.getChild("effect"));
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(effect.write("effect"));
		
		return base;
	}

	@Override
	protected String getClassKey() {
		return "applyeffect";
	}	
	
}
