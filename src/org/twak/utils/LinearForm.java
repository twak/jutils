package org.twak.utils;

import java.io.Serializable;
import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

/**
 * Matrix form of a line
 * Ax + By = c
 * and all that jazz.
 * @author twak
 */
public class LinearForm implements Serializable
{
    public double x;
    public double y;
    public double c;
    
//    private HashMap map;

    public LinearForm()
    {
        
    }
    
    public LinearForm ( Line in )
    {
        x = in.start.y - in.end.y;
        y = in.end.x - in.start.x;
        
        findC( in.start );
    }
    
    public LinearForm( double x, double y )
    {
        super();
        this.x = x;
        this.y = y;
    }

    public LinearForm( LinearForm l )
    {
        this.y = l.y;
        this.x = l.x;
        this.c = l.c;
    }

    public LinearForm(double x, double y, double c)
    {
        this.y = y;
        this.x = x;
        this.c = c;
    }

    public LinearForm findC( Tuple2d goesThrough )
    {
        c = x * goesThrough.x + y * goesThrough.y;
        return this;
    }

    /**
     * Returns the paramater wrt a collision with a line of parameterized form
     * @param start start location of paramaterized line
     * @param dir direction of paramaterized line
     * @return the paramater wrt a collision with a line of parameterized form or null if no intersection
     */
    public double intersectsP( Point2d start, Vector2d dir )
    {
        LinearForm lf = new LinearForm ( -dir.y, dir.x);
        lf.findC( start );
        
        Point2d p2 = intersect( lf );

        if ( p2 == null )
            return Double.POSITIVE_INFINITY; // ???
        
        if (dir.x > dir.y)
        {
            return (p2.x - start.x) / dir.x;
        }
        else
        {
            return (p2.y - start.y) / dir.y;
        }
    }

    @Override
    public String toString()
    {
        return "(" + x + "," + y + "," + c + ")";
    }
    
    public boolean isParallel( LinearForm other )
    {
        return x * other.y - other.x *y == 0;
    }

    public boolean isAlmostParallel( LinearForm other )
    {
        return Math.abs ( x * other.y - other.x *y) <= 0.0001;
    }
    
    public void perpendicular()
    {
        double tmp = x;
        x = -y;
        y = tmp;
    }
    
    /**
     * ignores c
     */
    public Vector2d unitVector()
    {
        Vector2d out = new Vector2d ( -y, x );
        out.normalize();
        return out;
    }
    
    public double gradient()
    {
        if (y == 0)
            return x > 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        return -x/y;
    }
    
    public Point2d intersect (LinearForm o)
    {
        double det = x * o.y - o.x * y;
        
        if (det == 0)
            return null; // parallel lines!
        
        double x_ = ( o.y * c - y * o.c) / det;
        double y_ = ( x * o.c - o.x * c) / det;
        
        return new Point2d(x_,y_);
    }
    
    /**
     * Checks that the values A and B are approx
     * the same in the given form.
     * @return true if they are the same, else false
     */
    public boolean sameOrientation (LinearForm o)
    {
        return Math.abs(x- o.x) < 0.001 && Math.abs(y-o.y) < 0.001;
    }

    /**
     * Strange code to create a line: start, end 
     * define the range on the chosen axis. The chosen axis
     * is based to avoid /0 errors.
     */
    public Line toLine( double start, double end )
    {
        return new Line (boxBound (start), boxBound(end));
    }

    public Point2d pointOnLine()
    {
        if ( Math.abs( x ) < Math.abs( y ) ) // divide by y
            return new Point2d( 0, c/y );
        else // divide by x
            return new Point2d( c/x , 0 );
    }

    private Point2d boxBound(double v)
    {
        if ( Math.abs( x ) < Math.abs( y ) )
        {
            return new Point2d(v, (c-x*v)/ y );
        }
        else
        {
            return new Point2d( (c - y*v ) / x, v );
        }
    }
    
    public double xAtY(double _y)
    {
        if (x == 0)
            throw new Error("can't do that");
        
        return (c - _y * y)/x;
    }
    
    public double yAtX(double _x)
    {
        if (y == 0)
            throw new Error("can't do that");
        
        return (c - _x * x)/y;
    }

    public double distance( Point2d pt )
    {
        return Math.abs (x*pt.x + y * pt.y - c) / Math.sqrt( x * x + y* y);
    }

    public double angle (LinearForm other)
    {
        return new Vector2d(x, y).angle( new Vector2d (other.x, other.y));
    }

    public Point2d project( Point2d pt )
    {
        double denom = -x*x-y*y; 
        return new Point2d ( (x * (-c) +y * (-y*pt.x + x * pt.y )) / denom, (y*(-c)+x*y*pt.x-x*x*pt.y)/denom);
    }

    /**
     * Which side of the line is this point on?
     * 
     */
    public boolean isInFront( Point2d pt )
    {
        return (x*pt.x + y * pt.y - c) > 0;
    }

    /**
     * Returns an ordering along this line, by paramaterizing hte point
     *
     * Needs thinking about. Just used for ordering points on the same line atm. Will not translate between different lines.
     */
    public double findPParam (Point2d pt_)
    {
        Point2d origin = project(new Point2d());
        Vector2d dir = unitVector();

        Point2d pt = project( pt_ );
        pt.sub(origin);

        if (Math.abs ( dir.x ) > Math.abs ( dir.y) )
            return pt.x/dir.x;
        else
            return pt.y/dir.y;
    }
    
	public Point2d fromPParam(double p) {
		
		Point2d origin = project(new Point2d());
		Point2d dir = new Point2d ( unitVector() );
		
		dir.scale(p);
		dir.add(origin);
		
		return dir;
	}

    public double angleFromNorth()
    {
        return Math.atan2( y, x );
    }

    public int compareAngle (LinearForm other)
    {
        return Double.compare (angleFromNorth(), other.angleFromNorth());
    }

    /**
     * Comparator based on p param
     */
    public int comparePPram( Point2d a, Point2d b )
    {
        if (a == null)
            return -1;
        if (b == null)
            return 1;

        double ap = findPParam( a ),
                bp = findPParam( b );

        return Double.compare (ap, bp);
    }

    public double getOffset( LinearForm other ) {
        return other.c - c;
    }
}
