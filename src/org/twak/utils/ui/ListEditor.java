package org.twak.utils.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.twak.utils.Mathz;
import org.twak.utils.collections.Arrayz;

/**
 * @author twak
 */
public class ListEditor<E> extends javax.swing.JPanel {

    public List<E> list = new ArrayList();
    public boolean fireSelection = true;

    public ListEditor() {}
    {
        initComponents();
        setEnabled( false );
    }

    /** Creates new form ListEditor */
    public ListEditor( List<E> list ) {
        this.list = list;
        initComponents();
        init();
        jFrames.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    }

    public void init()
    {
        Object[] vals = jFrames.getSelectedValues();

        DefaultListModel dlm = new DefaultListModel();

        if ( list != null )
            for ( Object l : list )
            {
                dlm.addElement( l );
            }

        fireSelection = false;
        jFrames.setModel( dlm );

        for ( Object o : vals )
            jFrames.setSelectedValue( o, true );
        
        fireSelection = true;
    }

    public void selected (E e){}
    public void add(MouseEvent evt, List<E> list){}
    public void changed(List<E> list){}
    public void rightClick (E e, MouseEvent evt,List<E> list) {}
    public void doubleClick (E e, List<E> list) {}
    
    private void moveSelected( E o, int i )
    {
        if (o != null)
        {
            int index = list.indexOf( o );
            list.remove( o );
            list.add( Mathz.clamp( index+i, 0, list.size()), o);
        }
        fireSelection= false;
        jFrames.setSelectedValue(o, true);
        fireSelection= true;
        init();
        changed(list);
    }

    public void setSelected (E e)
    {
        if ( jFrames.getSelectedValue() == e)
            return;
        
        jFrames.setSelectedValue(e, true);
    }

    public void setList(List<E> nList)
    {
        if (list == nList)
            return;

        setEnabled(nList != null);

        if (nList == null)
            nList = new ArrayList();

        list = nList;
//        jFrames.clearSelection();
        init();
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        for (Component c : jPanel1.getComponents())
            c.setEnabled(enabled);
    }

    public E getSelected()
    {
        return (E) jFrames.getSelectedValue();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jFrames = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        add = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        up = new javax.swing.JButton();
        down = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jFrames.setModel( new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jFrames.addMouseListener( new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListMouseClicked(evt);
            }
        });
        jFrames.addListSelectionListener( new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView( jFrames );

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new ListDownLayout());

        Dimension bSize = new Dimension( 50,50 );
        add.setText("+");
        add.setPreferredSize( bSize );
        add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addMouseClicked(evt);
            }
        });
        jPanel1.add(add);

        remove.setText("-");
        remove.setPreferredSize( bSize );
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        jPanel1.add(remove);

        up.setText("^");
        up.setPreferredSize( bSize );
        up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upActionPerformed(evt);
            }
        });
        jPanel1.add(up);

        down.setText("V");
        down.setPreferredSize( bSize );
        down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downActionPerformed(evt);
            }
        });
        jPanel1.add(down);

        add(jPanel1, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void jListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_jListValueChanged
    {//GEN-HEADEREND:event_jListValueChanged
        if (fireSelection && !evt.getValueIsAdjusting())
        {
            selected( (E) jFrames.getSelectedValue() );
        }

    }//GEN-LAST:event_jListValueChanged

    private void addMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_addMouseClicked
    {//GEN-HEADEREND:event_addMouseClicked

    	if (!add.isEnabled())
    		return;

        Object[] original = jFrames.getSelectedValues();
        add(evt, list);
        List<Object> neu = Arrayz.newElements (original, jFrames.getSelectedValues());

        changed(list);
        init();

        if (!neu.isEmpty())
            jFrames.setSelectedValue(neu.get(0), true);
    }//GEN-LAST:event_addMouseClicked

    private void removeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removeActionPerformed
    {//GEN-HEADEREND:event_removeActionPerformed
        int index = jFrames.getSelectedIndex();
        
        for (Object o : jFrames.getSelectedValues())
            list.remove((E)o);

        changed(list);
        init();
        jFrames.setSelectedIndex(Math.min (list.size()-1, index));
    }//GEN-LAST:event_removeActionPerformed

    private void upActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_upActionPerformed
    {//GEN-HEADEREND:event_upActionPerformed
        for (Object o : jFrames.getSelectedValues())
            moveSelected((E)o, -1);
    }//GEN-LAST:event_upActionPerformed

    private void downActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_downActionPerformed
    {//GEN-HEADEREND:event_downActionPerformed
        for (Object o : Arrayz.reverse ( jFrames.getSelectedValues()) )
            moveSelected((E)o, 1);
    }//GEN-LAST:event_downActionPerformed

    private void jListMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jListMouseClicked
    {//GEN-HEADEREND:event_jListMouseClicked
    	if (evt.getClickCount() == 2)
        {
            doubleClick((E) jFrames.getSelectedValue(), list);
            init();
        }
        else if (evt.getButton() == MouseEvent.BUTTON3)
        {
            rightClick((E) jFrames.getSelectedValue(), evt, list);
            init();
        }
    }//GEN-LAST:event_jListMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton add;
    private javax.swing.JButton down;
    private javax.swing.JList jFrames;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton remove;
    private javax.swing.JButton up;
    // End of variables declaration//GEN-END:variables

}
