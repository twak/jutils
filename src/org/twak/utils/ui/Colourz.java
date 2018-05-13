package org.twak.utils.ui;

import java.awt.Color;

/**
 * Very similar to Rainbow!
 * @author twak
 */
public class Colourz
{

    static float hue;
    static float sat;
    static float bri;
    static
    {
        reset();
    }
    
    public static Color sky = new Color( 180, 225, 246 );

    public static Color nextColor()
    {
        hue += 0.1 / Math.PI;
        return new Color( Color.HSBtoRGB( hue, sat, bri ) );
    }

    public static void reset()
    {
        hue = 0;
        sat = 0.7f;
        bri = 1f;
    }

    public static Color transparent( Color color, int i )
    {
        return new Color (color.getRed(), color.getGreen(), color.getBlue(), i);
    }

    public static Color toGrayscale( Color color )
    {
        int a = 0;
        a+= color.getRed();
        a+= color.getGreen();
        a+= color.getBlue();
        return new Color( (int)(a/3),(int)(a/3),(int)(a/3));
    }
    
    public static Color toGrayscale( Color color, float tween )
    {
        int a = 0;
        a+= color.getRed();
        a+= color.getGreen();
        a+= color.getBlue();
        
        return new Color( 
                (int)(((a / 3.) * tween + color.getRed() * (1-tween))/2) ,
                (int)(((a / 3.) * tween + color.getGreen() * (1-tween))/2) ,
                (int)(((a / 3.) * tween + color.getBlue() * (1-tween))/2) );
    }

	public static float[] toF4( Color color ) {
		return new float[] {
				color.getRed()   / 255f,
				color.getGreen() / 255f,
				color.getBlue()  / 255f,
				color.getAlpha() / 255f
			};
	}
	
	public static double[] toD4( Color color ) {
		return new double[] {
				color.getRed()   / 255f,
				color.getGreen() / 255f,
				color.getBlue()  / 255f,
				color.getAlpha() / 255f
		};
	}
	
	public static String toHex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());  
	}
	
	public static int asInt(int r, int g, int b, int a) {
		return ((a & 0xFF) << 24) |
				((r & 0xFF) << 16) |
				((g & 0xFF) << 8)  |
				((b & 0xFF) << 0);
	}
	
	public static int asInt(int r, int g, int b) {
		return asInt (r,g,b,255);
	}

	public static Color lighter( Color c ) {
		float[] hsb = new float[3];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		hsb[2] = Math.max (1, hsb[1] * 1.2f );
		return Color.getHSBColor( hsb[0], hsb[1], hsb[2] );
	}

	public static Color to4( float[] c ) {
		return new Color(c[0], c[1], c[2], c[3]);
	}
	public static Color to4( double[] c ) {
		return new Color( (float) c[0], (float)c[1], (float)c[2], (float)c[3]);
	}
}
