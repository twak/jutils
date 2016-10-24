package org.twak.utils;

import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

public class Graph2D extends MultiMap<Point2d, Line>  {

	public Graph2D(List<Line> sliceTri) {
		sliceTri.stream().forEach( i -> add(i) );
	}
	
	public Graph2D(){}

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
	
	public void removeInnerEdges() {
		
		Set<Line> togo = new HashSet<>();
		
		for (Point2d pt : keySet()) 
			for (Line l1 : get(pt))
				for (Line l2 : get(pt))
					if (
						l1.start.equals (l2.end) &&
						l2.start.equals (l1.end) ) {
							togo.add(l1);
							togo.add(l2);
					}

		for (Line l : togo) {
			remove(l.start, l);
			remove(l.end, l);
		}
	}
	

	private static Point2d transform(Point2d a, AffineTransform at) {

		double[] coords = new double[] { a.x, a.y };
		at.transform(coords, 0, coords, 0, 1);

		return new Point2d(coords[0], coords[1]);
	}


	public Set<Line> allLines() {
		Set<Line> seenLines = new HashSet<>();

		for (List<Line> l : map.values())
			seenLines.addAll(l);

		return seenLines;
	}

	public void add(Line l) {
		put(l.start, l);
		put(l.end, l);
	}
	
	public void add(Point2d a, Point2d b) {
		Line l = new Line (a,b);
		put(l.start, l);
		put(l.end, l);
	}

	public void addAll(Iterable<Line> portal) {
		for (Line l : portal)
			add(l);
	}

	public void remove(Line line) {
		
		remove(line.start, line);
		remove(line.end,   line);
	}

	public void removeAll(Iterable<Line> togo) {
		for (Line l : togo)
			remove(l);
	}
}
