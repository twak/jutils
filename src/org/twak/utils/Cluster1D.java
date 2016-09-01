package org.twak.utils;

//import edu.wlu.cs.levy.CG.KDTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author twak
 */
public abstract class Cluster1D <D>
{
//    KDTree kd = new KDTree( 1 );
//    Map<Double, List<D>> vals = new HashMap();
//
//    public Cluster1D(Iterable<D> stuff)
//    {
//        for (D d : stuff)
//        {
//            try
//            {
//                double val = getVal( d );
//
//                List<D> list = (List) kd.search( new double[] {val});
//                if (list == null)
//                {
//                    list = new ArrayList();
//                    kd.insert( new double[]
//                        {
//                            val
//                        }, list );
//                }
//                list.add( d );
//
//                vals.put( val, list );
//            }
//            catch ( Throwable ex )
//            {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    public Set<D> getStuffBetween( double min, double max )
//    {
//        Set<D> out = new HashSet();
//        try
//        {
//
//            Object[] res = kd.range( new double[]
//                    {
//                        min
//                    }, new double[]
//                    {
//                        max
//                    } );
//            for ( Object o : res )
//            {
//                List<D> list = (List)o;
////                double[] val = (double[]) o;
//                out.addAll( list );
//            }
//        }
//        catch ( Throwable ex )
//        {
//            ex.printStackTrace();
//        }
//        return out;
//    }
//
//    public abstract double getVal (D d);
//
//    public Set<D> getNear( double val, double delta )
//    {
//        return getStuffBetween( val - delta, val+delta);
//    }
}
