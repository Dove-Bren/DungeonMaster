package com.smanzana.dungeonmaster.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.DungeonMaster;
import com.smanzana.dungeonmaster.pawn.Player;
import com.smanzana.dungeonmaster.ui.common.MessageBox;

public class UI {
	
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
	
	private UI() {
		requests = new LinkedList<>();
	}
	
	private void pushRequest(UIRequest req) {
		synchronized(requests) {
			requests.add(req);
		}
	}
	
	private UIRequest popRequest() {
		synchronized(requests) {
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
	
	
	
}
