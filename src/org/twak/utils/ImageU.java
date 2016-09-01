package org.twak.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author twak
 */
public class ImageU
{
    public static File rootFile = new File( ".");///home/twak/Documents/facades/willard/"  );

    public static BufferedImage clone (BufferedImage in)
    {
        BufferedImage out = new BufferedImage( in.getWidth(), in.getHeight(), in.getType(), null);
        Graphics g = out.getGraphics();
        g.drawImage( in, 0, 0, null );
        g.dispose();
        return out;
    }
    
    public static BufferedImage getFlipH (String name)
    {
        BufferedImage image = getImage( name );
        BufferedImage i2 = new BufferedImage (image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)i2.getGraphics();
        g.drawImage( image, image.getWidth(), 0, -image.getWidth(), image.getHeight(),  null );
        return i2;
    }

    public static BufferedImage getImage (String name)
    {
        try
        {
            return ImageIO.read( new FileInputStream( new File( name ) ) );
        }
        catch ( Throwable ex )
        {
//            ex.printStackTrace();
            return null;
        }
    }

    public static BufferedImage clip (BufferedImage image, int x, int y, int width, int height)
    {
        try
        {
            BufferedImage out = new BufferedImage( width, height, image.getType() );
            Graphics g = out.getGraphics();
            g.drawImage( image, -x, -y, null);
            g.dispose();

            return out;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage getResourceAsImage(String image) {
        try {
            return ImageIO.read(ImageU.class.getResourceAsStream(image));
        } catch (IOException ex) {
            System.out.println("error loading image" + image);
            ex.printStackTrace();
        }
        return null;
    }

    public static Cache<String, BufferedImage> cacheResource = new Cache<String, BufferedImage>() {

        @Override
        public BufferedImage create( String i )
        {
            return getResourceAsImage( i );
        }
    };

    public static Cache<String, BufferedImage> cache = new Cache<String, BufferedImage>() {
        @Override
        public BufferedImage create( String i )
        {
            return getImage( i );
        }
    };

    public static Cache<String, BufferedImage> timeOutCache = new Cache<String, BufferedImage>() {

        Map<String, Long> lastRead = new HashMap();

        @Override
        public BufferedImage get(String in) { // should work on last-written time of image;?
            
            if (in == null)
                return null;
            
            BufferedImage out = cache.get(in);
            
            if (lastRead.get(in) == null || lastRead.get(in) < new File (in).lastModified())
            {
                cache.put(in, out = getImage(in));
                lastRead.put(in, System.currentTimeMillis());
            }

            return out;
        }

        @Override
        public BufferedImage create( String i )
        {
            return getImage( i );
        }
    };
}
