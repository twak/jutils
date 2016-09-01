
package org.twak.utils;

import java.util.Iterator;

/**
 *
 * @author twak
 */
public abstract class ItConverter < A, B> implements Iterable<B>
{
    Iterable<A> aIt;
    
    public ItConverter(Iterable<A> aIt)
    {
        this.aIt = aIt;        
    }

    public abstract B convert (A a);

    @Override
    public Iterator<B> iterator()
    {
        return new ItConvIt( aIt.iterator() );
    }

    public class ItConvIt implements Iterator<B>
    {
        Iterator<A> it;
        
        public ItConvIt (Iterator<A> it)
        {
            this.it = it;
        }

        @Override
        public boolean hasNext()
        {
            return it.hasNext();
        }

        @Override
        public B next()
        {
            return convert( it.next() );
        }

        @Override
        public void remove()
        {
            it.remove();
        }
    }

}
