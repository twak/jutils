package org.twak.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.twak.utils.HalfMesh2.HalfEdge;
import org.twak.utils.HalfMesh2.HalfFace;

public class PaintThing {
	
	public interface ICanPaint {
		public void paint (Graphics2D g, PanMouseAdaptor ma);
	}

	public interface ICanPaintU {
		public void paint (Object o, Graphics2D g, PanMouseAdaptor ma);
	}
	
	public static Map<Class, ICanPaintU> lookup = new HashMap<>();
	
	public static void paint (Object o, Graphics2D g, PanMouseAdaptor ma) {
		if (o == null)
			return;
		else if (o instanceof ICanPaint)
			((ICanPaint)o).paint(g, ma);
		else if (lookup.containsKey(o.getClass())) {
			((ICanPaintU)lookup.get(o.getClass())).paint(o, g, ma);
		}
		else if (o instanceof LoopL)
			p ((LoopL) o, g, ma);
		else if (o instanceof Loop)
			p ((Loop) o, g, ma);
		else if (o instanceof Graph2D)
			p ((Graph2D) o, g, ma);
		else if (o instanceof Point2d)
			p ((Point2d) o, g, ma);
		else if (o instanceof Point3d)
			p ((Point3d) o, g, ma);
		else if (o instanceof Line)
			p ((Line) o, g, ma);
		else if (o instanceof HalfMesh2)
			p ((HalfMesh2) o, g, ma);
		else if (o instanceof String)
			p ((String) o, g, ma);
		else if (o instanceof Rectangle2D)
			p ((Rectangle2D) o, g, ma);
		else if (o instanceof Iterable) {
//			int c = 0;
			for (Object o2 : (Iterable) o) {
//				g.setColor(Rainbow.getColour(c++));
				paint(o2, g, ma);
			}
		}
		else throw new Error( "can't paint " + o.getClass() );
	}

	private static void p( Rectangle2D o, Graphics2D g, PanMouseAdaptor ma ) {
		Rectangle2D r = (Rectangle2D) o;
		g.drawRect( ma.toX(r.getX()), ma.toY(-r.getY()), ma.toZoom( r.getWidth()), ma.toZoom( r.getHeight()) );
	}

	private static void p( HalfMesh2 o, Graphics2D g2, PanMouseAdaptor ma ) {

		double scatterRadius = 0.0;

		int fc = 0;
		for ( HalfFace f : o.faces ) {

			Polygon pwt = new Polygon();

			for ( HalfEdge e : f.edges() ) {
				pwt.addPoint( ma.toX( e.start.x + Math.random() * scatterRadius ), ma.toY( e.start.y + Math.random() * scatterRadius ) );
				p(e.start, g2, ma);
			}

			Color c = Rainbow.getColour( fc++ );
			
			g2.setColor( new Color (c.getRed(), c.getGreen(), c.getBlue(), 50) );
			g2.fill( pwt );

			g2.setColor( c );
			g2.draw( pwt );

			
			for ( HalfEdge e : f.edges() ) {
				drawArrow(g2, ma, e.line(), 5);

			}
		}
			
		for ( HalfFace f : o.faces ) 
				for ( HalfEdge e : f.edges() ) {
			if (	e.face  != f ||
					(e.over  != null && (e.over.face == f || e.over.over != e ) ) ||
					e.face  == null ||
					e.start == null ||
					e.end   == null ||
					e.next  == null )
			{
				g2.setColor( Color.red );
				g2.setStroke(  new BasicStroke( 4 ) );
				p (e.line(), g2, ma);
			}
		}
		g2.setStroke(  new BasicStroke( 1 ) );
	}

	private static void p(LoopL<Point2d> o, Graphics2D g, PanMouseAdaptor ma) {
		int cc = 0;
		for (Loop<Point2d> ll : o)
		{
			p2 (ll, g, ma, cc);
		}
	}
	
	private static void p(Loop<Point2d> ll, Graphics2D g, PanMouseAdaptor ma) {
		p2 (ll, g, ma, 0);
	}
	
	private static void p2(Loop<Point2d> ll, Graphics2D g, PanMouseAdaptor ma, int cc) {
		
		g.setColor(Rainbow.getColour(cc++));
		
		Polygon p = new Polygon();
		
		for (Point2d pt : ll)
			p.addPoint(ma.toX(pt.x), ma.toY(pt.y));

		Color c = g.getColor();
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
		g.fill(p);
		g.setColor(c);
		g.draw(p);
		
		for (Loop<Point2d> h : ll.holes)
			p (h, g, ma);
		
		if (true)
			for (Loopable<Point2d> able : ll.loopableIterator() )
				drawArrow(g, ma, new Line (able.get(), able.getNext().get()), 5);
	}
	

	private static void p(Graph2D o, Graphics2D g, PanMouseAdaptor ma) {

		double scatterRadius = 0.;
		
		Color c = g.getColor();
		
		for (Line l : o.allLines()) {

			if (l.length() < 0.001)
				g.setColor(Color.red);
			
			g.drawLine(
					ma.toX(l.start.x + Math.random() * scatterRadius),
					ma.toY(l.start.y + Math.random() * scatterRadius),
					ma.toX(l.end  .x + Math.random() * scatterRadius), 
					ma.toY(l.end  .y + Math.random() * scatterRadius) );

			drawArrow(g, ma, l, 5);
			
			g.setColor(Color.red);
			
			if (false) {
				if (o.get(l.start).size() != 2) {

					// if (o.get(l.start).size() == 1)
					// System.out.println(" >>>--"+l.start + " to " + l.end);

					g.fillOval(ma.toX(l.start.x), ma.toY(l.start.y), 5, 5);
					g.drawString(" " + o.get(l.start).size(), ma.toX(l.start.x), ma.toY(l.start.y));
				}
				g.setColor(Color.green);

				if (o.get(l.end).size() != 2) {

					// if (o.get(l.end).size() == 1)
					// System.out.println(" >>>++"+l.end + " from " + l.start);
					g.fillOval(ma.toX(l.end.x), ma.toY(l.end.y), 5, 5);

					g.drawString("     " + o.get(l.end).size(), ma.toX(l.end.x), ma.toY(l.end.y));
				}
			}
			
			g.setColor(c);
		}
	}

	public static void drawArrow(Graphics2D g, PanMouseAdaptor ma, Line l, int size) {
		
		Vector2d dir = l.dir();
		Point2d mid = l.fromFrac(0.5);
		
		AffineTransform old = g.getTransform();
		g.translate(ma.toX(mid.x), ma.toY(mid.y));
		g.rotate(-Math.atan2(dir.x, dir.y));

		g.drawLine(-size, -size, 0, 0);
		g.drawLine(size,  -size, 0, 0);

		g.setTransform(old);
	}

	private static void p(Point3d o, Graphics2D g, PanMouseAdaptor ma) {
		Point3d s = (Point3d) o;
		g.fillOval(ma.toX(s.x) - 2, ma.toY(s.z) - 2, 4, 4);
	}

	private static void p(Point2d o, Graphics2D g, PanMouseAdaptor ma) {
		Point2d s = (Point2d) o;
		g.fillOval(ma.toX(s.x) - 2, ma.toY(s.y) - 2, 4, 4);
	}
	
	private static void p(Line o, Graphics2D g, PanMouseAdaptor ma) {
		Line l = (Line) o;
		g.drawLine(ma.toX(l.start.x), ma.toY(l.start.y), ma.toX(l.end.x), ma.toY(l.end.y));

		drawArrow(g, ma, l, 5);
	}

	private static void p(String o, Graphics2D g, PanMouseAdaptor ma) {
		g.setColor( Color.black );
		g.drawString( o, 10, 30 );
	}

	public static MultiMap<Object, Object> debug = new MultiMap();
	
	public static void paintDebug(Graphics2D g, PanMouseAdaptor ma) {
		
		int count = 0;
		
		for (Object k : debug.keySet()) {
			
			for (Object o : debug.get(k) ) {
				g.setColor(Rainbow.getColour(count++));
				paint(o, g, ma);
			}
		}
	}
}
