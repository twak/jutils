package org.twak.utils.collections;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Two dimensional cache
 * @author twak
 */
public class MapMap<I1, I2,O>
{
    public Map<I1,Map<I2,O>> cache = new LinkedHashMap();

    public void put (I1 i1, I2 i2, O o)
    {
        Map<I2, O> cache2 = cache.get( i1 );
        if ( cache2 == null )
            cache.put( i1, cache2 = new LinkedHashMap<I2, O>() );

        cache2.put( i2, o );
    }

    public O get( I1 i1, I2 i2 )
    {
        Map<I2, O> cache2 = cache.get( i1 );
        if ( cache2 == null )
            return null;
//            cache.put( i1, cache2 = new LinkedHashMap<I2, O>() );

        return cache2.get( i2 );
    }

    public Map<I2,O> get( I1 i1 ) {
        return cache.get( i1 );
    }
}
