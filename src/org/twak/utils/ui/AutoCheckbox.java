package org.twak.utils.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;

/**
 *
 * @author twak
 */
public class AutoCheckbox extends JCheckBox implements ActionListener
{
    Field field;
    Object source;
    
    public AutoCheckbox( Object source, String fieldName, String desc ) {
        setText(desc);
        this.source = source;
        try {
        	this.field = source.getClass().getField(fieldName);
            setSelected ( (Boolean) field.get(source) );
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try 
        {
            field.set(source, isSelected() );
        }
        catch (Throwable ex) 
        {
            ex.printStackTrace();
        }
        
        updated( this.isSelected() );
    }
    
    public void updated( boolean selected )
    {
        //override me
    }
}
