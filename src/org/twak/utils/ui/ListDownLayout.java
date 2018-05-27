package org.twak.utils.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 *
 * @author twak
 */
public class ListDownLayout implements LayoutManager
{
    public void addLayoutComponent( String name, Component comp )
    {
    }

    public void removeLayoutComponent( Component comp )
    {
    }

    public Dimension preferredLayoutSize( Container parent )
    {
        int height = 0;
        
        Insets insets = parent.getInsets();
        
        for (Component c : parent.getComponents())
            height += c.getPreferredSize().getHeight();
        
        return new Dimension ( 100, height + insets.top + insets.bottom );
    }

    public Dimension minimumLayoutSize( Container parent )
    {
        int height = 0;
        Insets insets = parent.getInsets();
        
        for (Component c : parent.getComponents())
            height += c.getPreferredSize().getHeight();
        
        return new Dimension ( 100, height + insets.top + insets.bottom );
    }

    public void layoutContainer( Container parent )
    {
        synchronized ( parent.getTreeLock() )
        {
        	Insets insets = parent.getInsets();
        	
            int height = insets.top;
            for ( Component c : parent.getComponents() )
            {
                Dimension prefSize = c.getPreferredSize();
                c.setSize( parent.getWidth() - insets.left - insets.right, prefSize.height );
                c.setLocation( insets.left, height );
                height += prefSize.getHeight();
            }
        }
    }
}
