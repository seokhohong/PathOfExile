package geom;

import java.awt.Point;

/**
 * 
 * Converts a Cartesian point to a Polar point 
 * 
 * @author HONG
 *
 */
public class PolarPoint 
{
	private double angle;
	private double mag;
	private Point origPoint;		//Makes defensive copies for this
	public PolarPoint(Point p)
	{
		origPoint = new Point(p);
		angle = Math.atan2(p.y, p.x);
		mag = Math.sqrt(p.x * p.x + p.y * p.y);
	}
	public PolarPoint(double mag, double angle)
	{
		this.mag = mag;
		this.angle = angle;
		origPoint = new Point();
		origPoint.x = (int) (mag * Math.cos(angle));
		origPoint.y = (int) (mag * Math.sin(angle));
	}
	/**
	 * 
	 * Returns the angle in radians
	 * 
	 * @return
	 */
	public double getAngle()
	{
		return angle;
	}
	public double getMagnitude()
	{
		return mag;
	}
	/**
	 * 
	 * Converts this point into Cartesian form, centered at the same location
	 * 
	 * @return
	 */
	public Point toCartesian()	//Could use math, but cleaner without
	{
		return new Point(origPoint);
	}
	@Override
	public String toString()
	{
		return "("+angle+", "+mag+")";
	}
}
