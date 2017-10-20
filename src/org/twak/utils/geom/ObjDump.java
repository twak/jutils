package org.twak.utils.geom;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;

import org.twak.utils.Filez;
import org.twak.utils.collections.Loop;
import org.twak.utils.collections.LoopL;
import org.twak.utils.collections.MultiMap;

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
		public String name;
		public String filename;
		public int w, h;
		
		public
		double[] diffuse  = new double[] {1,1,1},
				 ambient  = new double[] {1,1,1},
				 specular = new double[] {1,1,1};
		
		public Material(){}
		
		public Material(String filename, String name, int w, int h) {
			this.name = name;
			this.filename = filename;
			this.w = w;
			this.h = h;
		}

		public Material( String name, double[] ambient, double[] diffuse ) {
			
			this.ambient = ambient;
			this.diffuse = diffuse;
			this.name = name +"_" +ambient[0]+ambient[1]+ambient[2];
		}

		public Material(Material mat ) {
			
			this.name = mat.name;
			this.filename = mat.filename;
			this.w = mat.w;
			this.h = mat.h;
			this.diffuse = mat.diffuse;
			this.ambient = mat.ambient;
			this.specular = mat.specular;
		}

		public Material( String name ) {
			this.name = name;
		}

		@Override
		public boolean equals(Object m) {
			
			Material oo = (Material)m;
			
			return equal ( oo.filename, filename  ) && equal (oo.name, name); 
		}

		private static boolean equal (String a, String b) {
			if (a == null && b == null)
				return true;
			else if (a == null || b == null)
				return false;
			return a.equals (b);
		}
		
		@Override
		public int hashCode() {
			return filename == null ? name.hashCode() : filename.hashCode();
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
		orderNorm = new ArrayList<Tuple3d>();
		orderUV   = new ArrayList<Tuple2d>();
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

	public void dump( File output )
	{
		try
		{
            if (output.getParentFile() != null)
            	output.getParentFile().mkdirs();
            
			BufferedWriter out = new BufferedWriter(new FileWriter(output));
			
			if (writeMtlFile && currentMaterial != null) {
				System.out.println("writing material file");
				StringBuffer materialFile = new StringBuffer();
				for ( Material mat : material2Face.keySet() ) {

					materialFile.append( "newmtl " + mat.name + "\n" );
					materialFile.append( "Ka " + mat.ambient[ 0 ] + " " + mat.ambient[ 1 ] + " " + mat.ambient[ 2 ] + "\n" );
					materialFile.append( "Kd " + mat.diffuse[ 0 ] + " " + mat.diffuse[ 1 ] + " " + mat.diffuse[ 2 ] + "\n" );
					materialFile.append( "Ks 0.000 0.000 0.000\n" );
					materialFile.append( "d 1.0\n" );
					materialFile.append( "illum 1\n" );
					if ( mat.filename != null ) {
						materialFile.append( "map_Kd " + mat.filename + "\n" );
					}
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
								( f.uvIndexes   == null ? (f.normIndexes==null?"":"/") : ("/" + ( f.uvIndexes.get(ii) + 1 ) )) +
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

	public void addFace(List<Point3d> lv) {
		addFace (lv, null, null);
	}

	public void addFace( List<Point3d> fVerts, List<Point3d> fNorms, List<Point2d> fUVs ) {
		Face face = new Face();
		material2Face.put(currentMaterial, face);
		
		if ( fVerts == null || 
				(fNorms != null && fNorms.size() != fVerts.size() ) ||
				(fUVs   != null && fUVs  .size() != fVerts.size() ) )
			throw new Error("bad length input");

        for ( Tuple3d vert : fVerts )
        {
            Tuple3d v = convertVertex( vert );

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
        
        if (fNorms != null) {
        	
        	normToNo = new HashMap<>();
        	face.normIndexes = new ArrayList<>();
        	
        	for ( Tuple3d norm: fNorms )
        	{
        		if ( normToNo.containsKey( norm ) )
        			face.normIndexes.add( normToNo.get( norm ) );
        		else
        		{
        			int number = orderNorm.size(); // size will be next index
        			face.normIndexes.add( number );
        			orderNorm.add( norm );
        			normToNo.put( norm, number );
        		}
        	}
        }
        
        if (fUVs != null) {
        	
        	uvToNo = new HashMap<>();
        	face.uvIndexes = new ArrayList<>();
        	
        	for ( Tuple2d uv: fUVs )
        	{
        		if ( uvToNo.containsKey( uv ) )
        			face.uvIndexes.add( uvToNo.get( uv ) );
        		else {
        			
        			int number = orderUV.size(); // size will be next index
        			face.uvIndexes.add( number );
        			orderUV.add( uv );
        			uvToNo .put( uv, number );
        		}
        	}
        }
        
	}
	
	public void addFace(double[][] points, double[][] uvs, double[][] norms) {
		if (uvs   != null && uvs.length   != points.length ||
			norms != null && norms.length != points.length )
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
     * Sets the texture map for following verts. 
     */
	public void setCurrentTexture(String textureFile, int w, int h) {
		currentMaterial = new Material (textureFile, textureFile.replace(".", "_"), w, h );
	}

	public void setCurrentMaterial( Color color, double ambientScale ) {
		setCurrentMaterial( "mat", color, ambientScale );
	}
	
	public void setCurrentMaterial( String namePrefix, Color color, double ambientScale ) {
		
		float[] res = new float[4];
		color.getComponents( res );
		double[] resD = new double[] {res[0], res[1], res[2], res[3]};
		
		currentMaterial = new Material (
				namePrefix,
				new double[] {
						(resD[0] * ambientScale),
						(resD[1] * ambientScale),
						(resD[2] * ambientScale) }, 
				resD  );
	}
	
    public void addAll( List<List<Point3d>> faces )
    {
        for (List<Point3d> face : faces)
            addFace( face );
    }

	public void addAll( ObjRead read ) {
		
		for ( int f = 0; f < read.faces.length; f ++ ) {
			
			List<double[]> pO  = new ArrayList<>(); 
//			List<double[]> uO   = new ArrayList<>(); 
			List<double[]> nO = new ArrayList<>(); 
			
			for (int i = 0; i < read.faces[f].length; i++) {
				
				pO.add(read.pts[read.faces[f][i]]);
				
				if (read.norms != null)
					nO.add(read.norms[ read.normI[f][i] ]);
				
			}
				
			addFace( 
					pO.toArray( new double[pO.size()][] ),
					null,
//					uO.toArray( uO.isEmpty() ? null : new double[uO.size()][] ),
					nO.toArray( nO.isEmpty() ? null : new double[nO.size()][] )
				);
		}
	}
	
	public ObjDump(File file) { 
		this();
		
		BufferedReader br = null;
		currentMaterial = null;
		try {
			System.out.println("reading " + file);
			br = new BufferedReader(new FileReader(file), 10 * 1024 * 1024);

			String line;
			
			while ((line = br.readLine()) != null) {

				try {
					String[] params = line.split(" ");

					if (params[0].equals("v")) 
						orderVert.add(new Point3d( Double.parseDouble(params[1]), Double.parseDouble(params[2]), Double.parseDouble(params[3])));
					else if (params[0].equals("vn")) 
						orderNorm.add(new Point3d( Double.parseDouble(params[1]), Double.parseDouble(params[2]), Double.parseDouble(params[3])));
					else if (params[0].equals("vt")) 
						orderUV.add( new Point2d( Double.parseDouble( params[ 1 ] ), Double.parseDouble( params[ 2 ] ) ) );
					else if (params[0].equals("usemtl")) 
						currentMaterial = readMaterial (file, params[1]);
					else if (params[0].equals("f")) {

						Face face = new Face();
						
						for (int i = 1; i < params.length; i++) {
							String[] inds = params[i].split("/");
							
							face.vtIndexes.add(Integer.parseInt(inds[0])-1);
							
							if (inds.length > 1) {
								if (face.uvIndexes == null)
									face.uvIndexes = new ArrayList<>();
								face.uvIndexes.add(Integer.parseInt(inds[1])-1);
							}
							
							if (inds.length > 2) {
								if (face.normIndexes == null)
									face.normIndexes = new ArrayList<>();
								face.normIndexes.add(Integer.parseInt(inds[2])-1);
							}
						}

						if (!face.vtIndexes.isEmpty())
							material2Face.put (currentMaterial, face);
					}
				} catch (Throwable th) {
					System.err.println("at line " + line);
					th.printStackTrace(System.err);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				System.out.println("done reading " + file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	Map<String, Material> namedMaterials = new HashMap<>();
	File namedMatFile;
	
	private Material readMaterial( File f, String name ) {

		File mFile = new File( f.getParentFile(), Filez.stripExtn( f.getName() ) + ".mtl" );
		BufferedReader br = null;

		if ( namedMatFile != mFile ) {
			try {
				br = new BufferedReader( new FileReader( mFile ), 10 * 1024 * 1024 );

				String line;

				Material mat = new Material();
				
				while ( ( line = br.readLine() ) != null ) {
					String[] params = line.split( " " );
					if ( params[ 0 ].equals( "newmtl" ) )
						namedMaterials.put( params[ 1 ], mat = new Material(params[1]) );
					else if ( params[ 0 ].equals( "Ka" ) )
						mat.ambient = new double[] { Double.parseDouble( params[ 1 ] ), Double.parseDouble( params[ 2 ] ), Double.parseDouble( params[ 3 ] ) };
					else if ( params[ 0 ].equals( "Kd" ) )
						mat.diffuse = new double[] { Double.parseDouble( params[ 1 ] ), Double.parseDouble( params[ 2 ] ), Double.parseDouble( params[ 3 ] ) };
					else if ( params[ 0 ].equals( "Ks" ) )
						mat.specular = new double[] { Double.parseDouble( params[ 1 ] ), Double.parseDouble( params[ 2 ] ), Double.parseDouble( params[ 3 ] ) };
					//				else if (params[0].equals("map_Ka"))
					//					currentMaterial.filename = params[0];
					else if ( params[ 0 ].equals( "map_Kd" ) )
						mat.filename = params[ 1 ];
					//				else if (params[0].equals("map_Ks"))
					//					currentMaterial.filename = params[0];
				}
			} catch ( Throwable th ) {
				th.printStackTrace();
			} finally {
				if ( br != null )
					try {
						br.close();
					} catch ( IOException e ) {
						e.printStackTrace();
					}
			}
		}

		return namedMaterials.get( name );
	}

	public void setCurrentMaterial( Material mat ) {
		currentMaterial = mat;
	}
}
