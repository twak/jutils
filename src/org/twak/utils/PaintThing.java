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

import org.twak.utils.collections.Loop;
import org.twak.utils.collections.LoopL;
import org.twak.utils.collections.Loopable;
import org.twak.utils.collections.MultiMap;
import org.twak.utils.geom.DRectangle;
import org.twak.utils.geom.Graph2D;
import org.twak.utils.geom.HalfMesh2;
import org.twak.utils.geom.HalfMesh2.HalfEdge;
import org.twak.utils.geom.HalfMesh2.HalfFace;
import org.twak.utils.ui.Colourz;
import org.twak.utils.ui.Rainbow;

public class PaintThing {
	
	public interface ICanPaint {
		public void paint (Graphics2D g, PanMouseAdaptor ma);
	}

	public interface ICanPaintU {
		public void paint (Object o, Graphics2D g, PanMouseAdaptor ma);
	}

	public static Map<Class, ICanPaintU> lookup = new HashMap<>();
	public static Map<Class, Class> editLookup = new HashMap<>();
	
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
		else if (o instanceof DRectangle)
			p ((DRectangle) o, g, ma);
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
		g.drawRect( ma.toX(r.getX()), ma.toY(r.getY()), ma.toZoom( r.getWidth()), ma.toZoom( r.getHeight()) );
		setBounds( new Point2d (o.getX(), o.getY()) );
	}
	
	private static void p( DRectangle o, Graphics2D g, PanMouseAdaptor ma ) {
		DRectangle r = (DRectangle) o;
		g.drawRect( ma.toX(r.x), ma.toY(r.y), ma.toZoom( r.width ), ma.toZoom( r.height ) );
		setBounds( o );
	}

	private static void p( HalfMesh2 o, Graphics2D g2, PanMouseAdaptor ma ) {

		double scatterRadius = 0.0;

		int fc = 0;
		for ( HalfFace f : o.faces ) {

			Polygon pwt = new Polygon();

			for ( HalfEdge e : f.edges() ) {
				pwt.addPoint( ma.toX( e.start.x + Math.random() * scatterRadius ), ma.toY( e.start.y + Math.random() * scatterRadius ) );
//				p(e.start, g2, ma);
				setBounds( e.start );
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

	private static void p(LoopL<? extends Point2d> o, Graphics2D g, PanMouseAdaptor ma) {
		int cc = 0;
		for (Loop<? extends Point2d> ll : o)
		{
			p2 (ll, g, ma, cc);
		}
	}
	
	private static void p(Loop<? extends Point2d> ll, Graphics2D g, PanMouseAdaptor ma) {
		p2 (ll, g, ma, 0);
	}
	
	private static void p2(Loop<? extends Point2d> ll, Graphics2D g, PanMouseAdaptor ma, int cc) {
		
		Color c = g.getColor();
		g.setColor( Colourz.transparent( c, 50 ) );
		
		Polygon p = new Polygon();
		
		for (Point2d pt : ll) {
			p.addPoint(ma.toX(pt.x), ma.toY(pt.y));
//			setBounds( pt );
		}

		g.fill(p);
		g.setColor(c);
		g.draw(p);
		
//		for (Loop<Point2d> h : ll.holes)
//			p (h, g, ma);
//		
		if (true)
			for (Loopable<? extends Point2d> able : ll.loopableIterator() )
				drawArrow(g, ma, new Line ( 
						new Point2d ( able.get().x, able.get().y ),
						new Point2d ( able.getNext().get().x, able.getNext().get().y ) ),5 );
	}
	

	private static void p(Graph2D o, Graphics2D g, PanMouseAdaptor ma) {

		double scatterRadius = 0.;
		
		Color c = g.getColor();
		
		for (Line l : o.allLines()) {

			if (l.length() < 0.001)
				g.setColor(Color.red);

			setBounds( l.start );
			
			g.drawLine(
					ma.toX(l.start.x + Math.random() * scatterRadius),
					ma.toY(l.start.y + Math.random() * scatterRadius),
					ma.toX(l.end  .x + Math.random() * scatterRadius), 
					ma.toY(l.end  .y + Math.random() * scatterRadius) );

			drawArrow(g, ma, l, 5);
			
			g.setColor(Color.red);
			g.setColor(c);
		}
	}

	public static void drawArrow(Graphics2D g, PanMouseAdaptor ma, Line l, int size) {
		
		Vector2d dir = l.dir();
		Point2d mid = l.fromPPram(0.5);
		
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
		g.fillOval(ma.toX(s.x) - 4, ma.toY(s.y) - 4, 8, 8);
	}
	
	private static void p(Line o, Graphics2D g, PanMouseAdaptor ma) {
		Line l = (Line) o;
		g.drawLine(ma.toX(l.start.x), ma.toY(l.start.y), ma.toX(l.end.x), ma.toY(l.end.y));

		setBounds( o.start );
		setBounds( o.end );
		
//		drawArrow(g, ma, l, 5);
	}

	private static void p(String o, Graphics2D g, PanMouseAdaptor ma) {
		g.setColor( Color.black );
		g.drawString( o, 10, 30 * stringCount );
		stringCount++;
	}

	public static MultiMap<Object, Object> debug = new MultiMap();
	
	static int stringCount = 0;
	
	
	public static void paintDebug(Graphics2D g, PanMouseAdaptor ma) {
		
		int count = 0;
		
		for (Object k : debug.keySet()) {
			
			g.setColor(Rainbow.getColour(count++));
			
			if (k instanceof Style)
				((Style)k).activate(g);
			
			for (Object o : debug.get(k) ) {
				paint(o, g, ma);
			}
		}
	}

	private static class Style {
		Color color;
		float width;
		public Style (Color color, float width) {
			this.color = color;
			this.width = width;
		}
		public void activate( Graphics2D g ) {
			g.setColor (color);
			g.setStroke( new BasicStroke( width ) );
		}
	}
	
	public static void debug( Color c, float f, Object clean ) {
		debug.put (new Style(c, f), clean );
	}
	
	private static DRectangle drawBounds = new DRectangle();

	
	public static DRectangle getBounds () {
		return drawBounds;
	}
	
	public static void resetBounds() {
		stringCount = 1;
		drawBounds = null;
	}
	
	public static void setBounds( DRectangle p ) {
		if (drawBounds == null)
			drawBounds = new DRectangle(p);
		else
			drawBounds = drawBounds.union( p );		
	}
	
	public static void setBounds(Point2d p) {
		if (drawBounds == null)
			drawBounds = new DRectangle(p);
		else
			drawBounds.envelop( p );
	}
}
