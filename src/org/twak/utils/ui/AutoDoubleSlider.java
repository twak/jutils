
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

    public AutoDoubleSlider ( Object o, String field, final String name, final double min, final double max )
    {
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
                    (int)(min * scale), 
                    (int)(max * scale), 
                    (int)(initialVal * scale) );
            
            slider.addChangeListener( new ChangeListener() {

                @Override
                public void stateChanged( ChangeEvent e )
                {
                    double val = slider.getValue() / (double)(slider.getMaximum() - slider.getMinimum());
                    set ((val * (max -min) ) + min, max, min);
                }
            });
            add(slider, BorderLayout.CENTER);
            
            textField = new JFormattedTextField( NumberFormat.getNumberInstance());
            textField.setValue( initialVal );
            textField.addPropertyChangeListener( "value", new PropertyChangeListener() {

                @Override
                public void propertyChange( PropertyChangeEvent evt )
                {
                    set ( (Double) textField.getValue(), max, min);
                }
            });
            
            textField.setPreferredSize( new Dimension( 50, textField.getMinimumSize().height ) );
            add(textField, BorderLayout.EAST);
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
//            if (!textField.getValue().equals( value ) )
                textField.setValue( value );
            
            slider.setValue( (int)( ((value-min)/(max-min)) * (slider.getMaximum() - slider.getMinimum()) + slider.getMinimum() ) );
            
        } catch ( Throwable ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            updated();
        }
    }
    
    public void updated()
    {
        // override me
    }
}
