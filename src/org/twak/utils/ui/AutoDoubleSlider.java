
package org.twak.utils.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author twak
 */
public class AutoDoubleSlider extends JPanel{
    Field f;
    Object o;
    JFormattedTextField textField;
    JSlider slider;
	private Runnable update;
	private boolean onDrag = true;
	boolean updating = false;
	
    public AutoDoubleSlider ( Object o, String field, final String name, final double min, final double max ) {
    	this (o, field, name, min, max, null);
    }
    
    public AutoDoubleSlider ( Object o, String field, final String name, final double min, final double max, Runnable update )
    {
    	this.update = update;
        try
        {
            UIManager.put("Slider.paintValue", Boolean.FALSE);
            
            this.o = o;
            f = o.getClass().getField( field );
            double initialVal = f.getDouble( o );

            setLayout (new BorderLayout());

            add( new JLabel( name ), BorderLayout.WEST );

            final int scale = 1000;
            slider = new JSlider( 
                    (int)0, 
                    (int)( ( max - min) * scale), 
                    (int)( ( initialVal- min) * scale) );
            
            slider.addChangeListener( new ChangeListener() {

                @Override
                public void stateChanged( ChangeEvent e )
                {
                	
                	if (updating)
                		return;
                	
                	if ( onDrag || !slider.getValueIsAdjusting()) {
                		double val = slider.getValue() / (double)(slider.getMaximum() - slider.getMinimum());
                		updating = true;
                    	set ((val * (max -min) ) + min, max, min);
                    	updating = false;
                	}
                }
            });
            add(slider, BorderLayout.CENTER);
            
            textField = new JFormattedTextField( NumberFormat.getNumberInstance());
            textField.setValue( initialVal );
            textField.addPropertyChangeListener( "value", new PropertyChangeListener() {

                @Override
                public void propertyChange( PropertyChangeEvent evt )
                {
                	if (!updating)
                		set ( (Double) textField.getValue(), max, min);
                }
            });
            
            textField.setPreferredSize( new Dimension( 50, textField.getMinimumSize().height ) );
            add(textField, BorderLayout.EAST);
            
            setPreferredSize( textField.getPreferredSize() );
        }
        catch ( Throwable ex )
        {
            ex.printStackTrace();
        }

    }
    
    void set( double value, double max, double min )
    {
        try
        {
            f.set( o, value );
            
            if (!textField.getValue().equals( value ) )
                 textField.setValue( value );
            
            int v =(int)( ((value-min)/(max-min)) * (slider.getMaximum() - slider.getMinimum()) + slider.getMinimum() ) ;
            
            if (v != slider.getValue())
            	slider.setValue( v );
            
        } catch ( Throwable ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            updated();
        }
    }
    
    public AutoDoubleSlider notWhileDragging() {
    	this.onDrag = false;
    	return this;
    }
    
    public void updated()
    {
        update.run();
    }
}
