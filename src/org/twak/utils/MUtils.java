package org.twak.utils;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
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

	public static double PI2 = Math.PI / 2, PI3 = Math.PI / 3, PI4 = Math.PI /4, PI6 = Math.PI / 6, TwoPI  = 2 * Math.PI;
	
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
        return interiorAngle( left, right );
    }
    
    public static double absAngleBetween( Tuple2d a, Tuple2d b, Tuple2d c )
    {
    	Vector2d left = new Vector2d( b );
    	Vector2d right = new Vector2d( c );
    	left.sub( a );
    	right.sub( b );
    	return left.angle( right );
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

 	public static double inax( boolean min, double ... vals ) {
 		if (min)
 			return min (vals);
 		else
 			return max(vals);
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
	
	public static double area( Point3d a, Point3d b, Point3d c ) {
		
		Vector3d ab = new Vector3d(b), ac = new Vector3d(c);
		
		ab.sub( a );
		ac.sub( a );
		
		ab.cross( ab, ac );
		
		return 0.5 * ab.length(); 
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

	public static class Frame {
		Matrix4d to, from;
		public Frame (Matrix4d to) {
			this.to = to;
			this.from = new Matrix4d(to);
			this.from.invert();
		}
	} 
	
	public static Frame buildFrame( Vector3d x, Vector3d y, Vector3d z, Point3d ref ) {
		
		x = norm (x);
		y = norm (y);
		z = norm (z);
		
		Matrix4d out = new Matrix4d();

		out.setRow( 0, x.x, x.y, x.z, 0 );
		out.setRow( 1, y.x, y.y, y.z, 0 );
		out.setRow( 2, z.x, z.y, z.z, 0 );

		Point3d start = new Point3d( ref );
		out.transform( start );
		out.m03 = -start.x;
		out.m13 = -start.y;
		out.m23 = -start.z;
		out.m33 = 1;
		
		return new Frame ( out );
	}

	private static Vector3d norm( Vector3d v ) {
		
		if (v.lengthSquared() == 1)
			return v;
		Vector3d out = new Vector3d( v);
		out.normalize();
		
		return out;
	}

	public static Vector2d toXY( Frame mat, Vector3d u2 ) {
		
		Vector3d  o3 = new Vector3d(u2);
		mat.to.transform( o3 );
		return new Vector2d( o3.x, o3.y );
	}

	public static Point3d fromXY( Frame mat, Point2d p ) {
		
		Point3d out = new Point3d(p.x, p.y, 0);
		mat.from.transform( out );
		
		return out;
	}

	public static double L2( int[] a, int[] b ) {
		
		double total = 0;
		
		for (int i = 0; i < a.length; i++) 
			total += Math.pow(b[i]-a[i], 2);
		
		return Math.sqrt( total );
	}

	public static boolean notNull( Object ...objects  ) {
		return Arrays.stream( objects ).filter( o -> o == null ).count() == 0;
	}

}
