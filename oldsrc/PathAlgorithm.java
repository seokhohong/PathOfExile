package map;

import java.awt.Point;

/**
 * Subclasses have various ways to find paths through a given 2D space
 * 
 * The various path algorithms deal with an unencapsulated ArrayList<Point> rather than a Path object to allow
 * the AStar (and perhaps other) class(es) to be ported more easily to other programs. 
 */
public abstract class PathAlgorithm 
{
	//# times the initial H-value where AStar gives up
	//Nodes it will expand before CA* gives up
	static final int G_LIMIT = 10000; 
	static final float H_MULT = 0.99f; //prioritize nodes closer to their goal

	public static int manhattanDist(Point a, Point b)
	{
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); 
	}
	/**
	 * Returns the manhattan distance from the origin to the destination
	 * @param pf	: Marker of frame
	 * @return		: Distance between origin and distance
	 */
	public static int manhattanDist(PathFrame pf)
	{
		return manhattanDist(pf.getOrigin(), pf.getDestination()); 
	}
}

@SuppressWarnings("serial")
class Node extends Point implements Comparable<Node>
{
	Node(int x, int y)
	{
		super(x, y);
	}
	Node(Point p)
	{
		super(p.x, p.y);
	}
	int f;
	float g;
	Node parent;
	public boolean equalsLocation(Point p)
	{
		return x == p.x && y == p.y;
	}
	@Override
	public int compareTo(Node other)
	{
		if(other.g<g)
		{
			return 1;
		}
		else if(other.g>g)
		{
			return -1;
		}
		else //compare f values after g values
		{
			if(other.f<f)
			{
				return 1;
			}
			else if(other.f>f)
			{
				return -1;
			}
			return 0;
		}
	}
}