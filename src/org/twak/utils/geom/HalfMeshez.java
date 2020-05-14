package org.twak.utils.geom;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.vecmath.Point2d;

import org.twak.utils.Line;
import org.twak.utils.Mathz;
import org.twak.utils.collections.MultiMap;
import org.twak.utils.geom.HalfMesh2.HalfEdge;
import org.twak.utils.geom.HalfMesh2.HalfFace;

public class HalfMeshez {


    public static void splitMergeCoincident ( HalfMesh2 hm, double tol ) {

        Set<HalfEdge> toProcess = new LinkedHashSet<>( hm.faces.stream().flatMap( f -> f.edgeList().stream() ).collect( Collectors.toList() ) ),
                toProcess1 = new LinkedHashSet<>( toProcess );

        int count = 0;

        while ( !toProcess1.isEmpty() ) {

            HalfEdge e1 = toProcess1.iterator().next();
            toProcess1.remove( e1 );

            if ( e1.over != null )
                continue;

            Set<HalfEdge> toProcess2 = new LinkedHashSet<>( toProcess );

            while ( !toProcess2.isEmpty() ) {

                HalfEdge e2 = toProcess2.iterator().next();
                toProcess2.remove( e2 );

//				if ( e1 == e2 || e2.over != null || e1.start.equals( e1.end ) || e2.start.equals( e2.end ) )
//					continue;

                Line l1 = e1.line(), l2 = e2.line();

                if ( l1.distance( l2 ) <= tol && Math.abs( l2.absAngle( l1 ) ) > Math.PI - 0.1 ) {

                    double  maxMin = l1.findPPram( l2.end   ),
                            minMax = l1.findPPram( l2.start );

                    if (Mathz.inRange( maxMin, 0.001, 0.999 ) )
                        e1.split( l1.fromPPram( maxMin ) );
                    else if ( Mathz.inRange( minMax,  0.001, 0.999 ) )
                        e1.split( l1.fromPPram( minMax ) );
                    else
                        continue;

                    for ( HalfEdge processAgain : new HalfEdge[] { e1, e1.next, e2 } ) {
                        toProcess.add( processAgain );
                        toProcess1.add( processAgain );
                    }

//					System.out.println( toProcess.size() );

                    count++;
                    if (count > 1000)
                        return;
                    break;
                }
            }
        }

        // fast lookup gets most...
        MultiMap<Point2d, HalfEdge> starts = new MultiMap<>();
        for ( HalfFace hf : hm )
            for ( HalfEdge he : hf )
                if ( he.over == null )
                    starts.put( new Point2d( he.start ), he );

        for ( HalfFace hf : hm )
            for ( HalfEdge he : hf )
                if ( he.over == null )
                    for ( HalfEdge o : starts.get( he.end ) )
                        if ( he.start.equals( o.end ) )
                            he.over = o;

        // slow for the remainder...?!
        for ( HalfFace hf : hm )
            for ( HalfEdge he : hf )
                if ( he.over == null )
                    for ( HalfFace hf2 : hm )
                        for ( HalfEdge he2 : hf2 )
                            if ( he2 != he && he2.over == null ) {
                                if (he.start.distance( he2.end ) < 0.01 &&
                                        he.end.distance( he2.start ) < 0.01 )
                                {
                                    he.over = he2;
                                    he2.over = he;
                                }
                            }

    }

	public static void findEdgeOvers( HalfMesh2 out ) {

        MultiMap<Point2d, HalfMesh2.HalfEdge> overs = new MultiMap<>(  );
        out.faces.stream().flatMap( x -> x.edgeList().stream() ).forEach( e -> overs.put(e.start, e) );

        for ( HalfMesh2.HalfFace ff : out)
            for ( HalfMesh2.HalfEdge e : ff )
            {
                for (HalfMesh2.HalfEdge over : overs.get(e.end) )
                    if (over.end .equals ( e.start ) && over.start.equals ( e.end ) ) {
                        over.over = e;
                        e.over = over;
                    }
            }
	}
}
