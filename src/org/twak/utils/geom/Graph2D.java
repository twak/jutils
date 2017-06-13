package org.twak.utils.geom;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.twak.utils.Line;
import org.twak.utils.collections.MultiMap;

public class Graph2D extends MultiMap<Point2d, Line> {

	public Graph2D( Collection<Line> sliceTri ) {
		sliceTri.stream().forEach( i -> add( i ) );
	}

	public Graph2D() {
	}

	public Graph2D apply( AffineTransform at ) {

		Graph2D out = new Graph2D();

		Map<Point2d, Point2d> seenPts = new HashMap<>();

		for ( List<Line> lines : map.values() )
			for ( Line l : lines ) {

				Point2d start = seenPts.get( l.start );
				if ( start == null )
					seenPts.put( l.start, start = transform( l.start, at ) );

				Point2d end = seenPts.get( l.end );
				if ( end == null )
					seenPts.put( l.end, end = transform( l.end, at ) );

				Line l2 = new Line( start, end );
				out.put( start, l2 );
				out.put( end, l2 );
			}

		return out;
	}

	public void removeInnerEdges() {

		Set<Line> togo = new HashSet<>();

		for ( Point2d pt : keySet() )
			for ( Line l1 : get( pt ) )
				for ( Line l2 : get( pt ) )
					if ( l1.start.equals( l2.end ) && l2.start.equals( l1.end ) ) {
						togo.add( l1 );
						togo.add( l2 );
					}

		for ( Line l : togo ) {
			remove( l.start, l );
			remove( l.end, l );
		}
	}

	private static Point2d transform( Point2d a, AffineTransform at ) {

		double[] coords = new double[] { a.x, a.y };
		at.transform( coords, 0, coords, 0, 1 );

		return new Point2d( coords[ 0 ], coords[ 1 ] );
	}

	public Set<Line> allLines() {
		Set<Line> seenLines = new HashSet<>();

		for ( List<Line> l : map.values() )
			seenLines.addAll( l );

		return seenLines;
	}

	public void add( Line l ) {
		put( l.start, l );
		put( l.end, l );
	}

	public void add( Point2d a, Point2d b ) {
		Line l = new Line( a, b );
		put( l.start, l );
		put( l.end, l );
	}

	public void addAll( Iterable<Line> portal ) {
		for ( Line l : portal )
			add( l );
	}

	public void remove( Line line ) {

		remove( line.start, line );
		remove( line.end, line );
	}

	public void removeAll( Iterable<Line> togo ) {
		for ( Line l : togo )
			remove( l );
	}

	public void mergeContiguous( double tolRads ) {

		Set<Point2d> togo = new HashSet<Point2d>( keySet() );

		while ( !togo.isEmpty() ) {

			Point2d pt = togo.iterator().next();
			togo.remove( pt );
			List<Line> lines = new ArrayList<Line> ( get( pt ) );

			for (int ai = 0; ai < lines.size(); ai++)
				for (int bi = 0; bi < ai; bi++) {
					
					Line a = lines.get(ai), b = lines.get(bi);
					
					if ( a.absAngle( b ) < tolRads ) {


						Line l;
						if ( a.end.equals( b.start ) ) {
							l = new Line( a.start, b.end );
							togo.add(a.end);
						}
						else if ( b.end.equals( a.start ) ) {
							l = new Line( b.start, a.end );
							togo.add(b.end);
						}
						else
							continue;

						remove( a );
						remove( b );
						
						add(l);
						
						togo.add( l.start );
						togo.add( l.end );
					}
				}
		}
	}
}
