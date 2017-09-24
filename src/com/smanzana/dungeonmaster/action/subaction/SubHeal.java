package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;

/**
 * Heal a pawn. Includes HP, MP, Stamina
 * @author Skyler
 *
 */
public class SubHeal extends SubAction {

	private static class Factory implements SubActionFactory<SubHeal> {
		@Override
		public SubHeal construct(DataNode data) {
			SubHeal ret = new SubHeal(null, null, null);
			ret.load(data);
			return ret;
		}
	}
	
	{
		SubAction.registerFactory(getClassKey(), new Factory());
	}

	private ValueSpecifier hp;
	private ValueSpecifier mp;
	private ValueSpecifier stamina;
	
	public SubHeal(ValueSpecifier hp, ValueSpecifier mp, ValueSpecifier stamina) {
		this.hp = hp;
		this.mp = mp;
		this.stamina = stamina;
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {
		if (hp != null && MechanicsConfig.instance().getBool(MechanicsKey.USE_HEALTH))
			target.heal(source, hp.fetchValue());
		
		if (mp != null && MechanicsConfig.instance().getBool(MechanicsKey.USE_MANA))
			target.recoverMana(source, mp.fetchValue());
			
		if (stamina != null && MechanicsConfig.instance().getBool(MechanicsKey.USE_STAMINA))
			target.recoverStamina(source, stamina.fetchValue());
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		
		if (null != (node = root.getChild("health"))) {
			hp = ValueSpecifier.fromData(node);
		}
		
		if (null != (node = root.getChild("mana"))) {
			mp = ValueSpecifier.fromData(node);
		}
		
		if (null != (node = root.getChild("stamina"))) {
			stamina = ValueSpecifier.fromData(node);
		}
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		base.addChild(hp.write("health"));
		base.addChild(mp.write("mana"));
		base.addChild(stamina.write("stamina"));
		
		return base;
	}

	@Override
	protected String getClassKey() {
		return "heal";
	}	
	
}
