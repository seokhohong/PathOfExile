package map;

import java.awt.Point;
import java.util.Set;

public class PathFrame 
{
	Point origin;
	Point destination;
	public PathFrame(Point origin, Point destination)
	{
		this.origin = origin;
		this.destination = destination;
	}
	public Point getOrigin()
	{
		return origin;
	}
	public Point getDestination()
	{
		return destination;
	}
	public void relocateDestination(int numMovables, State[][] pathable, Set<Point> occupied)
	{
		for(int r = 1; r < numMovables; r++) //maximum number of rings necessary is the number of destinations total
			//Yes it is also inefficient, as it revisits squares
		{
			//x direction
			for(int a = -r ; a < r ; a++)
			{
				if(destination.getX() + a > 0 && destination.getX() + a < pathable.length)
				{
					//y direction
					for(int b = -r ; b < r ; b++)
					{
						if(destination.getY() + b > 0 && destination.getY() + b < pathable[0].length)
						{
							Point newDest = new Point((int) destination.getX() + a, (int) destination.getY() + b);
							if(!occupied.contains(newDest) && pathable[newDest.x][newDest.y].isPathable())
							{
								destination = newDest;
								occupied.add(newDest); //remember that this Agent occupied it
								return;
							}
						}
					}
				}
			}
		}
	}
}
