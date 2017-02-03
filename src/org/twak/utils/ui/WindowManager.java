package org.twak.utils.ui;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * @author twak
 */
public class WindowManager {
    public static String iconName = "nonesuch.png";
    static BufferedImage icon;

    static List<WeakReference<JFrame>> frames = new ArrayList();

    public static void register (JFrame frame)
    {
        frame.setIconImage(getIcon());
        frame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
               for (WeakReference<JFrame> wrf : frames)
               {
                   Frame f = wrf.get();
                   if (f != null)
                   {
                       f.setAlwaysOnTop(true);
                       f.setAlwaysOnTop(false);
                   }
                }
            }
        });
    }

    static Image getIcon()
    {
        if (icon == null)
        {
            try {
                icon = ImageIO.read(WindowManager.class.getResourceAsStream(iconName));
            } catch (IOException ex) {
                ex.printStackTrace();
                icon = (BufferedImage)((ImageIcon) UIManager.getIcon("OptionPane.warningIcon")).getImage();
            }
        }
        return icon;
    }
}
