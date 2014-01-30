package macro;

import img.ImageLibrary;
import img.IntBitmap;

import java.awt.Point;

import map.Destination;
import map.GlobalMap;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
import arena.Arena;

/**
 * 
 * Navigates to the waypoint. Must invoke method WaypointNavigator.go to begin. Does not work from Sarn Encampment.
 * 
 * @author HONG
 *
 */
public class WaypointNavigator 
{
	private PWindow window;
	
	private static final int STEP_DIST = 190;

	public WaypointNavigator(PWindow window)
	{
		this.window = window;
	}
	private static final int WAYPOINT_TIME = 5000;
	//private static final int WAYPOINT_VARIATION = 5000;
	public void go(Arena destination, GlobalMap global, WindowThread thread) throws LogoutException, HaltThread
	{
		clickWaypoint(global, thread);
		clickOnMap(destination);
	}
	private static final int MINIMAP_TO_WORLD = 10;
	private void clickWaypoint(GlobalMap global, WindowThread thread) throws LogoutException, HaltThread
	{
		//Easier for client to read to pass destination in as an argument to the required method 
		Macro.sleep(50);
		global.moveHero(thread, Destination.WAYPOINT, 15);
		//Macro.macro.sleep(50);
		//GlobalMap.clickClosestLabel(window);
		
		IntBitmap map = null;
		Timer waypointTimer = new Timer(WAYPOINT_TIME); //flexible timeout to desync multiple threads accessing waypoint
		while(true)
		{
			if(window.destroyItemVisible())
			{
				window.leftClick(ScreenRegion.DESTROY_ITEM_RECT.getCenter());
			}
			if(waypointTimer.hasExpired()) 
			{
				throw new LogoutException("Took too long to find Waypoint");
			}
			map = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
			Point waypoint = Arena.findWaypoint(map);
			//System.out.println("Waypoint located at "+waypoint);
			thread.checkHalt();
			int dist = 0;
			double angle = 0;
			if(waypoint != null) //If there is no waypoint, click under character
			{
				//Uncenter this
				double xDist = waypoint.x - ScreenRegion.MAP_RECT.getWidth() / 2;
				double yDist = -(waypoint.y - ScreenRegion.MAP_RECT.getHeight() / 2);
				angle = Math.atan2(yDist, xDist);
				dist = (int) Math.sqrt(xDist * xDist + yDist * yDist) * MINIMAP_TO_WORLD;
			}
			if(hasClickedWaypoint()) 
			{
				break;
			}
			stepCloser(angle, dist);
		}
	}
	private boolean hasClickedWaypoint()
	{
		return window.worldNavVisible();
	}
	//Steps closer to waypoint
	private static final int CLOSE_THRESHOLD = 200;
	private void stepCloser(double angle, int dist)
	{
		int steppingDist = Math.min(STEP_DIST, dist);
		if(steppingDist > CLOSE_THRESHOLD)
		{
			PoEMacro.moveHero(window, angle, steppingDist);
			Macro.sleep(450); //walking time
		}
		else
		{
			PoEMacro.moveHeroCarefully(window, angle, steppingDist);
			Macro.sleep(700); //walking time
		}
		
	}
	
	private static final int MAX_DIALOG_NAV_TIME = 2000;
	private void findAndClickNav(IntBitmap img, ScreenRegion searchRegion)
	{
		//Display.show(img);
		Timer waitTime = new Timer(MAX_DIALOG_NAV_TIME);
		while(waitTime.stillWaiting())
		{
			if(!window.clickOnImageFound(img, searchRegion))
			{
				return;
			}
			Macro.sleep(50); //Give other threads a chance to breathe
		}
	}
	private static final int HIGHLIGHT_THRESHOLD = 20;
	//Designed for finding the highlighted or the unhighlighted icon, clicking if it is not highlighted
	private void waitForEitherHighlight(IntBitmap img, IntBitmap img2, ScreenRegion searchRegion)
	{
		Timer dialogNavTimer = new Timer(MAX_DIALOG_NAV_TIME);
		while(dialogNavTimer.stillWaiting())
		{
			if(window.clickOnImageFound(img, searchRegion) 
					|| window.imageExists(img2, IntBitmap.getInstance(window.takeScreenshot(searchRegion)), HIGHLIGHT_THRESHOLD))
			{
				return;
			}
			Macro.sleep(50);
		}
	}
	private void waitForNav()
	{
		Timer dialogNavTimer = new Timer(MAX_DIALOG_NAV_TIME);
		while(dialogNavTimer.stillWaiting())
		{
			if(window.worldNavVisible())
			{
				return;
			}
			Macro.sleep(50);
		}
	}
	public void clickOnMap(Arena dest) throws LogoutException
	{
		waitForNav();
		findAndClickNav(ImageLibrary.levelUnhighlight(dest.getLevel()), ScreenRegion.NAV_RECT);
		waitForEitherHighlight(ImageLibrary.actUnhighlight(dest.getAct()), ImageLibrary.actHighlight(dest.getAct()), ScreenRegion.NAV_RECT);
		findAndClickNav(ImageLibrary.actUnhighlight(dest.getAct()), ScreenRegion.NAV_RECT);
		if(window.myHealthVisible())
		{
			dest.clickDestination(window);
			waitPostNavigation();
		}
	}
	private void waitPostNavigation()
	{
		Timer tooLongWait = new Timer(60 * 1000);
		Macro.sleep(1000); //hopefully the screen moves by then
		while(!window.minimapWaypointVisible() && tooLongWait.stillWaiting()) //loading screen
		{
			Macro.sleep(300);
		}
	}
}
