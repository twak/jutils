package org.twak.utils.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.vecmath.Point2d;

import org.twak.utils.Loop;
import org.twak.utils.LoopL;
import org.twak.utils.PanMouseAdaptor;

public class PaintThing {
	
	public interface ICanPaint {
		public void paint (Graphics2D g, PanMouseAdaptor ma);
	}
	
	public static void paint (Object o, Graphics2D g, PanMouseAdaptor ma) {
		if (o == null)
			return;
		else if (o instanceof ICanPaint)
			((ICanPaint)o).paint(g, ma);
		else if (o instanceof LoopL)
				p ((LoopL) o, g, ma);
		else throw new Error( "can't paint " + o.getClass() );
	}

	private static void p(LoopL<Point2d> o, Graphics2D g, PanMouseAdaptor ma) {
		for (Loop<Point2d> ll : o)
		{
			
			Polygon p = new Polygon();
			
			for (Point2d pt : ll)
				p.addPoint(ma.toX(pt.x), ma.toY(pt.y));

			Color c = g.getColor();
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
			g.fill(p);
			g.setColor(c);
			g.draw(p);
			
			
			
		}
	}
	
	
}
