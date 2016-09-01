package org.twak.utils.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author twak
 */
public class SimplePopup2
{
//    DefaultListModel dlm = new DefaultListModel();
    JPopupMenu menu = new JPopupMenu();
    MouseEvent evt;
    ActionEvent runClick;

    public SimplePopup2( MouseEvent evt )
    {
        this.evt = evt;


        menu.addMouseListener( new MouseAdapter() {

            @Override
            public void mouseExited( MouseEvent e )
            {
                menu.setVisible( false );
            }
        });

    }

    public void add( String simpleName, final Runnable runnable )
    {
        JMenuItem item = new JMenuItem( simpleName );
        item.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e )
            {
                runClick = e;
                runnable.run();
            }
        });

        menu.add (item);

//        for ( int i = 0; i < dlm.getSize(); i++ )
//            if ( dlm.get( i ).toString().compareTo( simpleName ) == 0 )
//                return;
//
//        dlm.addElement(new Clickable( simpleName, runnable ));
    }

    public void addSubMenu (String name, Iterable<Runnable> options)
    {
        JMenu sub = new JMenu(name);

        for ( final Runnable r : options )
        {
            JMenuItem item = new JMenuItem( r.toString() );
            sub.add(item);
            
            item.addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    runClick = e;
                    r.run();
                }
            } );
        }

        menu.add (sub);
    }

    public void add( Runnable runnable )
    {
        if (runnable != null)
            add (runnable.toString(), runnable);
    }

    public void addAll( List<Runnable> howCanWeStartFrom )
    {
        if (howCanWeStartFrom == null)
            return;
        
        for (Runnable r : howCanWeStartFrom)
            add(r);
    }

//    class Clickable
//    {
//
//        String name;
//        Runnable runnable;
//
//        public Clickable( String name, Runnable runnable )
//        {
//            this.name = name;
//            this.runnable = runnable;
//        }
//
//        @Override
//        public String toString()
//        {
//            return name;
//        }
//    }

    public void show()
    {
        if (menu.getComponents().length == 0)
            return; // nothing to show!

         menu.show(evt.getComponent(),
                       evt.getX() - 10, evt.getY() - 10);

         menu.getComponent(0).requestFocusInWindow();
    }

    public ActionEvent clickEvent()
    {
        return runClick;
    }
}
