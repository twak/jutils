package org.twak.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author twak
 */
public class Listz {
    public static List union (List a, List b)
    {
        List out = new ArrayList();
        for (Object oa : a)
            if (b.contains (oa))
                out.add(oa);
        return out;
    }
    
    public static List<Integer> seq (int min, int max)
    {
        List<Integer> out = new ArrayList();
        for (int i = min; i <= max; i++)
            out.add(i);
        return out;
    }
    
    public static void trimRandomTo (List in, int n, Random randy)
    {
        while (in.size() > n)
            in.remove( randy.nextInt( in.size()) );
    }
}
