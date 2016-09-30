package org.twak.utils;

public class Anglez {
	
	/**
	 * for (a,b) in -Math.PI to Math.PI, what is the angle between them?
	 */
	public static double dist (double a, double b) {
		
		if (b < a) {
			double tmp = b;
			b = a;
			a = tmp;
		}
		
		double 
			tween = b - a,
			around = Math.PI - b + a + Math.PI;
		
		return Math.min ( tween, around );
		
	}
	
	public static double distHalf (double a, double b) {
		double out = dist(a,b);
		if (out > Math.PI / 2)
			return Math.abs(out - Math.PI);
		else
			return out; 
		
	}

	public static Double plusHalf(double angle) {
		return norm ( angle + Math.PI );
	}
	
	public static double norm (double angle) {
		
		while (angle > Math.PI) // fixme
			angle -= Math.PI;
		while (angle < -Math.PI)
			angle += Math.PI;
		
		return angle;
	}

	public static void main (String[] args) {
		System.out.println( dist (-2.1401295299552623, 1.0012516631378434 ));
	}
	
}
