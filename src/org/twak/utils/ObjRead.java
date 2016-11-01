package org.twak.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point3d;

public class ObjRead {

	public double[][] pts;
	public int[][] faces;

	public ObjRead(File f) {
		BufferedReader br = null;
		try {
			System.out.println("reading " + f);
			br = new BufferedReader(new FileReader(f), 10 * 1024 * 1024);

			String line;
			List<Point3d> points = new ArrayList<Point3d>();
			List<int[]> listFaces = new ArrayList<>();

			while ((line = br.readLine()) != null) {

				try {
					String[] params = line.split(" ");

					if (params[0].equals("v")) {
						points.add(new Point3d(Double.parseDouble(params[1]), Double.parseDouble(params[2]),
								Double.parseDouble(params[3])));
					} else if (params[0].equals("f")) {

						List<Integer> face = new ArrayList<>();

						for (int i = 1; i < params.length; i++) {
							String[] inds = params[i].split("/");
							face.add(Integer.parseInt(inds[0]));
						}

						if (!face.isEmpty()) {
							int[] vals = new int[face.size()];
							for (int i = 0; i < face.size(); i++)
								vals[i] = face.get(i);
							listFaces.add(vals);
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

			Iterator<int[]> iit = listFaces.iterator();

			iit: while (iit.hasNext()) {

				int[] inds = iit.next();

				for (int j = 0; j < inds.length; j++) {

					inds[j]--; // obj is 1 (not 0) indexed

					if (inds[j] >= pts.length || inds.length < 3) { // kill
																	// lines/invalids
						System.out.println("discarding poly w/o points " + inds[j]);
						iit.remove();
						continue iit;
					}
				}
			}

			faces = new int[listFaces.size()][];

			for (int i = 0; i < listFaces.size(); i++) {
				faces[i] = listFaces.get(i);
			}

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
}
