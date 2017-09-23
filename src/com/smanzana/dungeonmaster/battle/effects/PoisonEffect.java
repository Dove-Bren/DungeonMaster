package com.smanzana.dungeonmaster.battle.effects;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

public class PoisonEffect extends Effect {

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
			target.damage(damage);
		}
	}
	
	@Override
	public void load(DataNode root) {
		super.load(root);
		
		DataNode node = root.getChild("damage");
		if (node != null)
			try {
				this.damage = Integer.parseInt(node.getValue());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Could not convert " + node.getValue() + " to an int");
			}
	}
	
	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(new DataNode("damage", damage + "", null));
		
		return base;
	}
	
}
