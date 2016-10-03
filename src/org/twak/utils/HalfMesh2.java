package org.twak.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

public class HalfMesh2 {

	public List<HalfFace> faces = new ArrayList();

	public static class Builder {

		MultiMap<Point2d, HalfEdge> local = new MultiMap<>();
		HalfMesh2 mesh = new HalfMesh2();
		Point2d last, first; 
		HalfEdge lastEdge, firstEdge;

		public void newPoint(Point2d pt) {

			if (first == null)
				first = pt;

			if (last != null)
				newEdge(last, pt);

			last = pt;
		}

		public void newFace() {
			if (last != null) {

				HalfEdge edge = newEdge(last, first);

				lastEdge.next = edge;
				edge.next = firstEdge;
				
				HalfFace face = new HalfFace(edge);
				for (HalfEdge e : face.edges()) { 
					e.face = face;
				}

				mesh.faces.add(face);
			}

			first = last = null;
			firstEdge = lastEdge = null;
		}

		private HalfEdge newEdge(Point2d s, Point2d e) {

			HalfEdge edge = new HalfEdge(s, e);

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

		HalfEdge over, next;
		HalfFace face;
		Point2d start, end;

		public HalfEdge(Point2d s, Point2d e) {
			this.start = s;
			this.end = e;
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
			return out;
		}

	};

	public void paint(Graphics2D g2, PanMouseAdaptor ma) {

		double scatterRadius = 0.0;

		int fc = 0;
		for (HalfFace f : faces) {

			Polygon pwt = new Polygon();

			for (HalfEdge e : f.edges())
				pwt.addPoint(ma.toX(e.start.x + Math.random() * scatterRadius),
						ma.toY(e.start.y + Math.random() * scatterRadius));

			Color c = Rainbow.getColour(fc++);

			g2.setColor(c);
			g2.fill(pwt);

			g2.setColor(c.darker().darker());
			g2.draw(pwt);

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
}
