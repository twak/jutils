package org.twak.utils.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author twak
 */
public class AutoTextField extends JPanel {
	Field f;
	Object o;
	private Runnable update;

	public JTextField text;
	
	public AutoTextField() {}
	
	
	public AutoTextField( Object o, String field, final String name) {
		this (o, field, name, null);
	}
	
	public AutoTextField( Object o, String field, final String name, Runnable update ) {
		this.update = update;
		try {
			UIManager.put( "Slider.paintValue", Boolean.FALSE );

			this.o = o;
			
			f = o.getClass().getField( field );
			String initialVal = (String) f.get( o );

			setLayout( new BorderLayout() );

			add( new JLabel( name ), BorderLayout.WEST );

			text = new JTextField( initialVal );

			text.getDocument().addDocumentListener( new DocumentListener() {

				@Override
				public void removeUpdate( DocumentEvent e ) {
					set( text.getText() );
				}

				@Override
				public void insertUpdate( DocumentEvent e ) {
					set( text.getText() );
				}

				@Override
				public void changedUpdate( DocumentEvent e ) {
					set( text.getText() );
				}

			} );

			add( text, BorderLayout.CENTER );

			setPreferredSize( text.getPreferredSize() );
		} catch ( Throwable ex ) {
			ex.printStackTrace();
		}
	}

	private void set( String text ) {
		try {
			f.set( o, text );
		} catch ( IllegalArgumentException | IllegalAccessException e ) {
			e.printStackTrace();
		}
		updated();
	}

	public void updated() {
		if (update != null)
			update.run();
	}

	public static void linkFieldString( final JTextField sequenceVarField, final Object down, String fieldString ) {
		linkFieldString( sequenceVarField, down, fieldString, new AutoTextField(), "toString" ); // <-- toString unlikely to have any noticable effect ;)
	}

	public static void linkFieldString( final JTextField sequenceVarField, final Object down, String fieldString, final Object changeFireObject, final String changeFireField ) {
		try {
			final Field stringUpdateField = down.getClass().getField( fieldString );
			final Method changedFireMethod = changeFireObject.getClass().getMethod( changeFireField );

			sequenceVarField.setText( (String) stringUpdateField.get( down ) );

			sequenceVarField.getDocument().addDocumentListener( new AbstractDocumentListener() {

				@Override
				public void changed() {
					try {
						stringUpdateField.set( down, sequenceVarField.getText() );
						changedFireMethod.invoke( changeFireObject );
					} catch ( Throwable th ) {
						th.printStackTrace();
					}
				}
			} );

		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
}
