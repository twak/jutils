package org.twak.utils.geom;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.twak.utils.Mathz;

public class Line3d {
	public Point3d start, end;
	
	public Line3d (Point3d s, Point3d e) {
		this.start = s;
		this.end = e;
	}

	public Line3d( double ax, double ay, double az, double bx, double by, double bz ) {
		this (new Point3d( ax, ay, az), new Point3d (bx, by, bz) );
	}

	public Line3d( Line3d l ) {
		this.start = l.start;
		this.end = l.end;
	}

	public Vector3d dir() {
		Vector3d out = new Vector3d(end);
		out.sub(start);
		return out;
	}

	public Line3d move( Vector3d m ) {
		start = new Point3d(start);
		end = new Point3d(end );
		start.add( m );
		end.add( m );
		return this;
	}

	public Point3d[] points() {
		return new Point3d[] {start, end};
	}

	public Point3d fromPPram(double fParam) {
		
		Vector3d v2 = dir();
		v2.scale(fParam);
		v2.add(start);

		return new Point3d(v2);
	}

	/**
     * @return /in {0...1} if within line.
     */
    public double findPPram(Point3d pt)
    {
        Vector3d v1 = dir();
        Vector3d v2 = new Vector3d ( pt );
        v2.sub( start );
        return v1.dot( v2 ) / v1.dot( v1);
    }
    

	public Line3d reverse() {
		Point3d tmp = end;
		end = start;
		start = tmp;
		return this;
	}

	public double angle(Vector3d dir) {
		return dir.angle(dir());
	}

	public double lengthSquared() {
		return dir().lengthSquared();
	}
	
	public double length() {
		return dir().length();
	}

	public double distance( Point3d cen ) {
		return closestPointOn( cen, true ).distance( cen );
	}
	public double distanceSquared( Point3d cen ) {
		return closestPointOn( cen, true ).distanceSquared( cen );
	}
	
	public Point3d closestPointOn(Point3d p, boolean clamp) {
		
		Vector3d ap = new Vector3d(p), ab = new Vector3d(end);
		ap.sub( start );
		ab.sub( start );
		
		Point3d out = new Point3d (ab);
		
		double fac = ap.dot( ab ) / ab.dot(ab);
		
		if (clamp)
			fac = Mathz.clamp( fac, 0, 1 );
		
		out.scale( fac );
		out.add(start);

		return out;
	}

	public static Line3d fromRay( Point3d pt, Vector3d dir ) {
		Point3d end = new Point3d(pt);
		end.add(dir);
		return new Line3d(pt, end);
	}
}