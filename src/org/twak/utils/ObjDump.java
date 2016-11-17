package org.twak.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;

/**
 * @author twak
 */
public class ObjDump {

	public String name;

	public Material currentMaterial = null;
	
	public MultiMap<Material, Face> material2Face = new MultiMap<>();
	
	
	// configure output
	public boolean writeMtlFile = true;
	
	public static class Material {
		String name;
		public String filename;
		public int w, h;
		
		public Material(String filename, String name, int w, int h) {
			this.name = name;
			this.filename = filename;
			this.w = w;
			this.h = h;
		}

		@Override
		public boolean equals(Object m) {
			return filename.equals( ((Material)m ).filename);
		}
		
		@Override
		public int hashCode() {
			return filename.hashCode();
		}
	}
	
	public static class Face {		
		public List<Integer> vtIndexes = new ArrayList<>();
		public List<Integer> uvIndexes = null;//new ArrayList<>();
		public List<Integer> normIndexes = null;//new ArrayList<>();
	}
	
	public Map<Tuple3d, Integer> vertexToNo;
	public List<Tuple3d> orderVert;

	
	public Map<Tuple2d, Integer> uvToNo;
	public Map<Tuple3d, Integer> normToNo;
	public List<Tuple2d> orderUV;
	public List<Tuple3d> orderNorm;
	
	public ObjDump()
	{
		// reset hashes
		material2Face = new MultiMap<>();
		vertexToNo = new LinkedHashMap<Tuple3d, Integer>();
		orderVert = new ArrayList<Tuple3d>();
	}

    public ObjDump(ObjDump in) {
    	this.name = in.name;
    	this.currentMaterial = in.currentMaterial;
    	this.vertexToNo = new HashMap ( in.vertexToNo );
    	this.orderVert = new ArrayList( in.orderVert );
 		this.uvToNo = new HashMap ( in.vertexToNo );
 		this.orderUV = new ArrayList( in.orderUV);
 		
 		this.material2Face = new MultiMap<Material, Face>( in.material2Face );
	}

	public void allDone( File output )
	{
		try
		{
            if (output.getParentFile() != null)
            	output.getParentFile().mkdirs();
            
			BufferedWriter out = new BufferedWriter(new FileWriter(output));
			
			if (writeMtlFile && currentMaterial != null) {
				System.out.println("writing material file");
				StringBuffer materialFile = new StringBuffer();
				for (Material mat : material2Face.keySet()) {

					materialFile.append("newmtl " + mat.name + "\n");
					materialFile.append("Ka 1.000 1.000 1.000\n");
					materialFile.append("Kd 1.000 1.000 1.000\n");
					materialFile.append("Ks 0.000 0.000 0.000\n");
					materialFile.append("d 1.0\n");
					materialFile.append("illum 2\n");
					materialFile.append("map_Ka "+mat.filename+"\n");
					materialFile.append("map_Kd "+mat.filename+"\n");
					materialFile.append("map_Ks "+mat.filename+"\n\n");
				}
				
				String matFile = output.getName().substring(0,output.getName().indexOf('.')) + ".mtl";
				out.write("mtllib "+matFile+"\n" );
				
					Files.write(new File (output.getParentFile(), matFile).toPath(), materialFile.toString().getBytes());
			}
			
			int c = 0;
			for (Tuple3d v: orderVert) {
				out.write("v "+v.x+" "+v.y+" "+v.z+"\n");
				if (c++ % 10000 == 0)
					System.out.println("written verts "+c+"/"+orderVert.size());
			}
			
			if ( orderUV != null )
				for ( Tuple2d uv : orderUV )
					out.write( "vt " + uv.x + " " + uv.y + "\n" );
			
			if ( orderNorm != null )
				for ( Tuple3d norm : orderNorm )
					out.write( "vn " + norm.x + " " + norm.y + " " + norm.z + "\n" );
            
			for (Material mat : material2Face.keySet()) {
				if (mat != null) {
					out.write("usemtl " + mat.name+"\n");
					out.write("o " + mat.name+"\n"); // every object has a different material...right?
				}
				
				for (Face f : material2Face.get(mat)) {
					
					out.write("f ");
					for (int ii = 0; ii < f.vtIndexes.size(); ii ++)
						out.write( ( f.vtIndexes.get(ii) + 1) +  /** obj's first element is 1 */
								( f.uvIndexes   == null ? "" : ("/" + ( f.uvIndexes.get(ii) + 1 ) )) +
								( f.normIndexes == null ? "" : ("/" + ( f.normIndexes.get(ii) + 1 ) )) +" " ) ;
					
					out.write("\n");
				}
			}
			out.close();
			System.out.println("done!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

    /**
     * Extension hook
     */
    public Tuple3d convertVertex(Tuple3d pt)
    {
        return pt;
    }

	public void addFace(List<Point3d> lv)
	{
		Face face = new Face();
		material2Face.put(currentMaterial, face);
		

        for ( Tuple3d uv : lv )
        {
            Tuple3d v = convertVertex( uv );

            if ( vertexToNo.containsKey( v ) )
                face.vtIndexes.add( vertexToNo.get( v ) );
            else
            {
                int number = orderVert.size(); // size will be next index
                face.vtIndexes.add( number );
                orderVert.add( v );
                vertexToNo.put( v, number );
            }
        }
	}
	
	public void addFace(double[][] points, double[][] uvs, double[][] norms) {
		if (uvs.length != points.length)
			throw new Error();
		
		Face face = new Face();
		material2Face.put(currentMaterial, face);

		for ( double[] xyz : points )
		{
			Tuple3d v = new Point3d( xyz[0], xyz[1], xyz[2] );
			
			if ( vertexToNo.containsKey( v ) ) 
				face.vtIndexes.add( vertexToNo.get( v ) );
			else
			{
				int number = orderVert.size(); 
				face.vtIndexes.add( number );
				orderVert.add( v );
				vertexToNo.put( v, number );
			}
//			count++;
		}
		
		if ( uvs != null ) {
			face.uvIndexes = new ArrayList<>();

			if ( uvToNo == null ) {
				uvToNo = new HashMap<>();
				orderUV = new ArrayList<>();
			}

			for ( double[] uv : uvs ) {
				Tuple2d v = new Point2d( uv[ 0 ], uv[ 1 ] );

				if ( uvToNo.containsKey( v ) )
					face.uvIndexes.add( uvToNo.get( v ) );
				else {
					int number = orderUV.size();
					orderUV.add( v );
					face.uvIndexes.add( number );
					uvToNo.put( v, number );
				}
			}
		}
		
		if ( norms != null ) {
			face.normIndexes = new ArrayList<>();

			if ( normToNo == null ) {
				normToNo = new HashMap<>();
				orderNorm = new ArrayList<>();
			}

			for ( double[] n : norms ) {
				Tuple3d v = new Point3d( n[ 0 ], n[ 1 ], n[ 2 ] );

				if ( normToNo.containsKey( v ) )
					face.normIndexes.add( normToNo.get( v ) );
				else {
					int number = orderNorm.size();
					orderNorm.add( v );
					face.normIndexes.add( number );
					normToNo.put( v, number );
				}
			}
		}
	}
	
	public void addFace(float[][] points, float[][] uvs, float[][] norms) {
		addFace(toDouble(points), toDouble(uvs), toDouble(norms));
	}
	
	private static double[][] toDouble(float[][] in) {
		
		if (in == null)
			return null;
		
		double[][] out = new double[in.length][];
		for(int i =0; i < out.length;i++){
			out[i] = new double[in[i].length];
			for (int j = 0; j < out[i].length; j++)
				out[i][j] = in[i][j];
		}
		return out;
	}
	
	public void validateIndicies() {
		for (Material m : material2Face.keySet())
			for (Face f : material2Face.get(m)) {
				for (int i : f.uvIndexes)
					if (i >= orderUV.size() || i < 0)
						System.out.println("invalid uv index " + i);
				
				for (int i : f.vtIndexes)
					if (i >= orderVert.size() || i < 0)
						System.out.println("invalid vert index " + i);
				
			}
	}

    public void addAll( LoopL<Point3d> faces )
    {
        for (Loop<Point3d> loop : faces)
        {
            List<Point3d> face = new ArrayList<Point3d>();
            
            for (Point3d p : loop)
                face.add( p );

            addFace( face );
        }
    }
    
    /**
     * Sets the texture map for following verts
     */
	public void setCurrentTexture(String textureFile, int w, int h) {

		currentMaterial = new Material (textureFile, textureFile.replace(".", "_"), w, h );
		
	}

    public void addAll( List<List<Point3d>> faces )
    {
        for (List<Point3d> face : faces)
            addFace( face );
    }
}
