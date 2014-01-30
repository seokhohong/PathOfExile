package img;


import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import window.PWindow;

public class Bleeder 
{
	private int threshold;
	public Bleeder(int threshold)
	{
		this.threshold = threshold;
	}
	/**
	 * 
	 * Invoke this method to run the bleeder on the input image
	 * 
	 * @param image		: image to bleed
	 * @return			: A list of all BleedResults
	 */
	public ArrayList<BleedResult> find(BinaryImage image)
	{
		ArrayList<BleedResult> result = new ArrayList<BleedResult>();
		boolean[][] data = image.getData();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				if(data[a][b] == BinaryImage.WHITE)
				{
					result.add(bleed(data, a, b));
				}
			}
		}
		return result;
	}
	private BleedResult bleed(boolean[][] data, int a, int b)
	{
		HashSet<BleedPoint> usedPoints = new HashSet<BleedPoint>();
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MIN_VALUE;
		int numBled = 0;	//number of white pixels touched
		Queue<BleedPoint> bleedPoints = new LinkedList<BleedPoint>();
		BleedPoint origin = new BleedPoint(a, b, 0);
		bleedPoints.add(origin);
		BleedPoint dest = null;
		while(!bleedPoints.isEmpty()) //breadth-first search
		{
			BleedPoint curr = bleedPoints.poll();
			if(data[curr.getX()][curr.getY()] == BinaryImage.WHITE)
			{
				dest = curr;
				left = Math.min(left, curr.getX());		//expand boundaries
				right = Math.max(right, curr.getX());				
				top = Math.min(top, curr.getY());
				bottom = Math.max(bottom, curr.getY());
				numBled ++ ;
			}
			data[curr.getX()][curr.getY()] = BinaryImage.BLACK; //blacken the white pixel
			ArrayList<BleedPoint> neighbors = curr.getNeighbors(data, usedPoints);	//bleed to other neighbors
			for(BleedPoint point : neighbors)
			{
				bleedPoints.add(point);
			}
		}
		return new BleedResult(new Rectangle(left, top, right - left, bottom - top), numBled, origin, dest);
	}
	/**
	 * 
	 * Removes all bleed results below the threshold
	 * 
	 * @param threshold
	 */
	public static void removeWeakResults(ArrayList<BleedResult> results, int threshold)
	{
		Iterator<BleedResult> iter = results.iterator();
		while(iter.hasNext())
		{
			BleedResult br = iter.next();
			if(br.getNumPixels() < threshold)
			{
				iter.remove();
			}
		}
	}
	class BleedPoint
	{
		private int x;
		private int y;
		private int counter;
		
		public BleedPoint(int x, int y, int counter)
		{
			this.x = x;
			this.y = y;
			this.counter = counter;
		}
		public ArrayList<BleedPoint> getNeighbors(boolean[][] data, HashSet<BleedPoint> usedPoints)
		{
			ArrayList<BleedPoint> neighbors = new ArrayList<BleedPoint>();
			if(x > 0)
			{
				neighbors.add(new BleedPoint(x-1, y, data[x-1][y] == BinaryImage.BLACK ? counter + 1 : 0));
			}
			if(x < data.length - 1)
			{
				neighbors.add(new BleedPoint(x+1, y, data[x+1][y] == BinaryImage.BLACK ? counter + 1 : 0));
			}
			if(y > 0)
			{
				neighbors.add(new BleedPoint(x, y-1, data[x][y-1] == BinaryImage.BLACK ? counter + 1 : 0));
			}
			if(y < data[0].length - 1)
			{
				neighbors.add(new BleedPoint(x, y+1, data[x][y+1] == BinaryImage.BLACK ? counter + 1 : 0));
			} 
			ArrayList<BleedPoint> validNeighbors = new ArrayList<BleedPoint>();
			for(BleedPoint neighbor : neighbors)
			{
				if(!usedPoints.contains(neighbor) && neighbor.counter < threshold) //avoids repeats and bleeding too far
				{
					usedPoints.add(neighbor);
					validNeighbors.add(neighbor);
				}
			}
			return validNeighbors;
		}
		public int getX()
		{
			return x;
		}
		public int getY()
		{
			return y;
		}
		public Point toPoint()
		{
			return new Point(x, y);
		}
		@Override
		public String toString()
		{
			return x+" "+y;
		}
		@Override
		public boolean equals(Object o)
		{
			if(o instanceof BleedPoint)
			{
				BleedPoint other = (BleedPoint) o;
				return other.x == x && other.y == y;
			}
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return x * PWindow.getWidth() + y;
		}
	}
}

