package org.twak.utils.collections;

import java.util.HashMap;
import java.util.Map;

import org.twak.utils.Pair;

/**
 * @author twak
 */
public class CountOrderInd<T>
{

    private Map<OrderInd, Integer> things = new HashMap<OrderInd, Integer>();

    private class OrderInd
    {

        T a, b;

        public OrderInd(T a, T b)
        {
            if (a.hashCode() < b.hashCode())
            {
                this.a = a;
                this.b = b;
            }
            else
            {
                this.a = b;
                this.b = a;
            }
        }

        @Override
        public int hashCode()
        {
            int hash = 5;
            hash = 83 * hash + (this.a != null ? this.a.hashCode() : 0);
            hash = 83 * hash + (this.b != null ? this.b.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final OrderInd other = (OrderInd) obj;
            if (this.a != other.a && (this.a == null || !this.a.equals(other.a)))
                return false;
            if (this.b != other.b && (this.b == null || !this.b.equals(other.b)))
                return false;
            return true;
        }
    }

    public int add(T a, T b)
    {
        OrderInd ab = new OrderInd(a, b);
        Integer i = things.get(ab);
        if (i == null)
            i = 0;
        i++;

        if (i == 0)
            things.remove(ab);
        else
            things.put(ab, i);

        return i;
    }

    public int remove(T a, T b)
    {
        OrderInd ab = new OrderInd(a, b);
        Integer i = things.get(ab);
        if (i == null)
            i = 0;
        i--;

        if (i == 0)
            things.remove(ab);
        else
            things.put(ab, i);

        return i;
    }

    public int get(T a, T b)
    {
        OrderInd ab = new OrderInd(a, b);
        Integer i = things.get(ab);
        if (i == null)
            i = 0;
        return i;
    }

    public boolean allZero()
    {
        return things.isEmpty();
    }

    public Pair<T,T> getOne()
    {
        OrderInd out = things.keySet().iterator().next();
        return new Pair<T, T>(out.a, out.b);
    }
}
