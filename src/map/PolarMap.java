package map;

import img.BinaryImage;
import macro.LogoutException;

public class PolarMap 
{
	BinaryImage map;
	public PolarMap(BinaryImage polarMap)
	{
		map = polarMap;
	}
	private static final int CENTER_RADIUS = 7; //pixels in center to disregard
	private static final int DEG_PER_INC = 5; //degrees per increment
	private static final int DEG_PER_2PI = 360;
	private static final int MAX_LENGTH = 150 / 2; //maps are 150 in radius and its unfair to penalize side-ways directions for corner directions
	private int[] polarSearch() throws LogoutException
	{
		boolean data[][] = map.getData();
		int[] radius = new int[DEG_PER_2PI/DEG_PER_INC];
		for(int a = 0; a < radius.length; a++)
		{
			int dist = CENTER_RADIUS; //effectively clear the center out
			while(true)
			{
				int x = (int) (dist * Math.cos(Math.toRadians(a*DEG_PER_INC))) + map.getWidth() / 2;
				int y = (int) (dist * Math.sin(Math.toRadians(a*DEG_PER_INC))) + map.getHeight() / 2;
				if(x < 0 || y < 0 || x > map.getWidth()- 1 || y > map.getHeight() - 1) //out of bounds
				{
					break;
				}
				if(data[x][y] == BinaryImage.BLACK) break;
				dist++;
			}
			radius[a] = Math.min(dist, MAX_LENGTH);
		}
		if(isStuck(radius))
		{
			throw new LogoutException("Stuck");
		}
		//copy.display(true);
		return radius;
	}
	private static final int STUCK_THRESHOLD = CENTER_RADIUS + 5;
	private boolean isStuck(int[] pDists)
	{
		for(int dist : pDists)
		{
			if(dist > STUCK_THRESHOLD)
			{
				return false;
			}
		}
		return true;
	}
	//returns angle and distance that can be traveled at this angle
	public double bestMovement(double currAngle) throws LogoutException
	{
		int[] polar = polarSearch();
		double bestAngle = 0;
		double bestScore = 0;
		for(int a = 0; a < polar.length; a++)
		{
			//0 through 180
			double angleScore = getShortestAngle(currAngle, a * DEG_PER_INC) / 2;
			if(polar[a] - angleScore > bestScore)
			{
				bestScore = polar[a] - angleScore;
				bestAngle = a * DEG_PER_INC;
			}
		}
		return bestAngle;
	}
	private static double getShortestAngle(double currAngle, int ang2)
	{
		double angle = Math.abs(currAngle - ang2) % (Math.PI * 2);
		if(angle > DEG_PER_2PI / 2)
		{
			angle = DEG_PER_2PI - angle;
		}
		return angle;
	}
}
