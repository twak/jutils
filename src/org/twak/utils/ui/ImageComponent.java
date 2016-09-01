package org.twak.utils.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;

/**
 *
 * @author twak
 */
public class ImageComponent extends JComponent
{
    public BufferedImage image;

    public ImageComponent()
    {
        setBorder(new LineBorder(Color.yellow));
    }
    public ImageComponent (BufferedImage image)
    {
        this.image = image;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(), getHeight() );
        if (image != null)
            g.drawImage(image, (getWidth()-image.getWidth())/2, (getHeight()-image.getHeight())/2, this);
    }
}
