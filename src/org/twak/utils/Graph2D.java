package org.twak.utils;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class Graph2D extends MultiMap<Point2d, Point2d> {

	public Graph2D apply(AffineTransform at) {

		Graph2D out = new Graph2D();

		for (Point2d a : keySet()) {

			Point2d an = transform(a, at);

			for (Point2d b : get(a)) {
				
				Point2d bn = transform(b, at);

				out.put(an, bn);
			}

		}

		return out;
	}

	private Point2d transform(Point2d a, AffineTransform at) {

		double[] coords = new double[] { a.x, a.y };
		at.transform(coords, 0, coords, 0, 1);

		return new Point2d(coords[0], coords[1]);
	}

	public void paint(Graph2D graph, Graphics2D g2, PanMouseAdaptor ma) {
		for (Point2d a : graph.map.keySet())
			for (Point2d b : graph.map.get(a)) {

				Vector2d dir = new Vector2d(b);
				dir.sub(a);
				
				Point2d mid = new Point2d( dir );
				mid.scale(0.5);
				mid.add(a);

				g2.drawLine(ma.toX(a.x), ma.toY(a.y), ma.toX(b.x), ma.toY(b.y));

				AffineTransform old = g2.getTransform();
				g2.translate(ma.toX(mid.x), ma.toY( mid.y) );
				g2.rotate(-Math.atan2(dir.x, dir.y));

				g2.drawLine(5,  5, 0, 0);
				g2.drawLine(5, -5, 0, 0);

				g2.setTransform(old);

			}
	}

}
