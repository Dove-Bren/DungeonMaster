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
import com.smanzana.dungeonmaster.pawn.Mob;
import com.smanzana.dungeonmaster.pawn.NPC;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
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
	
	private static final String PATH_CONFIG = "data/";
	
	private List<String> notes;
	private List<Pawn> party;

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
		
	public GameSession(File root) {
		super(root, PATH_CONFIG);
		notes = new LinkedList<>();
		party = new LinkedList<>();
		requestQueue = new LinkedList<>();
		playerSessionKeys = new HashMap<>();
		activePawns = new HashMap<>();
		
		// just for testing
		load();
		// just for testing
		
		// Nothing special; Session does not begin until 'run' is called
	}
	
	/**
	 * Runs session
	 * Blocks until it has finished.
	 * Sessions are responsible for loading and saving all their own data
	 */
	public void run() {
		load();
		
		while (runLoop()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				; // who cares?
			}
		}
		
		save();
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
	
	public List<Player> getPlayers() {
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
	 * @return 0 on error. non-zero on success
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
	
	///////////////////////////////////////
	//              Commands             //
	///////////////////////////////////////
	
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
		
		if (pawn instanceof NPC) {
			return ((NPC) pawn).getInventory();
		} else if (pawn instanceof Player) {
			return ((Player) pawn).getInventory();
		} else {
			System.out.println("Could not get inventory from " + pawn);
			return null;
		}		
	}
	
	public void interactInventory(Pawn owner, int slotID, InventoryOperation operation) {
		Inventory inv = fetchInventory(owner);
		if (inv == null) {
			System.out.println("Cannot perform " + operation.name() + " on null inventory");
		} else {
			inv.performOperation(operation, slowID);
		}
	}
	
	public void broadcastLog(String line) {
		
	}
	
	public void broadcastInventory(Inventory inv) {
		
	}
	
	public void broadcastScreenClear() {
		
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
	
	public void modifyNPC(int pawnID, Mob.MobOverlay data) {
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
	
}
