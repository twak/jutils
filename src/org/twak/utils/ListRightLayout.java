package org.twak.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 *
 * @author twak
 */
public class ListRightLayout implements LayoutManager
{

    public void addLayoutComponent( String name, Component comp )
    {
    }

    public void removeLayoutComponent( Component comp )
    {
    }

    public Dimension preferredLayoutSize( Container parent )
    {
        int width = 0;
        for (Component c : parent.getComponents())
            width += c.getPreferredSize().getWidth();
        return new Dimension ( width, 10 );
    }

    public Dimension minimumLayoutSize( Container parent )
    {
        int width = 0;
        for (Component c : parent.getComponents())
            width += c.getPreferredSize().getWidth();
        return new Dimension ( width, 100 );
    }

    public void layoutContainer( Container parent )
    {
        synchronized ( parent.getTreeLock() )
        {
            int width = 0;
            for ( Component c : parent.getComponents() )
            {
                Dimension prefSize = c.getPreferredSize();
                c.setSize( prefSize.width, parent.getHeight() );
                c.setLocation( width, 0 );
                width += prefSize.getWidth();
            }
        }
    }

}
