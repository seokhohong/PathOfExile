package arena;

import img.BinaryImage;
import img.IntBitmap;
import img.RatioFilter;

import java.util.ArrayList;

import window.PWindow;

public class LionEyesWatch extends Arena
{
	public static final String NAME = "Lion Eyes Watch";		@Override
	public String getName() { return NAME; }
	
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new LionEyesWatch(a));
		}
		return arenas;
	}
	private LionEyesWatch(int level) 
	{
		super(NAME, level, 1);
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
	public void clickDestination(PWindow window)
	{
		clickLocation(window);
	}
	
	@Override
	public void clickLocation(PWindow window) 
	{
		window.leftClick(113, 244);
	}
}
