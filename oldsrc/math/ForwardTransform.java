package math;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import img.GreyscaleImage;

public class ForwardTransform 
{
	private double[][] expandedMatrix;		
	
	private int width;		public int getWidth() { return width; }
	private int height;		public int getHeight() { return height; }
	
	public ForwardTransform(GreyscaleImage img)
	{
		width = img.getWidth();
		height = img.getHeight();
		expandedMatrix = new double[width][height * 2];
		expandMatrix(img.getDoubleData());
		new DoubleFFT_2D(width, height).complexForward(expandedMatrix);
	}
	//Adds imaginary data to expand the second dimension of the matrix two fold
	private void expandMatrix(double[][] realData)
	{
		for(int a = 0; a < realData.length; a++)
		{
			for(int b = 0; b < realData[0].length; b++)
			{
				expandedMatrix[a][2 * b] = realData[a][b];
				expandedMatrix[a][2 * b + 1] = 0d;
			}
		}
	}
	public Complex[][] getComplexMatrix() 
	{ 
		Complex[][] complexMatrix = new Complex[width][height];
		for(int a = 0; a < width; a++)
		{
			for(int b = 0; b < height; b++)
			{
				complexMatrix[a][b] = new Complex(expandedMatrix[a][2 * b], expandedMatrix[a][2 * b + 1]);
			}
		}
		return complexMatrix; 
	}
}
