package main;

import img.BinaryImage;
import img.ImageToolkit;
import img.IntBitmap;

public class MakeMap 
{
	public static void main(String[] args)
	{
		new MakeMap().go();
	}
	private void go()
	{
		IntBitmap map = IntBitmap.getInstance(ImageToolkit.loadImage("map\\CrossroadsMap.bmp"));
		BinaryImage bin = map.toGreyscale().doubleCutoff(1);
		bin.export("map\\CrossroadsMap.bmp");
	}
}
