package org.twak.utils;

import java.util.Comparator;

import javax.vecmath.Point3d;

public class Point3dComparator implements Comparator<Point3d>{

	@Override
	public int compare(Point3d o1, Point3d o2) {
		
		int x = Double.compare (o1.x, o2.x);
		
		if (x != 0)
			return x;
		int y = Double.compare (o1.y, o2.y);
		
		if (y != 0)
			return x;
		
		return Double.compare (o1.z, o2.z);
	}

}
