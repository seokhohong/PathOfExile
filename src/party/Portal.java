package party;

import img.BleedResult;
import map.GlobalMap;

import java.awt.Rectangle;
import java.util.ArrayList;

import macro.LogoutException;
import macro.Macro;
import macro.Timer;
import window.PWindow;

public class Portal 
{
	
	private static final int MAX_LABEL_HEIGHT = 12;
	private static final int MIN_LABEL_WIDTH = 30;
	
	private static BleedResult getPortalLabel(PWindow window, int maxDist)
	{
		ArrayList<BleedResult> labels = GlobalMap.getLabels(window);
		//System.out.println("NumLabels: "+labels.size());
		for(BleedResult label : labels)
		{
			if(label.getCenter().distance(PWindow.getWindowCenter()) < maxDist)
			{
				Rectangle labelRect = label.toRectangle();
				//System.out.println("One Label "+label);
				if(labelRect.height < MAX_LABEL_HEIGHT && labelRect.width > MIN_LABEL_WIDTH)
				{
					return label;
				}
			}
		}
		return null;
	}
	private static final int MAX_PORTAL_DIST = 150;
	public static boolean exists(PWindow window)
	{
		return getPortalLabel(window, MAX_PORTAL_DIST) != null;
	}
	
	public static boolean click(PWindow window) throws LogoutException
	{
		Timer portalTimer = new Timer(5000);
		while(portalTimer.stillWaiting())
		{
			if(clickOnce(window, MAX_PORTAL_DIST))
			{
				return true;
			}
		}
		return false;
	}
	//Returns whether we clicked the portal
	public static boolean clickOnce(PWindow window, int maxDist)
	{
		BleedResult portalLabel = getPortalLabel(window, maxDist);
		if(portalLabel != null)
		{
			window.doubleClick(portalLabel.getCenter());
			waitForPortal(window);
			return true;
		}
		return false;
		/*
		IntBitmap screen = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT));
		MidpassFilter.maintainRanges(screen, MidpassFilterType.ON_SCREEN_PORTAL);
		BinaryImage bin = screen.toGreyscale().doubleCutoff(30);
		ArrayList<BleedResult> results = new Bleeder(1).find(bin);
		for(BleedResult result : results)
		{
			if(result.getNumPixels() > PORTAL_THRESHOLD)
			{
				System.out.println("Portal is at "+result.getCenter());
				window.doubleClick(result.getCenter());
				waitForPortal(window);
				return true;
			}
		}
		return false;
		*/
	}
	//Wait for health bar to show up
	private static void waitForPortal(PWindow window)
	{
		Macro.sleep(300);
		Timer maxPortalWait = new Timer(10000);
		while(maxPortalWait.stillWaiting())
		{
			if(window.myHealthVisible())
			{
				return;
			}
		}
	}
}
