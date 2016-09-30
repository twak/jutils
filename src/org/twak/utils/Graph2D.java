package org.twak.utils;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class Graph2D extends MultiMap<Point2d, Line> {

	public Graph2D apply(AffineTransform at) {

		Graph2D out = new Graph2D();
		
		Map<Point2d, Point2d> seenPts = new HashMap<>();
		
		for (List<Line> lines : map.values() )
			for (Line l : lines) {
				
				Point2d start = seenPts.get(l.start);
				if (start == null) 
					seenPts.put (l.start, start = transform(l.start, at));
				
				Point2d end = seenPts.get(l.end);
				if (end == null) 
					seenPts.put (l.end, end = transform(l.end, at));
				
				Line l2 = new Line( start, end );
				out.put(start, l2);
				out.put(end, l2);
			}
		
		return out;
	}

	private static Point2d transform(Point2d a, AffineTransform at) {

		double[] coords = new double[] { a.x, a.y };
		at.transform(coords, 0, coords, 0, 1);

		return new Point2d(coords[0], coords[1]);
	}

	public void paint(Graph2D graph, Graphics2D g2, PanMouseAdaptor ma) {

		for (Line l : allLines()) {

			g2.drawLine(ma.toX(l.start.x), ma.toY(l.start.y), ma.toX(l.end.x), ma.toY(l.end.y));

			Vector2d dir = l.dir();
			Point2d mid = l.fromFrac(0.5);

			AffineTransform old = g2.getTransform();
			g2.translate(ma.toX(mid.x), ma.toY(mid.y));
			g2.rotate(-Math.atan2(dir.x, dir.y));

			g2.drawLine(5, 5, 0, 0);
			g2.drawLine(5, -5, 0, 0);

			g2.setTransform(old);
		}
	}

	public Set<Line> allLines() {
		Set<Line> seenLines = new HashSet<>();

		for (List<Line> l : map.values())
			seenLines.addAll(l);

		return seenLines;
	}

	public void newLine(Point2d a, Point2d b) {
		Line l = new Line (a,b);
		put(l.start, l);
		put(l.end, l);
	}

}
