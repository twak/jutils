
package org.twak.utils;

import java.util.Iterator;
import java.util.List;

/**
 * Double linked-list wrapper around an arbitrary object
 * @author twak
 */
public class Loop <E> implements Iterable<E>
{
	public List<Loop<E>> children;
	
    public Loopable<E> start;
    
    public Loop ()
    {
        start = null;
    }
    
    public Loop(Loop<E> toClone) {
    	for (E e : toClone)
    		append(e);
    }

    public int count()
    {
        int count = 0;
        
        for (E e : this)
            count++;

        return count;
    }

    public void removeAll()
    {
        start = null;
    }
    
    public Loopable<E> append (E append)
    {
        if (start == null)
        {
            start = new Loopable ( append );
            start.setNext(start);
            start.setPrev(start);
            return start;
        }
        else
        {
            Loopable<E> toAdd = new Loopable(append);
            
            toAdd.setPrev(start.getPrev());
            toAdd.setNext(start);
            start.getPrev().setNext(toAdd);
            start.setPrev(toAdd);

            return toAdd;
        }
    }

    public Loopable<E> addAfter( Loopable<E> loopable, E bar )
    {
        
        Loopable<E> n = new Loopable( bar );
        if ( loopable == null )
        {
            start = n;
            start.setNext( start );
            start.setPrev( start );
        }
        else
        {
            n.setPrev( loopable );
            n.setNext( loopable.next );
            n.getPrev().setNext( n );
            n.getNext().setPrev( n );
        }
        return n;
    }

    public void remove (E remove)
    {
        Loopable<E> togo = find (remove);
        remove (togo);
    }

    public void remove (Loopable<E> togo)
    {
        if (togo == start)
        {
            if (togo.prev == togo)
                start = null;
            else
                start = togo.prev;
        }
        
        togo.prev.next = togo.next;
        togo.next.prev = togo.prev;
    }

    public Loopable<E> find (E remove)
    {
        Loopable<E> n = start;

        while ( n.next != start )
        {
            if (n.me.equals( remove ))
                return n;

            n = n.next;
        }

        if (n.me.equals( remove ))
                return n;
        
        return null;
    }

    public Loopable<E> getFirstLoopable()
    {
        return start;
    }
            
            
    public E getFirst()
    {
        if (start == null)
            return null;

        return start.me;
    }

    public Iterable<Loopable<E>> loopableIterator()
    {
        return new Iterable<Loopable<E>>()
        {
            public Iterator<Loopable<E>> iterator()
            {
                return new LoopableIterator();
            }
        };
    }

    public Iterator<E> iterator()
    {
        return new LoopIterator();
    }

    public void reverse()
    {
        if ( start == null )
            return;

        Loopable m = start;

        do
        {
            Loopable tmp = m.next;
            m.next = m.prev;
            m.prev = tmp;

            m = m.prev; // reversed ;)
        }
        while (m != start);
    }

    public class LoopableIterator implements Iterator<Loopable<E>>
    {
        Loopable<E> s, n;
        
        public LoopableIterator()
        {
            s = start;
            n = null;
        }
        public boolean hasNext()
        {
            if (s == null)
                return false;
            if (n == null)
                return true;
            return n != start;
        }

        public Loopable<E> next()
        {
            if (n == null)
                n = start;
            
            Loopable<E> out = n;
            n = n.getNext();
            return out;
        }

        public void remove()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    public class LoopIterator implements Iterator<E>
    {
        LoopableIterator lit = new LoopableIterator();

        public boolean hasNext()
        {
            return lit.hasNext();
        }

        public E next()
        {
            return lit.next().me;
        }

        public void remove()
        {
            lit.remove();
        }
    }

    public abstract class Map<O>
    {
        public Map(){}

        public Loop<O> run()
        {
            Loop<O> loopO = new Loop();
            for (Loopable<E> e : loopableIterator())
                loopO.append(map(e));

            return loopO;
        }
        // convert teh
        public abstract O map (Loopable<E> input);
    }
}
