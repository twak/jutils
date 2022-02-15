package org.twak.utils;

import java.io.Serializable;

public class MutableDouble implements Serializable {
	
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
