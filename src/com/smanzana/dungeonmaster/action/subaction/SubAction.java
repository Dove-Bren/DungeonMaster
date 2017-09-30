package com.smanzana.dungeonmaster.action.subaction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * An actual piece of "DO SOMETHING".
 * Sub actions make changes.
 * Actions are made up of subactions
 * @author Skyler
 *
 */
public abstract class SubAction implements DataCompatible {
	
	public abstract void apply(Pawn source, Pawn target);
	
	/**
	 * Must have empty constructor. Will be constructed with no arguments
	 * @author Skyler
	 *
	 * @param <T>
	 */
	protected static interface SubActionFactory<T extends SubAction> {
		
		public T construct(DataNode data);
	}
	
	private static Map<String, SubActionFactory<?>> factories = null;
	
	private static void init() {
		 factories = new HashMap<>();
		 
		 // compile-time enumeration :(
		 // Could make cooler with class annotations, but then have to iterate over all
		 // classpath locations or use a library
		 SubRest.register();
		 SubApplyEffect.register();
		 SubDamage.register();
		 SubHeal.register();
		 SubMeleeAttack.register();
		 SubCast.register();
	}
	
	public static SubAction fromData(DataNode data) {
		if (data == null)
			return null;
		
		if (factories == null)
			init();
		
		if (!factories.containsKey(data.getChild("type").getValue()))
			return null;
		
		return factories.get(data.getChild("type").getValue()).construct(data);
		
	}
	
	protected abstract String getClassKey();
	
	protected static void registerFactory(String classKey, SubActionFactory<?> factory) {
		factories.put(classKey, factory);
	}

	@Override
	public DataNode write(String key) {
		DataNode base = new DataNode(key, null, new LinkedList<>());
		
		base.addChild(new DataNode("type", getClassKey(), null));
		
		return base;
	}
	
}
