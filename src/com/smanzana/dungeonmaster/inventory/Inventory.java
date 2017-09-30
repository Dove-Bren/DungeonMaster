package com.smanzana.dungeonmaster.inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.inventory.item.Equipment;
import com.smanzana.dungeonmaster.inventory.item.Item;
import com.smanzana.dungeonmaster.inventory.item.Usable;
import com.smanzana.dungeonmaster.pawn.Entity;
import com.smanzana.dungeonmaster.pawn.NPC;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.configuration.MechanicsConfig;
import com.smanzana.dungeonmaster.session.configuration.MechanicsKey;
import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;

public class Inventory implements DataCompatible {

	public static enum InventoryOperation {
		DROP(true, false, false),
		STEAL(false, true, true),
		BUY(false, true, false),
		USE(true, false, false);
		
		private boolean onSelf;
		private boolean inShop;
		private boolean onOther;
		
		private InventoryOperation(boolean onSelf, boolean inShop, boolean onOther) {
			this.onSelf = onSelf;
			this.inShop = inShop;
			this.onOther = onOther;
		}

		// Available from own inventory
		public boolean isOnSelf() {
			return onSelf;
		}

		// Available from shop inventory
		public boolean isInShop() {
			return inShop;
		}

		// Available in non-self inventory
		public boolean isOnOther() {
			return onOther;
		}				
	}
	
	public static interface InventoryHook {
		/**
		 * Authorize (and half-process) a purchase of the provided item.
		 * Transferring item from the correct inventory as well as billing the
		 * purchaser is already done. Instead, do book keeping for the inventory holder
		 * @param item
		 * @param actor who's purchasing
		 * @return true if purchase is ok. False to cancel
		 */
		public boolean buy(Item item, Pawn actor);
		
		/**
		 * Someone is stealing an item.
		 * The owner (hook owner) should perform 'did they succeed' check
		 * @param item
		 * @param actor
		 * @return true to move on with the steal
		 * On false, no action is performed (That means negative side effects must
		 * by handled by the inventory owner!)
		 */
		public boolean steal(Item item, Pawn actor);
	}
	
	private int gold;
	private Map<Integer, Item> heldItems;
	private EquipmentSet equipment;
	private InventoryHook hook;
	
	public Inventory() {
		heldItems = new HashMap<>();
		gold = 0;
		equipment = new EquipmentSet();
	}
	
	public void setInventoryHook(InventoryHook hook) {
		this.hook = hook;
	}
	
	public void setGold(int newAmount) {
		this.gold = newAmount;
	}
	
	public void addGold(int amount) {
		this.gold += amount;
		if (gold < 0)
			gold = 0;
	}
	
	public int getGold() {
		return this.gold;
	}
	
	// Just for me <3.
	// This isn't a clone. be careful
	public Map<Integer, Item> getItemMap() {
		return heldItems;
	}
	
	public Collection<Item> getItems() {
		return this.heldItems.values();
	}
	
	public Item getItem(int slotKey) {
		return heldItems.get(slotKey);
	}
	
	public void removeItem(int slotKey) {
		heldItems.remove(slotKey);
	}
	
	/**
	 * Performs an inventory operation.
	 * @param actor The person who's buying/selling/stealing/using/dropping
	 * @param slotKey
	 * @param op
	 * @return
	 */
	public boolean performOperation(Pawn actor, int slotKey, InventoryOperation op) {
		Item item = getItem(slotKey);
		if (item == null) {
			System.out.println("Attempted operation on empty slot id");
			return false;
		}
		
		switch (op) {
		case DROP:
			if (MechanicsConfig.instance().getBool(MechanicsKey.ITEMS_DROPABLE)) {
				this.removeItem(slotKey);
				return true;
			}
			break;
		case BUY:
			if (MechanicsConfig.instance().getBool(MechanicsKey.ITEMS_TRADING)) {
				// first make sure actor has enough money
				Inventory actorInventory;
				if (actor == null) {
					// Means DM is 'buying' it.
					// no payment. no invenentory to transfer to (later).
					actorInventory = null;
				} else if (actor instanceof Entity) {
					actorInventory = ((Entity) actor).getInventory();
				} else {
					System.out.println("Purchase made by unknown entity");
					return false;
				}
				
				if (actorInventory != null && actorInventory.getGold() < item.getValue()) {
					DungeonMaster.getActiveSession().broadcastLog(
							"You cannot purchase the [" + item.getName()
							+ "] because you do not have enough money!"
							);
					return false;
				}
				
				if (hook != null && hook.buy(item, actor)) {
					// owner added money. Take money from the actor and add to inv
					if (actorInventory != null) {
						actorInventory.addGold(-item.getValue());
						actorInventory.addItem(item);
					}

					this.removeItem(slotKey);
					return true;
				}
				return false;
			}
			break;
		case STEAL:
			if (MechanicsConfig.instance().getBool(MechanicsKey.ITEMS_STEALABLE)) {
				if (this.hook != null) {
					if (this.hook.steal(item, actor)) {
						this.removeItem(slotKey);
						if (actor != null) {
							Inventory inv = null;
							if (actor instanceof Player)
								inv = ((Player) actor).getInventory();
							else if (actor instanceof NPC)
								inv = ((NPC) actor).getInventory();
							else
								System.out.println("Theft committed by unknown entity");
							
							if (inv != null)
								inv.addItem(item);
								
						}
						
						return true;
					}
				}
				return false;
			}
			break;
		case USE:
			if (MechanicsConfig.instance().getBool(MechanicsKey.ITEMS_USABLE)) {
				if (item instanceof Usable) {
					if (actor != null)
						((Usable) item).use(actor);
					this.removeItem(slotKey);
					return true;
				} else
					System.out.println("Can not use unusable item");
				
				return false;
			}
			break;
		}
		
		return false;
	}
	
	public EquipmentSet getEquipment() {
		return this.equipment;
	}
	
	private int generateKey() {
		int key;
		Random rand = new Random();
		key = rand.nextInt();

		if (heldItems.isEmpty())
			return key;
		
		while(heldItems.containsKey(key)) {
			key = rand.nextInt();
		}
		
		return key;
	}
	
	/**
	 * Adds item to inventory.
	 * Returns key used to index item
	 * @param item
	 * @return
	 */
	public int addItem(Item item) {
		int key = generateKey();
		this.heldItems.put(key, item);
		return key;
	}
	
	public void emptyItems() {
		this.heldItems.clear();
	}
	
	public void clearEquipment() {
		for (Equipment.Slot slot : Equipment.Slot.values()) {
			equipment.setPiece(slot, null);
		}
	}

	@Override
	public void load(DataNode root) {
		DataNode node;
		heldItems.clear();
		
		if (null != (node = root.getChild("gold"))) {
			this.gold = DataNode.parseInt(node);
		}
		
		if (null != (node = root.getChild("equipment"))) {
			this.equipment.load(node);
		}
		
		if (null != (node = root.getChild("items"))) {
			for (DataNode child : node.getChildren()) {
				heldItems.put(generateKey(), Item.fromData(child));
			}
		}
		
	}

	// Item keys NOT persisted through saving. They shouldn't need to be
	@Override
	public DataNode write(String key) {
		List<DataNode> list = new LinkedList<>();
		
		list.add(new DataNode("gold", this.gold + "", null));
		list.add(this.equipment.write("equipment"));
		list.add(DataNode.serializeAll("items", "item", heldItems.values()));
		
		return new DataNode(key, null, list);
	}	
}
