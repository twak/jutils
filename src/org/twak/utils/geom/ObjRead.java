package org.twak.utils.geom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Point3d;

public class ObjRead {

	public int[][] faces;
	public int[][] normI;
	public double[][] pts;
	public double[][] norms;

	public ObjRead(File f) {
		BufferedReader br = null;
		try {
			System.out.println("reading " + f);
			br = new BufferedReader(new FileReader(f), 10 * 1024 * 1024);

			String line;
			List<Point3d> points = new ArrayList<Point3d>();
			List<Point3d> normals = new ArrayList<Point3d>();
			List<int[]> listFaces = new ArrayList<>();
			List<int[]> listNorms = new ArrayList<>();

			while ((line = br.readLine()) != null) {

				try {
					String[] params = line.split(" ");

					if (params[0].equals("v")) {
						points.add(new Point3d(Double.parseDouble(params[1]), Double.parseDouble(params[2]),
								Double.parseDouble(params[3])));
						
					} else if (params[0].equals("vn")) {
						normals.add(new Point3d(Double.parseDouble(params[1]), Double.parseDouble(params[2]),
								Double.parseDouble(params[3])));
						
					} else if (params[0].equals("f")) {

						List<Integer> face = new ArrayList<>();
						List<Integer> ni = new ArrayList<>();

						for (int i = 1; i < params.length; i++) {
							String[] inds = params[i].split("/");
							
							face.add(Integer.parseInt(inds[0]));
							
							if (inds.length > 2)
								ni.add(Integer.parseInt(inds[2]));
						}

						if (!face.isEmpty()) {
							listFaces.add(toArray(face));
							listNorms.add(toArray(ni));
						}

					}
				} catch (Throwable th) {
					System.err.println("at line " + line);
					th.printStackTrace(System.err);
				}
			}

			pts = new double[points.size()][3];
			for (int i = 0; i < points.size(); i++) {
				Point3d pt = points.get(i);
				pts[i] = new double[] { pt.x, pt.y, pt.z };
			}
			
			if ( !normals.isEmpty() ) {
				norms = new double[normals.size()][3];
				for ( int i = 0; i < normals.size(); i++ ) {
					Point3d n = normals.get( i );
					norms[ i ] = new double[] { n.x, n.y, n.z };
				}
			}

			Iterator<int[]> iit = listFaces.iterator();
			Iterator<int[]> nit = listNorms.iterator();

			iit: while (iit.hasNext()) {

				int[] inds = iit.next(),
					nnds = nit.next();

				for (int j = 0; j < inds.length; j++) {

					inds[j]--; // obj is 1 (not 0) indexed
					if (nnds.length> 0)
						nnds[j]--;
					
					if (inds[j] >= pts.length || inds.length < 3) { // kill lines/invalids
						System.out.println("discarding poly w/o points " + inds[j]);
						iit.remove();
						nit.remove();
						continue iit;
					}
				}
			}

			faces =  arrayToArray (listFaces);
			normI =  arrayToArray (listNorms);
			

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				System.out.println("done reading " + f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static int[][] arrayToArray( List<int[]> is ) {
		int[][] out = new int[is.size()][];

		for (int i = 0; i < is.size(); i++) 
			out[i] = is.get(i);
		
		return out;
	}

	private static int[] toArray( List<Integer> is ) {
		
		int[] vals = new int[is.size()];
		
		for (int i = 0; i < is.size(); i++)
			vals[i] = is.get(i);
		
		return vals;
	}

	public ObjRead(ObjRead obj) {
		
		this.pts = new double[obj.pts.length][];
		for (int i = 0; i < obj.pts.length; i++)
			this.pts[i] = Arrays.copyOf(obj.pts[i], obj.pts[i].length);
		
		this.faces = new int[obj.faces.length][];
		for (int i = 0; i < obj.faces.length; i++)
			this.faces[i] = Arrays.copyOf(obj.faces[i], obj.faces[i].length);
	}

	public static void main(String[] args) {
		ObjRead or = new ObjRead(new File("/home/twak/Downloads/for_profile.obj"));
		System.out.println("found " + or.faces.length + " faces");
		System.out.println("found " + or.pts.length + " pts");

	}
	
	public List<Point3d> points() {
		return Arrays.stream( pts ).map( x -> new Point3d(x) ).collect( Collectors.toList() );
	}

	public double[] findExtent() {
		
		double[] out = new double[] { 
				Double.MAX_VALUE,  -Double.MAX_VALUE,
				Double.MAX_VALUE,  -Double.MAX_VALUE,
				Double.MAX_VALUE,  -Double.MAX_VALUE };
		
		for (double[] pt : pts) {
			for (int i = 0; i < 3; i++ ) {
				out[i*2+0] = Math.min (out[i*2+0], pt[i]);
				out[i*2+1] = Math.max (out[i*2+1], pt[i]);
			}
		}
		
		return out;
	}
}
