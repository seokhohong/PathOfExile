package arena;

import java.util.ArrayList;

import window.PWindow;


import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

public class DarkForest extends Arena
{
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new DarkForest(a));
		}
		return arenas;
	}
	private DarkForest(int level) 
	{
		super("Dark Forest", level, 2);
		addFilter(FilterType.DARK_FOREST);
		addFilter(FilterType.WATER);
		addFilter(FilterType.EQUAL_20);
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
		window.leftClick(110, 205);
	}
}
