package org.twak.utils;

import java.io.Serializable;

/**
 *
 * @author twak
 */
public class MutableInteger implements Serializable {
	public int i;

	public MutableInteger( int i ) {
		this.i = i;
	}

	public MutableInteger() {
		this( 0 );
	}

	@Override
	public String toString() {
		return "" + i;
	}
}
