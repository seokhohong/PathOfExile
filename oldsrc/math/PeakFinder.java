package math;

import java.awt.Point;

/**
 * 
 * Finds the highest value in the double[][]
 * 
 * @author Seokho
 *
 */
public class PeakFinder 
{
	private Point peak;		Point getPeak() { return peak; }
	public PeakFinder(InverseTransform invTransform)
	{
		double[][] arr = invTransform.getRealMatrix();
		int[][] intArr = new int[invTransform.getWidth()][invTransform.getHeight()];
		for(int a = 0; a < invTransform.getWidth(); a++)
		{
			for(int b = 0; b < invTransform.getHeight(); b++)
			{
				intArr[a][b] = (int) (arr[a][b] * 2000);
			}
		}
		//Display.show(new GreyscaleImage(intArr));
		double highestValue = Integer.MIN_VALUE;
		//double secondHighestValue = Integer.MIN_VALUE;
		for(int a = 0; a < invTransform.getWidth(); a++)
		{
			for(int b = 0; b < invTransform.getHeight(); b++)
			{
				if(arr[a][b] > highestValue)
				{
					//secondHighestValue = highestValue;
					highestValue = arr[a][b];
					peak = new Point(a, b);
				}
			}
		}
		//System.out.println(highestValue);
		//System.out.println(secondHighestValue);
	}
}
