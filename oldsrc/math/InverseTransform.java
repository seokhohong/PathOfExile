package math;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

public class InverseTransform 
{
	private double[][] realMatrix;		double[][] getRealMatrix() { return realMatrix; }
	
	private int width;		public int getWidth() { return width; }
	private int height;		public int getHeight() { return height; }
	
	public InverseTransform(PhaseCorrelator phase)
	{
		width = phase.getWidth();
		height = phase.getHeight();
		double[][] expandedMatrix = new Hanning(phase).getWindowed();
		new DoubleFFT_2D(width, height).complexInverse(expandedMatrix, true);
		realMatrix = new double[width][height];
		extractReal(expandedMatrix);
	}
	//Adds imaginary data to expand the second dimension of the matrix two fold
	private void extractReal(double[][] expandedData)
	{
		for(int a = 0; a < width; a++)
		{
			for(int b = 0; b < height; b++)
			{
				realMatrix[a][b] = expandedData[a][2 * b];
			}
		}
	}
}
