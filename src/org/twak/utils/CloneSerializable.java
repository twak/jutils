package org.twak.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
}
