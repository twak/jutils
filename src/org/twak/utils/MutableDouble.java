package org.twak.utils;

public class MutableDouble {
	
	public double d;

	public MutableDouble (double d )
    {
        this.d = d;
    }

	@Override
	public String toString() {
		return "" + d;
	}
}
