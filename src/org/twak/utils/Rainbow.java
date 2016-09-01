package org.twak.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author twak
 */
public class Rainbow
{

    static String[] rainbowStrings = new String[]
    {
        "red", "orange", "yellow", "green", "cyan", "blue", "magenta", "pink", "gray", "purple",
        "dark red", "brown", "dark orange", "dark yellow", "dark green", "dark blue"
    };
    static int rainbowIndex = 0;
    
    static Map<Object, Integer> indexes = new HashMap();

    public static Color[] rainbow = new Color[]
    {
        Color.red,
        Color.orange,
        Color.yellow,
        Color.green,
        Color.cyan,
        Color.blue,
        Color.magenta,
        Color.pink,
        Color.gray,
        new Color (152,0,255),
        Color.red.darker(),
        new Color (128,67,0),
        Color.orange.darker(),
        Color.yellow.darker(),
        Color.green.darker(),
        Color.blue.darker()
    };

    public static String lastAsString( Object key )
    {
        return rainbowStrings[(indexes.get( key ) -1 )% (rainbowStrings.length)];
    }

    public static Color next( Object key )
    {
        int val = indexes.containsKey( key ) ? indexes.get( key ) : 0;
        indexes.put( key, val+1 );
        return rainbow[val % (rainbowStrings.length)];
    }

    public static Color getColour (int i ){
        return rainbow[i%rainbow.length];
    }
}
