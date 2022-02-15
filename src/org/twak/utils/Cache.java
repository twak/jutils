package org.twak.utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author twak
 */
public abstract class Cache<I,O> implements Serializable
{
    public Map<I,O> cache = new LinkedHashMap();

    public O get( I in )
    {
        O o = cache.get( in );
        if ( o == null )
            cache.put( in, o = create( in ) );
        return o;
    }

    public abstract O create(I i);

    public void put( I start, O start0 )
    {
        cache.put( start, start0);
    }

    public void clear()
    {
        cache.clear();
    }

	public static <J> void  sort   ( List<J> ps, Cache<J, Double> scores ) {
		Collections.sort(ps, (a,b) -> Double.compare(scores.get(a), scores.get(b)));
	}
}
