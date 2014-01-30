package geom;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class CoordToolkit 
{
	/**
	 * Centers a Point within a region defined by width and height
	 */
	public static Point toCentered(Point p, int width, int height)
	{
		Point q = new Point();
		q.x = p.x - width / 2;
		q.y = - p.y + height / 2;
		return q;	
	}
	
	/**
	 * Centers a Point within a region defined by width and height
	 */
	public static Point toUncentered(Point p, int width, int height)
	{
		Point q = new Point();
		q.x = p.x + width / 2;
		q.y = -p.y + height / 2;
		return q;	
	}
	
	/**
	 * 
	 * Takes Cartesian coordinates oriented by a (0, 0) and re-orients them according to the toCentered method
	 * 
	 * @param topLefts
	 * @return
	 */
	public static ArrayList<Point> centerAll(ArrayList<Point> topLefts, int width, int height)
	{
		ArrayList<Point> centers = new ArrayList<Point>();
		for(Point p : topLefts)
		{
			centers.add(toCentered(p, width, height));
		}
		return centers;
	}
	/**
	 * 
	 * Converts a list of Cartesian point into PolarPoints
	 * 
	 * @param carts
	 * @return
	 */
	public static ArrayList<PolarPoint> polarAll(List<Point> carts)
	{
		ArrayList<PolarPoint> polars = new ArrayList<PolarPoint>();
		for(Point p : carts)
		{
			polars.add(new PolarPoint(p));
		}
		return polars;
	}
}
