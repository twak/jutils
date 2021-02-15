package org.twak.utils.collections;

import java.util.ArrayList;

/**
 * Seriously, what is this? re-does default map behaviour? +1 delete it!
 * @author twak
 */
public class HashableList<E> extends ArrayList<E>
{
    @Override
    public int hashCode()
    {
        int out = 0;
        for (E e : this)
            out += e.hashCode();

        return out;
    }

    @Override
    public boolean add(E e)
    {
        boolean out = super.add(e);
        return out;
    }

    @Override
    public boolean remove(Object o)
    {
        boolean out = super.remove ((E)o);
        return out;
    }

    @Override
    public boolean equals(Object o)
    {
        HashableList<E> other = (HashableList<E>)o;
        
        if (other.size() != size())
            return false;

        for (int i = 0; i < size(); i++)
        {
            if (!get(i).equals( other.get(i)) )
                return false;
        }


        return true;
    }
}
