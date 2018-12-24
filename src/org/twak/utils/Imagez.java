package org.twak.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import org.twak.utils.geom.DRectangle;

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
			jpegParams.setCompressionQuality( quality );

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
	
	private static void toComp( int c, double[] t2) {
		t2[0] =  ( ( c >> 16 ) & 0xFF );
		t2[1] =  ( ( c >> 8  ) & 0xFF );
		t2[2] =  ( ( c       ) & 0xFF );
	}
	
	public static int fromComp( double[] t2) {
		
		return 0xff000000 +
				(((int)t2[0]) << 16) + 
				(((int)t2[1]) <<  8) + 
				(((int)t2[2])      ); 
	}
	
	public static int fromComp( int[] t2) {
		
		return 0xff000000 +
				(t2[0] << 16) + 
				(t2[1] <<  8) + 
				(t2[2]      ); 
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

	public static BufferedImage scaleSquare (BufferedImage in, int s ) {
		return scaleSquare( in, s, null, Double.MAX_VALUE, Color.black );
	}
	
	public static BufferedImage scaleSquare (BufferedImage in, int s, double maxScale ) {
		return scaleSquare( in, s, null, maxScale, Color.black );
	}
	
	public static BufferedImage scaleSquare (BufferedImage in, int s, DRectangle pixeLocation, double maxScale, Color bg ) {

		double scale;
		int xpad, ypad;

		if ( in.getWidth() > in.getHeight() ) 
			scale = s / (double) in.getWidth();
		else
			scale = s / (double) in.getHeight();
		
		scale = Math.min (maxScale, scale);
		
		ypad = (int) ( s - in.getHeight() * scale ) / 2;
		xpad = (int) ( s - in.getWidth() * scale ) / 2;

		int nx = (int) ( xpad + in.getWidth() * scale ), ny = (int) ( ypad + in.getHeight() * scale );

		BufferedImage out = new BufferedImage( s, s, BufferedImage.TYPE_3BYTE_BGR );

		Graphics2D g = out.createGraphics();
		g.setColor( bg );
		g.fillRect( 0, 0, s, s );
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
		g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g.drawImage( in, xpad, ypad, nx, ny, 0, 0, in.getWidth(), in.getHeight(), null );
		g.dispose();
		
		if (pixeLocation != null) {
			pixeLocation.x = xpad; pixeLocation.y = ypad; pixeLocation.width = nx-xpad; pixeLocation.height = ny-ypad;
		}
		
		return out;
	}
	
	public static BufferedImage join ( BufferedImage a, BufferedImage b ) {
		BufferedImage both = new BufferedImage( a.getWidth() + b.getWidth(), a.getHeight(), BufferedImage.TYPE_3BYTE_BGR );
		Graphics2D g = (Graphics2D) both.getGraphics();
		g.drawImage( a, 0, 0, a.getWidth(), a.getHeight(), 0, 0, a.getWidth(), a.getHeight(), null );
		g.drawImage( b, a.getWidth(), 0, a.getWidth() + b.getWidth(), b.getHeight(), 0, 0, b.getWidth(), b.getHeight(), null );
		g.dispose();
		return both;
	}

	public static BufferedImage scaleTo( BufferedImage bi, int x, int y ) {
		
		BufferedImage out = new BufferedImage( x, y, BufferedImage.TYPE_3BYTE_BGR );
		Graphics2D  g = out.createGraphics();
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
		g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g.drawImage( bi, 0, 0, x, y, 0, 0, bi.getWidth(), bi.getHeight(), null );
		g.dispose();
		
		return out;
		
	}

	static Random randy = new Random();
	public static void gaussianNoise( BufferedImage b, double scale ) {
		
		double[] tmp = new double[3];
		
		for (int x = 0; x < b.getWidth(); x++)
			for (int y = 0; y < b.getHeight(); y++) {
				
				toComp( b.getRGB( x, y ), tmp );
				
				for (int i = 0; i < tmp.length; i++) 
					tmp[i] = Mathz.clamp( tmp[i]+randy.nextGaussian() * 255 * scale, 0, 255 );
				
				b.setRGB( x, y, fromComp ( tmp ) ); 
			}
	}

	public static BufferedImage padTo( BufferedImage in, DRectangle mask, int x, int y, Color bg ) {
		
		if (in.getWidth() > x || in.getHeight() > y)
			throw new Error();
		
		BufferedImage out = new BufferedImage( x, y, in.getType() );
		
		Graphics2D g = out.createGraphics();
		
		g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
		g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		
		int xd = (x - in.getWidth ())/2,
			yd = (y - in.getHeight())/2;

		g.setColor( bg );
		g.fillRect( 0, 0, x, y );
		
		g.drawImage( in, xd, yd,in.getWidth(), in.getHeight(), null );
		g.dispose();
		
		if (mask != null ){
			mask.x += xd;
			mask.y += yd;
		}
		
		
		return out;
	}

	public static BufferedImage cropShared( BufferedImage src, DRectangle w ) {
		return
				src.getSubimage(  
					(int) w.x, 
					(int) w.y,
					(int) w.width , 
					(int) w.height );
	}
	
	// https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage?rq=1
	public static BufferedImage clone(BufferedImage source){
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
	    Graphics2D g = b.createGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return b;
	}
	
	public static BufferedImage read( File f ) {
		try {
			return ImageIO.read( f );
		} catch ( Throwable th ) {
			return new BufferedImage( 1, 1, BufferedImage.TYPE_3BYTE_BGR );
		}
	}
}
