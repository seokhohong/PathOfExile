package main;

import java.awt.Point;
import java.util.*;

import img.*;
import math.*;

public class TestS_ofGP_variety 
{
	public static void main(String[] args) 
	{
		new TestS_ofGP_variety().go();
	}
	public void go()
	{
		ArrayList<String> imagesToSplice = new ArrayList<String>();
		for(int a = 0; a < 3 ; a ++)
		{
			imagesToSplice.add("img/MinimapA"+a+".bmp");
		}
		spliceTogether(imagesToSplice);
		/*
		IntBitmap image = IntBitmap.getInstance(ImageToolkit.loadImage("img/MinimapA0.bmp"));
		GreyscaleImage grey = image.toGreyscale();
		IntBitmap image2 = IntBitmap.getInstance(ImageToolkit.loadImage("img/MinimapA1.bmp"));
		GreyscaleImage grey2 = image2.toGreyscale();
		IntBitmap image3 = IntBitmap.getInstance(ImageToolkit.loadImage("img/MinimapA2.bmp"));
		GreyscaleImage grey3 = image3.toGreyscale();
		Point overlap = PhaseCorrelation.getOffset(grey, grey2);
		Point overlap2 = PhaseCorrelation.getOffset(grey2, grey3);
		System.out.println(overlap);
		System.out.println(overlap2);
		//java.awt.Point[x=5,y=17]
		//java.awt.Point[x=26,y=49]
		Display.showHang(ImageToolkit.splice(image, image2, overlap.x, overlap.y));
		*/
		//Display.showHang(FourierTransform.forwardTransform(grey).getRealImage());
		/*
		IntBitmap image = IntBitmap.getInstance(ImageToolkit.loadImage("img/Minimap1.bmp"));
		GreyscaleImage grey = image.toGreyscale();
		ComplexMatrix img = new ComplexMatrix(grey); 
		Display.showHang(img.fourierTransform().inverseFourierTransform().getReal());
		*/
	}
	private void spliceTogether(ArrayList<String> imagesToSplice)
	{
		ArrayList<IntBitmap> images = new ArrayList<IntBitmap>();
		for(String s : imagesToSplice)
		{
			images.add(IntBitmap.getInstance(ImageToolkit.loadImage(s)));
		}
		for(IntBitmap img : images)
		{
			process(img);
		}
		IntBitmap lastImage = null;
		IntBitmap totalImage = null;
		Point totalOffset = new Point(0, 0);
		for(IntBitmap img : images)
		{
			if(lastImage == null)
			{
				lastImage = img;
				totalImage = img;
				continue;
			}
			Point offset = PhaseCorrelation.getOffset(lastImage.toGreyscale(), img.toGreyscale());
			totalOffset = new Point(totalOffset.x + offset.x, totalOffset.y + offset.y);
			System.out.println("Offset: "+offset+" TotalOffset: "+totalOffset);
			totalImage = ImageToolkit.splice(totalImage, img, totalOffset.x, totalOffset.y);
			Display.show(totalImage);
			lastImage = img;
		}
	}
	private void process(IntBitmap image)
	{
		killWeak(image);
	}
	private static final int WEAK_THRESHOLD = 20;
	private void killWeak(IntBitmap image)
	{
		int[][][] data = image.getData();
		for(int a = 0; a < image.getWidth(); a++)
		{
			for(int b = 0; b < image.getHeight(); b++)
			{
				if(data[a][b][0] < WEAK_THRESHOLD && data[a][b][1] < WEAK_THRESHOLD && data[a][b][2] < WEAK_THRESHOLD)
				{
					data[a][b][0] = 0;
					data[a][b][1] = 0;
					data[a][b][2] = 0;
				}
			}
		}
	}
}
