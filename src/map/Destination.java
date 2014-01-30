package map;

import img.BinaryImage;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;

import java.awt.Point;
import java.util.HashMap;
public enum Destination 
{
	STASH(MidpassFilterType.GPS_STASH),
	STORE(MidpassFilterType.GPS_STORE),
	WAYPOINT(MidpassFilterType.GPS_WAYPOINT),
	PORTALS(MidpassFilterType.GPS_PORTALS);
	
	private MidpassFilterType filter;
	
	MidpassFilterType getFilter() { return filter; } 
	
	private static HashMap<GlobalMap, Point> locations;
	
	//public Point getLocation(GlobalMap map)
	{
		//return getLocation()
	}
	
	private Destination(MidpassFilterType filter)
	{
		this.filter = filter;
	}
	//private void getLocation()
	{
		/*
		if(locations == null)
		{
			locations = new HashMap<GlobalMap, Point>();
		}
		for(GlobalMap map : GlobalMap.values())
		{
			locations.put(map, getLocation(map.getImage()));
			System.out.println(getLocation(map.getImage()));
		}*/
	}
	public Point getLocation(final IntBitmap img)
	{
		IntBitmap map = IntBitmap.copy(img);
		//Display.showHang(map);
		MidpassFilter.maintainRanges(map, filter);
		//Display.showHang(map);
		BinaryImage bin = map.toGreyscale().doubleCutoff(2);
		//Display.showHang(bin);
		boolean[][] data = bin.getData();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				if(data[a][b] == BinaryImage.WHITE)
				{
					return new Point(a, b);
				}
			}
		}
		System.out.println("Problem finding point");
		return null;
	}
}
