package org.twak.utils;

public class Cancellable {
	
	public boolean cancelled = false;
	
	public void cancel() {
		this.cancelled = true;
	}
}
