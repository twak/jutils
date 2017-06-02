package org.twak.utils.collections;

import java.util.HashMap;

public class SuperLoop<E> extends Loop<E> {

	public java.util.Map<String, Object> properties = new HashMap<>();
	
	public SuperLoop( String name ) {
		super();
		properties.put("name", name);
	}

	public SuperLoop( java.util.Map<String, Object> p ) {
		properties.putAll( p );
	}

}
