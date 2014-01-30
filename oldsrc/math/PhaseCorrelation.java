package math;

import java.awt.Point;

import img.*;

public class PhaseCorrelation 
{
	public static Point getOffset(GreyscaleImage img1, GreyscaleImage img2)
	{
		ForwardTransform forward1 = new ForwardTransform(img1);
		ForwardTransform forward2 = new ForwardTransform(img2);
		InverseTransform inverse = new InverseTransform(new PhaseCorrelator(forward1, forward2));
		Point peak = new PeakFinder(inverse).getPeak();
		Point[] matchPoints = getMatchPoints(peak, img1.getWidth(), img1.getHeight());
		Point smallest = null;
		Point origin = new Point(0, 0);
		double minDist = Integer.MAX_VALUE;
		for(Point p : matchPoints)
		{
			if(p.distance(origin) < minDist)
			{
				minDist = p.distance(origin);
				smallest = p;
			}
		}
		return smallest;
		/*
		//Display.showHang(FourierTransform.inverseTransform(FourierTransform.forwardTransform(img1)).getRealImage());
		double[][] fourierData1 = FourierTransform.forwardTransform(new FourierDataSet(img1)).getFourierData();
		double[][] fourierData2 = FourierTransform.forwardTransform(new FourierDataSet(img2)).getFourierData();
		//second dimension length should already be twice the first
		Complex[][] product = new Complex[fourierData1.length][fourierData1[0].length/2];
		for(int a = 0; a < img1.getWidth(); a++)
		{
			for(int b = 0; b < img1.getHeight(); b++)
			{
				Complex img1Point = new Complex(fourierData1[a][2*b], fourierData1[a][2*b + 1], Basis.RECT);
				Complex img2Point = new Complex(fourierData2[a][2*b], fourierData2[a][2*b + 1], Basis.RECT); 
				product[a][b] = Complex.multiply(img1Point, img2Point.conjugate());
				product[a][b].normalize();
			}
		}
		Hanning.window(product);
		GreyscaleImage productImage = FourierTransform.inverseTransform(new FourierDataSet(product)).getRealImage();
		//Display.showHang(productImage);
		int[][] imgData = productImage.getData();
		int highest = 0;
		Point bestPoint = null;
		for(int a = 0; a < imgData.length; a++)
		{
			for(int b = 0; b < imgData[0].length; b++)
			{
				if(imgData[a][b] > highest)
				{
					highest = imgData[a][b];
					bestPoint = new Point(a, b);
				}
			}
		}
		if(bestPoint.x > img1.getWidth() / 2)
		{
			bestPoint.x = img1.getWidth() - bestPoint.x;
		}
		if(bestPoint.y > img1.getHeight() / 2)
		{
			bestPoint.y = img1.getHeight() - bestPoint.y;
		}
		return new Point(bestPoint.x, bestPoint.y);
		*/
	}
	public static Point[] getMatchPoints(Point peak, int width, int height)
	{
		Point[] points = new Point[4];
		points[0] = peak;
		points[1] = new Point(peak.x - width, peak.y);
		points[2] = new Point(peak.x , peak.y - height);
		points[3] = new Point(peak.x - width, peak.y - height);
		return points;
	}
}
