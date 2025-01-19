
package org.twak.utils.geom;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import org.twak.utils.results.LineOnPlane;
import org.twak.utils.results.OOB;

import Jama.Matrix;

/**
 * Definition of a plane in form
 * Ax + By + Cz + D = 0
 * @author twak
 */
public class LinearForm3D implements Cloneable
{
    public double 
            A, //x
            B, //y
            C, //z
            D; //offset

    public LinearForm3D ( LinearForm3D l )
    {
        this.A = l.A;
        this.B = l.B;
        this.C = l.C;
        this.D = l.D;
    }
    
    /**
     * @param normal normal to the plane
     * @param offset a point the plane passes through
     */
    public LinearForm3D (Vector3d normal, Tuple3d offset)
    {
        A = normal.x;
        B = normal.y;
        C = normal.z;
        D = -normal.dot( new Vector3d( offset) );
    }

    public LinearForm3D (double A,double B,double C,double D )
    {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
    }
    
    public LinearForm3D (double A,double B,double C )
    {
    	this.A = A;
    	this.B = B;
    	this.C = C;
    }
    
    public void findD (Tuple3d offset) {
        D = -normal().dot( new Vector3d( offset) );
    }

    public double pointDistance (Tuple3d point)
    {
        double den = Math.sqrt( A*A + B*B + C*C );
        if (den == 0)
            throw new Error("I'm not a plane " + this +"!");
        double num = A * point.x + B * point.y + C * point.z + D;
        return num/den;
    }
    
    public Point3d project (Tuple3d pt) {
    	
    	Point3d out = new Point3d( pt );
    	
    	Vector3d dir = normal();
    	dir.scale ( - pointDistance( pt ) / dir.length() );
    	out.add( dir );
    	
    	return out;
    }
    
    /**
     * @return a Point3d on success, 
     * an OOB on a on-line but out of range, 
     * a LineOnPlane for co-incident (to a tolerance) line/plane
     * and null for "they're fuppin parallel and miles apart"
     */
    
    public Point3d collide (Tuple3d rayOrigin, Tuple3d rayDirection)
    { return collide(rayOrigin, rayDirection, null); }
    
    public Point3d collide (Tuple3d rayOrigin, Tuple3d rayDirection, Double distance)
    {
        Vector3d direction = new Vector3d(rayDirection);
//        direction.normalize();
        
        // erm... dot product?
        double den = A * direction.x + B * direction.y + C *direction.z;
        
        // ray is parallel to plane, check for co-incidence
        if (den == 0)
        {
            if (pointDistance(rayOrigin) < 0.0001) // haven't tested this yet
            {
                LineOnPlane out = new LineOnPlane(rayOrigin, rayDirection, distance == null ? 0 : distance.doubleValue());
                return out;
            }
            
            return null; // not going to collide
        }
        
        double num =-D - A * rayOrigin.x - B * rayOrigin.y - C * rayOrigin.z;
        
        // parameter n is multiple of direction vector away from origin
        double n = num/den;
        
        direction.scale(n);
        direction.add(rayOrigin);
        
        if (n < 0)
            return new OOB(direction); // plane too early
        
        if (distance != null && distance != Double.POSITIVE_INFINITY)
        {
            if (distance < n)
                return new OOB(direction); // plane too late
        }
        
        return new Point3d(direction);
    }

    /**
     * Not a complete collision, compares normals to determine
     * direction of line-intersection
     * 
     * @param other the plane to collide with this one
     * @return null if planes are parallel, direction of collision line otherwise
     */
    public Vector3d collideToVector (LinearForm3D other)
    {
        // find the vector that occurs when both planes collide
        Vector3d n = createNormalVector();
        n.cross(n, other.createNormalVector());
        
        return n;
    }
    
    /**
     * Returns a line or null if the planes are parallel
     * @param other
     * @return
     */
    public Ray3d collide (LinearForm3D other)
    {
        // special solution is cross product of normals
        Vector3d spec = new Vector3d();
        spec.cross( createNormalVector(), other.createNormalVector() );
        
        if (spec.length() == 0) // planes are parallel
        {
            return null;
        }
        
        // particular solution can be found by solving the equation set of the two planes, and another perpendicular plane
        Matrix matrixA = new Matrix  ( new double[][] { { A, B, C }, {other.A, other.B, other.C}, {spec.x, spec.y, spec.z } } );
        // offset of perp plane can be 0, goes through the origin
        Matrix matrixB = new Matrix ( new double[][] {{-D},{-other.D}, {0} } );
        
        Matrix res = matrixA.solve(matrixB);
        return new Ray3d (new Point3d(res.get(0, 0), res.get(1, 0), res.get(2, 0) ), spec);
    }
    
    /**
     * Finds the intersection point of three planes in 3D space.
     */
    public Tuple3d collide(final LinearForm3D b, final LinearForm3D c) {
    	final LinearForm3D a = this;
        
        if (a.hasNaN() || b.hasNaN() || c.hasNaN()) {
            throw new Error();
        }
        
        // Calculate determinant directly
        final double det = a.A * (b.B * c.C - b.C * c.B)
                    - a.B * (b.A * c.C - b.C * c.A)
                    + a.C * (b.A * c.B - b.B * c.A);
                    
        // Check if planes are parallel/coincident (no single intersection point)
        if (Math.abs(det) < 1e-10) {
            return null;
        }
        
        // Use Cramer's rule to solve the system
        final double x = (-a.D * (b.B * c.C - b.C * c.B) +
                    -b.D * (c.B * a.C - c.C * a.B) +
                    -c.D * (a.B * b.C - a.C * b.B)) / det;
                    
        final double y = (-a.D * (c.A * b.C - b.A * c.C) +
                    -b.D * (a.A * c.C - c.A * a.C) +
                    -c.D * (b.A * a.C - a.A * b.C)) / det;
                    
        final double z = (-a.D * (b.A * c.B - c.A * b.B) +
                    -b.D * (c.A * a.B - a.A * c.B) +
                    -c.D * (a.A * b.B - b.A * a.B)) / det;
        
        return new Point3d(x, y, z);
    }
    
    public Vector3d createNormalVector()
    {
        Vector3d out = new Vector3d (A,B,C);
        out.normalize();
        return out;
    }
    
    @Override
    public String toString()
    {
        return A+","+B+","+C+","+D;
    }

    @Override
    public boolean equals( Object obj )
    {
        if (!(obj instanceof LinearForm3D))
            return false;
        
        LinearForm3D other = (LinearForm3D)obj;
        return A == other.A && B == other.B && C == other.C && D == other.D;
    }
    
    public Vector3d normal()
    {
        return new Vector3d (A,B,C);
    }
    

    /**
     * Keeps plane in the same place, but flips the normal
     */
    public void flipNormal()
    {
        A = -A;
        B = -B;
        C = -C;
        D = -D;
    }

    @Override
    public LinearForm3D clone()
    {
        return new LinearForm3D( A,B,C,D );
    }

    public boolean inFront( Tuple3d p )
    {
        return A * p.x + B * p.y + C * p.z + D > 0;
    }

    public boolean hasNaN()
    {
        return
                Double.isNaN( A ) ||
                Double.isNaN( B ) ||
                Double.isNaN( C ) ||
                Double.isNaN( D );
    }

	public static LinearForm3D linePerp( Point3d s, Point3d e ) {
		
		Vector3d dir = new Vector3d(e);
		dir.sub(s);
		dir.normalize();
		
		return new LinearForm3D( dir, e );
	}
}
