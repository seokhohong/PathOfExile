package main;

import java.awt.Point;
import java.util.ArrayList;

import geom.MathTools;
import img.IntBitmap;
import img.ImageToolkit;

public class TestChest 
{
	public static void main(String[] args)
	{
		new TestChest().go();
	}
	private void go()
	{
		/*IntBitmap img = IntBitmap.getInstance(ImageToolkit.loadImage("img/chest.png"));
		PWindow window = PWindow.getWindows(img).get(0);
		ArrayList<Item> items = window.findChests(img.subimage(window.getITEM_SCAN_RECT()));
		System.out.println();*/
		//chests are 26, tribal chests are 58
		
		IntBitmap img = IntBitmap.getInstance(ImageToolkit.loadImage("img/tribalChest3.bmp"));
		//img.isolate(Color.red);
		//img.export("img/chestBlotch.bmp");
		ArrayList<Point> points = new ArrayList<Point>();
		for(int a=0; a<6; a++)
		{
			for(int b=0; b<25; b++)
			{
				points.add(new Point(a,b));
			}
		}
		//ImageToolkit.ratioTool(img, points);
		
		/*RatioFilter.maintainRatio(img, FilterType.TRIBAL_CHEST);
		GreyscaleImage thisOne = img.toGreyscale();
		thisOne.highPass(90);
		thisOne.display(true);
		BinaryImage bin = thisOne.doubleCutoff(110);
		bin.killLoners(3);
		//System.out.println(bin.startRightSpread(6, 6));
		bin.toGreyscale().display(true);
		
		//result.killLoners(2);
		//result.toGreyscale().display(true);*/
		
		ArrayList<Double> ints = new ArrayList<Double>();
		ints.add(4d);
		ints.add(1d);
		ints.add(7d);
		ints.add(0d);
		ints.add(699d);
		ints.add(42d);
		System.out.println(MathTools.getMaxValue(ints));
	}
}
