package arena;

import java.util.ArrayList;

import window.PWindow;


import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

public class TheWesternForest extends Arena
{
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new TheWesternForest(a));
		}
		return arenas;
	}
	private TheWesternForest(int level) 
	{
		super("The Western Forest", level, 2);
		addFilter(FilterType.WESTERN_FOREST1);
		addFilter(FilterType.EQUAL_20);
		addFilter(FilterType.NEXT_REGION);
		//addFilter(FilterType.EQUAL_20);
	}
	@Override
	public BinaryImage processMap(IntBitmap map)
	{
		RatioFilter.maintainRatio(map, getObstacles());
		return processFiltered(map);
	}
	@Override
	public void clickLocation(PWindow window) 
	{
		window.leftClick(70, 200);
	}
}
