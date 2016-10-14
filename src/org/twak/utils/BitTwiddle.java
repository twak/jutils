package org.twak.utils;

public class BitTwiddle {
	
	
	
	public static int[][] byteToUByte(byte[] bytes, int size, int offset, int stride ) {
		
		int[][] out = new int[bytes.length / stride][size];
		
		for (int i = offset, x = 0; i < bytes.length && x < out.length; i+= stride, x++)
			for (int j = 0; j < size ; j++)  
				out[x][j] = (int) bytes[i+j] & 0xff;
		
		return out;
	}
	
	public static float[][] byteToFloat(byte[] bytes, int size, int offset, int stride ) {

		float[][] out = new float[bytes.length / stride][size];
		
		for (int i = offset, x = 0; i < bytes.length; i+= stride, x++)
			for (int j = 0; j < size ; j++)  {
				
				out[x][j] = Float.intBitsToFloat( 
				  ((bytes[i+j*4+0] & 0xFF) << 0 )
	            | ((bytes[i+j*4+1] & 0xFF) << 8 ) 
	            | ((bytes[i+j*4+2] & 0xFF) << 16) 
	            | ((bytes[i+j*4+3] & 0xFF) << 24) );
			}
		
		return out;
	}
	
	public static int[][] byteToUShort(byte[] bytes, int size, int offset, int stride ) {
		
		int[][] out = new int[bytes.length / stride][size];
		
		for (int i = offset, x = 0; i < bytes.length && x < out.length; i+= stride, x++)
			for (int j = 0; j < size ; j++)  {
				
				out[x][j] = 
						((bytes[i+j*2+0] & 0xFF) << 0 ) |
						((bytes[i+j*2+1] & 0xFF) << 8 );

			}
		
		return out;
		
	}

	public static int[] byteToUShort(byte[] in) {
		
		if (in == null)
			return null;
		
		int[] out = new int [in.length /2 ];
		
		for (int i = 0; i < out.length; i++ ) {
			out[i] = (in[i*2] & 0xff) | ((0xff & in[i*2+1]) << 8);
//			System.out.println(out[i]);
		}
		
		return out;
	}

	public static void main (String[] args) throws Throwable {
		new BitTwiddle();
	}
	
    public static int nextPowerOf2(final int a)
    {
        int b = 1;
        while (b < a)
        {
            b = b << 1;
        }
        return b;
    }
}
