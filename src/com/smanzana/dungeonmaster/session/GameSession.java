package com.smanzana.dungeonmaster.session;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.smanzana.dungeonmaster.action.Action;
import com.smanzana.dungeonmaster.action.ActionRegistry;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.inventory.Inventory.InventoryOperation;
import com.smanzana.dungeonmaster.pawn.Entity;
import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.NPC;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.session.datums.ActionDatumData;
import com.smanzana.dungeonmaster.session.datums.SpellDatumData;
import com.smanzana.dungeonmaster.setting.Setting;
import com.smanzana.dungeonmaster.utils.Notable;

public class GameSession extends SessionBase implements Notable {

	private static interface AsyncRequest {
		
		/**
		 * Called on main session thread.
		 * Variables uses MUST be made threadsafe. Make a deep copy
		 * and use that instead of the actual variable
		 * @return true to halt execution of session
		 */
		public boolean run(GameSession session);
		
	}
	
	public static final String PATH_CONFIG = "data/";
	
	/**
	 * All playable characters that exist in this session.
	 * Note this is different then the party
	 */ 
	private List<Player> PCs;	
	
	private List<String> notes;
	private List<Pawn> party;
	private Setting setting;

	/**
	 * Queue for requests from UI/clients
	 */
	private List<AsyncRequest> requestQueue;
	/**
	 * secret session key. How comms lookup the player they're
	 * talking for. Doubles as security against disconnect + still play
	 */
	private Map<Integer, Player> playerSessionKeys;
	/**
	 * Active pawns (including party members) that can be interacted with (and should be persisted)
	 * Key is unique ID passed out on registration and used to indicate target from clients
	 */
	private Map<Integer, Pawn> activePawns;
	
	private Boolean receivedShutdown;		
	
	/**
	 * Create and load a new game session from the given root.
	 * If loading from a template, make sure to specify so that the
	 * correct path information is used.
	 * @param root
	 * @param fromTemplate
	 */
	public GameSession(File root, boolean fromTemplate) {
		super(root, fromTemplate ? "" : PATH_CONFIG);
		load();
		notes = new LinkedList<>();
		party = new LinkedList<>();
		PCs = new LinkedList<>();
		requestQueue = new LinkedList<>();
		playerSessionKeys = new HashMap<>();
		activePawns = new HashMap<>();
		receivedShutdown = false;
		
		if (fromTemplate) {
			// Fix back up path since we're done loading
			this.configDir = PATH_CONFIG;
		}
				
		// Nothing special; Session does not begin until 'run' is called
	}
	
	/**
	 * Saves template out to current root
	 */
	public void save() {
		save(this.root);
	}
	
	/**
	 * Saves template to new root folder.
	 * Does no cleanup of the old root
	 * @param newRoot
	 */
	public void save(File newRoot) {
		this.root = newRoot;
		this.saveConfigs();
		this.saveDatums();
		
		// TODO needs to save notes, etc
	}
	
	/**
	 * Runs session
	 * Blocks until it has finished.
	 * Sessions are responsible for loading and saving all their own data
	 */
	public void run() {
		load();
		init();
		
		this.setting = this.lookupSetting("Cavern");
		System.out.println("Checking for non-admin actions. Lots of null. How to handle?");
		for (Action a : this.getAvailableActions(false)) {
			if (a == null) {
				System.out.println("null action...");
			} else {
				System.out.println("Action: " + a.getName());
			}
		}
		System.out.println();
		System.out.println("Checking for admin actions. Lots of null. How to handle?");
		for (Action a : this.getAvailableActions(true)) {
			if (a == null) {
				System.out.println("null action...");
			} else {
				System.out.println("Action: " + a.getName());
			}
		}
		
		int debug = 0;
		System.out.println("60...");
		while (runLoop()) {
			if (isShuttingDown()) {
				// perform shutdown stuff
				System.out.println("Game Thread processing shutdown");
				break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				; // who cares?
			}
			
			if (debug < 20) {
				debug++;
				if (debug % 10 == 0)
					System.out.println(60 - (debug / 10) + "...");
			}
			else
				break;
		}
		
		shutdown();
		
		save();
		System.out.println("Game Thread has finished shutting down");
	}
	
	public void shutdown() {
		synchronized(receivedShutdown) {
			if (receivedShutdown)
				return;
			
			receivedShutdown = true;
			save();
		}
	}
	
	public boolean isShuttingDown() {
		synchronized(receivedShutdown) {
			return receivedShutdown;
		}
	}
	
	private void init() {
		// Export actions from datum to actionregistry
		Action action;
		for (ActionDatumData data : this.actionDatum.getData()) {
			action = data.toAction();
			ActionRegistry.instance().register(action.getName(), action);
		}
	}
	
	@Override
	public void addNote(String note) {
		notes.add(note);
	}
	
	@Override
	public void clearNotes() {
		notes.clear();
	}
	
	@Override
	public Collection<String> getNotes() {
		return notes;
	}
	
	public List<Player> getAllPlayers() {
		return PCs;
	}
	
	public List<Player> getPartyPlayers() {
		List<Player> players = new LinkedList<>();
		
		for (Pawn p : party)
			if (p instanceof Player)
				players.add((Player) p);
		
		return players;
	}
	
	public List<Pawn> getParty() {
		return party;
	}
	
	///////////////////////////////////////
	//                Comms              //
	///////////////////////////////////////
	
	/**
	 * Generate a key that's not in use.
	 * Does not return 0 (which is error key)
	 * @return
	 */
	private int generateSessionKey() {
		int key;
		Random rand = new Random();
		
		key = rand.nextInt();
		while (key == 0 || this.playerSessionKeys.containsKey(key)) {
			key = rand.nextInt();
		}
		
		return key;		
	}
	
	/**
	 * When comms take control of a player, they are given a session key
	 * When they talk, they use the key to fetch the player they own
	 * Keys are invalided on disconnect
	 * @param sessionID
	 * @return null if sessionkey is invalid
	 */
	public Player getSessionPlayer(int sessionkey) {
		// Test for invalid key (passed back on failed registration)
		if (sessionkey == 0)
			return null; 
		return this.playerSessionKeys.get(sessionkey);
	}
	
	/**
	 * Tries to claim player with given name to the calling comm.
	 * If player is already claimed (or name is invalid), returns 0.
	 * @param playerName
	 * @return 0 on error. Returns session key for later use
	 */
	public int registerForCharacter(String playerName) {
		Player p = this.lookupPlayer(playerName);
		if (p == null)
			return 0;
				
		if (!this.playerSessionKeys.isEmpty()) {
			for (int key : this.playerSessionKeys.keySet()) {
				if (this.playerSessionKeys.get(key) == p)
					return 0; // already registered!
			}
		}
		
		// Getting here means not already registered
		int key = generateSessionKey();
		this.playerSessionKeys.put(key, p);
		
		return key;
	}
	
	public void unregisterCharacter(int sessionKey) {
		if (!this.playerSessionKeys.containsKey(sessionKey))
			return;
		
		this.playerSessionKeys.remove(sessionKey); // invalidate old key
	}
	
	public List<Player> getUnregisteredPlayers() {
		if (this.playerSessionKeys.isEmpty()) {
			return this.getPlayers();
		}
		
		List<Player> unregistered = new LinkedList<>();
		
		for (Player p : this.getPlayers()) {
			int foundkey = 0;
			for (int key : this.playerSessionKeys.keySet()) {
				if (this.playerSessionKeys.get(key) == p) {
					foundkey = key;
					break;
				}
			}
			if (foundkey == 0)
				unregistered.add(p);
		}
		
		return unregistered;
	}
	
	/**
	 * Returns the session (UI) key for the given player
	 * If the player hasn't been registered, returns 0
	 * @param p
	 * @return
	 */
	public int lookupSessionKey(Player p) {
		if (playerSessionKeys.isEmpty())
			return 0;
		
		for (Integer key : playerSessionKeys.keySet()) {
			if (playerSessionKeys.get(key).equals(p))
				return key;
		}
		
		return 0;
	}
	
	///////////////////////////////////////
	//              Commands             //
	///////////////////////////////////////
	
	/**
	 * Generate a key that's not in use.
	 * Does not return 0 (which is error key)
	 * @return
	 */
	private int generatePawnKey() {
		int key;
		Random rand = new Random();
		
		key = rand.nextInt();
		while (key == 0 || this.activePawns.containsKey(key)) {
			key = rand.nextInt();
		}
		
		return key;		
	}
	
	public void removePawn(int key) {
		// Handle if they're a player that has a comm!!!!
		// TODO
		
		if (key == 0)
			return;
		
		if (this.activePawns.isEmpty())
			return;
		
		this.activePawns.remove(key);
		
		broadcast();
	}
	
	// All of these happen on main thread.
	// To call from UI, etc, use request queue
	// Simple as wrapping call in anon class
	
	/*
	 * All COMs (DM and PC)
	 * Perform action (from name)
	 *   Handles most of what clients do
	 * Lookup pawn details
	 * Lookup player profile
	 * Fetch inventory
	 * Interact with inv(need owner, slot (or item id??), action)
	 * Disconnect (free up PC control)
	 * 
	 * DM:
	 *   Perform action from pawn
	 *   Broadcast details
	 *   Broadcast log
	 *   Modify inventory
	 *   Modify pawn
	 *   Broadcast a screen clear
	 *   Initiate combat
	 *   Create anon NPC
	 *   Shutdown (kill clients)
	 *   Move areas (clear NPCs and crap)
	 *   Send active signal (combat turn) to player
	 */
	
	// General commands
	public void performAction(Pawn performer, String name) {
		Action action = ActionRegistry.instance().lookupAction(name);
		if (action == null) {
			System.out.println("Cannot perform unknown action: " + name);
		} else {
			action.perform(performer);
		}
	}
	
	public Player lookupPlayer(String name) {
		for (Player p : getPlayers())
			if (p.getName().equals(name))
				return p;
		
		return null;
	}
	
	/**
	 * This is NOT "lookup comm player by sessionkey"
	 * For that, use {@link #getSessionPlayer}
	 * @param internalID
	 * @return
	 */
	public Pawn lookupPawn(int internalID) {
		// look up pawn in set of active pawns
		return activePawns.get(internalID);
	}
	
	public Inventory fetchInventory(Pawn pawn) {
		if (pawn == null)
			return null;
		
		if (pawn instanceof Entity) {
			return ((Entity) pawn).getInventory();
		} else {
			System.out.println("Could not get inventory from " + pawn);
			return null;
		}		
	}
	
	public void interactInventory(Pawn actor, Pawn owner, int slotID, InventoryOperation operation) {
		Inventory inv = fetchInventory(owner);
		if (inv == null) {
			System.out.println("Cannot perform " + operation.name() + " on null inventory");
		} else {
			inv.performOperation(actor, slotID, operation);
		}
	}
	
	
	// DM commands
	public void broadcastLog(String line) {
		// TODO
		junk();
	}
	
	public void broadcastInventory(Inventory inv) {
		// TODO
		junk();
	}
	
	public void broadcastScreenClear() {
		junk();
	}
	
	public void modifyPawn(int pawnID, Pawn.PawnOverlay data) {
		Pawn pawn = this.lookupPawn(pawnID);
		if (pawn == null) {
			System.out.println("Cannot perform pawn data overlay on null pawn");
			return;
		}
		
		pawn.applyOverlay(data);
	}
	
	public void modifyPlayer(int pawnID, Player.PlayerOverlay data) {
		Pawn pawn = this.lookupPawn(pawnID);
		if (pawn == null) {
			System.out.println("Cannot perform pawn data overlay on null pawn");
			return;
		}
		
		if (!(pawn instanceof Player)) {
			System.out.println("Cannot perform player operation on non-player pawn");
			return;
		}
		
		((Player) pawn).applyOverlay(data);
	}
	
	public void modifyNPC(int pawnID, NPC.NPCOverlay data) {
		Pawn pawn = this.lookupPawn(pawnID);
		if (pawn == null) {
			System.out.println("Cannot perform pawn data overlay on null pawn");
			return;
		}
		
		if (!(pawn instanceof NPC)) {
			System.out.println("Cannot perform NPC operation on non-NPC pawn");
			return;
		}
		
		((NPC) pawn).applyOverlay(data);
	}
	
	public void modifyMob(int pawnID, Mob.MobOverlay data) {
		Pawn pawn = this.lookupPawn(pawnID);
		if (pawn == null) {
			System.out.println("Cannot perform pawn data overlay on null pawn");
			return;
		}
		
		if (!(pawn instanceof Mob)) {
			System.out.println("Cannot perform Mob operation on non-Mob pawn");
			return;
		}
		
		((Mob) pawn).applyOverlay(data);
	}
	
	/**
	 * Adds the pawn to the list of active pawns
	 * Returns pawn key, in case you want it
	 * @param pawn
	 * @return 0 on error, non-zero key on success
	 */
	public int addActivePawn(Pawn pawn) {
		if (pawn == null)
			return 0;
		
		int key = this.generatePawnKey();
		this.activePawns.put(key, pawn);
		
		broadcast();
		return key;
	}
	
	/**
	 * Clears all pawns that aren't part of the party from
	 * the active list
	 */
	public void clearNonParty() {
		this.activePawns.entrySet().removeIf(e -> !party.contains(e.getValue()));
		broadcast();
	}
	
	/**
	 * Includes players <3
	 * @return
	 */
	public Collection<Pawn> getAllActivePawns() {
		return activePawns.values();
	}
	
	/**
	 * Gets all available actions based on active pawns and setting
	 * @return
	 */
	public List<Action> getAvailableActions(boolean admin) {
		List<Action> actions = new LinkedList<>();
		
		if (setting != null)
			actions.addAll(setting.getActions(admin, null));
		
		for (Pawn p : activePawns.values())
			actions.addAll(p.getActions(admin, null));
		
		return actions;
	}
	
	/**
	 * First, clears current setting. If clearPawns, also clears non-party pawns.
	 * Then, sets setting to new setting if it can be found.
	 * @param newSettingTitle
	 */
	public void moveSetting(String newSettingTitle, boolean clearPawns) {
		this.setting = null;
		this.clearNonParty();
		
		this.setting = this.lookupSetting(newSettingTitle);
		broadcast();
	}
	
	/**
	 * Can be null. Clients should show nothing when null
	 * @return
	 */
	public Setting getCurrentSetting() {
		return setting;
	}
	
	///////////////////////////////////////
	//             Threading             //
	///////////////////////////////////////
	
	/**
	 * Runs commands and side effects.
	 * Returns true if loop should continue after a sleep.
	 * False breaks loop
	 * @return
	 */
	private boolean runLoop() {
		// pull queued commands.
		// until we have none.
		// Then yield
		AsyncRequest request;
		
		while (null != (request = dequeueCommand())) {
			if (request.run(this))
				return false;
		}
		
		return true;
	}
	
	public void queueCommand(AsyncRequest request) {
		synchronized(this.requestQueue) {
			this.requestQueue.add(request);
		}
	}
	
	private AsyncRequest dequeueCommand() {
		synchronized(this.requestQueue) {
			if (this.requestQueue.isEmpty())
				return null;
			
			AsyncRequest pop = this.requestQueue.get(0);
			this.requestQueue.remove(0);
			return pop;
		}
	}

	public List<SpellDatumData> getAllSpells() {
		return this.spellDatum.getData();
	}
	
}
