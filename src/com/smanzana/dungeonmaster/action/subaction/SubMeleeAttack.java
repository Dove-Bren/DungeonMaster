package com.smanzana.dungeonmaster.action.subaction;

import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.item.Equipment.Slot;
import com.smanzana.dungeonmaster.inventory.item.Weapon;
import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Attacks with contents of main and off hands, if they are weapons
 * @author Skyler
 *
 */
public class SubMeleeAttack extends SubAction {

	private static class Factory implements SubActionFactory<SubMeleeAttack> {
		@Override
		public SubMeleeAttack construct(DataNode data) {
			SubMeleeAttack ret = new SubMeleeAttack();
			ret.load(data);
			return ret;
		}
	}
	
	{
		SubAction.registerFactory(getClassKey(), new Factory());
	}

	public SubMeleeAttack() {
		; // Nothing to do; Data pulled when applied
	}
	
	@Override
	public void apply(Pawn source, Pawn target) {
		int damage = 0;
		
		Inventory inv = null;
		if (source != null) {
			if (source instanceof Player) {
				inv = ((Player) source).getInventory();
			} else if (source instanceof Mob) {
				inv = ((Mob) source).getInventory();
			} else {
				// Invalid source type
				System.out.println("Could not use source " + source);
			}
		}
		if (inv == null) {
			return;
		}

		Weapon main, off;
		main = (Weapon) inv.getEquipment().getPiece(Slot.MAIN_HAND);
		off = (Weapon) inv.getEquipment().getPiece(Slot.OFF_HAND);
		
		if (main != null)
			damage += main.getAttack().fetchValue();
		if (off != null && !off.isShield())
			damage += off.getAttack().fetchValue();
		
		if (null == main && null == off)
			damage = 1; // pity
		
		target.damage(source, damage);
	}

	@Override
	public void load(DataNode root) {
		; // nothing to do
	}

	@Override
	public DataNode write(String key) {
		DataNode base = super.write(key);
		
		// no data to store
		
		return base;
	}

	@Override
	protected String getClassKey() {
		return "melee";
	}
	
}
