package main;

import java.util.ArrayList;

import img.BinaryImage;
import img.Display;
import img.FilterType;
import img.ImageToolkit;
import img.IntBitmap;
import img.MidpassFilterType;
import img.RatioFilter;
import img.MidpassFilter;

public class TestWaterFinder 
{
	public static void main(String[] dafuq)
	{
		new TestWaterFinder().go();
	}
	private void go()
	{
		IntBitmap water = IntBitmap.getInstance(ImageToolkit.loadImage("img/Water11.bmp"));
		water.lowPass(40);
		IntBitmap water2 = IntBitmap.copy(water);
		IntBitmap water3 = IntBitmap.copy(water);
		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		filters.add(FilterType.ACTUAL_CAVE_WATER1);
		filters.add(FilterType.ACTUAL_CAVE_WATER2);
		RatioFilter.maintainRatio(water, filters);
		MidpassFilter.maintainRanges(water2, MidpassFilterType.CAVE_DARK_WATER2);
		MidpassFilter.maintainRanges(water3, MidpassFilterType.CAVE_DARK_WATER3);
		BinaryImage b2 = water2.toGreyscale().doubleCutoff(10);
		BinaryImage b3 = water3.toGreyscale().doubleCutoff(10);
		BinaryImage result = water.toGreyscale().doubleCutoff(20);
		//result.add(b2);
		b2.add(b3);
		b2.killLoners(0);
		Display.showHang(b2);
	}
}
