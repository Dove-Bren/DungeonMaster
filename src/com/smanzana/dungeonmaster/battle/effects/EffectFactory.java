package com.smanzana.dungeonmaster.battle.effects;

public interface EffectFactory<T extends Effect> {
	
	public T construct();
	
}
