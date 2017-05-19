package org.twak.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedOutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.binary.BinaryStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *
 * @author twak
 */
public class CloneSerializable
{
    public static Object clone( Object orig )
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( orig );
            oos.flush();
            oos.close();

            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
            return in.readObject();
        } catch ( Throwable th )
        {
            th.printStackTrace();
            return null;
        }
    }
    
    public static Object xClone (Object orig) {
    	
    	XStream x = new XStream(new BinaryStreamDriver());

    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	x.toXML( orig, bos );
    	
    	return x.fromXML( new ByteArrayInputStream( bos.toByteArray() ) );
    	
//        return XSTREAM.fromXML(XSTREAM.toXML(orig));
    }
}
