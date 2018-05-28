/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twak.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author twak
 */
public class Filez
{
    public static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static void copyfile( String srFile, String dtFile )
    {
        try
        {
            File f1 = new File( srFile );
            File f2 = new File( dtFile );
            InputStream in = new FileInputStream( f1 );

            //For Append the file.
//      OutputStream out = new FileOutputStream(f2,true);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream( f2 );

            byte[] buf = new byte[1024];
            int len;
            while ( (len = in.read( buf )) > 0 )
                out.write( buf, 0, len );
            in.close();
            out.close();
            System.out.println( "File copied." );
        }
        catch ( FileNotFoundException ex )
        {
            System.out.println( ex.getMessage() + " in the specified directory." );
            System.exit( 0 );
        }
        catch ( IOException e )
        {
            System.out.println( e.getMessage() );
        }
    }
    
    public static String stripExtn(String name) {
    	return name.substring( 0, name.lastIndexOf( '.' ) );
    }
//  public static void main(String[] args){
//    switch(args.length){
//      case 0: System.out.println("File has not mentioned.");
//          System.exit(0);
//      case 1: System.out.println("Destination file has not mentioned.");
//          System.exit(0);
//      case 2: copyfile(args[0],args[1]);
//          System.exit(0);
//      default : System.out.println("Multiple files are not allow.");
//            System.exit(0);
//    }
//  }
    
	public static String getExtn( String filename ) {
		int i = filename.lastIndexOf( '.' );
		if (i >= 0)
			return filename.substring( i+1, filename.length() );
		return "";
	}
	public static String extTo( String filename, String png ) {
		return stripExtn( filename ) + png;
	}
}
