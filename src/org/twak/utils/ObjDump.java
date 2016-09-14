package org.twak.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
 * .obj face output
 * @author twak
 */
public class ObjDump {
/**
 * Really simple anchor that aggregates the points and then outputs the results
 * to a abject file
 *
 * @author twak
 *
 */
	public String name;

	public List<List<Integer>> tris;
	public List<List<Integer>> uvIndexes;
	
	public Map<Tuple3d, Integer> vertexToNo;
	public List<Tuple3d> orderVert;

	
	public Map<Tuple2d, Integer> uvToNo;
	public List<Tuple2d> orderUV;
	
	public ObjDump()
	{
		// reset hashes
		tris = new ArrayList<List<Integer>>();
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
			for (Tuple3d v: orderVert)
				out.write("v "+v.x+" "+v.y+" "+v.z+"\n");
			
			if (orderUV != null)
			for (Tuple2d uv: orderUV)
				out.write("vt "+uv.x+" "+uv.y+"\n");
            
			for (int ti = 0; ti < tris.size(); ti ++)
            {
                out.write("f ");
                for (int ii = 0; ii < tris.get(ti).size(); ii ++)
                    out.write( tris.get(ti).get(ii) + ( uvIndexes == null ? "" : ("/" + uvIndexes.get(ti).get(ii) )) +" ");
                
                out.write("\n");
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
        List<Integer> face = new ArrayList();

        int count = 0;
        for ( Tuple3d uv : lv )
        {
            Tuple3d v = convertVertex( uv );

            if ( vertexToNo.containsKey( v ) )
                face.add( vertexToNo.get( v ) );
            else
            {
                int number = orderVert.size() + 1; // size will be next index
                face.add( number );
                orderVert.add( v );
                vertexToNo.put( v, number );
            }
            count++;
        }
        tris.add( face );
	}
	
	public void addFace(float[][] points, float[][] uvs)
	{
		if (uvs.length != points.length)
			throw new Error();
		
		List<Integer> face = new ArrayList();
		
//		int count = 0;
		for ( float[] xyz : points )
		{
			Tuple3d v = new Point3d( xyz[0], xyz[1], xyz[2] );
			
			if ( vertexToNo.containsKey( v ) ) 
				face.add( vertexToNo.get( v ) );
			else
			{
				int number = orderVert.size() + 1; 
				face.add( number );
				orderVert.add( v );
				vertexToNo.put( v, number );
			}
//			count++;
		}
		tris.add( face );
		
		List<Integer> faceUV = new ArrayList();
		
		if (uvIndexes == null) {
			uvIndexes = new ArrayList<>();
			uvToNo = new HashMap<>();
			orderUV = new ArrayList<>();
		}
		
		for ( float[] uv : uvs )
		{
			Tuple2d v = new Point2d( uv[0], uv[1] );
			
			if ( uvToNo.containsKey( v ) )
				faceUV.add( uvToNo.get( v ) );
			else
			{
				orderUV.add( v );
				int number = orderUV.size();
				faceUV.add( number );
				uvToNo.put( v, number );
			}
		}
		uvIndexes.add(faceUV);
	}

    public void addAll( LoopL<Point3d> faces )
    {
        for (Loop<Point3d> loop : faces)
        {
            List<Point3d> face = new ArrayList();
            
            for (Point3d p : loop)
                face.add( p );

            addFace( face );
        }
    }

    public void addAll( List<List<Point3d>> faces )
    {
        for (List<Point3d> face : faces)
            addFace( face );
    }

}
