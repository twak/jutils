package org.twak.utils.collections;

import java.util.*;
import java.util.Map.Entry;

/**
 * HashMap backed mutli-item hash. eg a map for every entry in the hash table
 * @author twak
 */
public class MultiMapSet <A,B> //implements Map<A,List<B>>
{
    public Map <A, Set<B>> map = new LinkedHashMap();

    public void addEmpty( A a )
    {
        if (!map.containsKey( a ))
            map.put ( a, new LinkedHashSet());
    }

    public int size()
    {
        return map.size();
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public boolean containsKey( Object key )
    {
        return map.containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public Set<B> get( A key )
    {
        Set<B> out = map.get( key );
        if (out == null)
            return new LinkedHashSet();
        return out;
    }

    public Set<B> getOrAdd( A key )
    {
        Set<B> out = map.get( key );
        if ( out == null )
        {
            Set<B> b = new LinkedHashSet();
            map.put( key, b );
            return b;
        }
        return out;
    }

    public boolean remove (A key, B value)
    {
        Set<B> out = map.get( key );
        if (out == null)
            return false;

        return out.remove( value );
    }

    public void put ( A key, B value )
    {
        Set<B> out = map.get( key );
        if (out == null)
        {
            out = new LinkedHashSet();
            map.put( key, out );
        }
        out.add( value );
    }

    /**
     * Removes entire map indexed by key
     * @param key
     * @return
     */
    public Set<B> remove( A key )
    {
        return map.remove( key );
    }

    public void removeClean( A a, B b )
    {
        Set<B> out = map.get( a );
        if (out == null)
            return;

        out.remove( b );
        
        if (out.isEmpty())
            map.remove( a );
    }
    
    public void putAll( Map<? extends A, ? extends B> m )
    {
        for (Map.Entry<? extends A, ? extends B> entry : m.entrySet())
            put( entry.getKey(), entry.getValue() );
    }

    public void clear()
    {
        map.clear();
    }

    public Set<A> keySet()
    {
        return map.keySet();
    }

    public Collection<Set<B>> values()
    {
        return map.values();
    }

    public Set<Entry<A, Set<B>>> entrySet()
    {
        return map.entrySet();
    }

    public void putAll( A a, Iterable<B> bs )
    {
        for (B b : bs)
            put( a, b );
    }

    public void removeAll( A a, Iterable<B> bs )
    {
        for (B b : bs)
            remove( a, b );
    }
}
