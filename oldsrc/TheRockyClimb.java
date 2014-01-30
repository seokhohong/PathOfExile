package arena;

import java.util.ArrayList;

import window.PWindow;


import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

public class TheRockyClimb extends Arena
{
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new TheRockyClimb(a));
		}
		return arenas;
	}
	private TheRockyClimb(int level) 
	{
		super("The Rocky Climb", level, 1);
		addFilter(FilterType.BROWN_WALL);
		addFilter(FilterType.NEXT_REGION);
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
		window.leftClick(140, 155);
	}
}
