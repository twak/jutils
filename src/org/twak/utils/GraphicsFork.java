
package org.twak.utils;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * @author twak
 */
public class GraphicsFork extends Graphics2D
{
    public Graphics2D a, b;
    
    public GraphicsFork (Graphics2D a, Graphics2D b)
    {
        Graphics2D dummy = null;
        if (a== null || b == null )
            dummy = (Graphics2D) new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB).getGraphics();

        this.a = a == null ? dummy :a;
        this.b = b == null ? dummy : b;
    }
    
    @Override
    public void draw( Shape s )
    {
        a.draw(s);
        b.draw(s);
    }

    @Override
    public boolean drawImage( Image img, AffineTransform xform, ImageObserver obs )
    {
        a.drawImage(img, xform, obs);
        return b.drawImage(img, xform, obs);
    }

    @Override
    public void drawImage( BufferedImage img, BufferedImageOp op, int x, int y )
    {
        a.drawImage(img, op, x, y);
        b.drawImage(img, op, x, y);
    }

    @Override
    public void drawRenderedImage( RenderedImage img, AffineTransform xform )
    {
        a.drawRenderedImage(img, xform);
        b.drawRenderedImage(img, xform);
    }

    @Override
    public void drawRenderableImage( RenderableImage img, AffineTransform xform )
    {
        a.drawRenderableImage(img, xform);
        b.drawRenderableImage(img, xform);
    }

    @Override
    public void drawString( String str, int x, int y )
    {
        a.drawString(str, x, y);
        b.drawString(str, x, y);
    }

    @Override
    public void drawString( String str, float x, float y )
    {
        a.drawString(str, x, y);
        b.drawString(str, x, y);
    }

    @Override
    public void drawString( AttributedCharacterIterator iterator, int x, int y )
    {
        a.drawString(iterator, x, y);
        b.drawString(iterator, x, y);
    }

    @Override
    public void drawString( AttributedCharacterIterator iterator, float x, float y )
    {
        a.drawString(iterator, x, y);
        b.drawString(iterator, x, y);
    }

    @Override
    public void drawGlyphVector( GlyphVector g, float x, float y )
    {
        a.drawGlyphVector(g, x, y);
        b.drawGlyphVector(g, x, y);
    }

    @Override
    public void fill( Shape s )
    {
        a.fill(s);
        b.fill(s);
    }

    @Override
    public boolean hit( Rectangle rect, Shape s, boolean onStroke )
    {
        a.hit(rect, s, onStroke);
        return b.hit(rect, s, onStroke);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration()
    {
        return b.getDeviceConfiguration();
    }

    @Override
    public void setComposite( Composite comp )
    {
        a.setComposite(comp);
        b.setComposite(comp);
    }

    @Override
    public void setPaint( Paint paint )
    {
        a.setPaint(paint);
        b.setPaint(paint);
    }

    @Override
    public void setStroke( Stroke s )
    {
        a.setStroke(s);
        b.setStroke(s);
    }

    @Override
    public void setRenderingHint( Key hintKey, Object hintValue )
    {
        a.setRenderingHint(hintKey, hintValue);
        b.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint( Key hintKey )
    {
        return b.getRenderingHint(hintKey);
    }

    @Override
    public void setRenderingHints( Map<?, ?> hints )
    {
        a.setRenderingHints(hints);
        b.setRenderingHints(hints);
    }

    @Override
    public void addRenderingHints( Map<?, ?> hints )
    {
        a.addRenderingHints(hints);
        b.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints()
    {
        return b.getRenderingHints();
    }

    @Override
    public void translate( int x, int y )
    {
        a.translate(x, y);
        b.translate(x, y);
    }

    @Override
    public void translate( double tx, double ty )
    {
        a.translate(tx, ty);
        b.translate(tx, ty);
    }

    @Override
    public void rotate( double theta )
    {
        a.rotate(theta);
        b.rotate(theta);
    }

    @Override
    public void rotate( double theta, double x, double y )
    {
        a.rotate(theta,x,y);
        b.rotate(theta,x,y);
    }

    @Override
    public void scale( double sx, double sy )
    {
        a.scale(sx, sy);
        b.scale(sx, sy);
    }

    @Override
    public void shear( double shx, double shy )
    {
        a.shear(shx, shy);
        b.shear(shx, shy);
    }

    @Override
    public void transform( AffineTransform Tx )
    {
        a.transform(Tx);
        b.transform(Tx);
    }

    @Override
    public void setTransform( AffineTransform Tx )
    {
         a.setTransform(Tx);
         b.setTransform(Tx);
    }

    @Override
    public AffineTransform getTransform()
    {
        return b.getTransform();
    }

    @Override
    public Paint getPaint()
    {
        return b.getPaint();
    }

    @Override
    public Composite getComposite()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void setBackground( Color color )
    {
        a.setBackground(color);
        b.setBackground(color);
    }

    @Override
    public Color getBackground()
    {
        return b.getBackground();
    }

    @Override
    public Stroke getStroke()
    {
        return b.getStroke();
    }

    @Override
    public void clip( Shape s )
    {
        a.clip(s);
        b.clip(s);
    }

    @Override
    public FontRenderContext getFontRenderContext()
    {
        return b.getFontRenderContext();
    }

    @Override
    public Graphics create()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public Color getColor()
    {
        return b.getColor();
    }

    @Override
    public void setColor( Color c )
    {
        a.setColor(c);
        b.setColor(c);
    }

    @Override
    public void setPaintMode()
    {
        a.setPaintMode();
        b.setPaintMode();
    }

    @Override
    public void setXORMode( Color c1 )
    {
        a.setXORMode(c1);
        b.setXORMode(c1);
    }

    @Override
    public Font getFont()
    {
        return b.getFont();
    }

    @Override
    public void setFont( Font font )
    {
        a.setFont(font);
        b.setFont(font);
    }

    @Override
    public FontMetrics getFontMetrics( Font f )
    {
       return b.getFontMetrics();
    }

    @Override
    public Rectangle getClipBounds()
    {
        return b.getClipBounds();
    }

    @Override
    public void clipRect( int x, int y, int width, int height )
    {
        a.clipRect(x, y, width, height);
        b.clipRect(x, y, width, height);
    }

    @Override
    public void setClip( int x, int y, int width, int height )
    {
        a.setClip(x, y, width, height);
        b.setClip(x, y, width, height);
    }

    @Override
    public Shape getClip()
    {
        return b.getClip();
    }

    @Override
    public void setClip( Shape clip )
    {
        a.setClip(clip);
        b.setClip(clip);
    }

    @Override
    public void copyArea( int x, int y, int width, int height, int dx, int dy )
    {
        a.copyArea(x, y, width, height, dx, dy);
        b.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public void drawLine( int x1, int y1, int x2, int y2 )
    {
        a.drawLine(x1, y1, x2, y2);
        b.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void fillRect( int x, int y, int width, int height )
    {
        a.fillRect(x, y, width, height);
        b.fillRect(x, y, width, height);
    }

    @Override
    public void clearRect( int x, int y, int width, int height )
    {
        a.clearRect(x, y, width, height);
        b.clearRect(x, y, width, height);
    }

    @Override
    public void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight )
    {
        a.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        b.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight )
    {
        a.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        b.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawOval( int x, int y, int width, int height )
    {
        a.drawOval(x, y, width, height);
        b.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval( int x, int y, int width, int height )
    {
        a.fillOval(x, y, width, height);
        b.fillOval(x, y, width, height);
    }

    @Override
    public void drawArc( int x, int y, int width, int height, int startAngle, int arcAngle )
    {
        a.drawArc(x, y, width, height, startAngle, arcAngle);
        b.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillArc( int x, int y, int width, int height, int startAngle, int arcAngle )
    {
        a.fillArc(x, y, width, height, startAngle, arcAngle);
        b.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawPolyline( int[] xPoints, int[] yPoints, int nPoints )
    {
        a.drawPolyline(xPoints, yPoints, nPoints);
        b.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolygon( int[] xPoints, int[] yPoints, int nPoints )
    {
        a.drawPolygon(xPoints, yPoints, nPoints);
        b.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillPolygon( int[] xPoints, int[] yPoints, int nPoints )
    {
        a.fillPolygon(xPoints, yPoints, nPoints);
        b.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public boolean drawImage( Image img, int x, int y, ImageObserver observer )
    {
        a.drawImage( img, x, y, observer );
        return b.drawImage( img, x, y, observer );
    }

    @Override
    public boolean drawImage( Image img, int x, int y, int width, int height, ImageObserver observer )
    {
        a.drawImage(img, x, y, width, height, observer);
        return b.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage( Image img, int x, int y, Color bgcolor, ImageObserver observer )
    {
        a.drawImage(img, x, y, bgcolor, observer);
        return b.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage( Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer )
    {
        a.drawImage(img, x, y, width, height, bgcolor, observer);
        return b.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer )
    {
        a.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
        return b.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer )
    {
        a.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
        return b.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public void dispose()
    {
        a.dispose();
        b.dispose();
    }
}
