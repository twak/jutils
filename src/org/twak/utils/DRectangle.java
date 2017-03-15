package org.twak.utils;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.vecmath.Point2d;

/**
 *
 * @author twak
 */
public class DRectangle {
    public double x,y,width,height;

    public DRectangle(){}
    public DRectangle(double x, double y, double width, double height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public DRectangle( DRectangle dr )
    {
        this (dr.x, dr.y, dr.width, dr.height);
    }

    public DRectangle( Rectangle bounds )
    {
        this (bounds.x, bounds.y, bounds.width, bounds.height );
    }
    
    public DRectangle (List <Point2d> envelop)
    {
        this (envelop.get( 0 ).x, envelop.get( 0 ).y, 0, 0 );
        for (Point2d pt : envelop)
            envelop( pt );
    }

    public boolean contains( double X, double Y )
    {
        double w = this.width;
	double h = this.height;
        
	if ( w < 0 || h < 0)
        {
	    // At least one of the dimensions is negative...
	    return false;
	}
        
	if (X < x || Y < y) {
	    return false;
	}
	w += x;
	h += y;
	//    overflow || intersect
	return ((w < x || w > X) &&
		(h < y || h > Y));
    }

    // not sure about this, I like width, height being +ve
    public boolean containsAllowingNegative( double X, double Y )
    {
        double w = this.width;
        double h = this.height;

        if ( w < 0 || h < 0 )
        {
            // At least one of the dimensions is negative...
            DRectangle pos = toPositive();
            return pos.containsAllowingNegative( X, Y );
        }

        if ( X < x || Y < y )
            return false;
        w += x;
        h += y;
        //    overflow || intersect
        return ( ( w < x || w > X ) &&
                ( h < y || h > Y ) );
    }

    public boolean intersects( DRectangle other )
    {
	if (width <= 0 || height <= 0 || other.width <= 0 || other.height <= 0) {
	    return false;
	}
        
	return  other.x + other.width > x &&
		other.y + other.height > y &&
		other.x < x + width &&
		other.y < y + height;
    }

    public Rectangle toInteger()
    {
        return new Rectangle (
                (int)x,
                (int)y,
                (int)width,
                (int)height );
    }

    /**
     * Same bounds, positive height and width
     * @return
     */
    public DRectangle toPositive()
    {
        if (width > 0 && height > 0)
            return this;
        
        return new DRectangle( x + (width < 0 ? width : 0), y + (height < 0 ? height : 0), Math.abs(width), Math.abs(height ) );
    }

    public void setFrom (DRectangle rect)
    {
        this.width = rect.width;
        this.height = rect.height;
        this.x = rect.x;
        this.y = rect.y;
    }

    public double area()
    {
        return Math.abs(width * height);
    }

    public double getMaxX()
    {
        return x + width;
    }

    public double getMaxY()
    {
        return y+height;
    }

    public DRectangle union(DRectangle b)
    {
        DRectangle out = new DRectangle();
        out.x = Math.min( x, b.x );
        out.y = Math.min( y, b.y );
        out.width = Math.max (getMaxX(), b.getMaxX()) - out.x;
        out.height = Math.max (getMaxY(), b.getMaxY()) - out.y;
        
        return out;
    }

    @Override
    public String toString()
    {
        return "( "+x+" ,"+y+" ,"+width + " ,"+ height+")";
    }

    public boolean sameAs (DRectangle o)
    {
        return
                (x == o.x) &&
                (y == o.y) &&
                (width == o.width) &&
                (height == o.height);
    }

    public void set (Object b, double value, boolean keepMax)
    {
        switch ( (Bounds) b)
        {
            case XMIN:
                double delta = value - x;
                x = value;
                if (keepMax)
                    width -= delta;
                break;
            case XMAX:
                width = value-x;
                break;
            case YMIN:
                delta = value - y;
                y = value;
                if (keepMax)
                    height -= delta;
                break;
            case YMAX:
                height = value - y;
                break;
            default:
                throw new Error("WtF?");
        }
    }

    public double get( Object object )
    {
        switch ( (Bounds) object )
        {
            case XMIN:
                return x;
            case XMAX:
                return x + width;
            case YMIN:
                return y;
            case YMAX:
                return y + height;
            default:
                throw new Error("WtF?");
        }
    }

    /**
     * Contains entirity of given rectanlge
     */
    public boolean contains( DRectangle r )
    {
        return
                contains( r.x, r.y ) &&
                contains( r.x+r.width, r.y ) &&
                contains( r.x, r.y+r.height ) &&
                contains( r.x+r.width, r.y+r.height );

    }


    public enum Bounds
    {
        XMIN, XMAX, YMIN, YMAX;
    }

    public final static Bounds
            XMIN = Bounds.XMIN,
            XMAX = Bounds.XMAX,
            YMIN = Bounds.YMIN,
            YMAX = Bounds.YMAX;

    public static class FromComparator implements Comparator<DRectangle>
    {
        Direction dir;
        public FromComparator( Direction l )
        {
            this.dir = l;
        }

        @Override
        public int compare( DRectangle arg0, DRectangle arg1 )
        {
            switch (dir)
            {
                case Left:
                    return Double.compare (arg0.x, arg1.x);
                case Top:
                default:
                    return Double.compare (arg0.y, arg1.y);
            }
        }
    }

    public enum Direction
    {
        Left, Top;
    }

    public final static Direction
            Left = Direction.Left,
            Top = Direction.Top;


    public void envelop(double px, double py) {
        if (px < x) {
            width += x - px;
            x = px;
        }
        else if (px > x + width)
            width = px - x;

        if (py < y) {
            height += y - py;
            y = py;
        }
        else if (py > y + height)
            height = py - y;    }

    public void envelop (Point2d pt) {
        envelop (pt.x, pt.y);
    }
    
    public static class RectDir {
    	public boolean dirX;
    	public DRectangle rect;
    	public RectDir (boolean dir, DRectangle rect) {
    		this.rect = rect;
    		this.dirX = dir;
    	}
    }
    
    public interface WidthGen {
    	public List<Double> gen (RectDir in);
    }
    
    
    public List<DRectangle> split (boolean dir, WidthGen gen) {
    	
    	List <DRectangle> out =new ArrayList<>();
    	
    	List<Double> loc = gen.gen( new RectDir(dir, this ) );
    	
    	if (loc.isEmpty())
    		return out;
    	
    	double sum = (dir ? width : height )/loc.stream().mapToDouble( xx -> xx ).sum();
    	
    	double o = 0;
    	
    	for (double d : loc) {
    		if (dir)
    			out.add(new DRectangle(o + x, y, d * sum, height ));
    		else
    			out.add(new DRectangle(x, o + y, width, d * sum ));
    		o+= d;
    	}
    	
    	return out;
    }
    
    public List<DRectangle> splitX (WidthGen gen) {
    	return split(true, gen);
    }
    public List<DRectangle> splitY (WidthGen gen) {
    	return split(false, gen);
    }
    
	public Point2d[] points() {
		return new Point2d[] {
				new Point2d(x      ,y),
				new Point2d(x      ,y+height) ,
				new Point2d(x+width,y+height),
				new Point2d(x+width,y),
				};
	}
	public float heightF() {
		return (float) height;
	}
	public float widthF() {
		return (float) width;
	}
	public float xF() {
		return (float) x;
	}
	public float yF() {
		return (float) y;
	}
}
