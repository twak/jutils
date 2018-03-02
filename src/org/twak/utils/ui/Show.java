package org.twak.utils.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author twak
 */
public class Show extends JFrame
{
    
    public Show ( BufferedImage n )
    {
        add (new P(n));
        setSize (n.getWidth()+ 40, n.getHeight()+40);
//        setUndecorated( true );
        WindowManager.register( this );
        setVisible( true );
        
        MouseAdapter ma = new MouseAdapter() {
            
            Point last;
            boolean resize = false;
            
            @Override
            public void mousePressed( MouseEvent e )
            {
                last = e.getLocationOnScreen();
                resize = true;
            }
            
            @Override
            public void mouseDragged( MouseEvent e )
            {
                Point ePt = e.getLocationOnScreen();
                
                if (resize)
                {
                     // resize window
                    Dimension d = Show.this.getSize();
                    Show.this.setSize(  d.width + ePt.x - last.x, d.height + ePt.y - last.y );
                    
                }
                last = ePt;
            }
        };
        
        addMouseMotionListener( ma );
        addMouseListener( ma );
    }

    public Show( BufferedImage current, String title )
    {
        this(current);
        setTitle( title );
    }
    
    public class P extends JComponent
    {
        BufferedImage bi;

        public P( BufferedImage n )
        {
            this.bi = new BufferedImage( n.getWidth(), n.getHeight(), n.getType() );

            Graphics g = bi.getGraphics();
            g.drawImage( n, 0, 0, null );
        }

        @Override
        public void paint( Graphics g )
        {
            g.setColor (Color.cyan);
            g.fillRect( 0, 0, getWidth(), getHeight() );
            g.drawImage( bi, 0, 0, null );
        }
    }
}
