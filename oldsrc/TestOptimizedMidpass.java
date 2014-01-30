package main;

import img.ImageToolkit;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;

public class TestOptimizedMidpass 
{
	public static void main(String[] args)
	{
		new TestOptimizedMidpass().go();
	}
	private void go()
	{
		IntBitmap img = IntBitmap.getInstance(ImageToolkit.loadImage("img/chestOutline.bmp"));
		double time = System.currentTimeMillis();
		MidpassFilter.maintainRanges(img, MidpassFilterType.CURRENCY_BOX);
		System.out.println("Midpass Time "+(System.currentTimeMillis() - time));
		
		img = IntBitmap.getInstance(ImageToolkit.loadImage("img/chestOutline.bmp"));
		time = System.currentTimeMillis();
		MidpassFilter.maintainRanges(img, MidpassFilterType.CURRENCY_BOX);
		System.out.println("Optimized Time "+(System.currentTimeMillis() - time));
	}
}
