package org.twak.utils.ui.auto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.twak.utils.ui.AbstractDocumentListener;
import org.twak.utils.ui.ListDownLayout;
import org.twak.utils.ui.WindowManager;

public class Auto {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Name {
		String name();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Ignore {}

	
	Object o;
	JButton okay = new JButton( "okay" ), cancel = new JButton( "cancel" );
	JFrame frame;
	List<Apply> applies = new ArrayList<>();

	public Auto( Object o ) {
		this.o = o;
		build();
	}

	public JComponent build() {

		JPanel out = new JPanel();

		out.setLayout( new ListDownLayout() );

		int pWidth = 100;

		for ( Field f : o.getClass().getFields() ) {

			JPanel entry = new JPanel( new BorderLayout() );
			JComponent e = create( f );

			if ( e == null )
				continue;

			applies.add( (Apply) e );

			entry.add( new JLabel( getName( f ) ), BorderLayout.WEST );
			entry.add( e, BorderLayout.EAST );

			entry.setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
			pWidth = Math.max( pWidth, entry.getPreferredSize().width + 5 );

			out.add( entry );
		}

		JPanel okayCancel = new JPanel( new FlowLayout( FlowLayout.TRAILING ) );

		okay.addActionListener( x -> apply() );
		cancel.addActionListener( x -> close() );

		updateOkayCancel();

		okayCancel.add( okay );
		okayCancel.add( cancel );
		out.add( okayCancel );

		out.setPreferredSize( new Dimension( pWidth, out.getPreferredSize().height ) );

		return out;
	}

	private void updateOkayCancel() {
		okay.setEnabled( applies.stream().mapToInt( a -> a.changed() ? 1 : 0 ).sum() > 0 );
	}

	public void apply() {
		applies.stream().forEach( a -> a.apply() );
		close();
	}

	public void close() {
		if ( frame != null ) {
			frame.setVisible( false );
			frame.dispose();
		}
	}

	private JComponent create( Field f ) {

		if (f.getAnnotation( Auto.Ignore.class ) != null)
			return null;
		
		try {
			Class c = f.getType();
			if ( c == Integer.TYPE ) {
				return new AutoInteger( f );
			}
			else if ( c == Boolean.TYPE ) {
				return new AutoBool( f );
			}
			else if (c == String.class) {
				return new AutoString( f );
			}
			else if (Enum.class.isAssignableFrom( c )) {
				return new AutoEnum( f );
			}
		} catch ( Throwable e ) {
			e.printStackTrace();
		}

		return null;
	}

	private interface Apply {
		public void apply();

		public boolean changed();
	}

	private String getName( Field f ) {
		Name n = f.getAnnotation( Name.class );
		String out;
		if ( n != null )
			out = n.name();
		else
			return splitCamelCase( f.getName() ).toLowerCase();

		return out + ":";
	}

	public JFrame frame() {
		frame = new JFrame();
		WindowManager.register( frame );

		frame.setContentPane( build() );
		frame.pack();
		frame.setVisible( true );

		return frame;
	}

	static String splitCamelCase( String s ) {
		return s.replaceAll( String.format( "%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])" ), " " );
	}

	private class AutoInteger extends JSpinner implements Apply {

		int orig;
		Field f;

		public AutoInteger( Field f ) throws IllegalArgumentException, IllegalAccessException {
			super( new SpinnerNumberModel( f.getInt( o ), -Integer.MAX_VALUE, Integer.MAX_VALUE, 1 ) );
			this.f = f;
			this.orig = f.getInt( o );
			addChangeListener( c -> updateOkayCancel() );
		}

		@Override
		public void apply() {
			try {
				f.set( o, getValue() );
			} catch ( Throwable e ) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean changed() {
			return (Integer) getValue() != orig;
		}
	}
	
	private class AutoBool extends JCheckBox implements Apply {
		
		boolean orig;
		Field f;
		
		public AutoBool( Field f ) throws IllegalArgumentException, IllegalAccessException {
			super();
			this.f = f;
			this.orig = f.getBoolean( o );
			addChangeListener( c -> updateOkayCancel() );
		}
		
		@Override
		public void apply() {
			try {
				f.set( o, isSelected() );
			} catch ( Throwable e ) {
				e.printStackTrace();
			}
		}
		
		@Override
		public boolean changed() {
			return isSelected() != orig;
		}
	}

	private class AutoString extends JTextField implements Apply {

		String orig;
		Field f;

		public AutoString( Field f ) throws IllegalArgumentException, IllegalAccessException {
			super( (String) f.get( o ) );
			this.f = f;
			this.orig = (String) get();
			getDocument().addDocumentListener( new AbstractDocumentListener() {
				@Override
				public void changed() {
					updateOkayCancel();
				}
			} );
			
			if (orig == null)
				setPreferredSize( new Dimension (100, getPreferredSize().height) );
		}

		@Override
		public void apply() {
			try {
				f.set( o, get() );
			} catch ( Throwable e ) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean changed() {
			return get() != orig;
		}
		
		public String get() {
			String out = getText();
			if (out.length() == 0)
				return null;
			return out;
		}
	}
	
	private class PrettyEnum
	{
		Enum e;
		public PrettyEnum(Enum e) {
			this.e = e;
		}
		
		@Override
		public String toString() {
			return e.toString().replaceAll( "_", " " ).toLowerCase();
		}
	}
	
	private class AutoEnum extends JComboBox implements Apply {
		
		Enum<?> orig;
		Field f;
		
		public AutoEnum( Field f ) throws IllegalArgumentException, IllegalAccessException {
			
			super();
			
			this.f = f;
			this.orig = (Enum) f.get( o ); 
			
	        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();

	        for (Enum e : orig.getClass().getEnumConstants())
	        {
	            dcbm.addElement( new PrettyEnum ( e ) );
	        }

	        setModel( dcbm );
	        setSelectedItem( orig );
	        addActionListener( l -> updateOkayCancel() );
		}
		
		@Override
		public void apply() {
			try {
				f.set( o,  ((PrettyEnum)getSelectedItem()).e );
			} catch ( Throwable e ) {
				e.printStackTrace();
			}
		}
		
		@Override
		public boolean changed() {
			try {
				return f.get( o ) != orig;
			} catch ( Throwable e ) {
				e.printStackTrace();
			}
			return false;
		}
	}
}
