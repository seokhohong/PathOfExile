package geom;

import java.awt.Point;
import java.util.ArrayList;

public class MathTools 
{
	public static int manhattanDist(Point one, Point two)
	{
		return Math.abs(one.x - two.x) + Math.abs(one.y - two.y);
	}
	public static int getIndexMin(ArrayList<Double> input)
	{
		int min = 0;
		for(int i = 0; i < input.size(); i++)
		{
			if(input.get(i) < input.get(min))
			{
				min = i;
			}
		}
		return min;
	}
	public static double getMinValue(ArrayList<Double> input)
	{
		return input.get(MathTools.getIndexMin(input));
	}
	
	public static int getIndexMax(ArrayList<Double> input)
	{
		int max = 0;
		for(int i = 0; i < input.size(); i++)
		{
			if(input.get(i) > input.get(max))
			{
				max = i;
			}
		}
		return max;
	}
	public static double getMaxValue(ArrayList<Double> input)
	{
		return input.get(MathTools.getIndexMax(input));
	}
	
	public static double sumOfValues(ArrayList<Double> values)
	{
		double result = 0;
		for(int i = 0; i < values.size(); i++)
		{
			result += values.get(i);
		}
		return result;
	}
	
	public static double squared(double num)
	{
		return num * num;
	}
	
}
