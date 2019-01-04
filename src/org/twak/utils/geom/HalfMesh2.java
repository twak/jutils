package org.twak.utils.geom;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.twak.utils.IdentityHashSet;
import org.twak.utils.Line;
import org.twak.utils.Mathz;
import org.twak.utils.collections.LoopL;
import org.twak.utils.collections.Loopable;
import org.twak.utils.collections.Loopz;
import org.twak.utils.collections.MultiMap;
import org.twak.utils.geom.HalfMesh2.HalfFace;

public class HalfMesh2 implements Iterable<HalfFace> {

	public List<HalfFace> faces = new ArrayList();
	
	public static class Builder {

		MultiMap<Point2d, HalfEdge> local = new MultiMap<>();
		public HalfMesh2 mesh = new HalfMesh2();
		Point2d last, first; 
		HalfEdge lastEdge, firstEdge;
		Class edgeClass = HalfEdge.class, faceClass = HalfFace.class;
		
		public Builder(){}
		public Builder(Class edgeClass, Class faceClass) {
			this.edgeClass = edgeClass;
			this.faceClass = faceClass;
		}
		
		public void setMesh(HalfMesh2 mesh) {
			this.mesh = mesh;
		}
		
		public HalfEdge newPoint(Point2d pt) {

			if (first == null)
				first = pt;

			HalfEdge out = null;
			
			if (last != null)
				out = newEdge(last, pt);

			last = pt;
			
			return out; 
		}

		public HalfFace newFace() {
			
			HalfFace face = null;
			
			if (last != null) {

				HalfEdge edge = createEdge( edgeClass, last, first, null);

				lastEdge.next = edge;
				edge.next = firstEdge;
				
				face = createFace( faceClass, edge);
				for (HalfEdge e : face.edges()) { 
					e.face = face;
				}

				mesh.faces.add(face);
			}

			first = last = null;
			firstEdge = lastEdge = null;
			
			return face;
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
		
		public HalfEdge() {}
		
		public HalfEdge(Point2d s, Point2d e) { // called via reflection
			this.start = s;
			this.end = e;
		}
		
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

			if (next == over && over.next == this ) {

				if (face.e == this || face.e == over ) {
					// brute force search for another edge in the face
					
				}
				
				return; // nothing to do!
			}
			else if (next == over) {
				findBefore().next = over.next;
				
				if (face.e == this || face.e == over)
					face.e = over.next;
			}
			else if (over.next == this) {
				over.findBefore().next = next;
				
				if (face.e == this || face.e == over)
					face.e = next;
				
			}
			else {
				
				HalfEdge ob = over.findBefore();
				findBefore().next = over.next;
				ob.next = next;
				
				if (face.e == this)
					face.e = next;
				
				for (HalfEdge e : face.edges())
					e.face = face;
			}
			
			if (over.face != face)
				mesh.faces.remove (over.face);
			
			
		}
		
		public boolean replaceByPoint ( HalfMesh2 mesh, Point2d pt ) {
			
			for ( HalfEdge start : new HalfEdge[] { this, over } ) {

				HalfEdge n = start;
				
				while ( n != null && n.next != start.over ) {
					n = n.next;

					n.start = pt;

					if ( n.over != null )
						n.over.end = pt;

					n = n.over;
				}
			}
			
			HalfEdge before = findBefore();
			
			if (over != null) {
				over.findBefore().next = over.next;
				if (over.face.e == over )
					over.face.e = over.next;
			}
			else {
				before.end = pt;
			}
			
			before.next = next;
			
			if (face.e == this)
				face.e = next;
			
			if (face.e.next.next == face.e) {
				face.remove( mesh );
				return true;
			}
			
			return false;
		}

		public HalfEdge findBefore() {
			
			HalfEdge last = this.next;
			
			Set<HalfEdge> seen = new HashSet<>();
			
			do {
				HalfEdge n = last.next;
				if (n == this)
					return last;
				
				last = n;
				
				if (seen.contains( n ))
					throw new Error("loop detected");
				seen.add(n);
				
			} 
			while (last != this);
			
			return null;
		}

		public List<HalfEdge> collectAroundEnd() {
			List<HalfEdge> out = new ArrayList();
			
			out.add(this);
			HalfEdge c= this;

			do {
				out.add(c);
				c = c.next;
				out.add( c );
				c = c.over;
				
			} while ( c!= null && c != this );
			
			if (c == this)
				return out;
			
			c = next;
			
			do {
				
				if (c != next)
					out.add(c);
				
				c = c.findBefore();
				
				if (c != this)
					out.add(c);
				
				c = c.over;
				
			} while (c != null);
			
			return out;
		}
	}

	public static class HalfFace implements Iterable<HalfEdge> {

		public HalfEdge e;

		public HalfFace() {}
		
		public HalfFace(HalfEdge e) {
			this.e = e;
		}

		public void insert (HalfEdge he) {
			he.face = this;
			
			for (HalfEdge f : this)
				if (f.next == e)
					f.next = he;
			
			he.next = e; 
			
			e = he;
		}
		
		public Iterable<HalfEdge> edges() {
			return new Iterable() {
				@Override
				public Iterator<HalfEdge> iterator() {
					return new EdgeIterator(e);
				}
			};
		}

		public List<HalfEdge> edgeList() {
			
			List<HalfEdge> out = new ArrayList<>();
			
			for (HalfEdge e : edges())
				out.add( e );
			
			return out;
		}

		
		public LoopL<HalfEdge> findHoles() {
			
			
			Graph2D g2 = new Graph2D();
			
			class L2 extends Line
			{
				HalfEdge edge;
				public L2 (HalfEdge e) {
					super (e.start, e.end);
					this.edge= e;
				}
			}
			
			for (HalfEdge e : this)
				g2.add( new L2(e) );
			
			g2.removeInnerEdges();
			
			UnionWalker uw = new UnionWalker();
			
			for (Point2d a : g2.map.keySet()) {
				for (Line l : g2.get(a)) {
					uw.addEdge(l.start, l.end);
				}
			}
			
			LoopL<Point2d>pointLoop = uw.findAll();

			if (pointLoop.isEmpty())
				return new LoopL<>();
			
			int outer = -1;
			double bestArea = -Double.MAX_VALUE;
			
			for (int i = 0; i < pointLoop.size(); i++) {
				double area = Math.abs ( Loopz.area ( pointLoop.get(i) ) );
				if (area > bestArea) {
					outer = i;
					bestArea = area;
				}
			}
			
			pointLoop.add(0, pointLoop.remove( outer ) );
			
			LoopL<HalfEdge> out = pointLoop.new Map<HalfEdge>() {
				@Override
				public HalfEdge map( Loopable<Point2d> input ) {
					for (Line l : g2.map.get( input.get() ) ) {
						if (l.end.equals (input.next.get())) {
							return ((L2)l).edge;
						}
					}
					
					throw new Error();
				}
			}.run();
			
			return out;
		}

		public Set<HalfFace> getNeighbours() {

			Set<HalfFace> out = new HashSet<>();

			for (HalfEdge e : this)
				if (e.over != null)
					out.add(e.over.face);
			
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
				
				Line el = e.line();
				
				if (!el.isOnLeft( origin ))
					continue;
				
				Point2d p = el.intersects (origin, dir);
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

			mesh.faces.remove (this);
			
			for (HalfEdge e : edges()) 
				if (e.over != null)
					e.over.over = null;
			
		}

		@Override
		public Iterator<HalfEdge> iterator() {
			return edges().iterator();
		}

		public int edgeCount() {
			int i = 0;
			for (HalfEdge e : this)
				i++;
			return i;
		}

		public List<List<HalfEdge>> parallelFaces( double d ) {
			
			List<List<HalfEdge>> out = new ArrayList<>();
			
			HalfEdge s = e;
			
			while (s.line().absAngle( s.next.line() ) < d)
				s = s.next;
			
			s = s.next;
			HalfEdge c = s;
			
			List<HalfEdge> ce = new ArrayList<>();
			
			do {
				
				ce.add(c);
				
				if (c.line().absAngle( c.next.line() ) >= d) {
					if (!ce.isEmpty())
						out.add(ce);
					ce = new ArrayList();
				}
				
				c = c.next;
				
			} while ( c != s);
			
			if (!ce.isEmpty())
				out.add(ce);
			
			return out;
		}

		public double area() {
			
			Point2d origin = e.start;
			double area = 0;
			
			for (HalfEdge e : this)
				area += Mathz.area(origin, e.start, e.end);
			
			return area;
		}

		public void merge( HalfMesh2 mesh, HalfFace togo ) {
			
			for ( HalfEdge e : this )
				if ( e.over != null && e.over.face == togo ) {
					e.dissolve( mesh );
					break;
				}
			
//			boolean again = true;   this isn't smart enough to deal with leaving splitting a loop in two....
//			again: while ( again ) {
//				again = false;
//				for ( HalfEdge e : this )
//					if ( e.over != null && e.over.face == this ) {
//						e.dissolve( mesh );
//						again = true;
//						continue again;
//					}
			}
	}

	public static class EdgeIterator implements Iterator<HalfEdge> {

		HalfEdge start, current;

		Set<HalfEdge> seen = new IdentityHashSet<>();
		
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
				
				if (seen.contains( start ))
					throw new Error("infinite loop!");
				seen.add(start);
				
				return start;
			}
			
			HalfEdge out = current;
			current = current.next;
			
			if (current == null)
				start = current;
			
			if (seen.contains( out )) {
				throw new Error("infinite loop!");
			}
			seen.add(out);
			
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

	@Override
	public Iterator<HalfFace> iterator() {
		return faces.iterator();
	}

	public DRectangle getBounds() {
		
		DRectangle.Enveloper out = new DRectangle.Enveloper();
		
		for (HalfFace f : this)
			for (HalfEdge e : f)
				out.envelop( e.start );
		
		return out;
	}

	public void add( HalfFace hf ) {
		faces.add( hf );
	}
}
