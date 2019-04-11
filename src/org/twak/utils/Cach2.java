package org.twak.utils;

public class Cach2<I1, I2,O> extends Cache2<I1, I2,O> {

	public interface Create<I1, I2,O> {
		public O create (I1 a, I2 b);
	}
	
	Create<I1, I2,O> create;

	public Cach2 (Create create) {
		this.create = create;
	}
	
	@Override
	public O create(I1 i1, I2 i2) {
		return create.create( i1, i2 );
	}
}
