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

	public String currentMaterial = null;
	
	public MultiMap<String, Face> material2Face = new MultiMap<>();
	
	private static class Face {		
		
		public List<Integer> vtIndexes = new ArrayList<>();
		public List<Integer> uvIndexes = null;//new ArrayList<>();
	}
	
	public Map<Tuple3d, Integer> vertexToNo;
	public List<Tuple3d> orderVert;

	
	public Map<Tuple2d, Integer> uvToNo;
	public List<Tuple2d> orderUV;
	
	public ObjDump()
	{
		// reset hashes
		material2Face = new MultiMap<>();
		vertexToNo = new LinkedHashMap<Tuple3d, Integer>();
		orderVert = new ArrayList<Tuple3d>();
		// ask for file name, bootstraping for sity frame
	}

    public void allDone( File output )
	{
		try
		{
            if (output.getParentFile() != null)
            	output.getParentFile().mkdirs();
            
			BufferedWriter out = new BufferedWriter(new FileWriter(output));
			
			System.out.println("writing material file");
			
			if (materialFile != null) {
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
			
			if (orderUV != null)
			for (Tuple2d uv: orderUV)
				out.write("vt "+uv.x+" "+uv.y+"\n");
            
			for (String mat : material2Face.keySet()) {
				if (mat != null) {
					out.write("usemtl " + mat+"\n");
					out.write("o " + mat+"\n"); // every object has a different material...right?
				}
				
				for (Face f : material2Face.get(mat)) {
					
					out.write("f ");
					for (int ii = 0; ii < f.vtIndexes.size(); ii ++)
						out.write( f.vtIndexes.get(ii) + ( f.uvIndexes == null ? "" : ("/" + f.uvIndexes.get(ii) )) +" ");
					
					out.write("\n");
				}
			}
			out.close();
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
                int number = orderVert.size() + 1; // size will be next index
                face.vtIndexes.add( number );
                orderVert.add( v );
                vertexToNo.put( v, number );
            }
        }
	}
	
	public void addFace(float[][] points, float[][] uvs)
	{
		if (uvs.length != points.length)
			throw new Error();
		
		Face face = new Face();
		material2Face.put(currentMaterial, face);

		for ( float[] xyz : points )
		{
			Tuple3d v = new Point3d( xyz[0], xyz[1], xyz[2] );
			
			if ( vertexToNo.containsKey( v ) ) 
				face.vtIndexes.add( vertexToNo.get( v ) );
			else
			{
				int number = orderVert.size() + 1; 
				face.vtIndexes.add( number );
				orderVert.add( v );
				vertexToNo.put( v, number );
			}
//			count++;
		}
		
		face.uvIndexes = new ArrayList<>();
		
		if (uvToNo == null) {
			uvToNo = new HashMap<>();
			orderUV = new ArrayList<>();
		}
		
		for ( float[] uv : uvs )
		{
			Tuple2d v = new Point2d( uv[0], uv[1] );
			
			if ( uvToNo.containsKey( v ) )
				face.uvIndexes.add( uvToNo.get( v ) );
			else
			{
				orderUV.add( v );
				int number = orderUV.size();
				face.uvIndexes.add( number );
				uvToNo.put( v, number );
			}
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
	StringBuffer materialFile = null;
	private Set<String> seenTextures = new HashSet<String>();

	public void setCurrentTexture(String textureFile) {

		if (materialFile == null)
			materialFile = new StringBuffer();

		currentMaterial = textureFile.replace(".", "_");

		if (!seenTextures.contains(textureFile)) {
			seenTextures.add(textureFile);

			materialFile.append("newmtl " + currentMaterial + "\n");
			materialFile.append("Ka 1.000 1.000 1.000\n");
			materialFile.append("Kd 1.000 1.000 1.000\n");
			materialFile.append("Ks 0.000 0.000 0.000\n");
			materialFile.append("d 1.0\n");
			materialFile.append("illum 2\n");
			materialFile.append("map_Ka "+textureFile+"\n");
			materialFile.append("map_Kd "+textureFile+"\n");
			materialFile.append("map_Ks "+textureFile+"\n\n");
		}

	}

    public void addAll( List<List<Point3d>> faces )
    {
        for (List<Point3d> face : faces)
            addFace( face );
    }

}
