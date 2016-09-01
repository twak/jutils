
package org.twak.utils;

import java.util.Comparator;

/**
 *
 * @author twak
 */
public class HashCodeComparator implements Comparator<Object>{

    @Override
    public int compare( Object o1, Object o2 )
    {
         int a1 = o1.hashCode(), a2 = o2.hashCode();
         if (a1 > a2) return 1;
         if (a1 < a2) return -1;
         return 0;
    }

}
