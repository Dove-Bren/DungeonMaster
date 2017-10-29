package com.smanzana.dungeonmaster.battle.effects;

import java.util.EnumMap;
import java.util.Map;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueCapsule;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;

public class PoisonEffect extends Effect {

	private static class Factory implements EffectFactory<PoisonEffect> {

		@Override
		public PoisonEffect construct() {
			return new PoisonEffect("", "", 0, 0);
		}
		
	}
	
	protected static void init() {
		Effect.registerEffect(classKey(), new Factory());
	}
	
	// damage per turn
	private ValueSpecifier damage;
	
	public PoisonEffect(String name, String description, int duration, int damage) {
		this(name, description, duration, new ValueConstant(damage));
	}
	
	public PoisonEffect(String name, String description, int duration, ValueSpecifier damage) {
		super(name, description, duration);
		this.damage = damage;
	}
	
	private static String classKey() {
		return "poison";
	}

	@Override
	public String getClassKey() {
		return classKey();
	}

	@Override
	protected void doAction(Phase phase, Pawn caster, Pawn target, ValueCapsule value) {
		if (phase == Phase.TURN_START) {
			this.decrementDuration();
			target.damage(caster, damage.fetchValue());
		}
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		
		DataNode node = root.getChild("damage");
		if (node != null)
			this.damage = ValueSpecifier.fromData(node);
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(damage.write("damage"));
		
		return base;
	}
	
	@Override
	public Effect clone() {
		return new PoisonEffect(this.getName(), this.getDescription(), this.getDurationCount(), this.damage);
	}

	@Override
	public Map<DataType, String> getApplicableTypes() {
		Map<DataType, String> map = new EnumMap<>(DataType.class);
		
		map.put(DataType.AMOUNT_HP, "damage");
		
		return map;
	}
	
}
