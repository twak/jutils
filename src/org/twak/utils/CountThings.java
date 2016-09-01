package org.twak.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author
 * twak
 */
public class CountThings<E>
{

    Cache<E, MutableInteger> counts = new Cache<E, MutableInteger>()
    {

        @Override
        public MutableInteger create( E i )
        {
            return new MutableInteger( 0 );
        }
    };
    int seen = 0;

    public void count( E e )
    {
        counts.get( e ).i++;
        seen++;
    }

    public Pair<E, Integer> getMax()
    {
        E val = null;
        int max = Integer.MIN_VALUE;
        
        for ( Map.Entry<E, MutableInteger> e : counts.cache.entrySet() )
        {
            if (e.getValue().i > max)
            {
                max = e.getValue().i;
                val = e.getKey();
            }
        }
        
        return new Pair(val, max);
    }
    
    public Pair<Set<E>, Integer> getMaxes()
    {
        Set<E> val = null;
        int max = Integer.MIN_VALUE;
        
        for ( Map.Entry<E, MutableInteger> e : counts.cache.entrySet() )
        {
            if (e.getValue().i > max)
            {
                if ( e.getValue().i != max )
                {
                    val = new HashSet();
                    max = e.getValue().i;
                }
                val.add ( e.getKey() );
            }
        }
        
        return new Pair(val, max);
    }

    public int getSize()
    {
        return seen;
    }
}
