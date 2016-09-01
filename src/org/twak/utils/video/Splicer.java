package org.twak.utils.video;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.twak.utils.ImageU;

/**
 *
 * @author twak
 */
public class Splicer {
    public Splicer()
    {
        File root = new File ("C:\\Users\\twak\\Desktop\\22stills\\");
        
        ImageU.rootFile = root;

        List<File> files = new ArrayList( );
        for (File f : root.listFiles() )
            if (f.getName().endsWith("jpg"))
                files.add(f);

        Collections.sort(files, new Comparator<File>()
        {
            @Override
            public int compare(File o1, File o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
            }
        } );

        for ( int i = 0; i < files.size(); i++ ) {
            System.out.println(i);
            BufferedImage p = ImageU.getImage( files.get(i).getName() );
            BufferedImage n = ImageU.getImage( files.get(files.size() -1 - i).getName() );

            BufferedImage out = new BufferedImage (p.getWidth(), p.getHeight(), p.getType() );
            Graphics2D g2 = ((Graphics2D)out.getGraphics());

            g2.drawImage(p, 0, -p.getHeight()/3+50, null);
            
            g2.drawImage(n,
                    0, p.getHeight()/2, p.getWidth(),  (int) (p.getHeight() * (0.5+2./3.)),
                    0, p.getHeight()/3, p.getWidth(), p.getHeight()
                    , null);
            try {
                ImageIO.write(out, "png", 
                        new File(String.format("C:\\Users\\twak\\Desktop\\22stills\\res\\%04d.png", i)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            g2.dispose();

//            break;
        }
    }
    public static void main (String[] args) {
        new Splicer();
    }
}

