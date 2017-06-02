package org.twak.utils.ui;

public class Cancellable {
	
	public boolean cancelled = false;
	
	public void cancel() {
		this.cancelled = true;
	}
}
