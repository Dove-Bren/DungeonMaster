package com.smanzana.dungeonmaster.battle.effects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueCapsule;

public abstract class Effect implements DataCompatible {
	
	protected static enum Phase {
		BEFORE_DAMAGE,
		AFTER_DAMAGE,
		TURN_START,
		TURN_END,
		ON_USE,
	}

	private static Map<String, EffectFactory<?>> factories = new HashMap<>();
	
	public static Effect fromData(DataNode node) {
		if (node == null)
			return null;
		
		DataNode type = node.getChild("type");
		if (type == null)
			return null;
		
		
		EffectFactory<?> factory = factories.get(type.getValue());
		if (factory == null)
			return null;
		
		Effect ef = factory.construct();
		ef.load(node);
		return ef;
	}
	
	/**
	 * If you don't register your effect with a unique type string,
	 * It will not be deserialized correctly. Dont' be stupid.
	 * @param type
	 * @param factory
	 */
	protected static void registerEffect(String type, EffectFactory<?> factory) {
		factories.put(type, factory);
	}
	
	public static void doPostEffects(Collection<Effect> effects, Pawn caster, Pawn target, ValueCapsule value) {
		for (Effect e : effects) {
			e.doAction(Phase.AFTER_DAMAGE, caster, target, value);
		}
		cleanEffects(effects);
	}
	
	public static void doPreEffects(Collection<Effect> effects, Pawn caster, Pawn target, ValueCapsule value) {
		for (Effect e : effects) {
			e.doAction(Phase.BEFORE_DAMAGE, caster, target, value);
		}
		cleanEffects(effects);
	}
	
	public static void doTurnStartEffects(Collection<Effect> effects, Pawn caster, Pawn target, ValueCapsule value) {
		for (Effect e : effects) {
			e.doAction(Phase.TURN_START, caster, target, value);
		}
		cleanEffects(effects);
	}
	
	public static void doTurnEndEffects(Collection<Effect> effects, Pawn caster, Pawn target, ValueCapsule value) {
		for (Effect e : effects) {
			e.doAction(Phase.TURN_END, caster, target, value);
		}
		cleanEffects(effects);
	}
	
	public static void doOnUseEffects(Collection<Effect> effects, Pawn caster, Pawn target, ValueCapsule value) {
		for (Effect e : effects) {
			e.doAction(Phase.ON_USE, caster, target, value);
		}
		cleanEffects(effects);
	}
	
	public static void cleanEffects(Collection<Effect> effects) {
		Iterator<Effect> it = effects.iterator();
		while (it.hasNext()) {
			Effect e = it.next();
			if (e.durationCount <= 0)
				it.remove();
		}
	}
	
	private String name;
	private String description;
	private int durationCount;
	
	protected Effect(String name, String description, int duration) {
		this.name = name;
		this.description = description;
		this.durationCount = duration;
	}
	
	protected abstract String getClassKey();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDurationCount() {
		return durationCount;
	}

	public void setDurationCount(int durationCount) {
		this.durationCount = durationCount;
	}
	
	protected void decrementDuration() {
		this.durationCount -= 1;
	}
	
	protected abstract void doAction(Phase phase, Pawn caster, Pawn target, ValueCapsule value);
	
	@Override
	public DataNode write(String key) {
		List<DataNode> list = new LinkedList<>();
		
		list.add(new DataNode("type", getClassKey(), null));
		list.add(new DataNode("name", this.name, null));
		list.add(new DataNode("description", this.description, null));
		list.add(new DataNode("durationcount", this.durationCount + "", null));
		
		return new DataNode(key, null, list);
	}
	
	@Override
	public void load(DataNode root) {
		DataNode node;
		
		if (null != (node = root.getChild("name"))) {
			this.name = node.getValue();
		}
		
		if (null != (node = root.getChild("description"))) {
			this.description = node.getValue();
		}
		
		if (null != (node = root.getChild("durationcount"))) {
			this.durationCount = DataNode.parseInt(node);
		}
	}
	
	public abstract Effect clone();
	
}
