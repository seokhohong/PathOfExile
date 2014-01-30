package arena;

import img.BinaryImage;
import img.IntBitmap;
import img.RatioFilter;

import java.util.ArrayList;

import window.PWindow;

public class ForestEncampment extends Arena
{
	public static final String NAME = "ForestEncampment";		
	@Override
	public String getName() { return NAME; }
	
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new ForestEncampment(a));
		}
		return arenas;
	}
	private ForestEncampment(int level) 
	{
		super(NAME, level, 3);
	}
	@Override
	public double startAngle()
	{
		return 0;
	}
	@Override
	public BinaryImage processMap(IntBitmap map)
	{
		return null;
		//RatioFilter.maintainRatio(map, getObstacles());
		//return processFiltered(map);
	}
	
	@Override
	public void clickDestination(PWindow window)
	{
		clickLocation(window);
	}
	
	@Override
	public void clickLocation(PWindow window) 
	{
		window.leftClick(230, 240);
	}
}
