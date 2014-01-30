package main;

import java.awt.Point;
import java.util.ArrayList;

import img.BinaryImage;
import img.Display;
import img.FilterType;
import img.GreyscaleImage;
import img.IntBitmap;
import img.RatioFilter;
import inventory.Inventory;
import items.Item;
import items.ItemFinder;
import items.ItemType;
import macro.LogoutException;
import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import macro.WaypointNavigator;
import map.Destination;
import map.GlobalMap;
import party.Portal;
import process.AHKBridge;
import process.Quittable;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;
import window.WindowThread;
import data.Config;

public class TestCrossroads implements Quittable
{
	public static void main(String[] args)
	{
		new TestCrossroads().go();
	}
	private void go()
	{
		Config config = new Config();
		AHKBridge.runExitHook(this, config);
		WindowManager winMgr = new WindowManager(config);
		PWindow window = winMgr.getWindows().get(0);
		GlobalMap home = GlobalMap.CROSSROADS;
		WindowThread thread = new WindowThread(config, winMgr, null, window);
	
		try {
			home.moveHero(thread, Destination.PORTALS);
		} catch (HaltThread e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LogoutException e)
		{
			System.out.println("Left Waypoint Region");
		}
		
		try {
			findGreenLedge(thread);
			goUpRamp(thread);
			fight(thread);
			pickUpItems(thread, INITIAL_PICKUP, INITIAL_WAIT);
			enterPortal(thread);
		} catch (LogoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HaltThread e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GlobalMap myHome = GlobalMap.FOREST_CAMP;
		try {
			new Inventory(thread, myHome).build(0.0);
			thread.checkHalt();
		} catch (HaltThread | LogoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("Got here!");
		//new WaypointNavigator(window).go(this, myHome, thread);

	}
	private static void enterPortal(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		Timer portalTimer = new Timer(10000);
		while(true)
		{
			thread.checkHalt();
			if(portalTimer.hasExpired())
			{
				throw new LogoutException("Could not make portal");
			}
			if(!Portal.exists(window))
			{
				window.mouseMove(PWindow.getWindowCenter());
				Macro.sleep(20);
				window.type("q");
				Macro.sleep(2600);
			}
			else
			{
				break;
			}
		}
		Portal.click(thread.getWindow());
	}
	private static final int INITIAL_PICKUP = 25;
	private static final int INITIAL_WAIT = 3000;
	private static final double MVMT_SPEED = 2.0d; //for item pickup speed
	private static void pickUpItems(WindowThread thread, int attempts, int minWait) throws HaltThread
	{
		ArrayList<ItemType> types = new ArrayList<ItemType>();

		types.add(ItemType.MAGIC);
		types.add(ItemType.RARE);
		types.add(ItemType.UNIQUE);
		types.add(ItemType.CURRENCY);
		
		PWindow window = thread.getWindow();
		Timer minWaitTimer = new Timer(minWait);
		int numTries = 0;
		while(numTries < attempts)
		{
			thread.checkHalt();
			ArrayList<Item> items = ItemFinder.findItems(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT)), types);
			System.out.println("Found "+items.size()+" items!");
			if(items.isEmpty() && minWaitTimer.hasExpired())
			{
				break;
			}
			else if(!items.isEmpty())
			{
				for(Item item : items)
				{
					numTries ++;
					double distToItem = item.toPoint().distance(PWindow.getWindowCenter());	//how far away the item is
					window.leftClick(item.getCenter()); //don't click on the top left corner!
					Macro.sleep((int) (distToItem * MVMT_SPEED));
					break;
				}
			}
		}
		System.out.println("Done With Items");
	}
	private static void fight(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		for(int a = 0; a < 3; a++)
		{
			window.rightClick(PWindow.getWindowCenter());
			Macro.sleep(500);
		}
		window.middleClick(PWindow.getWindowCenter());
	}
	private static final double LEDGE_SEARCH_ANGLE = Math.PI / 2 + Math.PI / 4;
	private static final int GREEN_THRESHOLD = 300;
	private void findGreenLedge(WindowThread thread) throws LogoutException, HaltThread
	{
		Timer greenTimer = new Timer(10000);
		PWindow window = thread.getWindow();
		while(true)
		{
			thread.checkHalt();
			//find number of "green" pixels
			BinaryImage bin = processForGreenLedge(window);
			int numGreenPixels = bin.countWhite();
			//System.out.println(numGreenPixels);
			if(numGreenPixels > GREEN_THRESHOLD)
			{
				return;
			}
			if(greenTimer.hasExpired())
			{
				throw new LogoutException("Could not find Green Ledge");
			}
			PoEMacro.moveHero(thread.getWindow(), LEDGE_SEARCH_ANGLE, 200);
			Macro.sleep(200);
		}
	}
	private static final int MINIMAP_TO_WORLD = 10;
	private static final int CLOSE_ENOUGH = 100; //on the world scale
	private static final int MAX_DIST = 200;
	//Pretty bad code here
	private void goUpRamp(WindowThread thread) throws HaltThread, LogoutException
	{
		System.out.println("Going up Ramp");
		Timer rampTimer = new Timer(5000);
		while(true)
		{
			thread.checkHalt();
			if(rampTimer.hasExpired())
			{
				throw new LogoutException("No Ramp");
			}
			BinaryImage bin = processForGreenLedge(thread.getWindow());
			int lineIndex = findGap(bin); //on the line from middle-bottom to middle-right
			Point destOnMinimap = findRamp(bin, lineIndex);
			//Uncenter this
			double xDist = destOnMinimap.x - ScreenRegion.MAP_RECT.getWidth() / 2;
			double yDist = -(destOnMinimap.y - ScreenRegion.MAP_RECT.getHeight() / 2);
			double angle = Math.atan2(yDist, xDist);
			int dist = (int) Math.sqrt(xDist * xDist + yDist * yDist) * MINIMAP_TO_WORLD;
			if(dist < CLOSE_ENOUGH)
			{
				break;
			}
			PoEMacro.moveHero(thread.getWindow(), angle, Math.min(dist, MAX_DIST));
			Macro.sleep(200);
		}
	}
	private static final int RAMP_SEARCH_RADIUS = 10;
	private static final int RAMP_OFFSET = 20; //to actually climb up the ramp
	private Point findRamp(BinaryImage bin, int lineIndex)
	{
		boolean[][] imgData = bin.getData();
		Point lineStart = new Point(bin.getWidth() / 2 + lineIndex, bin.getHeight() - lineIndex);
		int dist = 0; 
		//number of pixels in area, indexed by distance along line
		int[] clusterDensities = new int[bin.getWidth()]; 
		while(true)
		{
			//fairly rigid way to code for an angle, could use trig
			Point linePoint = new Point(lineStart.x - dist, lineStart.y - dist);
			if(linePoint.x < 0 || linePoint.y < 0 || linePoint.x == bin.getWidth() || linePoint.y == bin.getHeight())
			{
				break;
			}
			
			clusterDensities[dist] = numColor(imgData, linePoint, BinaryImage.WHITE, RAMP_SEARCH_RADIUS);
			//System.out.println("Cluster at "+dist+" = "+clusterDensities[dist]);
			dist ++;
		}
		int largestIndex = largest(clusterDensities) + RAMP_OFFSET;
		return new Point(lineStart.x - largestIndex, lineStart.y - largestIndex);
	}
	//Checks how clear lines at 3PI/4 are to see where the gap is located
	//Naturally inclined to aim for closer to the top-left/bottom-right diagonal
	private int findGap(BinaryImage bin)
	{
		boolean[][] imgData = bin.getData();
		int[] clarity = new int[bin.getWidth() / 2]; //clear distances
		for(int a = 0; a < bin.getWidth() / 2; a++) //loops along the starting points of each line
		{
			Point lineStart = new Point(bin.getWidth() / 2 + a, bin.getHeight() - a);
			int dist = 0; //distance along the line
			while(true)
			{
				//fairly rigid way to code for an angle, could use trig
				Point linePoint = new Point(lineStart.x - dist, lineStart.y - dist);
				if(linePoint.x < 0 || linePoint.y < 0 || linePoint.x == bin.getWidth() || linePoint.y == bin.getHeight())
				{
					break;
				}
				if(imgData[linePoint.x][linePoint.y] == BinaryImage.WHITE
						&& lotOfWhite(imgData, linePoint))
				{
					break;
				}
				dist ++;
			}
			clarity[a] = dist;
			//System.out.println("Clarity["+a+"] = "+dist);
		}
		return largest(clarity);
	}
	private static final int SEARCH_RADIUS = 3;
	private static final int LOT_THRESHOLD = 15;
	private static boolean inBounds(boolean[][] data, int x, int y)
	{
		return x >= 0 && y >=0 && x < data.length && y < data[0].length;
	}
	//Number of a particularly colored pixel in a square around point p
	private static int numColor(boolean[][] data, Point p, boolean color, int radius)
	{
		int count = 0;
		for(int a = -radius; a < radius; a++)
		{
			for(int b = -radius; b < radius; b++)
			{
				if(inBounds(data, a + p.x, b + p.y))
				{
					if(data[a + p.x][b + p.y] == color)
					{
						count++;
					}
				}
			}
		}
		return count;
	}
	//Is there a cluster of white at this point?
	private static boolean lotOfWhite(boolean[][] data, Point p)
	{
		return numColor(data, p, BinaryImage.WHITE, SEARCH_RADIUS)> LOT_THRESHOLD;
	}
	//Returns the index of the longest number
	private int largest(int[] arr)
	{
		int maxVal = 0;
		int bestIndex = -1;
		for(int a = 0; a < arr.length; a++)
		{
			if(arr[a] > maxVal)
			{
				maxVal = arr[a];
				bestIndex = a;
			}
		}
		return bestIndex;
	}
	private BinaryImage processForGreenLedge(PWindow window)
	{
		IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		RatioFilter.maintainRatio(minimap, FilterType.GREEN_ROCK);
		GreyscaleImage grey = minimap.toGreyscale();
		BinaryImage bin = grey.doubleCutoff(50);
		bin.killLoners(1, BinaryImage.WHITE);
		
		grey = bin.toGreyscale();
		grey.blur(2);
		bin = grey.doubleCutoff(50);
		return bin;
	}
	@Override
	public void exitProgram() 
	{
		
	}
}
