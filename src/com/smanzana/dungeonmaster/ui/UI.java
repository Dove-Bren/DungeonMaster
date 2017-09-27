package com.smanzana.dungeonmaster.ui;

import java.util.LinkedList;
import java.util.List;

public class UI {
	
	private static interface UIRequest extends UICallback, UIRequestRun {
		
	}
	
	private static interface UIRequestRun {
		public void run();
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
			public void run() {
				req.run();
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
	
	private void 
	
	
	
	
	
	
	
	//////////////////////////////
	// Public facing functions  //
	//////////////////////////////
	
	public boolean askDM(String desc) {
		
		
		
		// lock.ret is return
		// passfail returns "pass" or "fail"
		return (ret != null && ret.equals("pass"));
		
	}
	
	
	
}
