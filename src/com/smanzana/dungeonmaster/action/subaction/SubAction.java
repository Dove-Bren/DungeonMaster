package com.smanzana.dungeonmaster.action.subaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.CombatAction;
import com.smanzana.dungeonmaster.action.PartyAction;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.Displayable;
import com.smanzana.templateeditor.api.ICustomData;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.IntField;

/**
 * An actual piece of "DO SOMETHING".
 * Sub actions make changes.
 * Actions are made up of subactions
 * @author Skyler
 *
 */
public abstract class SubAction implements DataCompatible, Displayable, ICustomData {
	
	protected static enum DataType {
		AMOUNT_HP,
		AMOUNT_MP,
		AMOUNT_STAMINA,
		EFFECT,
	}
	
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
	private static List<String> editorSubactions = null;
	
	private static void init() {
		if (factories != null || editorSubactions != null)
			return;
		
		 factories = new HashMap<>();
		 editorSubactions = new LinkedList<>();
		 
		 // compile-time enumeration :(
		 // Could make cooler with class annotations, but then have to iterate over all
		 // classpath locations or use a library
		 // Or could also have .jars be put in a .extensions folder and do something with that
		 SubRest.register();
		 SubApplyEffect.register();
		 SubDamage.register();
		 SubHeal.register();
		 SubMeleeAttack.register();
		 SubCast.register();
		 
		 Action.register();
		 CombatAction.register();
		 PartyAction.register();
	}
	
	public static SubAction fromData(DataNode data) {
		if (data == null)
			return null;
		
		init();
		
		if (!factories.containsKey(data.getChild("type").getValue()))
			return null;
		
		return factories.get(data.getChild("type").getValue()).construct(data);
		
	}
	
	protected abstract String getClassKey();
	
	protected static void registerFactory(String classKey, SubActionFactory<?> factory, boolean sysonly) {
		init();
		
		factories.put(classKey, factory);
		if (!sysonly)
			editorSubactions.add(classKey);
	}
	
	public static List<String> getRegisteredTypes() {
		init();
		return editorSubactions;
	}
	
	public static SubAction constructFromType(String type) {
		init();
		
		if (factories.containsKey(type))
			return factories.get(type).construct(new DataNode("dummy", "", null));
		
		return null;
	}

	@Override
	public DataNode write(String key) {
		DataNode base = new DataNode(key, null, new LinkedList<>());
		
		base.addChild(new DataNode("type", getClassKey(), null));
		
		return base;
	}
	
	/**
	 * Return a map between DataTypes this subaction requires from the editor
	 * and the key they are read out of a DataNode
	 * @return
	 */
	protected abstract Map<DataType, String> getApplicableTypes();
	
	@Override
	public EditorField<SubAction> getField() {
		return null;
		//return new IntField(temperature);
	}

	@Override
	public SubAction fillFromField(EditorField<?> field) {
		return null;
		//this.temperature = (Integer) field.getObject();
	}
	
}
