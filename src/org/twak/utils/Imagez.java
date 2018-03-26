package org.twak.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

public class Imagez {

	public static void writeSummary( File file, List<BufferedImage> images ) {

		int width = 0;
		int height = 0;
		for ( BufferedImage vi : images ) {
			width = Math.max( width, vi.getWidth() );
			height += vi.getHeight();
		}

		if ( width == 0 || height == 0 )
			return;

		BufferedImage out = new BufferedImage( width, height, BufferedImage.TYPE_3BYTE_BGR );
		Graphics2D g = out.createGraphics();

		int y = 0;
		for ( BufferedImage vi : images ) {
			g.drawImage( vi, ( width - vi.getWidth() ) / 2, y, vi.getWidth(), vi.getHeight(), null );
			y += vi.getHeight();
		}

		try {
			ImageIO.write( out, Filez.getExtn( file.getName() ), file );
		} catch ( IOException e ) {
			e.printStackTrace();
		}

	}

	public static void writeJPG( BufferedImage rendered, float quality, File f ) {
		
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();

		try {
			JPEGImageWriteParam jpegParams = new JPEGImageWriteParam( null );
			jpegParams.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
			jpegParams.setCompressionQuality( 1f );

			ImageWriter writer = ImageIO.getImageWritersByFormatName( "jpeg" ).next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode( ImageWriteParam.MODE_EXPLICIT ); // Needed see javadoc
			param.setCompressionQuality( quality ); // Highest quality
			writer.setOutput( new FileImageOutputStream( f ) );
			writer.write( null, new IIOImage( rendered, null, null ), jpegParams );
		} catch ( Throwable th ) {
			th.printStackTrace();
		}
	}
	
	
	private static Cache<Integer, float[][]> gaussian = new Cache<Integer, float[][]>() {

		@Override
		public float[][] create( Integer radius ) {

			int size = radius * 2 + 1;
			
	        float[] data = new float[size];
	        
	        float sigma = radius / 3.0f;
	        float twoSigmaSquare = 2.0f * sigma * sigma;
	        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
	        float total = 0.0f;
	        
	        for (int i = -radius; i <= radius; i++) {
	            float distance = i * i;
	            int index = i + radius;
	            data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
	            total += data[index];
	        }
	        
	        for (int i = 0; i < data.length; i++) {
	            data[i] /= total;
	        }        
	        
	        float[][] d2 = new float[size][size];
	        for (int x = 0; x < data.length; x++) 
	        	for (int y = 0; y < data.length; y++) {
	        		d2[x][y] = data[x] * data[y];
	        	}
	        
	        return d2;
		}
		
	};

	private static void toComp( int c, double[] t2, double scale ) {
		t2[0] +=  ( ( c >> 16 ) & 0xFF ) * scale;
		t2[1] +=  ( ( c >> 8  ) & 0xFF ) * scale;
		t2[2] +=  ( ( c       ) & 0xFF ) * scale;
	}
	
    public static BufferedImage blur(int radius, BufferedImage in ) {
        
    	if (radius < 1) 
        	return in;
        
		int size = radius * 2 + 1;
        float[][] filter = gaussian.get(radius);
        
        double[] accm = new double[4];
        BufferedImage out = new BufferedImage( in.getWidth(), in.getHeight(), BufferedImage.TYPE_3BYTE_BGR );
        
        for (int xs = 0; xs < in.getWidth(); xs++)
        	for (int ys = 0; ys < in.getHeight(); ys++) {
        		
        		Arrays.fill( accm, 0 );
        		
        		for (int xf = 0; xf < size; xf++)
        			for (int yf = 0; yf < size; yf ++) {
        				
        				int px = Mathz.clamp (xs + xf - radius, 0, in.getWidth ()-1);
        				int py = Mathz.clamp (ys + yf - radius, 0, in.getHeight()-1);
        				
        				toComp (in.getRGB( px, py ), accm, filter[xf][yf] );
        			}
        		
        		out.setRGB( xs, ys,
        				0xff000000+
        				(((int) accm[0]) << 16) + 
        				(((int) accm[1]) <<  8) + 
        				(((int) accm[2]) <<  0)  );
        	}
        
        return out;
    }

	public static BufferedImage scaleLongest (BufferedImage in, int longest) {
		
		
		double scale; 
		
		if (in.getWidth() > in.getHeight()) {
			scale = longest / (double) in.getWidth() ;
		}
		else {
			scale = longest / (double) in.getHeight();
		}
	
		BufferedImage out = new BufferedImage( 
				(int) (in.getWidth() * scale), 
				(int) (in.getHeight() * scale),
				BufferedImage.TYPE_3BYTE_BGR );
		
		Graphics2D  g = out.createGraphics();
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
//	    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g.drawImage( in, 0, 0, out.getWidth(), out.getHeight(), 0, 0, in.getWidth(), in.getHeight(), null );
		g.dispose();
		
		return out;
	}

}
