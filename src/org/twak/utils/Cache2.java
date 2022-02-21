package org.twak.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Two dimensional cache
 * 
 * @author twak
 */
public abstract class Cache2<I1, I2, O> {
	
	public Map<I1, Map<I2, O>> cache = new LinkedHashMap();

	public O get( I1 i1, I2 i2 ) {
		Map<I2, O> cache2 = cache.get( i1 );
		if ( cache2 == null )
			cache.put( i1, cache2 = new LinkedHashMap<I2, O>() );

		O o2 = cache2.get( i2 );
		if ( o2 == null )
			cache2.put( i2, o2 = create( i1, i2 ) );

		return o2;
	}

	public abstract O create( I1 i1, I2 i2 );

	public void put( I1 i1, I2 i2, O start0 ) {
		Map<I2, O> cache2 = cache.get( i1 );
		if ( cache2 == null )
			cache.put( i1, cache2 = new LinkedHashMap<I2, O>() );

		cache2.put( i2, start0 );
	}

	public static void main( String[] args ) {

		Cach2 flibble = new Cach2<>( ( a, b ) -> new ArrayList<>() );

		flibble.get( new Object(), new Object() );

	}

	public void clear() {
		cache.clear();
	}

    public int size() {
		return cache.values().stream().mapToInt( x -> x.size() ).sum();

    }
}
