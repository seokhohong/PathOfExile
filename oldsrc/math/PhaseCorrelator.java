package math;

public class PhaseCorrelator 
{
	private Complex[][] product;
	
	private int width;		public int getWidth() { return width; }
	private int height;		public int getHeight() { return height; }
	
	PhaseCorrelator(ForwardTransform ft1, ForwardTransform ft2)
	{
		width = ft1.getWidth();
		height = ft1.getHeight();
		product = new Complex[width][height];
		Complex[][] ft1Data = ft1.getComplexMatrix();
		Complex[][] ft2Data = ft2.getComplexMatrix();
		for(int a = 0; a  < width; a++)
		{
			for(int b = 0; b < height; b++)
			{
				product[a][b] = Complex.multiply(ft1Data[a][b], ft2Data[a][b].conjugate()).normalize();
			}
		}
	}
	double[][] getDoubleMatrix()
	{
		double[][] doubleMatrix = new double[width][height * 2];
		for(int a = 0; a < width; a++)
		{
			for(int b = 0; b < height; b++)
			{
				doubleMatrix[a][2 * b] = product[a][b].getReal();
				doubleMatrix[a][2 * b + 1] = product[a][b].getImaginary();
			}
		}
		return doubleMatrix;
	}
}
