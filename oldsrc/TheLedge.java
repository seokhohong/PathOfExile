package arena;

import java.util.ArrayList;

import window.PWindow;


import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

public class TheLedge extends Arena
{
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new TheLedge(a));
		}
		return arenas;
	}
	private TheLedge(int level) 
	{
		super("The Ledge", level, 1);
		addFilter(FilterType.BROWN_WALL);
		addFilter(FilterType.NEXT_REGION);
		addFilter(FilterType.WATER);
		addFilter(FilterType.WAYPOINT);
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
		window.leftClick(175, 174);
	}
}
