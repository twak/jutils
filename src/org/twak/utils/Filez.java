package org.twak.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;

import org.apache.commons.io.FilenameUtils;

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
	public static File extTo( File wf, String png ) {
		return new File ( stripExtn ( wf.getPath() ) +png );
	}
	
	
	
	// http://www.java2s.com/Tutorial/Java/0180__File/UnpackanarchivefromaURL.htm
	  public static File unpackArchive(URL url, File targetDir, ProgressMonitor pm) throws IOException {

		try {
			if ( !targetDir.exists() ) 
				targetDir.mkdirs();
			pm.setMaximum( 100 );
			
			InputStream in = new BufferedInputStream( url.openStream(), 1024 );
			// make sure we get the actual file
			File zip = new File ( targetDir , FilenameUtils.getName( url.getPath() ) );
			OutputStream out = new BufferedOutputStream( new FileOutputStream( zip ) );
			
			pm.setNote( "downloading "+ url );
			copyInputStream( in, out, pm );
			out.close();
			return unpackArchive( zip, targetDir, pm );

		} finally {
			pm.close();
		}
	  }
	  
	  /**
	   * Unpack a zip file
	   * 
	   * @param theFile
	   * @param targetDir
	   * @return the file
	   * @throws IOException
	   */
	  public static File unpackArchive(File theFile, File targetDir, ProgressMonitor pm) throws IOException {
		  
		  
	      if (!theFile.exists()) {
	          throw new IOException(theFile.getAbsolutePath() + " does not exist");
	      }
	      
	      if (!buildDirectory(targetDir)) {
	          throw new IOException("Could not create directory: " + targetDir);
	      }
	      
	      ZipFile zipFile = new ZipFile(theFile);
	      
	      pm.setMaximum( zipFile.size() );
	      int c = 0;
	      
	      for (Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
	          ZipEntry entry = (ZipEntry) entries.nextElement();
	          
	          File file = new File(targetDir, File.separator + entry.getName());
	          
	          pm.setProgress( c++ );
	          pm.setNote( "extracting "+ file.getName() );
	          
	          if (!buildDirectory(file.getParentFile())) {
	        	  zipFile.close();
	        	  pm.close();
	        	  JOptionPane.showMessageDialog( null, "failed to created "+ file.getParentFile() );
	              throw new IOException("Could not create directory: " + file.getParentFile());
	          }
	          if (!entry.isDirectory()) {
	              copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)), pm);
	          } else {
	              if (!buildDirectory(file)) {
	            	  zipFile.close();
	            	  JOptionPane.showMessageDialog( null, "failed to create "+ file );
	            	  pm.close();
	                  throw new IOException("Could not create directory: " + file);
	              }
	          }
	      }
	      zipFile.close();
	      return theFile;
	  }

	  public static void copyInputStream(InputStream in, OutputStream out, ProgressMonitor pm) throws IOException {
	      byte[] buffer = new byte[1024];
	      int len = in.read(buffer);
	      
	      while (len >= 0) {
	          out.write(buffer, 0, len);
	          len = in.read(buffer);
	          pm.setProgress( (int) ( Math.random() * 100 ) );
	      }
	      in.close();
	      out.close();
	  }

	  public static boolean buildDirectory(File file) {
	      return file.exists() || file.mkdirs();
	  }
}
