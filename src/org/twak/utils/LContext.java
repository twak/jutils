package org.twak.utils;

import java.util.Iterator;

/**
 * Loopl context - an item and it's location in a lopp of loops
 * @author twak
 * @param <E> the type of item
 */
public class LContext<E>
{

    public Loopable<E> loopable;
    public Loop<E> loop;
    public Object hook; // attachement for misc extensions

    public LContext( Loopable<E> loopable, Loop<E> loop )
    {
        this.loopable = loopable;
        this.loop = loop;
    }

    public E get()
    {
        return loopable.get();
    }

    /**
     * Assumes target loop has same topology as us, and finds the corresponding LContext
     * for target
     * @param target
     * @return
     */
    public LContext tranlsate(LoopL<E> us, LoopL target)
    {
        Iterator<Loop<E>> usit = us.iterator();
        Iterator<Loop> tait = target.iterator();

        while (usit.hasNext())
        {
            assert tait.hasNext();
            Loop foundLoop = tait.next();
            if (usit.next() == loop)
            {
                Iterator<Loopable<E>> eIt = loop.loopableIterator().iterator();
                Iterator<Loopable> tIt = foundLoop.loopableIterator().iterator();

                while (eIt.hasNext())
                {
                    assert (tIt.hasNext());
                    Loopable founfLoopable = tIt.next();
                    if (eIt.next() == loopable)
                    {
                        return new LContext(founfLoopable, foundLoop);
                    }
                }
                return null;
            }
        }
        
        return null;
    }
}
