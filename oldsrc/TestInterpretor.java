package main;

import java.awt.Point;
import java.util.ArrayList;

import img.*;

public class TestInterpretor 
{
	public static void main(String[] args)
	{
		new TestInterpretor().go();
	}
	public void go()
	{
		IntBitmap map = IntBitmap.getInstance(ImageToolkit.loadImage("img/MinimapA0.bmp"));
		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		filters.add(FilterType.CAVE_WATER);
		filters.add(FilterType.CAVE_JUNK);
		//filters.add(FilterType.CAVE_BRIDGE);
		RatioFilter.eliminateRatio(map, filters);
		//MidpassFilter.maintainRanges(map, MidpassFilterType.BRIDGES);
		map.export("img/wipedJunk.bmp");
		Display.showHang(map);
		
		MidpassFilter.maintainRanges(map, MidpassFilterType.BRIDGES);
		Bleeder bridgeBleeder = new Bleeder(1);
		
		int[][][] mapData = map.getData();
		for(BleedResult result : bridgeBleeder.find(map.toGreyscale().doubleCutoff(50)))
		{
			if(result.getNumPixels() < 10)
			{
				continue;
			}
			Point origin = result.getOrigin();
			Point dest = result.getDest();
			for(int a = 0; a < IntBitmap.RGB; a++)
			{
				mapData[origin.x][origin.y][a] = 255;
			}
			for(int a = 0; a < IntBitmap.RGB; a++)
			{
				mapData[dest.x][dest.y][a] = 255;
			}
		}
		map.export("img/wipedJunk.bmp");
		//Display.showHang(map);
	}
}
