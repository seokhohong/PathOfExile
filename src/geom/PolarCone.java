package geom;

import java.util.ArrayList;

/**
 * Creates a mathematical region equivalent to the sector of a circle in polar coordinates. 
 * 
 * Was created specifically for determining ranger optimal attack angle using HP bars.
 * 
 * @author Jamison
 *
 */

public class PolarCone 
{
	private double start;
	private double terminal;
	
	private static final int MIN_DIST = 0;
	
	public PolarCone(double start, double span) //span will set region termination angle counterclockwise from the start
	{
		this.start = start;
		terminal = start + span;
	}
	
	/**
	 * Returns whether or not a PolarPoint p is within the bounds of the polar cone sector
	 * 
	 * @param p
	 * @return 
	 */
	public boolean overlapCheck(PolarPoint p)
	{
		//Has issues with points too close to center
		return p.getMagnitude() > MIN_DIST && p.getAngle() >= start && p.getAngle() <= terminal;
	}
	/**
	 * Returns the number of points in an input array list which fall into the polar cone region.
	 * 
	 * @param points
	 * @return
	 */
	public int overlapCount(ArrayList<PolarPoint> points)
	{
		int hitCount = 0; 
		for(int i = 0; i < points.size(); i++)
		{
			if(overlapCheck(points.get(i)))
			{
				hitCount++;
			}
		}
		return hitCount;
	}
}
