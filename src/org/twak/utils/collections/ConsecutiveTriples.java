package org.twak.utils.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.twak.utils.Triple;

/**
 * Iterator that returns all consecutive triples in a map
 * @author twak
 */
public class ConsecutiveTriples <E> implements Iterator<Triple<E,E,E>>, Iterable<Triple<E,E,E>>
{
    List<E> input;
    int a = 0, size;
    boolean loop;

    /**
     *
     * @param input the map to returns triples from
     * @param loop is it cyclic? (do we return {end-1, end, start} etc...))
     */
    public ConsecutiveTriples (Iterable<E> input, boolean loop)
    {
        List<E> ha = new ArrayList();
        Iterator<E> it = input.iterator();
        while (it.hasNext())
            ha.add( it.next());
        
        setup( ha, loop );
    }
    public ConsecutiveTriples (List<E> input, boolean loop)
    {
        setup (input,loop);
    }

    private void setup  (List<E> input, boolean loop)
    {
        this.input = input;
        this.loop = loop;
        this.size = input.size();
        if (input.size() < 3)
            a = -1;
    }

    public boolean hasNext()
    {
        return a >= 0;
    }

    public Triple<E,E,E> next()
    {
        Triple<E, E, E> out = new Triple<E, E, E>(
                input.get( a % size ),
                input.get( ( a + 1 ) % size ),
                input.get( ( a + 2 ) % size ) );

        a++;
        if (!loop && a == size - 2)
            a = -1; // end
        else if (loop && a == size )
            a = -1; // end

        return out;
    }

    public void remove()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public Iterator<Triple<E, E, E>> iterator()
    {
        return this;
    }
    
    public static void main (String[] args)
    {
        List<Integer> list = new ArrayList();
        list.add( 1 );
        list.add( 2 );
//        map.add( 3 );
//        map.add( 4 );
//        map.add( 5 );
        for ( Triple <Integer, Integer, Integer> t : new ConsecutiveTriples<Integer>( list, false ))
        {
            System.out.println(t);
        }
    }

}
