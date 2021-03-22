package org.twak.utils.collections;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Two dimensional cache
 * @author twak
 */
public class MapMapMap<I1, I2, I3,O>
{
    public Map<I1,Map<I2,Map<I3,O>>> cache = new LinkedHashMap();

    public void put (I1 i1, I2 i2, I3 i3, O o)
    {
        Map<I2,Map<I3,O>> cache2 = cache.get( i1 );
        if ( cache2 == null )
            cache.put( i1, cache2 = new LinkedHashMap<I2,Map<I3,O>>() );

        Map<I3, O> cache3 = cache2.get( i2 );
        if ( cache3 == null )
            cache2.put( i2, cache3 = new LinkedHashMap<I3,O>() );


        cache3.put( i3, o );
    }

    public O get( I1 i1, I2 i2, I3 i3 )
    {
        Map<I2,Map<I3,O>> cache2 = cache.get( i1 );

        if ( cache2 == null )
            return null;

        Map<I3, O> cache3 = cache2.get( i2 );

        if ( cache3 == null )
            return null;

        return cache3.get( i3 );
    }

    public Map<I2,Map<I3,O>> get( I1 i1 ) {
        return cache.get( i1 );
    }
    public Map<I3,O> get( I1 i1, I2 i2 ) {
        Map<I2,Map<I3,O>> cache2 = cache.get( i1 );

        if ( cache2 == null )
            return null;

        return cache2.get( i2 );
    }
}
