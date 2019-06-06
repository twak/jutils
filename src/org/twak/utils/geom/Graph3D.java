package org.twak.utils.geom;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import org.twak.utils.Cache;
import org.twak.utils.collections.MultiMap;

public class Graph3D extends MultiMap<Point3d, Point3d> {

	public void transform( Matrix4d toOrigin ) {
		
		Set<Point3d> seen = new HashSet<>()	;
		
		Cache<Point3d, Point3d> oldNew = new Cache<Point3d, Point3d>() {

			@Override
			public Point3d create( Point3d i ) {
				Point3d out = new Point3d (i);
				toOrigin.transform( out );
				return out;
			}
		};
		
		MultiMap<Point3d, Point3d> n = new MultiMap<>();
		
		for (Point3d k : keySet())
			for (Point3d v : get(k)) 
				n.put( oldNew.get( k ), oldNew.get( v ));
		
		map = n.map;
	}

	public Set<Point3d> getAllDiscrete() {
		Set<Point3d> out = new HashSet<>();
		
		for (Point3d k : keySet()) {
			out.add(k);
			for (Point3d v : get(k)) 
				out.add(v);
		}
		
		return out;
	}

}
