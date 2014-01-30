package arena;

import img.BinaryImage;
import img.FilterType;
import img.GreyscaleImage;
import img.IntBitmap;
import img.RatioFilter;
import inventory.Inventory;
import items.Item;
import items.ItemFinder;
import items.ItemType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import data.Potion;
import party.Portal;
import macro.LogoutException;
import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import macro.WaypointNavigator;
import map.Destination;
import map.GlobalMap;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;

public class Crossroads extends Arena
{
	public static final String NAME = "Crossroads";		
	@Override
	public String getName() { return NAME; }
	
	static ArrayList<ItemType> itemTypes = new ArrayList<ItemType>();

	static
	{
		itemTypes.add(ItemType.MAGIC);
		itemTypes.add(ItemType.RARE);
		itemTypes.add(ItemType.UNIQUE);
		itemTypes.add(ItemType.CURRENCY);
	}
	
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new Crossroads(a));
		}
		return arenas;
	}
	private Crossroads(int level) 
	{
		super(NAME, level, 2);
	}
	
	private static final double MVMT_SPEED = 2.0d; //for item pickup speed
	private static final double CHANCE_ID_MAGIC = 0.15d;
	public void openArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		GlobalMap foundHome = GlobalMap.findHome(window);
		if(foundHome != GlobalMap.FOREST_CAMP)
		{
			foundHome.moveHero(thread, Destination.WAYPOINT, 10); //arbitrary distance
			new WaypointNavigator(window).go(Arena.fromString("ForestEncampment1"), foundHome, thread);
		}
		GlobalMap myHome = GlobalMap.FOREST_CAMP;
		
		PoEMacro.levelUpGems(window);
		
		window.checkMana();
		thread.checkHalt();
		
		new Inventory(thread, myHome).build(CHANCE_ID_MAGIC);
		thread.checkHalt();
		//System.out.println("Got here!");
		new WaypointNavigator(window).go(this, myHome, thread);
		thread.checkHalt();
	}
	//Hillock runner
	@Override
	public boolean clearArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		//window.getProfile().processMemos(thread.getNetwork(), window);
		window.getProfile().useAuras(window);
		window.getProfile().usePotion(window, Potion.QUICKSILVER);
		
		headNorth(thread);
		window.getProfile().usePotion(window, Potion.QUICKSILVER);
		findGreenLedge(thread);
		
		getToRampBottom(thread);
		goUpRamp(thread);
		fight(thread);
		pickUpItems(thread, INITIAL_PICKUP, INITIAL_WAIT);
		enterPortal(thread);
		
		return true;
	}
	private void headNorth(WindowThread thread) throws HaltThread
	{
		try 
		{
			GlobalMap.CROSSROADS.moveHero(thread, Destination.PORTALS);
		} 
		//It's normal to catch this
		catch (LogoutException e) 
		{
			System.out.println("Left Waypoint Region");
		}
	}
	private void enterPortal(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		Timer portalTimer = new Timer(8000);
		boolean firstPortal = true;
		while(true)
		{
			thread.checkHalt();
			if(portalTimer.hasExpired())
			{
				throw new LogoutException("Could not make portal");
			}
			if(!Portal.exists(window))
			{
				if(!firstPortal)
				{
					window.getProfile().castAttackTotem(window);
				}
				window.getProfile().castPortal(window);
				firstPortal = false;
			}
			else if(!firstPortal) //Couldn't possibly have a portal if we haven't casted it (occasionally errors with tribal chests)
			{
				break;
			}
		}
		Portal.click(thread.getWindow());
	}
	private static final int INITIAL_PICKUP = 30;
	private static final int INITIAL_WAIT = 6000;
	private static final int BOSS_DEAD = 6;
	private static final int TOO_FAR = 250; //items can be too far, or it might even be bad signal
	private void pickUpItems(WindowThread thread, int attempts, int minWait) throws HaltThread
	{
		PWindow window = thread.getWindow();
		Timer minWaitTimer = new Timer(minWait);
		int numTries = 0;
		boolean bossDead = false; //if lots of items appear, then probably dead
		boolean secondChance = true; //item finder may miss, need two reports of "empty" to consider it so
		ScreenRegion scanRegion = ScreenRegion.ITEM_SCAN_RECT;
		while(numTries < attempts)
		{
			thread.checkHalt();
			ArrayList<Item> items = ItemFinder.findPrioritizedItems(window, scanRegion);
			removeDistantItems(items, TOO_FAR);
			if(items.isEmpty())
			{
				if(scanRegion == ScreenRegion.ITEM_SCAN_RECT) //scanned the full screen
				{
					if(bossDead && secondChance)
					{
						secondChance = false;
					}
					else if(minWaitTimer.hasExpired() || bossDead)
					{
						break;
					}
				}
				else
				{
					scanRegion = ScreenRegion.ITEM_SCAN_RECT;
				}
			}
			else if(!items.isEmpty())
			{
				scanRegion = ScreenRegion.NARROW_ITEM_RECT;
				if(items.size() > BOSS_DEAD)
				{
					bossDead = true;
				}
				for(Item item : items)
				{
					numTries ++;
					double distToItem = item.distFromWindowCenter();	//how far away the item is
					window.leftClick(item.getCenter()); //don't click on the top left corner!
					Macro.sleep((int) (distToItem * MVMT_SPEED));
					break;
				}
			}
		}
		System.out.println("Done With Items");
	}
	private void removeDistantItems(ArrayList<Item> items, int tooFar)
	{
		Iterator<Item> iter = items.iterator();
		while(iter.hasNext())
		{
			Item nextItem = iter.next();
			if(nextItem.distFromWindowCenter() > tooFar)
			{
				iter.remove();
			}
		}
	}
	//private static final int MIN_ATTACKS = 2;
	private void fight(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		window.getProfile().castAttackTotem(window);
	}
	private static final double LEDGE_SEARCH_ANGLE = Math.PI / 2 + Math.PI / 4;
	private static final int GREEN_THRESHOLD = 350;
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
	private void getToRampBottom(WindowThread thread) throws HaltThread, LogoutException
	{
		Timer rampTimer = new Timer(3000);
		while(true)
		{
			thread.checkHalt();
			if(rampTimer.hasExpired())
			{
				throw new LogoutException("No Ramp");
			}
			Point destOnMinimap = findRampBottom(thread);
			if(moveToMinimapPoint(thread.getWindow(), destOnMinimap))
			{
				break;
			}
		}
	}
	private void goUpRamp(WindowThread thread) throws HaltThread, LogoutException
	{
		Timer rampTimer = new Timer(4000);
		while(true)
		{
			thread.checkHalt();
			if(rampTimer.hasExpired())
			{
				throw new LogoutException("Can't get to Top of Ramp");
			}
			Point destOnMinimap = findRampTop(thread);
			if(moveToMinimapPoint(thread.getWindow(), destOnMinimap))
			{
				break;
			}
		}
	}
	//Returns true if we're close enough
	private boolean moveToMinimapPoint(PWindow window, Point dest)
	{
		//Uncenter this
		double xDist = dest.x - ScreenRegion.MAP_RECT.getWidth() / 2;
		double yDist = -(dest.y - ScreenRegion.MAP_RECT.getHeight() / 2);
		double angle = Math.atan2(yDist, xDist);
		int dist = (int) Math.sqrt(xDist * xDist + yDist * yDist) * MINIMAP_TO_WORLD;
		if(dist < CLOSE_ENOUGH)
		{
			return true;
		}
		PoEMacro.moveHero(window, angle, Math.min(dist, MAX_DIST));
		Macro.sleep(200);
		return false;
	}
	private static final int RAMP_SEARCH_RADIUS = 10;
	private static final int RAMP_TOP_OFFSET = 10; //to actually climb up the ramp
	private static final int RAMP_BOTTOM_OFFSET = -15;
	private Point findRampTop(WindowThread thread)
	{
		BinaryImage bin = processForGreenLedge(thread.getWindow());
		int lineIndex = findGap(bin); //on the line from middle-bottom to middle-right
		return findRampPoint(bin, lineIndex, RAMP_TOP_OFFSET);
	}
	private Point findRampBottom(WindowThread thread)
	{
		BinaryImage bin = processForGreenLedge(thread.getWindow());
		int lineIndex = findGap(bin); //on the line from middle-bottom to middle-right
		return findRampPoint(bin, lineIndex, RAMP_BOTTOM_OFFSET);
	}
	private Point findRampPoint(BinaryImage bin, int lineIndex, int offset)
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
		int largestIndex = largest(clusterDensities) + offset;
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
		grey.blur(3);
		bin = grey.doubleCutoff(50);
		return bin;
	}
	
	@Override
	public double startAngle()
	{
		return 0d;
	}
	@Override
	public BinaryImage processMap(IntBitmap map)
	{
		return null; 
	}
	@Override
	public void clickLocation(PWindow window) 
	{
		window.leftClick(240, 165);
	}

}
