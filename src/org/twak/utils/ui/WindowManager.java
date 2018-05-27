package org.twak.utils.ui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.twak.utils.ImageU;

/**
 *
 * @author twak
 */
public class WindowManager {
    public static String iconName = "nonesuch.png";
    static BufferedImage icon;

    static List<WeakReference<JFrame>> frames = new ArrayList();

    static long last = System.currentTimeMillis();
    
    static String appName = "super happy fun app";
    
    public  static void init (String name, String iconName ) {
    	appName = name;
    	icon = ImageU.cacheResource.get( iconName );
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
    }
    
    public static void setTitle(String title) {
    	appName = title;
    	for (WeakReference<JFrame> r : frames ) {
    		JFrame f  = r.get();
    		if (f != null )
    			f.setTitle( appName );
    	}
    }
    
    public static void register( JFrame frame) {
    	register (frame, "");
    }
    
	public static void register( JFrame frame, String title ) {
		
		frame.setIconImage( getIcon() );
		frame.setTitle( appName+": "+title );
		
		if ( false )
			frame.addWindowFocusListener( new WindowFocusListener() {

			@Override
			public void windowLostFocus( WindowEvent e ) {}

			@Override
			public void windowGainedFocus( WindowEvent e ) {
				
				long nowTime = System.currentTimeMillis();
				
				if (nowTime - last < 500)
					return;
				
				last = nowTime;
				
				for ( WeakReference<JFrame> wrf : frames ) {
					
					
					Frame f = wrf.get();
					if ( f != null && f != e.getComponent() ) {
						f.toFront();
					}
				}
				
				((JFrame)e.getComponent()).toFront();
			}
		} );
		
		frames.add( new WeakReference<JFrame>( frame ));
	}

	public static JFrame frame( String title, JComponent content ) {
		JFrame out = new JFrame( title );
		out.setContentPane( content );
		register (out, title);
		return out;
	}
    

	
    static Image getIcon()
    {
		if ( icon == null ) {
			icon = new BufferedImage( 256, 256, BufferedImage.TYPE_4BYTE_ABGR );
			Graphics2D g = (Graphics2D) icon.getGraphics();

			g.setColor( Rainbow.random() );
			g.fillRect( 0, 0, icon.getWidth(), icon.getHeight() );
			g.setColor( Color.black );
			g.drawString( System.currentTimeMillis() + "", 30, 30 );

			g.dispose();
		}
		
        return icon;
    }

}
