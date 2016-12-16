package org.twak.utils;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class HalfMesh2 {

	public List<HalfFace> faces = new ArrayList();

	public static boolean DBG = false;
	
	public interface EdgeFactory {
		public HalfEdge create (Point2d s, Point2d e);
	}
	
	public static class Builder {

		MultiMap<Point2d, HalfEdge> local = new MultiMap<>();
		HalfMesh2 mesh = new HalfMesh2();
		Point2d last, first; 
		HalfEdge lastEdge, firstEdge;
		Class edgeClass = HalfEdge.class, faceClass = HalfFace.class;
		
		public Builder(){}
		public Builder(Class edgeClass, Class faceClass) {
			this.edgeClass = edgeClass;
			this.faceClass = faceClass;
		}
		
		public void newPoint(Point2d pt) {

			if (first == null)
				first = pt;

			if (last != null)
				newEdge(last, pt);

			last = pt;
		}

		public void newFace() {
			
			if (last != null) {

				HalfEdge edge = createEdge( edgeClass, last, first, null);

				lastEdge.next = edge;
				edge.next = firstEdge;
				
				HalfFace face = createFace( faceClass, edge);
				for (HalfEdge e : face.edges()) { 
					e.face = face;
				}

				mesh.faces.add(face);
			}

			first = last = null;
			firstEdge = lastEdge = null;
		}

		private HalfEdge newEdge(Point2d s, Point2d e) {

			HalfEdge edge = createEdge( edgeClass, s, e, null );

			if (lastEdge != null)
				lastEdge.next = edge;
			
			for (HalfEdge e2 : local.get(s))
				if (e2.start == edge.end && edge.start == e2.end)
					edge.over = e2;

			local.put(edge.start, edge);
			local.put(edge.end, edge);
			
			if (firstEdge == null)
				firstEdge = edge;
			
			return lastEdge = edge;
		}

		public HalfMesh2 done() {
			return mesh;
		}

	}

	public static class HalfEdge {

		public HalfEdge over, next;
		public HalfFace face;
		public Point2d start, end;
		
		public HalfEdge(Point2d s, Point2d e, HalfEdge parent) { // called via reflection
			this.start = s;
			this.end = e;
		}

		public double length() {
			return start.distance( end );
		}
		
		public HalfEdge split( Point2d pt ) {
			
			HalfEdge ourSide = createEdge (this.getClass(), pt, end, this);
			
			ourSide.face = face;
			ourSide.next = next;
			end = pt;

			if ( over != null ) {
				HalfEdge otherSide = createEdge(this.getClass(), pt, start, over );
				
				over.end = pt;
				
				otherSide.face = over.face;
				otherSide.over = this;
				otherSide.next = over.next;
				over.next = otherSide;
				over.over = ourSide;
				
				ourSide.over = over;
				
				over = otherSide;

			}
			
			next = ourSide;
			
			return this;
		}

		public Line line() {
			return new Line (start, end );
		}
		
		@Override
		public String toString() {
			return "("+start+", "+end+")";
		}

		public void dissolve( HalfMesh2 mesh ) {

			HalfEdge ob = over.findBefore();
			
			findBefore().next = over.next;
			ob.next = next;
			
			mesh.faces.remove (over.face);
			if (face.e == this)
				face.e = next;
		}

		private HalfEdge findBefore() {
			
			HalfEdge last = this.next;
			
			do {
				HalfEdge n = last.next;
				if (n == this)
					return last;
				
				last = n;
			} 
			while (last != this);
			
			return null;
		}
	}

	public static class HalfFace {

		HalfEdge e;

		public HalfFace(HalfEdge e) {
			this.e = e;
		}

		public Iterable<HalfEdge> edges() {
			return new Iterable() {
				@Override
				public Iterator<HalfEdge> iterator() {
					return new EdgeIterator(e);
				}
			};
		}

		public List<HalfFace> getNeighbours() {

			List<HalfFace> out = new ArrayList<>();

			HalfEdge c = e;

			do {
				out.add(e.face);
				c = e.next;
			} while (c != e);

			return out;
		}

		public HalfEdge fracture( Point2d origin, Vector2d dir, HalfEdge...ignore ) {
			
			double bestDist = Double.MAX_VALUE;
			Point2d bestPt = null;
			HalfEdge bestEdge = null;
			
//			if (DBG) {
//				PaintThing.debug.put(1, ignore[0].line() );
//				PaintThing.debug.put(1, ignore[1].line() );
//				PaintThing.debug.put(1, origin );
//				
//				Point2d p = new Point2d( dir );
//				p.scale( 10 );
//				p.add(origin);
//				
//				PaintThing.debug.put(1, new Line (origin,p) );
//			}
			
			e:
			for (HalfEdge e : edges()) {

				for (HalfEdge e2 : ignore)
					if (e2 == e)
						continue e;
				
				Point2d p = e.line().intersects (origin, dir);
				if ( p != null ){
					double dist = p.distanceSquared( origin );
					if (dist < bestDist) {
						bestDist = dist;
						bestPt = p;
						bestEdge = e;
					}
				}
				
			}
			
			if (bestEdge == null)
				return null;
			
			return bestEdge.split( bestPt );
		}

		public HalfEdge split( HalfMesh2 m, HalfEdge prev1, HalfEdge prev2 ) {
			
			m.faces.remove (this);
			
			HalfEdge e12 = createEdge ( prev1.getClass(), prev1.end, prev2.end, null ),
					 e21 = createEdge ( prev1.getClass(), prev2.end, prev1.end, null );
			
			HalfFace left = createFace( this.getClass(), e12 ),
					right = createFace( this.getClass(), e21 );
			
			m.faces.add(left);
			m.faces.add(right);
			m.faces.remove(this);
			
			e12.face = left;
			e21.face = right;
			
			setFace (prev1.next, prev2, right);
			setFace (prev2.next, prev1, left);
			
			e12.over = e21;
			e21.over = e12;

			e12.next = prev2.next;
			e21.next = prev1.next;
			
			prev1.next = e12;
			prev2.next = e21;
			
			return e12;
		}

		private static void setFace( HalfEdge from, HalfEdge to, HalfFace face ) {

			HalfEdge current = from;
			current.face = face;
			
			do {
				current = current.next;
				current.face = face;
			
			} while (current != to);
		}

		public boolean contains( Point2d pt ) {
			
			Vector2d left = new Vector2d(-1,0);
			
			int count = 0;
			for (HalfEdge e : edges())
				if ( e.line().intersects( pt, left ) != null )
					count++;

			return count % 2 == 1;
		}

		public void remove( HalfMesh2 mesh ) {

			for (HalfEdge e : edges()) 
				if (e.over != null)
					e.over.over = null;
			
			mesh.faces.remove (this);
		}
	}

	public static class EdgeIterator implements Iterator<HalfEdge> {

		HalfEdge start, current;

		public EdgeIterator(HalfEdge first) {
			start = first;
		}

		@Override
		public boolean hasNext() {
			return current != start;
		}

		@Override
		public HalfEdge next() {
			
			if (!hasNext())
				throw new Error();
			
			if (current == null) {
				current = start.next;
				return start;
			}
			
			HalfEdge out = current;
			current = current.next;
			
			if (current == null)
				start = current;
			
			return out;
		}

	}

	public void apply(AffineTransform af) {

		Map<Point2d, Point2d> seen = new IdentityHashMap();
		for (HalfFace hf : faces)
			for (HalfEdge e : hf.edges())
				for (Point2d pt : new Point2d[] { e.start, e.end })
					if ( !seen.containsKey(pt) ) {
						seen.put(pt, pt);
						transform(pt, af);
					}
	}

	private static void transform(Point2d a, AffineTransform at) {
		
		double[] coords = new double[2];
		a.get(coords);
		System.out.println("in "+coords[0]+" " + coords[1]);
		at.transform(coords, 0, coords, 0, 1);
		a.set(coords);
		System.out.println("out "+coords[0]+" " + coords[1]);
	}
	
	private static HalfEdge createEdge( Class klass, Point2d s, Point2d e, HalfEdge parent ) {

		try {
			return (HalfEdge) klass.getConstructor( Point2d.class, Point2d.class, HalfEdge.class ).newInstance( s, e, parent );
		} catch ( Throwable th ) {
			th.printStackTrace();
			return null;
		}
	}

	private static HalfFace createFace( Class<? extends HalfFace> klazz, HalfEdge e ) {
		
		try {
			return (HalfFace) klazz.getConstructor( HalfEdge.class ).newInstance( e );
		} catch ( Throwable f ) {
			f.printStackTrace();
			return null;
		}
	}
}
