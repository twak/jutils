package org.twak.utils;

import org.twak.utils.geom.DRectangle;

import javax.vecmath.Point2d;

public class FixedPanMA extends PanMouseAdaptor {

    DRectangle bounds;

    public FixedPanMA ( DRectangle bounds ) {

        super ( new Point2d(bounds.x, bounds.y),  Math.max(bounds.width, bounds.height));
        this.comp = null;
        this.bounds = bounds;

    }

    public int compGetWidth() {
        return (int)bounds.width;
    }

    public int compGetHeight() {
        return (int) bounds.height;
    }

}
