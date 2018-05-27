/*
 * AutoEnumCombo.java
 *
 * Created on 08-Mar-2011, 00:44:08
 */

package org.twak.utils.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author twak
 */
public class AutoListCombo<E> extends javax.swing.JPanel {

	List<E> objects;
	String label;
	Object o;
	Map<E, Wrapper> wrappers = new HashMap<>();
	
	public AutoListCombo( Object o, String field, String label, List<E> objects ) {
		this.o = o;
		this.objects = objects;
		this.label = label+":";

		initComponents();

		DefaultComboBoxModel dcbm = new DefaultComboBoxModel();

		for ( E e : objects ) {
			
			Wrapper w = new Wrapper( e, getName( e ) ) ;
			wrappers.put (e, w);
			dcbm.addElement( w );
		}

		combo.setModel( dcbm );

		try {
			combo.setSelectedItem( wrappers.get( o.getClass().getField( field ).get( o ) ) );
		} catch ( Throwable e ) {
			e.printStackTrace();
		}

		combo.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent evt ) {
				comboActionPerformed( evt );
			}
		} );
	}

	public String getName( E o2 ) {
		return o2.toString().toLowerCase();
	}

	private class Wrapper {
		E o;
		String name;

		public Wrapper( E o, String name ) {
			this.o = o;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	public void fire( E e ) {

	}

	@SuppressWarnings( "unchecked" )
	private void initComponents() {

		nameLabel = new javax.swing.JLabel();
		combo = new javax.swing.JComboBox();

		nameLabel.setText( label );

//		combo.setModel( new javax.swing.DefaultComboBoxModel( new String[] { "Item 1", "Item 2", "Item 3", "Item 4" } ) );

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout( this );
		this.setLayout( layout );
		layout.setHorizontalGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addGroup( layout.createSequentialGroup().addComponent( nameLabel ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent( combo, 0, 349, Short.MAX_VALUE ) ) );
		layout.setVerticalGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.BASELINE ).addComponent( combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE ).addComponent( nameLabel ) ) );
	}

	private void comboActionPerformed( java.awt.event.ActionEvent evt )
	{
		Wrapper num = (Wrapper) combo.getSelectedItem();
		try {
			fire( num.o );
		} catch ( Throwable th ) {
			th.printStackTrace();
		}
	}

	private javax.swing.JComboBox combo;
	private javax.swing.JLabel nameLabel;
}
