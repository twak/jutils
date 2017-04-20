package org.twak.utils;

import java.util.Collection;

public class DumbCluster1DImpl<E> extends DumbCluster1D<E>{

	ToDouble<E> toDouble;
	
	public interface ToDouble<E> {
		public double toDouble( E e );
	}
	
	public DumbCluster1DImpl (double tol, Collection<E> thingsIn, ToDouble<E> todouble) {
		this.toDouble = todouble;
		setup (tol, thingsIn);
	}

	@Override
	public double toDouble( E e ) {
		return toDouble.toDouble( e );
	}
}
