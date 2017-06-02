package org.twak.utils.geom;

import javax.vecmath.*;

/**
 * @author twak
 */
public class OneCosAngle {
    double angle;
    public OneCosAngle (double angle)
    {
        this.angle = angle;
    }

    public boolean withinAngle (Point2d a, Point2d b, Point2d c) {
        Vector2d s = new Vector2d(b), e = new Vector2d(c);
        s.sub (a);
        e.sub (b);

        return s.angle(e) < angle;
    }
}
