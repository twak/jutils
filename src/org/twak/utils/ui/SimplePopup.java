package org.twak.utils.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author twak
 */
public class SimplePopup
{
    DefaultListModel dlm = new DefaultListModel();
    MouseEvent evt;

    public SimplePopup( MouseEvent evt )
    {
        this.evt = evt;
    }

    public void add( String simpleName, Runnable runnable )
    {
        for ( int i = 0; i < dlm.getSize(); i++ )
            if ( dlm.get( i ).toString().compareTo( simpleName ) == 0 )
                return;

        dlm.addElement(new Clickable( simpleName, runnable ));
    }

    public void add( Runnable runnable )
    {
        add (runnable.toString(), runnable);
    }

    public void addAll( List<Runnable> runs )
    {
        if (runs == null)
            return;
        
        for (Runnable r : runs)
            add(r);
    }

    class Clickable
    {

        String name;
        Runnable runnable;

        public Clickable( String name, Runnable runnable )
        {
            this.name = name;
            this.runnable = runnable;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    public void show()
    {
        if (dlm.isEmpty())
            return; // nothing to show!

        Point pt = evt.getPoint();
//        pt = SwingUtilities.convertPoint( evt.getComponent(), pt, null );
        SwingUtilities.convertPointToScreen( pt, evt.getComponent() );

        final JList list = new JList(dlm);

        list.setBorder(new LineBorder(new Color (100,100,100)));

        final Popup pop = PopupFactory.getSharedInstance().getPopup( evt.getComponent(), list, pt.x - 10, pt.y - 10 );
        pop.show();

        list.addMouseListener( new MouseAdapter()
        {

            @Override
            public void mouseExited( MouseEvent e )
            {
                pop.hide();
            }
        } );

        list.getSelectionModel().addListSelectionListener( new ListSelectionListener()
        {
            public void valueChanged( ListSelectionEvent e )
            {
                Object o = list.getSelectedValue();
                if ( o != null && o instanceof Clickable )
                    // adds to plan!
                    ((Clickable) o).runnable.run();
                pop.hide();
            }
        } );
    }
}
