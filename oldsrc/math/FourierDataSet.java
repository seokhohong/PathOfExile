package math;

import img.*;

public class FourierDataSet 
{
	private int[][] imageData;
	private double[][] fourierData;
	private GreyscaleImage realImage;
	private GreyscaleImage imaginaryImage;
	private int fourierDataWidth;
	private int fourierDataHeight;
	
	public FourierDataSet(GreyscaleImage realImage)
	{
		this.realImage = realImage;
		imageData = realImage.getData();
		fourierDataWidth = realImage.getWidth();
		fourierDataHeight = 2*realImage.getHeight();
		fourierData = toFourierData();
	}
	FourierDataSet(double[][] fourierData)
	{
		this.fourierData = fourierData;
		fourierDataWidth = fourierData.length;
		fourierDataHeight = fourierData[0].length;
		realImage = toRealImage();
		imaginaryImage = toImaginaryImage();
	}
	FourierDataSet(Complex[][] complexArray)
	{
		fourierData = new double[complexArray.length][complexArray[0].length * 2];
		for(int a = 0; a < complexArray.length; a++)
		{
			for(int b = 0; b < complexArray[0].length; b++)
			{
				fourierData[a][2*b] = complexArray[a][b].getReal();
				fourierData[a][2*b + 1] = complexArray[a][b].getImaginary();
			}
		}
	}
	private double[][] toFourierData()
	{
		double[][] array = new double[fourierDataWidth][fourierDataHeight];
		for(int a = 0; a < fourierDataWidth; a++)
		{
			for(int b = 0; b < fourierDataHeight; b++)
			{
				if(b%2 == 0)
				{
					array[a][b] = imageData[a][b/2];
				}
				else
				{
					array[a][b] = 0;
				}
			}
		}
		return array;
	}
	private GreyscaleImage toRealImage()
	{
		int[][] imageData = new int[fourierDataWidth][fourierDataHeight/2];
		for(int a = 0; a < fourierDataWidth; a++)
		{
			for(int b = 0; b < fourierDataHeight; b++)
			{
				if(b%2 == 0)
				{
					imageData[a][b/2] = (int) (fourierData[a][b] * 1000);
				}
			}
		}
		return new GreyscaleImage(imageData);
	}
	private GreyscaleImage toImaginaryImage()
	{
		int[][] imageData = new int[fourierDataWidth][fourierDataHeight/2];
		for(int a = 0; a < fourierDataWidth; a++)
		{
			for(int b = 0; b < fourierDataHeight; b++)
			{
				if(b%2 == 1)
				{
					imageData[a][(b - 1)/2] = (int) fourierData[a][b];
				}
			}
		}
		return new GreyscaleImage(imageData);
	}
	public double[][] getFourierData()
	{
		return fourierData;
	}
	public void windowHamming()
	{
		for(int a = 0; a < fourierData.length; a++)
		{
			for(int b = 0; b < fourierData[0].length; b++)
			{
				
			}
		}
	}
	public GreyscaleImage getRealImage()
	{
		return realImage;
	}
	public GreyscaleImage getImaginaryImage()
	{
		return imaginaryImage;
	}
}
