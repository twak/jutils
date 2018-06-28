package org.twak.utils.geom;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;

import org.twak.utils.Line;
import org.twak.utils.Pair;
import org.twak.utils.collections.ConsecutiveItPairs;

/**
 *
 * @author twak
 */
public class DRectangle {
	public double x, y, width, height;

	public DRectangle() {
	}

	public DRectangle( double x, double y, double width, double height ) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public DRectangle( double width, double height ) {
		this.x = 0;
		this.y = 0;
		this.width = width;
		this.height = height;
	}

	public DRectangle( DRectangle dr ) {
		this( dr.x, dr.y, dr.width, dr.height );
	}

	public DRectangle( Rectangle bounds ) {
		this( bounds.x, bounds.y, bounds.width, bounds.height );
	}

	public DRectangle( List<Point2d> envelop ) {
		this( envelop.get( 0 ).x, envelop.get( 0 ).y, 0, 0 );
		for ( Point2d pt : envelop )
			envelop( pt );
	}

	public DRectangle( Point2d start ) {
		this.x = start.x;
		this.y = start.y;
		this.width = this.height = 0;
	}

	public DRectangle( Line line ) {
		this( line.start );
		envelop( line.end );
	}

	public boolean contains( Tuple2d pt ) {
		return contains( pt.x, pt.y );
	}

	public boolean contains( double X, double Y ) {
		double w = this.width;
		double h = this.height;

		if ( w < 0 || h < 0 ) {
			// At least one of the dimensions is negative...
			return false;
		}

		if ( X < x || Y < y ) {
			return false;
		}
		w += x;
		h += y;
		//    overflow || intersect
		return ( ( w < x || w > X ) && ( h < y || h > Y ) );
	}

	// not sure about this, I like width, height being +ve
	public boolean containsAllowingNegative( double X, double Y ) {
		double w = this.width;
		double h = this.height;

		if ( w < 0 || h < 0 ) {
			// At least one of the dimensions is negative...
			DRectangle pos = toPositive();
			return pos.containsAllowingNegative( X, Y );
		}

		if ( X < x || Y < y )
			return false;
		w += x;
		h += y;
		//    overflow || intersect
		return ( ( w < x || w > X ) && ( h < y || h > Y ) );
	}

	public DRectangle grow( double e ) {
		
		x -= e;
		y -= e;
		width += 2 * e;
		height += 2 * e;
		
		return this;
	}

	public boolean intersects( DRectangle other ) {
		if ( width <= 0 || height <= 0 || other.width <= 0 || other.height <= 0 )
			return false;

		return other.x + other.width > x && other.y + other.height > y && other.x < x + width && other.y < y + height;
	}

	public Rectangle toInteger() {
		return new Rectangle( (int) x, (int) y, (int) width, (int) height );
	}

	/**
	 * Same bounds, positive height and width
	 * 
	 * @return
	 */
	public DRectangle toPositive() {
		if ( width > 0 && height > 0 )
			return this;

		return new DRectangle( x + ( width < 0 ? width : 0 ), y + ( height < 0 ? height : 0 ), Math.abs( width ), Math.abs( height ) );
	}

	public void setFrom( DRectangle rect ) {
		this.width = rect.width;
		this.height = rect.height;
		this.x = rect.x;
		this.y = rect.y;
	}

	public double area() {
		return Math.abs( width * height );
	}

	public double getMaxX() {
		return x + width;
	}

	public double getMaxY() {
		return y + height;
	}

	public Point2d getCenter() {
		return new Point2d( x + width / 2, y + height / 2 );
	}

	public DRectangle union( DRectangle b ) {
		DRectangle out = new DRectangle();
		out.x = Math.min( x, b.x );
		out.y = Math.min( y, b.y );
		out.width = Math.max( getMaxX(), b.getMaxX() ) - out.x;
		out.height = Math.max( getMaxY(), b.getMaxY() ) - out.y;

		return out;
	}

	public DRectangle intersect( DRectangle b ) {
		DRectangle out = new DRectangle();
		out.x = Math.max( x, b.x );
		out.y = Math.max( y, b.y );
		out.width = Math.min( getMaxX(), b.getMaxX() ) - out.x;
		out.height = Math.min( getMaxY(), b.getMaxY() ) - out.y;

		if ( out.width < 0 || out.height < 0 )
			return null;

		return out;
	}

	@Override
	public String toString() {
		return "( " + x + " ," + y + " ," + width + " ," + height + ")";
	}

	public boolean sameAs( DRectangle o ) {
		return ( x == o.x ) && ( y == o.y ) && ( width == o.width ) && ( height == o.height );
	}

	public void set( Bounds b, double value ) {
		set( b, value, false );
	}

	public void set( Bounds b, double value, boolean mod ) {
		switch ( b ) {
		case XMIN:
			double delta = value - x;
			x = value;
			if ( mod )
				width -= delta;
			break;
		case XCEN:
			x = value - width / 2;
			break;
		case XMAX:
			width = value - x;
			break;
		case YMIN:
			delta = value - y;
			y = value;
			if ( mod )
				height -= delta;
			break;
		case YCEN:
			y = value - height / 2;
			break;
		case YMAX:
			height = value - y;
			break;
		case HEIGHT:
			delta = height - value;
			height = value;
			if ( mod )
				y += delta / 2;
			break;
		case WIDTH:
			delta = width - value;
			width = value;
			if ( mod )
				x += delta / 2;
			break;
		default:
			throw new Error( "WtF?" );
		}
	}

	public double get( Bounds object ) {
		switch ( object ) {
		case XMIN:
			return x;
		case XCEN:
			return x + width / 2;
		case XMAX:
			return x + width;
		case YMIN:
			return y;
		case YCEN:
			return y + height / 2;
		case YMAX:
			return y + height;
		case WIDTH:
			return width;
		case HEIGHT:
			return height;
		default:
			throw new Error( "WtF?" );
		}
	}

	/**
	 * Contains entirity of given rectanlge
	 */
	public boolean contains( DRectangle r ) {
		return contains( r.x, r.y ) && contains( r.x + r.width, r.y ) && contains( r.x, r.y + r.height ) && contains( r.x + r.width, r.y + r.height );

	}

	public enum Bounds {
		XMIN, XCEN, XMAX, YMIN, YCEN, YMAX, WIDTH, HEIGHT;

		boolean horizontal;
	}

	public final static Bounds XMIN = Bounds.XMIN, XCEN = Bounds.XCEN, XMAX = Bounds.XMAX, YMIN = Bounds.YMIN, YCEN = Bounds.YCEN, YMAX = Bounds.YMAX, WIDTH = Bounds.WIDTH, HEIGHT = Bounds.HEIGHT;

	public static class FromComparator implements Comparator<DRectangle> {
		Direction dir;

		public FromComparator( Direction l ) {
			this.dir = l;
		}

		@Override
		public int compare( DRectangle arg0, DRectangle arg1 ) {
			switch ( dir ) {
			case Left:
				return Double.compare( arg0.x, arg1.x );
			case Top:
			default:
				return Double.compare( arg0.y, arg1.y );
			}
		}
	}

	public enum Direction {
		Left, Top;
	}

	public final static Direction Left = Direction.Left, Top = Direction.Top;

	public void envelop( double px, double py ) {

		if ( px < x ) {
			width += x - px;
			x = px;
		} else if ( px > x + width )
			width = px - x;

		if ( py < y ) {
			height += y - py;
			y = py;
		} else if ( py > y + height )
			height = py - y;
	}

	public void envelop( Tuple2d pt ) {

		envelop( pt.x, pt.y );
	}

	public static class RectDir {
		public boolean dirX;
		public DRectangle rect;

		public RectDir( boolean dir, DRectangle rect ) {
			this.rect = rect;
			this.dirX = dir;
		}
	}

	public interface WidthGen {
		public List<Double> gen( RectDir in );
	}

	public List<DRectangle> split( boolean dir, WidthGen gen ) {

		List<DRectangle> out = new ArrayList<>();

		List<Double> loc = gen.gen( new RectDir( dir, this ) );

		if ( loc.isEmpty() )
			return out;

		double sum = ( dir ? width : height ) / loc.stream().mapToDouble( xx -> xx ).sum();

		double o = 0;

		for ( double d : loc ) {
			if ( dir )
				out.add( new DRectangle( o + x, y, d * sum, height ) );
			else
				out.add( new DRectangle( x, o + y, width, d * sum ) );
			o += d * sum;
		}

		return out;
	}

	public double distance( Line ray ) {

		double dist = Double.MAX_VALUE;

		if ( contains( ray.start ) || contains( ray.end ) )
			return 0;

		for ( Pair<Point2d, Point2d> l : new ConsecutiveItPairs<>( Arrays.asList( points() ) ) )
			dist = Math.min( dist, new Line( l.first(), l.second() ).distance( ray ) );

		return dist;
	}

	public List<DRectangle> splitX( WidthGen gen ) {
		return split( true, gen );
	}

	public List<DRectangle> splitY( double l ) {
		List<DRectangle> out = new ArrayList();

		if ( l > height ) {
			out.add( new DRectangle( this ) );
		} else {
			out.add( new DRectangle( x, y, width, l ) );
			out.add( new DRectangle( x, y + l, width, height - l ) );
		}

		return out;
	}

	public List<DRectangle> splitY( WidthGen gen ) {
		return split( false, gen );
	}

	public List<DRectangle> splitX( double l ) {

		List<DRectangle> out = new ArrayList();

		if ( l > width ) {
			out.add( new DRectangle( this ) );
		} else {
			out.add( new DRectangle( x, y, l, height ) );
			out.add( new DRectangle( x + l, y, width - l, height ) );
		}

		return out;
	}

	public Line getEdge( Bounds b ) {
		switch ( b ) {
		case XMAX:
			return new Line( get( Bounds.XMAX ), get( Bounds.YMIN ), get( Bounds.XMAX ), get( Bounds.YMAX ) );
		case XMIN:
			return new Line( get( Bounds.XMIN ), get( Bounds.YMIN ), get( Bounds.XMIN ), get( Bounds.YMAX ) );
		case YMAX:
			return new Line( get( Bounds.XMIN ), get( Bounds.YMAX ), get( Bounds.XMAX ), get( Bounds.YMAX ) );
		case YMIN:
			return new Line( get( Bounds.XMIN ), get( Bounds.YMIN ), get( Bounds.XMAX ), get( Bounds.YMIN ) );
		}
		throw new Error();
	}

	public Point2d[] points() {
		return new Point2d[] { new Point2d( x, y ), new Point2d( x, y + height ), new Point2d( x + width, y + height ), new Point2d( x + width, y ), };
	}

	public float heightF() {
		return (float) height;
	}

	public float widthF() {
		return (float) width;
	}

	public float xF() {
		return (float) x;
	}

	public float yF() {
		return (float) y;
	}
	
	public int heightI() {
		return (int) height;
	}
	
	public int widthI() {
		return (int) width;
	}
	
	public int xI() {
		return (int) x;
	}
	
	public int yI() {
		return (int) y;
	}

	public static Comparator<DRectangle> comparator( Bounds bounds, boolean ascending ) {
		return new Comparator<DRectangle>() {

			private double score( DRectangle d ) {
				return d.get( bounds ) * ( ascending ? 1 : -1 );
			}

			@Override
			public int compare( DRectangle o1, DRectangle o2 ) {
				return Double.compare( score( o1 ), score( o2 ) );
			}
		};
	}

	public static Comparator<DRectangle> comparatorArea( boolean ascending ) {

		return new Comparator<DRectangle>() {

			private double score( DRectangle d ) {
				return d.area() * ( ascending ? 1 : -1 );
			}

			@Override
			public int compare( DRectangle o1, DRectangle o2 ) {
				return Double.compare( score( o1 ), score( o2 ) );
			}
		};
	}

	public double distance( Point2d screenPt ) {
		if ( contains( screenPt ) )
			return 0;
		return distanceFromBorder( screenPt );
	}

	public double distanceFromBorder( Point2d screenPt ) {
		Point2d[] pts = points();

		double out = Double.MAX_VALUE;

		for ( int i = 0; i < pts.length; i++ )
			out = Math.min( out, new Line( pts[ i ], pts[ ( i + 1 ) % pts.length ] ).distance( screenPt ) );

		return out;
	}

	public DRectangle normalize( DRectangle rect ) {
		DRectangle out = new DRectangle( rect );

		out.x = ( out.x - this.x ) / this.width;
		out.y = ( out.y - this.y ) / this.height;

		out.width /= this.width;
		out.height /= this.height;

		return out;
	}
	
	public Point2d normalize( Point2d p ) {
		
		return new Point2d ( 
				( p.x - this.x ) / this.width,
				( p.y - this.y ) / this.height );
	}
	
	public Point2d transform( Point2d p ) {
		
		return new Point2d (
				p.x * this.width  + this.x,
				p.y * this.height + this.y  );
	}

	public DRectangle transform( DRectangle rect ) {
		DRectangle out = new DRectangle( rect );

		out.x = ( out.x * this.width ) + this.x;
		out.y = ( out.y * this.height ) + this.y;

		out.width *= this.width;
		out.height *= this.height;

		return out;
	}

	public DRectangle scale( double s ) {
		DRectangle t = new DRectangle( this );
		t.x *= s;
		t.height *= s;
		t.y *= s;
		t.width *= s;
		return t;
	}

	/**
	 * find same position in dest, as we are currently to src. linear tween.
	 */

	public DRectangle transform( DRectangle src, DRectangle dest ) {
		DRectangle out = new DRectangle( this );

		out.x = ( out.x - src.x ) / src.width;
		out.y = ( out.y - src.y ) / src.height;

		out.width /= src.width;
		out.height /= src.height;

		out.x = ( out.x * dest.width ) + dest.x;
		out.y = ( out.y * dest.height ) + dest.y;

		out.width *= dest.width;
		out.height *= dest.height;

		return out;
	}
	
	public static class Enveloper extends DRectangle {

		boolean seen = false;
		
		public void envelop( Tuple2d pt ) {
			
			if (!seen) {
				x = pt.x;
				y = pt.y;
				width = height = 0;
				seen = true;
			}
			else
				super.envelop( pt );
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits( height );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		temp = Double.doubleToLongBits( width );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		temp = Double.doubleToLongBits( x );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		temp = Double.doubleToLongBits( y );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		return result;
	}

	
	@Override
	public boolean equals( Object obj ) {
		
		if (!(obj instanceof DRectangle))
			return false;
		
		DRectangle o = (DRectangle)obj;
		
		return o.x == x && o.y == y && o.width == width && o.height == height;
	}
	
}
