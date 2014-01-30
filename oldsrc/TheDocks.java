package arena;

import java.util.ArrayList;

import window.PWindow;


import img.BinaryImage;
import img.IntBitmap;
import img.RatioFilter;

public class TheDocks extends Arena
{
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new TheDocks(a));
		}
		return arenas;
	}
	private TheDocks(int level) 
	{
		super("The Docks", level, 3);
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
		window.leftClick(160, 140);
	}
}
