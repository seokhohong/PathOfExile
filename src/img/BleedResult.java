package img;

import img.Bleeder.BleedPoint;

import java.awt.Point;
import java.awt.Rectangle;

public class BleedResult 
{
	private Rectangle rect;			public Rectangle toRectangle() { return rect; }
									public Point getCenter() { return new Point(rect.x + rect.width / 2, rect.y + rect.height / 2); }
	private int numPixels;			public int getNumPixels() { return numPixels; }
	
	private Point origin;			public Point getOrigin() { return origin; }
	private Point dest;				public Point getDest() { return dest; }
	
	public BleedResult(Rectangle rect, int numPixels, BleedPoint origin, BleedPoint dest)
	{
		this.rect = rect;
		this.numPixels = numPixels;
		this.origin = origin.toPoint();
		this.dest = dest.toPoint();
	}
	@Override
	public String toString()
	{
		return rect + " : "+numPixels;
	}
}
