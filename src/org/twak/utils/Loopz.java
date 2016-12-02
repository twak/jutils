package org.twak.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.twak.utils.Intersector.Collision;
import org.twak.utils.triangulate.EarCutTriangulator;


public class Loopz {

	
	public static LoopL<Point2d> insideOutside(LoopL<Point2d> a, LoopL<Point2d> b) {

		LoopL<Point2d> out = a;
		
		Set<Line> lines = new HashSet();
		
		for (Loopable<Point2d> ctx : a.getLoopableIterable() )
			lines.add( new Line( ctx.get(), ctx.getNext().get()) );
		
		for (Loopable<Point2d> ctx : b.getLoopableIterable() )
			lines.add( new Line( ctx.get(), ctx.getNext().get()) );
		
		
		Intersector is = new Intersector();

		List<Collision> cols = is.intersectLines(lines);

		
		MultiMap<Line, Point2d> cutLineAt = new MultiMap<>();
		
		Set<Point2d> debug = new HashSet<>();
		
		for (Collision c : cols)
			for (Line l : c.lines)
				if (!l.start.equals(c.location) && !l.end.equals(c.location)) {
					cutLineAt.put(l, c.location);
					debug.add(c.location );
				}
		
//		System.out.println("found " + debug.size() + " collisions from " + a.count() +" + " + b.count() +" inputs ");
		
		for (Line l : cutLineAt.keySet()) {
			final Line ll = l;
			lines.remove(l);
			
			List<Point2d> cutPoints = cutLineAt.get(l);
			
			cutPoints.sort(new Comparator<Point2d>() {
				@Override
				public int compare(Point2d o1, Point2d o2) {
					return Double.compare ( ll.findFrac(o1), ll.findFrac(o2) );
				}
			} );
			
			cutPoints.add(0, l.start);
			cutPoints.add(l.end);
			
			for (Pair<Point2d, Point2d> cut : new ConsecutivePairs<>(cutPoints, false)) 
				lines.add(new Line (cut.first(), cut.second()));
		}
		
		UnionWalker uw = new UnionWalker();
		lines.stream().forEach ( x -> uw.addEdge(x.start, x.end) );

		out = uw.findAll();
		
//		new Plot( out );
		
		return out;
	}
	
	public static double area(Loop<Point2d> loop) {
		
		Point2d origin = loop.iterator().next();
		
		double area = 0;
		
		for (Loopable<Point2d> pt : loop.loopableIterator()) 
			area += MUtils.area(origin, pt.getNext().get(), pt.get());
		
		return area;
	}

	public static double area( LoopL<Point2d> insideOutside ) {
		return insideOutside.stream().mapToDouble( x -> area (x) ).sum();
	}

	public static void writeXZObj( LoopL<Point2d> lloops, File file, boolean filterHoles ) {

		ObjDump obj = new ObjDump();
		
		for ( Loop<Point2d> l : lloops ) {

			if ( (!filterHoles) || area( l ) < 0 ) {

				List<Point3d> ptz = new ArrayList();
				for ( Point2d p : l )
					ptz.add( new Point3d( p.x, 0, p.y ) );

				obj.addFace( ptz );
			}
		}
		
		obj.allDone( file );
	}

	public static Graph2D toGraph( LoopL<Point2d> edges ) {
		
		Graph2D g = new Graph2D();
		
		for (Loop<Point2d> ot : edges)
			for (Loopable<Point2d> ll : ot.loopableIterator())
				g.add( ll.get(), ll.getNext().get() );
		
		return g;
	}

//	public static LoopL<Point2d> toXZLoop( List<Polygon> polies ) {
//		
//		LoopL<Point2d> out = new LoopL<>();
//		
//		for (Polygon p : polies) {
//			Loop<Point2d>l = new Loop<>();
//			out.add(l);
//			for (Line3d pt : p.lines )
//				l.append( new Point2d(pt.start.x, pt.start.z) );
//		}
//			
//		
//		return out;
//	}
	
	
	public static void triangulate (Loop<Point3d> loop, boolean reverseTriangles, 
			List<Integer> indsO, List<Float> posO, List<Float> normsO) {
		
		List<Float> pos = new ArrayList();
		List<Integer> inds = new ArrayList();
		
		Vector3d normal = new Vector3d();
		
		int[] order = reverseTriangles ? new int[] {2,1,0} : new int[] {0,1,2};
		
		for ( Loopable<Point3d> pt : loop.loopableIterator() ) {

			inds.add( inds.size() );

			Point3d p = pt.get();
			
			pos.add( (float) p.x );
			pos.add( (float) p.y );
			pos.add( (float) p.z );
			
			Vector3d l = new Vector3d( pt.get() );
			l.sub(pt.getPrev().get());

			Vector3d n = new Vector3d(pt.getNext().get());
			n.sub(pt.get());
			if (l.lengthSquared() > 0 && n.lengthSquared() > 0) {
				l.normalize();
				n.normalize();

				l.cross(l, n);
				normal.add(l);
			}
		}
		
		float[] n = new float[] { (float) normal.x, (float) normal.y, (float) normal.z };
		float[] p = Arrayz.toFloatArray( pos );
		int[] i = Arrayz.toIntArray( inds ), ti = new int[i.length * 3];

		int tris = EarCutTriangulator.triangulateConcavePolygon(p,
                0,
                inds.size(),
                i,
                ti,
                n
                 );
		
		if (tris > 0) {
//			ti = Arrays.copyOf( ti, tris * 3 );
			
			int offset = posO.size() / 3;
			
			for (int j = 0; j < tris * 3; j+=3) {
				indsO.add(ti[j+order[0]] + offset);
				indsO.add(ti[j+order[1]] + offset);
				indsO.add(ti[j+order[2]] + offset);
			}
			
			for (int j = 0; j < pos.size(); j+=3) {
				posO.add(pos.get(j+0));
				posO.add(pos.get(j+1));
				posO.add(pos.get(j+2));
				
				normsO.add(n[0]);
				normsO.add(n[1]);
				normsO.add(n[2]);
			}
		}
		
	}
	
	public static LoopL<Point2d> removeInnerEdges(LoopL<Point2d> edges) {
		Graph2D g2 = Loopz.toGraph(edges);
		g2.removeInnerEdges();
		
		UnionWalker uw = new UnionWalker();
		
		for (Point2d a : g2.map.keySet()) {
			for (Line l : g2.get(a)) {
				uw.addEdge(l.start, l.end);
			}
		}
		
		edges = uw.findAll();
		return edges;
	}

	public static double[] minMaxXZ(Loop<Point3d> in) {
		
		double[] out = new double[] {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE };
		
		for (Point3d p : in) {
			out[0]=Math.min(out[0], p.x);
			out[1]=Math.max(out[1], p.x);
			out[2]=Math.min(out[2], p.z);
			out[3]=Math.max(out[3], p.z);
		}
		
		return out;
	}

	public static double[] minMaxXY(LoopL<Point3d> in) {
		
		double[] out = new double[] {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE };
		
		for (Loop<Point3d> lp : in)
		for (Point3d p : lp) {
			out[0]=Math.min(out[0], p.x);
			out[1]=Math.max(out[1], p.x);
			out[2]=Math.min(out[2], p.y);
			out[3]=Math.max(out[3], p.y);
		}
		
		return out;
	}


	public static double[] minMax(LoopL<Point3d> in) {

		double[] out = new double[] { 
				Double.MAX_VALUE, -Double.MAX_VALUE, 
				Double.MAX_VALUE, -Double.MAX_VALUE,
				Double.MAX_VALUE, -Double.MAX_VALUE };

		for (Loop<Point3d> lp : in)
			for (Point3d p : lp) {
				out[0] = Math.min(out[0], p.x);
				out[1] = Math.max(out[1], p.x);
				out[2] = Math.min(out[2], p.y);
				out[3] = Math.max(out[3], p.y);
				out[4] = Math.min(out[4], p.z);
				out[5] = Math.max(out[5], p.z);
			}

		return out;
	}
	
	public static void expand(double[] minMax, double i) {
		
		for (int j = 0; j < minMax.length; j+=2) {
			minMax[j+0]-=i;
			minMax[j+1]+=i;
		}
	}

	public static LoopL<Point2d> toXZLoop(LoopL<Point3d> list) {
		
		LoopL<Point2d> out = new LoopL<>();

		for (Loop<Point3d> ll : list)
			out.add( toXZLoop( ll) );
		
		
		return out;
	}

	private static Loop<Point2d>  toXZLoop( Loop<Point3d> ll) {
		Loop<Point2d> o = new Loop<>();
		
		for (Point3d p : ll) 
			o.append(new Point2d(p.x, p.z));
		
		for (Loop<Point3d> hole : ll.holes)
			o.holes.add(toXZLoop(hole));
		
		return o;
	}

	public static LoopL<Point2d> to2dLoop(LoopL<Point3d> in, int axis, Map<Point2d, Point3d> to3d) {
		LoopL<Point2d> out = new LoopL<>();
		
		for (Loop<Point3d> ll : in) {
			Loop<Point2d> ol = new Loop<>();
			out.add(ol);

			for (Point3d p3 : ll) {
				Point2d p2 = axis == 0? new Point2d( p3.y, p3.z ) : axis == 1 ? new Point2d (p3.x, p3.z) : new Point2d (p3.y, p3.z);
				to3d.put (p2, p3);
				ol.append(p2);
			}
		}
		
		return out;
	}

	public static LoopL<Point2d> removeNegativeArea( LoopL<Point2d> gis, double sign ) {
		
		Iterator<Loop<Point2d>> eit = gis.iterator();
		while (eit.hasNext()) { 
			double area = area (eit.next());
			if ( sign * area > 0)
				eit.remove();
				
		}

		return gis;
	}

	public static LoopL<Point2d> mergeAdjacentEdges( LoopL<Point2d> in, double areaTol, double angleTol ) {
		
		LoopL<Point2d> out = new LoopL<> (in);
		
		Iterator<Loop<Point2d>> eit = out.iterator();
		
		while (eit.hasNext()) {
			Loop<Point2d> loop = eit.next();
			Loopable<Point2d> start = loop.start, current = start;
			int size = loop.count();
			
			boolean again;
			do {
				again = false;
				Point2d a = current.getPrev().get(),
						b = current.get(),
						c = current.getNext().get();
				
				Line ab = new Line(a,b),
				     bc = new Line (b,c);
				
				double angle = Anglez.dist( ab.aTan2(), bc.aTan2() );
				
				if ( 
						a.distanceSquared(b) < 0.0001 ||
						b.distanceSquared(c) < 0.0001 ||
						angle < angleTol && Math.abs ( MUtils.area(a, b, c) ) < 50 * areaTol * areaTol  ) 
				{
					current.getPrev().setNext(current.getNext());
					current.getNext().setPrev(current.getPrev());
					size--;
					if (start == current)
						loop.start = start = current.getPrev();
					
					again = true;
					current = current.getPrev();
				}
				else
					current = current.getNext();
			}
			while ( ( again || current != start) && size > 2);
			
			if (size <= 2)
				eit.remove();
		}
		
		return out;
	}

	
	


}
