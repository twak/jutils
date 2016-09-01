package org.twak.utils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author twak
 */
public class Setz {
    public static Set union (Set a, Set b)
    {
        Set out = new HashSet();
        for (Object o : a)
            if (b.contains(o))
                out.add (o);
        return out;
    }
    
    public static void unionInA (Set a, Set b)
    {
        for (Object ao : a)
            if (!b.contains(ao))
                a.remove (ao);
    }
    
    public static double mean (Iterable<? extends Number> set)
    {
        double score = 0;
        int count = 0;
        for (Number n : set)
        {
            score += n.doubleValue();
            count++;
        }
        
        return score/count;
    }

    public static boolean containsAny( Set seen, Set near )
    {
        if ( seen.size() > near.size() )
        {
            for ( Object o : near )
                if ( seen.contains( o ) )
                    return true;
        }
        else
        {
            for ( Object o : seen )
                if ( near.contains( o ) )
                    return true;
        }
        return false;
    }
}
