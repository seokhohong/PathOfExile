package main;

import img.ImageToolkit;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;

public class TakeMinimapScreenshot 
{
	public static void main(String[] args)
	{
		new TakeMinimapScreenshot().go();
	}
	private void go()
	{
		WindowManager winMgr = new WindowManager();
		PWindow window = winMgr.getWindows().get(0);
		
		ImageToolkit.exportImage(window.takeScreenshot(ScreenRegion.MAP_RECT), "img/minimap.bmp");
		
	}
}
