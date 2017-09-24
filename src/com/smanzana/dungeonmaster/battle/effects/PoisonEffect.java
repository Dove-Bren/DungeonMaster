package com.smanzana.dungeonmaster.battle.effects;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

public class PoisonEffect extends Effect {

	private static class Factory implements EffectFactory<PoisonEffect> {

		@Override
		public PoisonEffect construct() {
			return new PoisonEffect("", "", 0, 0);
		}
		
	}
	
	{
		Effect.registerEffect(getClassKey(), new Factory());
	}
	
	// damage per turn
	private int damage;
	
	public PoisonEffect(String name, String description, int duration, int damage) {
		super(name, description, duration);
		this.damage = damage;
	}

	@Override
	protected String getClassKey() {
		return "poison";
	}

	@Override
	protected void doAction(Phase phase, Pawn caster, Pawn target, ValueCapsule value) {
		if (phase == Phase.TURN_START) {
			this.decrementDuration();
			target.damage(caster, damage);
		}
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		
		DataNode node = root.getChild("damage");
		if (node != null)
			this.damage = DataNode.parseInt(node);
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("damage", damage + "", null));
		
		return base;
	}
	
	@Override
	public Effect clone() {
		return new PoisonEffect(this.getName(), this.getDescription(), this.getDurationCount(), this.damage);
	}
	
}
