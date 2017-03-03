package org.twak.utils;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.geom.Point2D;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 *
 * @author twak
 */
public class MUtils
{

	public static final Vector2d UP = new Vector2d(0,1);

	public static final Tuple3d X_POS = new Vector3d(1,0,0);
	public static final Tuple3d Y_POS = new Vector3d(0,1,0);
	public static final Tuple3d Z_POS = new Vector3d(0,0,1);

	public static double PI2 = Math.PI / 2, TwoPI  = 2 * Math.PI;
	
    public static Color[] rainbow = new Color[]
    {
        Color.red,
        Color.orange,
        Color.yellow,
        Color.green,
        Color.cyan,
        Color.blue,
        Color.magenta
    };

    public static double clamp( double a, double min, double max )
    {
        return a < min ? min : a > max ? max : a;
    }
    
    public static float clamp( float a, float min, float max )
    {
    	return a < min ? min : a > max ? max : a;
    }

    public static int clamp( int a, int min, int max )
    {
        return a < min ? min : a > max ? max : a;
    }
    
	public static double clamp01( double a ) {
		return clamp( a, 0, 1 );
	}

	public static double signedAngle( Vector2d a, Vector2d b ) {

        if ( cross( b, a ) >= 0 )
        	return a.angle( b );
        else
        	return -a.angle( b );
	}
    
    public static double interiorAngle( Vector2d a, Vector2d b )
    {
        if ( cross( b, a ) >= -10E-15 )
            return PI - a.angle( b );
        else
            return 2 * PI - a.angle( b );
    }

    public static double interiorAngleBetween( Tuple2d a, Tuple2d b, Tuple2d c )
    {
        Vector2d left = new Vector2d( b );
        Vector2d right = new Vector2d( c );
        left.sub( a );
        right.sub( b );
//        left.normalize();
//        right.normalize();
        return interiorAngle( left, right );
    }

    public static double cross( Tuple2d a, Tuple2d b )
    {
        return a.x * b.y - a.y * b.x;
    }

    public static boolean inRange( double query, double min, double max )
    {
        return query >= min && query <= max;
    }
    
    public static boolean inRangeTol( double query, double value, double tol )
    {
    	return query >= value - tol && query <= value + tol;
    }

    public static double min (double ... vals)
    {
        double min = Double.MAX_VALUE;
        for (double v : vals)
        {
                min = Math.min (v, min);
        }
        return min;
    }
    
    public static float min (float ... vals)
    {
    	float min = Float.MAX_VALUE;
    	for (float v : vals)
    		min = Math.min (v, min);
    	
    	return min;
    }
    
    public static double max (double ... vals)
    {
        double max = -Double.MAX_VALUE;
        for (double v : vals)
        {
                max = Math.max (v, max);
        }
        return max;
    }
    
    public static int min (int ... vals)
    {
        int min = Integer.MAX_VALUE;
        for (int v : vals)
        {
                min = Math.min (v, min);
        }
        return min;
    }
    
     public static int max (int ... vals)
    {
        int max = Integer.MIN_VALUE;
        for (int v : vals)
        {
                max = Math.max (v, max);
        }
        return max;
    }

    public static Integer clip( int a, int min, int max )
    {
        return a < min ? null : a > max ? null : a;
    }

    public static Point2D.Double toAWT(Point2d pt)
    {
        return new Point2D.Double ( pt.x, pt.y );
    }

    public static Point2d toVecmath(Point2D.Double pt)
    {
        return new Point2d ( pt.x, pt.y );
    }

	public static double[] minMax(double...ds) {
		return new double[] {min(ds), max(ds) };
	}
	
	public static double area (Point2d a, Point2d b, Point2d c) {
		return 0.5 * (
			   -b.x * a.y + 
				c.x * a.y + 
				a.x * b.y - 
				c.x * b.y - 
				a.x * c.y + 
				b.x * c.y
				);
	}

	public static boolean order( double ... pairs ) {
		for (int i =0; i < pairs.length; i+=2) {
			double score = pairs[i] - pairs[i+1];
			if (score < 0)
				return false;
			else if (score > 0)
				return true;
		}
		return false;
	}

}
