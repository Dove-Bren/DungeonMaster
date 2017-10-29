package com.smanzana.dungeonmaster.action.subaction;

import java.util.Map;
import java.util.TreeMap;

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
	
	protected static void register() {
		SubAction.registerFactory(ClassKey(), new Factory(), false);
	}
	
	private Effect effect;
	
	public SubApplyEffect(Effect effect) {
		this.effect = effect;
	}
	
	public Effect getEffect() {
		return effect;
	}
	
	public void setEffect(Effect ef) {
		effect = ef;
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
	public String getClassKey() {
		return SubApplyEffect.ClassKey();
	}
	
	protected static String ClassKey() {
		return "applyeffect";
	}

	@Override
	public String getDisplayName() {
		return "Apply Effect";
	}

	@Override
	public String getDisplayTooltip() {
		return "Applies a status effect";
	}

	@Override
	public Map<DataType, String> getApplicableTypes() {
		Map<DataType, String> map = new TreeMap<>();
		map.put(DataType.EFFECT, "effect");
		return map;
	}
	
}
