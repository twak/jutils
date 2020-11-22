package org.twak.utils;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point2d;

import org.twak.utils.geom.DRectangle;

public class PanMouseAdaptor extends MouseAdapter implements Cloneable {
	int zoomInt = 0;
	public double cenX;
	public double cenY;
	public double zoom = 1;
	Integer startX;
	Integer startY;
	public Component comp;
	public int button = MouseEvent.BUTTON2;

	public List<RangeListener> listeners = new ArrayList();

	public PanMouseAdaptor(Point2d center, double v) {
		this.cenX= center.x;
		this.cenY = center.y;
		this.zoom = v * 2;
	}

	public static PanMouseAdaptor buildFixedMA( Component comp, double x, double y, double width, double height, double outWidth ) {

		PanMouseAdaptor out = new PanMouseAdaptor();

		out.comp = comp;

		out.cenX = x + width / 2;
		out.cenY = y + height / 2;

		out.zoom = outWidth / width;

		return out;
	}

	public static PanMouseAdaptor buildFixedMA( double x, double y, double width, double height, double outWidth, double compWidth, double compHeight ) {

		PanMouseAdaptor out = new PanMouseAdaptor() {
			public int compGetWidth() {
				return (int)compWidth;
			}
			public int compGetHeight() {
				return (int)compHeight;
			}
		};

		out.cenX = x + width / 2;
		out.cenY = y + height / 2;

		out.zoom = outWidth / width;

		return out;
	}

	public PanMouseAdaptor() {
		super();
	}

	private class PanKeyListener implements KeyListener {

		final float speed = 6;

		@Override
		public void keyPressed( KeyEvent e ) {

			switch ( e.getKeyCode() ) {
			case KeyEvent.VK_LEFT:
				cenX -= compGetWidth() / ( speed * zoom );
				break;
			case KeyEvent.VK_RIGHT:
				cenX += compGetWidth() / ( speed * zoom );
				break;
			case KeyEvent.VK_UP:
				cenY -= compGetHeight() / ( speed * zoom );
				break;
			case KeyEvent.VK_DOWN:
				cenY += compGetHeight() / ( speed * zoom );
				break;
			case KeyEvent.VK_PAGE_UP:
				e.consume();
				setZoom( 1 );
				return;
			case KeyEvent.VK_PAGE_DOWN:
				e.consume();
				setZoom( -1 );
				return;
			default:
				return;
			}

			e.consume();
			if (comp != null)
			PanMouseAdaptor.this.comp.repaint();
		}

		@Override
		public void keyReleased( KeyEvent arg0 ) {
		}

		@Override
		public void keyTyped( KeyEvent arg0 ) {
		}
	}

	public int compGetWidth() {
		return comp.getWidth();
	}

	public int compGetHeight() {
		return comp.getHeight();
	}

	public PanMouseAdaptor( Component comp ) {
		super();
		this.comp = comp;
		comp.addMouseListener( this );
		comp.addMouseWheelListener( this );
		comp.addMouseMotionListener( this );

		comp.addKeyListener( new PanKeyListener() );

		//        SwingUtilities.invokeLater( new Runnable()
		//        {
		//
		//            public void run()
		//            {
		//
		//                cenX = PanMouseAdaptor.this.compGetWidth() / 2;
		//                cenY = PanMouseAdaptor.this.compGetHeight() / 2;
		//            }
		//
		//        });

		comp.addComponentListener( new ComponentAdapter() {

			@Override
			public void componentResized( ComponentEvent e ) {
				if (comp != null)
					PanMouseAdaptor.this.comp.repaint();
				fireListeners();
			}
		} );
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		if (comp != null)
		comp.requestFocus();
		if ( e.getButton() == button ) {
			startX = e.getPoint().x;
			startY = e.getPoint().y;
		}
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		if ( startX != null ) {
			int endX = e.getPoint().x;
			int endY = e.getPoint().y;
			cenX -= ( endX - startX ) / zoom;
			cenY -= ( endY - startY ) / zoom;
			startX = endX;
			startY = endY;
			if (comp != null)
			comp.repaint();
			fireListeners();

			//            System.out.println("cen x,y are "+cenX +", "+cenY);
		}
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		startX = null;
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		setZoom( -e.getWheelRotation() );
		if ( e.getWheelRotation() < 0 ) // recenter when zooming in
		{
			Point2d pt = from( e );
			center( pt );
		}
	}

	public void resetView() {
		center( new Point2d() );
		zoomInt = 0;
		setZoom( 0 );
	}

	public void resetView( PanMouseAdaptor ma ) {
		this.cenX = ma.cenX;
		this.cenY = ma.cenY;
		this.zoom = ma.zoom;
		this.zoomInt = ma.zoomInt;
	}

	public void setZoom( int direction ) {
		zoomInt += direction;
		zoomInt = Mathz.clamp( zoomInt, -100, 100 );
		zoom = zoomIntToZoom( zoomInt );
		if (comp != null)
		comp.repaint();
		fireListeners();

		//        System.out.println("zoom is "+zoom+" .. "+zoomInt);
	}
	
	private static double zoomIntToZoom (double zI) {
		return Math.exp( zI / 4. );
	}
	
	private static int zoomToZoomInt (double zI) {
		return (int) (Math.log( zI ) * 4 );
	}

	public double getZoom() {
		return zoom;
	}

	public double fromX( int val ) {
		return ( val - compGetWidth() / 2 ) / zoom + cenX;
	}
	
	public double fromY( int val ) {
		return ( val - compGetHeight() / 2 ) / zoom + cenY;
	}

	public double fromZoom( double width ) {
		return width / zoom;
	}

	public int toX( double val ) {
		return (int) ( ( val - cenX ) * zoom + compGetWidth() / 2 );
	}

	public int toY( double val ) {
		return (int) ( ( val - cenY ) * zoom + compGetHeight() / 2 );
	}

	public int toZoom( double width ) {
		return (int) ( width * zoom );
	}

	public Point to( Point2d end ) {
		return new Point( toX( end.x ), toY( end.y ) );
	}

	public Point2d from( Point point ) {
		return new Point2d( fromX( point.x ), fromY( point.y ) );
	}

	public void center( Point2d point2d ) {
		cenX = point2d.x;
		cenY = point2d.y;
	}

	public Point2d getCenter() {
		return new Point2d( cenX, cenY );
	}

	public double getMaxRange() {
		return fromZoom( Math.max( compGetWidth(), compGetHeight() ) );
	}

	public Point2d from( MouseEvent e ) {
		return new Point2d( fromX( e.getX() ), fromY( e.getY() ) );
	}

	public Rectangle to( DRectangle r ) {
		return new Rectangle( toX( r.x ), toY( r.y ), toZoom( r.width ), toZoom( r.height ) );
	}
	
	public DRectangle toD( DRectangle r ) {
		return new DRectangle( toX( r.x ), toY( r.y ), toZoom( r.width ), toZoom( r.height ) );
	}

	public double viewLeft() {
		return cenX - viewWidth() / 2;
	}

	public double viewWidth() {
		return fromZoom( compGetWidth() );
	}

	public double viewTop() {
		return cenY - viewHeight() / 2;
	}

	public double viewHeight() {
		return fromZoom( compGetHeight() );
	}

	public boolean isDragging() {
		return startX != null;
	}

	public void addListener( RangeListener ra ) {
		listeners.add( ra );
	}

	public void drawImage( Graphics g, Image im, double x, double y ) {
		g.drawImage( im, toX( x ), toY( y ), toZoom( im.getWidth( null ) ), toZoom( im.getHeight( null ) ), null );
	}

	public AffineTransform getAffineTransform( AffineTransform current ) {
		AffineTransform at = new AffineTransform();

		at.translate( compGetWidth() / 2, compGetHeight() / 2 );
		at.scale( zoom, zoom );
		at.translate( -cenX, -cenY );

		current.concatenate( at );

		return current;
	}

	public static abstract class RangeListener {
		public abstract void changed( PanMouseAdaptor ma );
	}

	private void fireListeners() {
		for ( RangeListener ra : listeners )
			ra.changed( this );
	}

	public double toXD( double val ) {
		return ( val - cenX ) * zoom + compGetWidth() / 2;
	}

	public double toYD( double val ) {
		return ( val - cenY ) * zoom + compGetHeight() / 2;
	}

	public double toZoomD( double width ) {
		return width * zoom;
	}

	public double fromXD( double val ) {
		return ( val - compGetWidth() / 2 ) / zoom + cenX;
	}

	public double fromYD( double val ) {
		return ( val - compGetHeight() / 2 ) / zoom + cenY;
	}
	
	public Point2d toD( Point2d end ) {
		return new Point2d( toXD( end.x ), toYD( end.y ) );
	}

	public PanMouseAdaptor clone() {
		PanMouseAdaptor out = new PanMouseAdaptor();
		out.cenX = cenX;
		out.cenY = cenY;
		out.zoom = zoom;
		return out;
	}

	public boolean sameLocation( PanMouseAdaptor o ) {
		return o.cenX == cenX && o.cenY == cenY && o.zoom == zoom;
	}

	public void view( DRectangle bounds ) {
		center( bounds.getCenter() );
		zoomInt = 0;

		zoomFromWidth( bounds.width == 0 ? bounds.height : bounds.width );
	}

	public void zoomFromWidth( double width ) {
		
		int cw = compGetWidth();
		
		if (cw == 0 || width == 0)
			return;
		
		zoom = cw / width;
		zoomInt = zoomToZoomInt( zoom * 0.8 );
		setZoom( 0 );
	}

	public void viewOnSetSize( DRectangle bounds ) {
		
		ComponentAdapter ca = new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				view( bounds );
				if (comp != null)
				comp.removeComponentListener( this );
			}
		} ;

		if (comp != null)
		comp.addComponentListener( ca );
		
	}
}
