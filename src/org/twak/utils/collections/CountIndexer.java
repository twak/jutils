package org.twak.utils.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * We wish to index stuff (O) based on the number of certain types of things (E).
 *
 * @author twak
 */
public class CountIndexer <E, O>
{
    List<E> current = new ArrayList();

    MultiMap<List<E>, O> index = new MultiMap<List<E>, O>();

    public void add(E e)
    {
        current.add(e);
    }

    public void register (O o)
    {
        registerDontReset( o );
        reset();
    }

    public void registerDontReset (O o)
    {
        Collections.sort( current, new HashCodeComparator());
        index.put (current, o);
    }

    public void reset() {
        current = new ArrayList();
    }

    public List<O> find ()
    {
        Collections.sort( current, new HashCodeComparator());

        List<E> tmp = current;
        current = new ArrayList();

        return index.get( tmp );
    }

}
