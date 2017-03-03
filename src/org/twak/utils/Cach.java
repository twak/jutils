package org.twak.utils;

public class Cach <I,O> extends Cache<I, O> {

	public interface Make<I,O> {
		public O make(I i);
	}
	
	Make<I,O> make;
	
	public Cach (Make<I,O> creator) {
		this.make = creator;
	}
	
	@Override
	public O create( I i ) {
		return make.make( i );
	}

}
