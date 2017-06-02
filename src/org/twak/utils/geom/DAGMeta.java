package org.twak.utils.geom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.twak.utils.collections.Loop;
import org.twak.utils.collections.LoopL;

/**
 * Graph of E, with meta data per arc D
 *
 * @author twak
 */
public class DAGMeta<E, D>
{

    public Map<E, List<ArcInfo<E, D>>> map = new LinkedHashMap();

    public static class ArcInfo<E, D>
    {

        public E e;
        public D d;

        public ArcInfo( E e, D d )
        {
            this.e = e;
            this.d = d;
        }
    }

    public void add( E a, E b, D d )
    {
        addEntry( a, b, d );
//        addEntry( b, a, d );
    }

    private void addEntry( E a, E b, D d )
    {
        List<ArcInfo<E, D>> res = map.get( a );
        if ( res == null )
        {
            res = new ArrayList();
            map.put( a, res );
        }

        res.add( new ArcInfo<E, D>( b, d ) );
    }

    public List<ArcInfo<E, D>> get( E a )
    {
        return map.get( a );
    }

    public void clear()
    {
        map.clear();
    }

    /**
     * Problem specific routine - removes every entry a,b if map also contains b,a
     */
    public void removeReturning()
    {
        Map<E, ArcInfo<E, D>> toRemove = new HashMap();

//        Iterator<E> sit = map.keySet().iterator();
//
//        while (sit.hasNext()){
//            E s = sit.next();
//            Iterator<ArcInfo<E,D>> eit = map.get(s).iterator();
//            while (eit.hasNext())
//            {
//                ArcInfo<E,D> aED = eit.next();
//                if (aED.e == s)
//                {
//
//                }
//            }
//        }


        for ( E s : map.keySet() )
            for ( ArcInfo<E, D> aiE : map.get( s ) )
                for ( ArcInfo<E, D> aiS : map.get( aiE.e ) )
                    if ( aiS.e == s )
                    {
                        toRemove.put( s, aiE );
                        toRemove.put( aiE.e, aiS );
                    }


        for ( E e : toRemove.keySet() )
            map.get( e ).remove( toRemove.get( e ) );


        Iterator<Map.Entry<E, List<ArcInfo<E, D>>>> it = map.entrySet().iterator();
        while ( it.hasNext() )
        {
            Map.Entry<E, List<ArcInfo<E, D>>> entry = it.next();
            if ( entry.getValue().isEmpty() )
                it.remove();
        }
    }

    public LoopL<E> debug()
    {
        LoopL<E> out = new LoopL();

        Iterator<Map.Entry<E, List<ArcInfo<E, D>>>> it = map.entrySet().iterator();
        while ( it.hasNext() )
        {

            Map.Entry<E, List<ArcInfo<E, D>>> entry = it.next();
            for ( ArcInfo<E, D> res : entry.getValue() )
            {
                Loop<E> loop = new Loop();

                out.add( loop );

                loop.append( entry.getKey() );
                loop.append( res.e );
            }


        }

        return out;
    }
}
