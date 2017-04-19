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

    public Cache<E, MutableInteger> counts = new Cache<E, MutableInteger>()
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
    
    public void count( E e, int by )
    {
    	counts.get( e ).i+= by;
    	seen += by;
    }

    public void uncount( E e )
    {
        counts.get( e ).i--;
        seen--;
    }
    
    public int total( E e ) {
    	return counts.get(e).i;
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
    
    public Pair<E, Integer> getMin()
    {
    	E val = null;
    	int min = Integer.MAX_VALUE;
    	
    	for ( Map.Entry<E, MutableInteger> e : counts.cache.entrySet() ) {
    		
    		if (e.getValue().i < min)
    		{
    			min = e.getValue().i;
    			val = e.getKey();
    		}
    	}
    	
    	return new Pair(val, min);
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
