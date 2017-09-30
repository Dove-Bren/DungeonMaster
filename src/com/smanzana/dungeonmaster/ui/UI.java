package com.smanzana.dungeonmaster.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.inventory.Inventory;
import com.smanzana.dungeonmaster.pawn.NPC;
import com.smanzana.dungeonmaster.pawn.Pawn;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.ui.common.InventoryView;
import com.smanzana.dungeonmaster.ui.common.MessageBox;
import com.smanzana.dungeonmaster.ui.common.NPCView;
import com.smanzana.dungeonmaster.ui.common.PlayerView;
import com.smanzana.dungeonmaster.ui.common.TargetView;

public class UI implements Runnable {
	
	private static interface UIRequest extends UICallback, UIRequestRun {
		
	}
	
	private static interface UIRequestRun {
		public void run(UICallback hook);
	}
	
	private static class Callback {
		private Boolean lock;
		private String ret;
		
		public Callback() {
			lock = false;
		}
		
		public void setReturn(String ret) {
			this.ret = ret;
		}
		
		public String getReturn() {
			return this.ret;
		}
		
		public void set(boolean lock) {
			synchronized(this.lock) {
				this.lock = lock;
			}
		}
		
		public boolean get() {
			synchronized(this.lock) {
				return this.lock;
			}
		}
	}

	private static UI instance = null;
	
	public static UI instance() {
		if (instance == null)
			instance = new UI();
		
		return instance;
	}
	
	private List<UIRequest> requests;
	private Comm DMComm;
	private Map<Integer, Comm> PlayerComms; // PlayerID (from session) to comm
	private Boolean running = false;
	
	private UI() {
		requests = new LinkedList<>();
	}
	
	public void run() {
		UIRequest next;
		
		System.out.println("Starting UI Thread...");
		running = true;
		
		while (true) {
			
			next = popRequest();
			if (next != null) {
				next.run(next);
			}
					
			
			synchronized(running) {
				if (!running)
					break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				;
			}
		}
		
		System.out.println("UI Thread has Shutdown");
	}
	
	public void halt() {
		synchronized(running) {
			System.out.println("UI Thread receieved halt");
			running = false;
		}
	}
	
	private void pushRequest(UIRequest req) {
		synchronized(requests) {
			requests.add(req);
		}
	}
	
	private UIRequest popRequest() {
		synchronized(requests) {
			if (requests.size() == 0)
				return null;
			
			UIRequest ret = requests.get(0);
			requests.remove(0);
			return ret;
		}
	}
	
	private String pushRequestBlocking(UIRequestRun req) {
		final Callback callback = new Callback();
		pushRequest(new UIRequest() {

			@Override
			public void run(UICallback hook) {
				req.run(hook);
			}

			@Override
			public void callback(String serialReturn) {
				callback.setReturn(serialReturn);
				callback.set(true);
			}
			
		});
		
		while (!callback.get()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
		return callback.getReturn();
	}
	
	private Comm getDMComm() {
		return this.DMComm;
	}
	
	private Comm getPlayerComm(Player p) {
		return getPlayerComm(DungeonMaster.getActiveSession().lookupSessionKey(p));
	}
	
	private Comm getPlayerComm(int sessionKey) {
		return this.PlayerComms.get(sessionKey);
	}
	
	private void displayMessage(Comm comm, String message, List<String> options, UICallback callback) {
		MessageBox box = new MessageBox(message, options);
		comm.showMessageBox(box, callback);
	}
	
	private void displayYesNo(Comm comm, String message, UICallback cb) {
		List<String> opts = new ArrayList<>(3);
		opts.add("Yes");
		opts.add("No");
		
		MessageBox box = new MessageBox(message, opts);
		final UICallback stat = cb; 
		
		comm.showMessageBox(box, new UICallback() {
			@Override
			public void callback(String serialResponse) {
				stat.callback(serialResponse.equalsIgnoreCase("yes") ? "true" : "false");
			}
		});
	}
	
	
	
	
	
	
	
	//////////////////////////////
	// Public facing functions  //
	//////////////////////////////
	
	/**
	 * Simple yes/no question.
	 * DM will have access to a die, but not any real roll checking
	 * @param desc
	 * @return
	 */
	public boolean askDM(String desc) {
		
		String decision = pushRequestBlocking(new UIRequestRun() {
			@Override
			public void run(UICallback hook) {
				displayYesNo(getDMComm(), desc, hook);
			}
		});
		
		// Yes/No converts to 'true' or 'false'
		return (decision != null && decision.equals("true"));
		
	}
	
	// Returns false if player cannot be found (no comm)
	// Maybe should ask DM instead in this case... //TODO
	public boolean askPlayer(Player p, String desc) {
		if (p == null || getPlayerComm(p) == null)
			return false;
		
		String decision = pushRequestBlocking(new UIRequestRun() {
			@Override
			public void run(UICallback hook) {
				displayYesNo(getPlayerComm(p), desc, hook);
			}
		});
		
		// Yes/No converts to 'true' or 'false'
		return (decision != null && decision.equals("true"));
	}
	
	public boolean askParty(String desc) {
		// iterate over players.
		// Dont' use blocking call, so that we can ask all at the same time
		// Then check all returns for a false.
		
		if (this.PlayerComms.isEmpty()) 
			return true; // No party to query
		
		final Map<Integer, Boolean> results = new HashMap<>();
		List<String> opts = new ArrayList<>(3);
		opts.add("Agree");
		opts.add("Refute");
		for (Integer key : PlayerComms.keySet()) {
			final int myKey = key;
			results.put(key, null); // to signal that we haven't got anything back
									// but that we've asked
			this.displayMessage(PlayerComms.get(key), desc, opts, new UICallback() {

				@Override
				public void callback(String serialResponse) {
					results.put(myKey, serialResponse.equals(opts.get(0))); // 0 is "Agree"
				}
				
			});
		}
		
		while (true) {
			// Sleep first since we're going to have to wait anyways
			// Plus following a 'continue' we hit a sleep :)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				;
			}
			
			// Check for any outstanding queries
			for (Integer key : results.keySet()) {
				// if key maps to NULL, no response has come in
				if (results.get(key) == null)
					continue;
			}
			
			// No comms had null, so we're good to eval
			break;
		}
		
		boolean hit = false;
		for (Boolean bool : results.values()) {
			if (!bool) {
				hit = true;
				break;
			}
		}
		
		return !hit;
	}
	
	public void sendInventory(Player player, Inventory inventory) {
		if (player == null || getPlayerComm(player) == null)
			return;
		
		InventoryView inv = new InventoryView(inventory);
		pushRequest(new UIRequest() {

			@Override
			public void callback(String serialResponse) {
				; // Don't care
			}

			@Override
			public void run(UICallback hook) {
				getPlayerComm(player).showInventory(inv);
			}
			
		});
	}
	
	public void sendNPC(Player player, NPC npc) {
		if (player == null || getPlayerComm(player) == null)
			return;
		
		NPCView view = new NPCView(npc);
		pushRequest(new UIRequest() {

			@Override
			public void callback(String serialResponse) {
				; // Don't care
			}

			@Override
			public void run(UICallback hook) {
				getPlayerComm(player).showNPC(view);
			}
			
		});
	}
	
	public void sendPlayer(Player player, Player viewedPlayer) {
		if (player == null || getPlayerComm(player) == null)
			return;
		
		PlayerView view = new PlayerView(viewedPlayer);
		pushRequest(new UIRequest() {

			@Override
			public void callback(String serialResponse) {
				; // Don't care
			}

			@Override
			public void run(UICallback hook) {
				getPlayerComm(player).showPlayer(view);
			}
			
		});
	}
	
	/**
	 * 
	 * @param picker Player picking. If null, asks DM
	 * @param targets
	 * @return
	 */
	public Pawn selectSingleTarget(Player picker, Collection<Pawn> targets) {
		// convert into collection of TargetViews
		// Then call comm and block
		// on reply, look up returned string to match to targetview
		// then return targetview->target
				
		if (targets == null || targets.isEmpty())
			return null;
		
		Comm comm;
		if (picker == null)
			comm = this.DMComm;
		else
			comm = getPlayerComm(picker);
		
		if (comm == null)
			return null;
		
		List<TargetView<? extends Pawn>> views = new LinkedList<>();
		for (Pawn targ : targets) {
			views.add(new TargetView<>(targ));
		}
		
		String selected = pushRequestBlocking(new UIRequestRun() {

			@Override
			public void run(UICallback hook) {
				comm.showTargetSelect(views, false, hook);
			}
			
		});
		
		if (selected == null || selected.isEmpty())
			return null;
		
		selected = selected.trim();
		
		for (TargetView<? extends Pawn> view : views) {
			if (view.getName().trim().equals(selected))
				return view.getTarget();
		}
		
		return null;
	}
	
	/**
	 * @param picker Player picking. If null, asks DM
	 * @param targets
	 * @return Null on basic error. Empty list if no matches
	 */
	public Collection<Pawn> selectTargets(Player picker, Collection<Pawn> targets) {
		if (targets == null || targets.isEmpty())
			return null;
		
		Comm comm;
		if (picker == null)
			comm = this.DMComm;
		else
			comm = getPlayerComm(picker);
		
		if (comm == null)
			return null;
		
		List<TargetView<? extends Pawn>> views = new LinkedList<>();
		for (Pawn targ : targets) {
			views.add(new TargetView<>(targ));
		}
		
		String selected = pushRequestBlocking(new UIRequestRun() {

			@Override
			public void run(UICallback hook) {
				comm.showTargetSelect(views, true, hook);
			}
			
		});
		
		if (selected == null || selected.isEmpty())
			return null;
		
		selected = selected.trim();
		
		List<Pawn> selTargs = new LinkedList<>();
		
		for (TargetView<? extends Pawn> view : views) {
			if (view.getName().trim().equals(selected))
				selTargs.add(view.getTarget());
		}
		
		return selTargs;
	}
	
}
