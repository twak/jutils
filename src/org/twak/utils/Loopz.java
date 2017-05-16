package org.twak.utils;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.twak.utils.HalfMesh2.HalfEdge;
import org.twak.utils.HalfMesh2.HalfFace;
import org.twak.utils.Intersector.Collision;
import org.twak.utils.results.OOB;
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
		
//		Set<Point2d> debug = new HashSet<>();
		
		for (Collision c : cols)
			for (Line l : c.lines)
				if (!l.start.equals(c.location) && !l.end.equals(c.location)) {
					cutLineAt.put(l, c.location);
//					debug.add(c.location );
				}
		
//		System.out.println("found " + debug.size() + " collisions from " + a.count() +" + " + b.count() +" inputs ");
		
		for (Line l : cutLineAt.keySet()) {
			final Line ll = l;
			lines.remove(l);
			
			List<Point2d> cutPoints = cutLineAt.get(l);
			
			cutPoints.sort(new Comparator<Point2d>() {
				@Override
				public int compare(Point2d o1, Point2d o2) {
					return Double.compare ( ll.findPPram(o1), ll.findPPram(o2) );
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
	
	public static double area3(Loop<Point3d> loop) {
		
		Point3d origin = loop.iterator().next();
		
		double area = 0;
		
		for (Loopable<Point3d> pt : loop.loopableIterator()) 
			area += MUtils.area(origin, pt.getNext().get(), pt.get());
		
		return area;
	}

	public static double area3( LoopL<Point3d> insideOutside ) {
		return insideOutside.stream().mapToDouble( x -> area3 (x) ).sum();
	}
	
	public static void writeXZObj( LoopL<Point2d> lloops, File file, boolean filterHoles ) {

		ObjDump obj = new ObjDump();
		
		for ( Loop<Point2d> l : lloops ) {

			if ( !filterHoles || area( l ) < 0 ) {

				List<Point3d> ptz = new ArrayList();
				for ( Point2d p : l )
					ptz.add( new Point3d( p.x, 0, p.y ) );

				obj.addFace( ptz );
			}
		}
		
		obj.dump( file );
	}

	public static Graph2D toGraph( LoopL<Point2d> edges ) {
		
		Graph2D g = new Graph2D();
		
		for (Loop<Point2d> ot : edges)
			for (Loopable<Point2d> ll : ot.loopableIterator())
				g.add( ll.get(), ll.getNext().get() );
		
		return g;
	}
	
	public static void triangulate (Loop<? extends Point3d> loop, boolean reverseTriangles, 
			List<Integer> indsO, List<Float> posO, List<Float> normsO) {
		
		List<Float> pos = new ArrayList();
		List<Integer> inds = new ArrayList();
		
		Vector3d normal = new Vector3d();
		
		int[] order = reverseTriangles ? new int[] {2,1,0} : new int[] {0,1,2};
		
		for ( Loopable<? extends Point3d> pt : loop.loopableIterator() ) {

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
	
	/**
	 * All faces in positions 1+ are taken to be correctly oriented holes. This
	 * inserts edges to let a triangulator run.
	 */
	public static LoopL<Point3d> insertInnerEdges(LoopL<Point3d> ll) {
		
		while (ll.size() > 1) {
			LoopL<Point3d> out = new LoopL<>();
			
			Loop <Point3d>
				hole = ll.get(1), 
				peri = ll.get(0);
			
			out.add( peri );
			for (int i = 2; i < ll.size(); i++) 
				out.add(ll.get(i));

			Loopable <Point3d> hi = null, pi = null;
			double bestDist = Double.MAX_VALUE;
			for ( Loopable<Point3d> h : hole.loopableIterator() )
				for (Loopable<Point3d> p : peri.loopableIterator()) 
				{
					double dist = h.get().distanceSquared( p.get() );
					if (dist < bestDist) {
						hi = h;
						pi = p;
						bestDist = dist;
					}
				}
			
			Loopable<Point3d> 
				h2 = new Loopable<Point3d>( new Point3d ( hi.get() ) ),
				p2 = new Loopable<Point3d>( new Point3d ( pi.get() ) );
					
			
			h2.prev = hi.prev;
			h2.next = p2;
			p2.next = pi.next;
			p2.prev = h2; 
			pi.next.prev = p2;
			hi.prev.next = h2;
			
			
			hi.prev = pi;
			pi.next = hi;
			
			ll = out;
		}
		
		return ll;
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
	

	public static double[] minMax2d( Loop<Point2d> in ) {
		
		double[] out = new double[] { 
				Double.MAX_VALUE, -Double.MAX_VALUE, 
				Double.MAX_VALUE, -Double.MAX_VALUE
			};

			for (Point2d p : in) {
				out[0] = Math.min(out[0], p.x);
				out[1] = Math.max(out[1], p.x);
				out[2] = Math.min(out[2], p.y);
				out[3] = Math.max(out[3], p.y);
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

	public static Loop<Point2d>  toXZLoop( Loop<Point3d> ll) {
		
		Loop<Point2d> o = ll instanceof SuperLoop ? new SuperLoop(((SuperLoop)ll).properties) :new Loop<>();
		
		for (Point3d p : ll) 
			o.append(new Point2d(p.x, p.z));
		
		for (Loop<Point3d> hole : ll.holes)
			o.holes.add(toXZLoop(hole));
		
		return o;
	}

	public static LoopL<Point2d> to2dLoop(LoopL<Point3d> in, int axis, Map<Point2d, Point3d> to3d) {
		LoopL<Point2d> out = new LoopL<>();
		
		for (Loop<Point3d> ll : in) {
			Loop<Point2d> ol = to2dLoop (ll, axis, to3d); 
			out.add(ol);
		}
		
		return out;
	}
	
	public static Loop<Point2d> to2dLoop(Loop<Point3d> in, int axis, Map<Point2d, Point3d> to3d) {

		Loop<Point2d> out = new Loop<>();
		for (Point3d p3 : in) {
			Point2d p2 = axis == 0? new Point2d( p3.y, p3.z ) : axis == 1 ? new Point2d (p3.x, p3.z) : new Point2d (p3.y, p3.z);
			if (to3d != null)
				to3d.put (p2, p3);
			out.append(p2);
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

	public static LoopL<Point3d> to3d( LoopL<Point2d> gis, double h, int i ) {
		
		LoopL<Point3d> out = new LoopL<>();
		
		for (Loop<Point2d> lp : gis) {
			Loop<Point3d> ol = to3d(lp, h, i);
			out.add(ol);
		}
		
		return out;
	}

	public static Loop<Point3d> to3d( Loop<Point2d> lp, double h, int i) {
		Loop<Point3d> ol = new Loop<>();
		for (Point2d pt : lp)
			ol.append( i == 1 ? 
					new Point3d (pt.x, h, pt.y) : i == 2 ? 
					new Point3d (pt.x, pt.y, h ) : 
					new Point3d (h, pt.x, pt.y) );
		return ol;
	}

	public static List<Point3d> intersect( LoopL<Point3d> gis, LinearForm3D lf ) {
		
		List<Point3d> out = new ArrayList();
		
		for (Loop<Point3d> loop : gis)
			for (Loopable<Point3d> pt : loop.loopableIterator()) {
				
				Vector3d dir = new Vector3d(pt.getNext().get());
				dir.sub(pt.get());
				
				Point3d res = lf.collide( pt.get(), dir, dir.length() );
				if (!(res instanceof OOB ) ) {
					out.add(res);
				}
			}
		
		return out;
	}

	
	public static LoopL<Point2d> from (HalfMesh2 hm) {
		
		LoopL<Point2d> out = new LoopL<>();

		
		for (HalfFace hf : hm.faces) {
			out.add(from( hf ));
		}
		
		return out;
	}

	public static Loop<Point2d> from( HalfFace hf ) {
		Loop<Point2d> loop = new Loop();
		for (HalfEdge he : hf.edges())
			loop.append( he.start );
		return loop;
	}

	public static boolean inside( Point2d pt, LoopL<Point2d> poly ) {
		return poly.stream().anyMatch( l -> inside( pt, l ) );
	}

	public static boolean inside( Point2d pt, Loop<Point2d> poly ) {

		int crossings = 0;
		Vector2d left = new Vector2d(-1, 0);
		
		for (Loopable<Point2d> ll : poly.loopableIterator()) {
			
			Line l = new Line (ll.get(), ll.getNext().get());
			if (
			   (l.start.y < pt.y && l.end.y > pt.y ||
				l.start.y > pt.y && l.end.y < pt.y ) && 
				l.intersects( pt, left ) != null ) {
				
				crossings++;
			}
		}
		
		return crossings % 2 == 1;
	}

	public static LinearForm3D findPlane( LoopL<Point3d> loopL ) {
		
		Vector3d normal = new Vector3d();
		Point3d cen = new Point3d();
		int count = 0;
		
		for (Loop<Point3d> loop : loopL)
		for ( Loopable<Point3d> pt : loop.loopableIterator() ) {

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
			
			cen.add(pt.get());
			count++;
		}
		
		cen.scale( 1./count );
		
		return new LinearForm3D( normal, cen );
	}

	public static Loop<Point3d> transform( Loop<? extends Point3d> ll, Matrix4d mat ) {
		Loop<Point3d> out = new Loop<>();
		
		for (Point3d p : ll) {
			Point3d pn = new Point3d(p);
			mat.transform( pn );
			out.append( pn );
		}
		
		return out;
	}
	
	public static LoopL<Point3d> transform( LoopL<? extends Point3d> ll, Matrix4d mat ) {
		
		LoopL<Point3d> out = new LoopL<>();
		
		for (Loop<? extends Point3d> lp : ll)
			out.add(transform(lp, mat));
		
		return out;
	}

	public static Loop<Point2d>[] cutConvex ( Loop<Point2d> loop, LinearForm cut ) { // dirty method; does a single cut
		
		Loop<Point2d>[] out = new Loop[] {new Loop<>(), new Loop<>()};
		
		
		Loopable<Point2d> start = null;
		int cuI = -1;
		
		for (Loopable<Point2d> l : loop.loopableIterator()) {
			boolean a = cut.isInFront( l.get() ), b = cut.isInFront( l.getNext().get() ); 
			
			if (a ^ b) {
				start = l;
				cuI = a? 1 : 0;
				break;
			}
			cuI = a? 1 : 0;
		}
		
		if (start == null) {
			out[ cuI ] = loop;
			return out;
		}
		
		Loopable<Point2d> current = start;
		
		do {
			
			boolean a = cut.isInFront( current.get() ), b = cut.isInFront( current.getNext().get() ); 
			
			out[cuI].append( current.get() );
			
			if (a ^ b) {
				
				Point2d sec = new LinearForm (current.get(), current.getNext().get()) .intersect( cut );
				
				if (sec == null)
					sec = current.get();
				
				out[cuI].append( sec );
				
				cuI = 1-cuI;
				
				out[cuI].append( sec );
			}
			
			
			current = current.next;
			
		} while (current != start);
		
		
		return out;
	}

	public static void dirtySnap( Loop<Point3d> poly, double snapFootprintVert ) {
		dirtySnap( poly, snapFootprintVert, new ArrayList<Point3d>() );
	}

	private static void dirtySnap( Loop<Point3d> poly, double snapFootprintVert, List<Point3d> seen) {
		
		pt:
		for (Loopable<Point3d> pt : poly.loopableIterator()) {
			
			for (Point3d pt2 : seen) {
				if (pt.get().distance( pt2 ) < snapFootprintVert) {
					pt.me = new Point3d (pt2);
					continue pt;
				}
			}
			seen.add(pt.get());
		}
	}

	public static void dirtySnap( LoopL<Point3d> polies, double snapFootprintVert ) {
		List<Point3d> seen = new ArrayList<>();
		for (Loop <Point3d> loop : polies) 
			dirtySnap( loop, snapFootprintVert, seen );
	}

	public static Point2d average( LoopL<Point2d> polies ) {

		Point2d out = new Point2d();
		int count = 0;

		for ( Loop<Point2d> ll : polies )
			for ( Point2d pt : ll ) {
				out.x += pt.x;
				out.y += pt.y;
				count++;
			}

		out.x /= count;
		out.y /= count;

		return out;
	}
}
