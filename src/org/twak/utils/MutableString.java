
package org.twak.utils;

import java.io.Serializable;

/**
 *
 * @author twak
 */
public class MutableString implements Serializable {
    public String val;
    public MutableString(String val)
    {
        this.val = val;
    }
}
