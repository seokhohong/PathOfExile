package arena;

import java.util.ArrayList;

import window.PWindow;


import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

public class TheCityOfSarn extends Arena
{
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new TheCityOfSarn(a));
		}
		return arenas;
	}
	private TheCityOfSarn(int level) 
	{
		super("The City Of Sarn", level, 3);
		addFilter(FilterType.SARN_DARKSTONE);
		addFilter(FilterType.SARN_LIGHTSTONE);
		addFilter(FilterType.SARN_STONE);
		addFilter(FilterType.SARN_LIGHTROOF);
		addFilter(FilterType.WAYPOINT);
	}
	@Override
	public double startAngle()
	{
		return Math.PI;
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
		window.leftClick(265, 240);
	}
}
