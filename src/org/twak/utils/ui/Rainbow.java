package org.twak.utils.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author twak
 */
public class Rainbow
{

    static String[] rainbowStrings = new String[]
    {
    		"purple", "green", "blue", "orange", "pink"
    		
//        "red", "orange", "green", "cyan", "blue", "magenta", "pink", "gray", "purple",
//        "dark red", "brown", "dark orange", "dark yellow", "dark green", "dark blue", "yellow"
    };
    static int rainbowIndex = 0;
    
    static Map<Object, Integer> indexes = new HashMap();

    public static Color[] rainbow = new Color[]
    {
    	new Color (170,0,255),
    	new Color (0,255,0),
    	new Color (0,170,255),
    	new Color (255,170,0),
    	new Color (255,0,170),
    	new Color (0,255,170),
    	
//    	new Color (116,255,116),
//    	new Color (255,116,116),
//    	new Color (116,116,255),
//    	
//        Color.red,
//        Color.orange,
//        Color.green,
//        Color.cyan,
//        Color.blue,
//        Color.magenta,
//        Color.pink,
//        Color.gray,
//        new Color (152,0,255),
//        Color.red.darker(),
//        new Color (128,67,0),
//        Color.orange.darker(),
//        Color.yellow.darker(),
//        Color.green.darker(),
//        Color.blue.darker(),
//        Color.yellow,
    };

    static {
    	
    	List<Color> cols = new ArrayList();
    	for (Color c : rainbow)
    		cols.add(c);
    	
//    	for (int i = 0; i < 100; i++) {
//    		
//    		cols.add ( Color.getHSBColor( (float) Math.random(), 1f, 1f ) );
//    		
//    	}
    	
    	rainbow = cols.toArray( new Color[cols.size()] );
    }
    
    public static String lastAsString( Object key )
    {
        return rainbowStrings[(indexes.get( key ) -1 )% (rainbowStrings.length)];
    }

    public static Color next( Object key )
    {
        int val = indexes.containsKey( key ) ? indexes.get( key ) : 0;
        indexes.put( key, val+1 );
        return rainbow[val % (rainbow.length)];
    }

    public static Color getColour (int i ){
        return rainbow[i%rainbow.length];
    }

	public static Color random() {
		return getColour( (int) ( Math.random() * rainbow.length ) );
	}
}
