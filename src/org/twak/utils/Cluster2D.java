package org.twak.utils;

//import edu.wlu.cs.levy.CG.KDTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.vecmath.Point2d;

/**
 *
 * @author twak
 */
public abstract class Cluster2D <D>
{
//    KDTree kd = new KDTree( 2 );
//    public Map<Pair<Double,Double>, List<D>> vals = new HashMap();
//
//    public Cluster2D(){}
//    public Cluster2D(Iterable<D> stuff)
//    {
//        add (stuff);
//    }
//
//    public void add (Iterable<D> stuff)
//    {
//        for (D d : stuff)
//        {
//            try
//            {
//                Pair<Double, Double> val = getVal( d );
//
//                List<D> list = (List) kd.search( new double[] {val.first(), val.second()});
//                if (list == null)
//                {
//                    list = new ArrayList();
//                    kd.insert( new double[]
//                        {
//                            val.first(),
//                            val.second()
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
//    public Set<D> getStuffBetween( double minX, double maxX, double minY, double maxY )
//    {
//        Set<D> out = new HashSet();
//        try
//        {
//
//            Object[] res = kd.range( new double[]
//                    {
//                        minX, minY
//                    }, new double[]
//                    {
//                        maxX, maxY
//                    } );
//            for ( Object o : res )
//            {
//                List<D> list = (List)o;
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
//    public abstract Pair<Double, Double> getVal (D d);
//
//    public Set<D> getNear( double x, double y, double delta )
//    {
//        return getStuffBetween( x - delta, x + delta, y - delta, y + delta);
//    }
}