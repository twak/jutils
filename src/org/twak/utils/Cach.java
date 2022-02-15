package org.twak.utils;

import java.io.Serializable;

public class Cach <I,O> extends Cache<I, O> {

	public interface Make<I,O> {
		public O make(I i);
	}
	
	Make<I,O> make;
	
	public Cach (Make<I,O> creator) {
		super();
		this.make = creator;
	}
	
	@Override
	public O create( I i ) {
		
		if (make == null)
			return null;
		
		return make.make( i );
	}

    public static class ConstMake<T,C> implements Make<T,C>, Serializable {
		C x;
		public ConstMake(C x) {
			this.x = x;
		}
		@Override
		public C make(T t) {
			return (C) CloneSerializable.clone(x);
		}
	}
}
