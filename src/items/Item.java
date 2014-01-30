package items;

import java.awt.Point;
import java.awt.Rectangle;

import window.PWindow;

public class Item implements Comparable<Item>
{
	private Rectangle location;
	public Item(Rectangle rect)
	{
		this.location = rect;
	}
	public int getX()
	{
		return location.x;
	}
	public int getY()
	{
		return location.y;
	}
	public int getWidth()
	{
		return location.width;
	}
	public int getHeight()
	{
		return location.height;
	}
	public Rectangle getRectangle()
	{
		return location;
	}
	public Point toPoint()
	{
		return new Point(location.x, location.y);
	}
	public Point getCenter()
	{
		return new Point(location.x + location.width / 2, location.y + location.height / 2);
	}
	public double distFromWindowCenter()
	{
		return toPoint().distance(PWindow.getWindowCenter());
	}
	@Override
	public String toString()
	{
		return location.toString();
	}
	@Override
	public int compareTo(Item otherItem) 
	{
		Point center = new Point(PWindow.getWidth() / 2, PWindow.getHeight() / 2);
		double thisDist = center.distance(toPoint());
		double otherDist = center.distance(otherItem.toPoint());
		//System.out.println("Dist Between "+toPoint()+" To Center is "+thisDist);
		//System.out.println("Dist Between "+otherItem.toPoint()+" To Center is "+otherDist);
		if(thisDist < otherDist)
		{
			return -1;
		}
		else if(thisDist > otherDist)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
}
