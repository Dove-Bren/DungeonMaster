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
	
	protected static interface SubActionFactory<T extends SubAction> {
		
		public T construct(DataNode data);
	}
	
	private static Map<String, SubActionFactory<?>> factories = new HashMap<>();
	
	public static SubAction fromData(DataNode data) {
		if (data == null)
			return null;
		
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
