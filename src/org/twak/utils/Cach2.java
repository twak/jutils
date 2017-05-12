package org.twak.utils;

public class Cach2<I1, I2,O> extends Cache2<I1, I2,O> {

	public interface Create<I1, I2,O> {
		public O create (I1 a, I2 b);
	}
	
	Create create;

	public Cach2 (Create create) {
		this.create = create;
	}
	
	@Override
	public Object create( Object i1, Object i2 ) {
		return create( i1, i2 );
	}

}
