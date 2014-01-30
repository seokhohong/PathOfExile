package main;

import img.Display;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;
import map.Destination;
import map.GlobalMap;
import window.ScreenRegion;
import window.WindowManager;
public class TestGPS 
{
	public static void main(String[] args)
	{
		new TestGPS().go();
	}
	private void go()
	{
		
		GlobalMap.LIONS_EYE_WATCH.moveHero(new WindowManager().getWindows().get(0), Destination.PORTALS);
		IntBitmap screen = IntBitmap.getInstance(new WindowManager().getWindows().get(0).takeScreenshot(ScreenRegion.ITEM_SCAN_RECT));
		MidpassFilter.maintainRanges(screen	, MidpassFilterType.ON_SCREEN_PORTAL);
		Display.showHang(screen);
	}
	
}
