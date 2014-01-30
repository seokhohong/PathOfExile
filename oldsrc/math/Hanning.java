package math;

public class Hanning 
{
	private double[][] data;		public double[][] getWindowed() { return data; }		
	Hanning(PhaseCorrelator phase)
	{
		this.data = phase.getDoubleMatrix();
		window();
	}
	public void window()
	{
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length / 2; b++)
			{
				double x = (a - 1) * 2d * Math.PI / (data.length - 1);
				double y = (b - 1) * 2d * Math.PI / (data[0].length - 1);
				double weight = 0.5d * (1.0d - Math.cos(x)) + 0.5d * (1.0d - Math.cos(y));
				data[a][2 * b] *= weight;
				data[a][2 * b + 1] *= weight;
			}
		}
	}
}
