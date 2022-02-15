
package org.twak.utils;

import java.io.Serializable;

/**
 *
 * @author twak
 */
public class MutableBoolean implements Serializable {
    boolean val;

    public MutableBoolean( boolean value )
    {
        this.val = value;
    }

    public void set(boolean value)
    {
        this.val = value;
    }

    public boolean get()
    {
        return val;
    }
}
