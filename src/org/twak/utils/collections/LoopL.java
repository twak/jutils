package org.twak.utils.collections;

/**
 *
 * @author twak
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import org.twak.utils.LContext;

/**
 * a loop of loops, with an iterator for the contained primitive (corners!)
 * @author twak
 */
public class LoopL<E> extends ArrayList<Loop<E>>
{
    public LoopL(){}
    public LoopL( Loop<E> fromPoints )
    {
        this();
        add( fromPoints );
    }
    public LoopL( LoopL<E> toClone)
    {
        this();
        for (Loop<E> loop : toClone)
        	add(new Loop<E>(loop) );
    }
    
    
    public Iterable<E> eIterator()
    {
        return new EIterable();
    }
    
    public class EIterable implements Iterable<E>
    {
        public Iterator<E> iterator()
        {
            return new ItIt( LoopL.this );
        }
    }

    public void addLoopL( LoopL<E> e )
    {
        addAll( e );
    }

    public int count()
    {
        int i = 0;
        
        for ( Loop<E> l : this )
            for ( E e : l)
                i++;
            
        return i;
    }



    public void reverseEachLoop()
    {
        for (Loop<E> loop : this)
            loop.reverse();
    }

    public Iterator<LContext<E>> getCIterator()
    {
        return new ContextIt();
    }
    
    public Iterable<LContext<E>> getCIterable()
    {
        return new Iterable<LContext<E>>()
        {
            public Iterator<LContext<E>> iterator()
            {
                return getCIterator();
            }

        };
    }

    public class ContextIt implements Iterator <LContext<E>>
    {
        Iterator<Loop<E>> loopIt  = null;
        Iterator<Loopable<E>> loopableIt = null;

        // next values to return
        Loopable<E> loopable;
        Loop<E> loop;

        public ContextIt()
        {
            loopIt = LoopL.this.iterator();
            findNext();
        }

        private void findNext()
        {
            if (loopIt == null)
            {
                return; // finished!
            }
            else if (loopableIt != null && // start
                    loopableIt.hasNext())
            {
                loopable = loopableIt.next();
            }
            else if (loopIt.hasNext())
            {
                loopableIt = (loop = loopIt.next()).loopableIterator().iterator();
                findNext();
            }
            else
            {
                loopIt = null;
            }
        }

        public boolean hasNext()
        {
            return loopIt != null;
        }

        public LContext next()
        {
            LContext out = new LContext( loopable, loop );
            findNext();
            return out;
        }

        public void remove()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }
    
    public Iterator<Loopable<E>> getLoopableIterator()
    {
        return new LoopableLIterator();
    }
    
    public Iterable<Loopable<E>> getLoopableIterable()
    {
        return new Iterable<Loopable<E>>()
        {
            public Iterator<Loopable<E>> iterator()
            {
                return new LoopableLIterator();
            }

        };
    }
    
    public class LoopableLIterator implements Iterator <Loopable<E>>
    {
        Iterator<Loop<E>> loopIt  = null;
        Iterator<Loopable<E>> loopableIt = null;

        // next values to return
        Loopable<E> loopable;
        Loop<E> loop;

        public LoopableLIterator()
        {
            loopIt = LoopL.this.iterator();
            findNext();
        }

        private void findNext()
        {
            if (loopIt == null)
            {
                return; // finished!
            }
            else if (loopableIt != null && // start
                    loopableIt.hasNext())
            {
                loopable = loopableIt.next();
            }
            else if (loopIt.hasNext())
            {
                loopableIt = (loop = loopIt.next()).loopableIterator().iterator();
                findNext();
            }
            else
            {
                loopIt = null;
            }
        }

        public boolean hasNext()
        {
            return loopIt != null;
        }

        public Loopable<E> next()
        {
            Loopable<E> out = loopable;
            findNext();
            return out;
        }

        public void remove()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    public abstract class Map<O>
    {
        public Map(){}
        
        public LoopL<O> run()
        {
            LoopL<O> out = new LoopL();

            for (Loop<E> loopE : LoopL.this)
            {
                Loop<O> loopO = new Loop();
                out.add( loopO );
                for (Loopable<E> e : loopE.loopableIterator())
                    loopO.append( map( e ) );
            }

            return out;
        }
        // convert teh
        public abstract O map (Loopable<E> input);
    }

    public static class LoopLoopable<E>
    {
        public Loop<E> loop;
        public Loopable<E> loopable;
        public LoopLoopable (Loop loop, Loopable loopable) {
            this.loop = loop;
            this .loopable = loopable;
        }
    }

    public LoopLoopable<E> find (E e) {
        for (Loop loop : this)
        {
            Loopable<E> lp = loop.find (e);
            if (lp != null)
                return new LoopLoopable<E>(loop, lp);
        }
        return null;
    }
    
    public Stream<E> streamE() {
    	return stream().flatMap( l -> l.stream() );
    }
    
	public Loop<E> loop() {
		Loop<E> out = new Loop<>();
		add( out );
		return out;
	}
}

