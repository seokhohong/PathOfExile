package map;

import java.awt.Point;

/**
 * 
 * Note: This class implements the Comparable interface in such a way that makes it inconsistent with equals
 * 
 * @author Seokho
 *
 */
@SuppressWarnings("serial")
public class Node extends Point implements Comparable<Node>
{
	private static final int LARGE_NUMBER = (int) Math.sqrt(Integer.MAX_VALUE);
	
	private int h = 0;		public void setH(int h) { this.h = h; }		public int getH() { return h; } 
	private int g = 0;		public void setG(int g) { this.g = g; }		public int getG() { return g; }
																		
	private Node parent;	public void setParent(Node parent) { this.parent = parent; }
							public Node getParent() { return parent; }
	
	public Node(Point p)
	{
		super(p);
	}
	
	public Node(int x, int y)
	{
		super(x, y);
	}
	
	public Point toPoint()
	{
		return new Point(x, y);
	}
	
	@Override
	public int compareTo(Node otherNode) 
	{
		if(otherNode.h + otherNode.g > h + g)
		{
			return -1;
		}
		if(otherNode.h + otherNode.g < h + g)
		{
			return 1;
		}
		return 0;
	}
	@Override
	public int hashCode()	//hashes with Point but I'll leave this here though
	{
		return x * LARGE_NUMBER + y;
	}
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Point)
		{
			Point otherNode = (Point) o;
			return otherNode.x == x && otherNode.y == y;
		}
		return false;
	}
	@Override
	public String toString()
	{
		return "("+x+", "+y+")  H: "+h+"  G:"+g;
	}
}
