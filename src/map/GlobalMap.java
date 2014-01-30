package map;

import img.BinaryImage;
import img.BleedResult;
import img.Bleeder;
import img.FilterType;
import img.ImageToolkit;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;
import img.RatioFilter;
import inventory.InventoryMacro;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import macro.HomeNavigator;
import macro.LogoutException;
import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;

public enum GlobalMap implements Pathable
{
	LION_EYES_WATCH("map/LioneyeWatchMap.bmp", 0, 22, MidpassFilterType.WAYPOINT, Label.NESSA, 10, 125, 0.9d),
	FOREST_CAMP("map/ForestEncampmentMap.bmp", 0, 50, MidpassFilterType.WAYPOINT, Label.GREUST, 10, 275, 1.75d),
	SARN("map/SarnMap.bmp", 40, 28, MidpassFilterType.WAYPOINT, Label.CLARISSA, 10, 250, 1.5d),
	TWILIGHT_STRAND("map/TwilightStrandMap.bmp", 0, 0, MidpassFilterType.ORANGE_DOOR, null, 15, 250, 1.5d),
	CROSSROADS("map/CrossroadsMap.bmp", 0, 0, MidpassFilterType.WAYPOINT, null, 15, 250, 1.5d);
	
	private IntBitmap map;		IntBitmap getImage() { return map; }
	
	private boolean[][] pathability;
	
	private Point imageWaypoint; //location of waypoint on minimap
	private static final Point characterPoint = new Point(77, 77); //Location of character on minimap
	
	private final MidpassFilterType waypointFilter;
	
	private final Label storeLabel;							public Label getStoreLabel() { return storeLabel; }
	
	private int moveDist; //number of AStar nodes moved per movement
	private int moveInterval; //distance walked in pixels per movement
	
	private double moveTimeFactor;
	
	private int buyFromBottom;
	private int sellFromBottom;
	
	private GlobalMap(
			String mapFilename, 
			int buyFromBottom, 
			int sellFromBottom, 
			MidpassFilterType waypointFilter, 
			Label storeLabel, 
			int moveDist, 
			int moveInterval, 
			double moveTimeFactor)
	{
		map = IntBitmap.getInstance(ImageToolkit.loadImage(mapFilename));
		pathability = map.toGreyscale().doubleCutoff(200).getData();
		imageWaypoint = findImageWaypoint(map);
		this.waypointFilter = waypointFilter;
		this.buyFromBottom = buyFromBottom;
		this.sellFromBottom = sellFromBottom;
		this.storeLabel = storeLabel;
		this.moveDist = moveDist;
		this.moveInterval = moveInterval;
		this.moveTimeFactor = moveTimeFactor;
	}
	
	public void blindMove(WindowThread thread, double angle) throws HaltThread
	{
		Timer maxMove = new Timer(1000 * 10);
		while(maxMove.stillWaiting())
		{
			thread.checkHalt();
			if(waypointLocation(thread.getWindow()) != null)
			{
				System.out.println("Sees Waypoint Now");
				break;
			}
			PoEMacro.moveHero(thread.getWindow(), angle, 100);
			Macro.sleep(100);
		}
	}
	private static final int DEFAULT_CLOSE_ENOUGH = 8;
	public void moveHero(WindowThread thread, Destination dest) throws LogoutException, HaltThread
	{
		moveHero(thread, dest, DEFAULT_CLOSE_ENOUGH);
	}
	//How long to wait per unit distance moved (milliseconds/pixel)
	//private static final double MOVE_TIME_FACTOR = 1.4d; 
	/** closeEnough is measured in image pixels 
	 * @throws LogoutException 
	 * @throws HaltThread */
	public void moveHero(WindowThread thread, Destination dest, int closeEnough) throws LogoutException, HaltThread
	{
		Timer maxMove = new Timer(1000 * 10);
		while(maxMove.stillWaiting())
		{
			thread.checkHalt();
			if(moveHeroOnce(thread, dest, closeEnough))
			{
				break;
			}
		}
	}
	public boolean moveHeroOnceStore(WindowThread thread) throws LogoutException, HaltThread
	{
		return moveHeroOnce(thread, Destination.STORE, getStoreLabel().closeEnough());
	}
	private static final int INTERVAL_BASE = 40;
	private static final double GREATER_PRECISION_DISTANCE = 1.75; //multiple of closeEnough at which we need more precision
	private static final int PRECISION_FACTOR = 2; //divide moving distance by this factor
	/** Returns true if finished moving */
	public boolean moveHeroOnce(WindowThread thread, Destination dest, int closeEnough) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		thread.checkHalt();
		if(window.worldNavVisible())
		{
			window.pressEscape();
		}

		ArrayList<Point> path = getPath(thread, dest);
		//System.out.println(path.size()+" "+closeEnough);
		if(path.size() < closeEnough) 
		{
			Macro.sleep(100);
			return true;
		}
		
		int singleMoveInterval = moveInterval; //how far to move
		if(path.size() < closeEnough * GREATER_PRECISION_DISTANCE) //then get more precision
		{
			singleMoveInterval = moveInterval / PRECISION_FACTOR;
		}
		
		checkDestroyItem(window);

		Point destPoint = path.get(Math.min(path.size() - 1, moveDist));
		double angle = angleFor(window, thread, destPoint);
		
		if(window.inventoryVisible()) //bad news, could grab an item
		{
			return true;
		}
		
		PoEMacro.moveHeroCarefully(window, angle, singleMoveInterval);
		Macro.sleep((int) ((singleMoveInterval - INTERVAL_BASE) * moveTimeFactor));
		return false;
	}
	public boolean checkDestroyItem(PWindow window)
	{
		if(window.destroyItemVisible())
		{
			window.leftClick(ScreenRegion.DESTROY_ITEM_RECT.getCenter());
			return true;
		}
		return false;
	}
	private double angleFor(PWindow window, WindowThread thread, Point movePoint) throws LogoutException, HaltThread
	{
		//System.out.println("A: "+System.currentTimeMillis());
		Point hero = getHeroPosition(thread);
		//System.out.println("B: "+System.currentTimeMillis());
		if(hero == null) return 0; //move to the right, until we can see waypoint
		return Math.atan2(hero.y - movePoint.y, - (hero.x - movePoint.x));
	}
	public Point waypointLocation(PWindow window)
	{
		ArrayList<BleedResult> result = new ArrayList<BleedResult>();
		for(int a = 0; a < 2; a++) //try a few times at least
		{
			IntBitmap map = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
			MidpassFilter.maintainRanges(map, waypointFilter);
			BinaryImage bin = map.toGreyscale().doubleCutoff(10);
			Bleeder bl = new Bleeder(1);
			result = bl.find(bin);
			if(!result.isEmpty())
			{
				break;
			}
		}
		if(result.isEmpty())
		{
			return null;
		}
		return result.get(0).getCenter();
	}
	public void waitForWaypoint(WindowThread thread) throws LogoutException, HaltThread
	{
		Timer waypointTimer = new Timer(2000);
		while(true)
		{
			thread.checkHalt();
			if(waypointLocation(thread.getWindow()) != null)
			{
				return;
			}
			if(waypointTimer.hasExpired())
			{
				return;
			}
		}
	}
	private static final int NUM_TRIES = 3;
	public Point getHeroPosition(WindowThread thread) throws LogoutException, HaltThread
	{
		Timer heroPositionTimer = new Timer(3000);
		Point p = null;
		while(true)
		{
			for(int a = 0; a < NUM_TRIES ; a ++)
			{
				p = waypointLocation(thread.getWindow());
				if(p != null)
				{
					break;
				}
			}
			if(p == null)
			{
				if(!checkDestroyItem(thread.getWindow()))
				{
					throw new LogoutException("Can't See Waypoint");
				}
				Macro.sleep(500);
			}
			else
			{
				break;
			}
			if(heroPositionTimer.hasExpired())
			{
				throw new LogoutException("Can't See Waypoint (Time)");
			}
		}
		return new Point(imageWaypoint.x + (characterPoint.x - p.x), imageWaypoint.y + (characterPoint.y - p.y)); //offsets from waypoint
	}
	private ArrayList<Point> getPath(WindowThread thread, Destination dest) throws LogoutException, HaltThread
	{
		Point start = getHeroPosition(thread);
		if(start == null)
		{
			return new ArrayList<Point>();
		}
		Point end = dest.getLocation(map);
		return AStar.getPath(this, start, end);
	}
	
	public void useDialog(PWindow window, int pixFromBottom) throws LogoutException
	{
		int bottom = InventoryMacro.getBottomOfMenu(window);
		Point sellPoint = new Point(PWindow.getWidth() / 2, bottom - sellFromBottom);
		if(this == GlobalMap.LION_EYES_WATCH)
		{
			sellPoint = window.getProfile().getTwilightSellPoint();
		}
		if(sellPoint.x < 0 || sellPoint.y < 0)
		{
			throw new LogoutException("Dialog Failure");
		}
		window.mouseMove(sellPoint);
		window.leftClickCarefully(sellPoint);
		window.leftClick(sellPoint);
		window.leftClickCarefully(sellPoint);
	}

	public void openSellWindow(PWindow window) throws LogoutException
	{
		useDialog(window, sellFromBottom);
	}
	
	public void openBuyWindow(PWindow window) throws LogoutException
	{
		useDialog(window, buyFromBottom);
	}
	
	private Point findImageWaypoint(IntBitmap map)
	{
		IntBitmap img = IntBitmap.copy(map);
		MidpassFilter.maintainRanges(img, MidpassFilterType.GPS_WAYPOINT);
		BinaryImage bin = img.toGreyscale().doubleCutoff(50);
		for(int a = 0; a < bin.getWidth(); a++)
		{
			for(int b = 0; b < bin.getHeight(); b++)
			{
				if(bin.getData()[a][b] == BinaryImage.WHITE)
				{
					return new Point(a, b);
				}
			}
		}
		System.err.println("Failed to find Waypoint on image");
		return null;
	}
	
	@Override
	public List<Node> getNeighbors(Node p) 
	{
		List<Node> nodes = new ArrayList<Node>();
		ArrayList<Node> surrounding = new ArrayList<Node>();
		surrounding.add(new Node(p.x + 1, p.y));
		surrounding.add(new Node(p.x, p.y + 1));
		surrounding.add(new Node(p.x - 1, p.y));
		surrounding.add(new Node(p.x, p.y - 1));
		for(Node n : surrounding)
		{
			if(n.x >=0 
					&& n.x < pathability.length 
					&& n.y >= 0 
					&& n.y < pathability[0].length 
					&& pathability[n.x][n.y] == BinaryImage.BLACK)
			{
				nodes.add(n);
			}
		}
		return nodes;
	}
	public static GlobalMap findHome(PWindow window) throws LogoutException
	{
		//Always will be in a particular order because bleeder works from left to right
		switch(getNumDoors(window))
		{
		case 0: return SARN;
		case 1: return LION_EYES_WATCH;
		case 2: return FOREST_CAMP; //coming from portal
		case 3: return FOREST_CAMP;
		default: 
			{
				throw new LogoutException("Could not identify Home");
			}
		}
	}
	public static int getNumDoors(PWindow window)
	{
		IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		RatioFilter.maintainRatio(minimap, FilterType.NEXT_REGION);
		BinaryImage bin = minimap.toGreyscale().doubleCutoff(50);
		int numDoors = 0;
		for(BleedResult result : new Bleeder(1).find(bin))
		{
			if(result.getNumPixels() > 5)
			{
				numDoors ++;
			}
		}
		return numDoors;
	}
	//eww^2
	public void clickClosestStoreLabel(HomeNavigator homeNav, PWindow window)
	{
		Timer searchTimer = new Timer(3000);
		while(searchTimer.stillWaiting())
		{
			if(homeNav.storeDialogOpen())
			{
				break;
			}
			ArrayList<BleedResult> results = getLabels(window);
			removeBadLabels(results, storeLabel);
			clickClosestLabelOnce(window, results);
			Macro.sleep(500);
		}
	}
	//eww
	public static boolean clickClosestStashLabel(PWindow window)
	{
		Timer searchTimer = new Timer(2000);
		while(searchTimer.stillWaiting())
		{
			ArrayList<BleedResult> results = getLabels(window);
			removeBadLabels(results, Label.STASH);
			if(clickClosestLabelOnce(window, results))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean clickClosestLabel(PWindow window, Label label)
	{
		Timer searchTimer = new Timer(2000);
		while(searchTimer.stillWaiting())
		{
			ArrayList<BleedResult> results = getLabels(window);
			removeBadLabels(results, label);
			if(clickClosestLabelOnce(window, results))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean clickClosestLabelOnce(PWindow window, ArrayList<BleedResult> results)
	{
		if(results.isEmpty())
		{
			//System.out.println("No labels to find Closest");
			return false;
		}
		else
		{
			BleedResult closest = getResultClosestTo(results, PWindow.getWindowCenter());
			window.mouseMove(closest.getCenter());
			window.doubleClick(closest.getCenter());
			return true;
		}
	}
	
	public static void clickFarthestLabel(PWindow window, Label label)
	{
		Timer searchTimer = new Timer(2000);
		while(searchTimer.stillWaiting())
		{
			ArrayList<BleedResult> results = getLabels(window);
			removeBadLabels(results, label);
			if(results.isEmpty())
			{
				//System.out.println("No labels to find Farthest");
			}
			else
			{
				BleedResult farthest = getResultFarthestFrom(results, PWindow.getWindowCenter());
				window.mouseMove(farthest.getCenter());
				window.doubleClick(farthest.getCenter());
				window.doubleClick(farthest.getCenter());
				return;
			}
		}
	}
	
	private static void removeBadLabels(ArrayList<BleedResult> results, Label label)
	{
		Iterator<BleedResult> iter = results.iterator();
		while(iter.hasNext())
		{
			BleedResult bl = iter.next();
			if(!label.withinRange((int) bl.toRectangle().getWidth()))
			{
				iter.remove();
			}
		}
	}
	
	private static BleedResult getResultClosestTo(ArrayList<BleedResult> results, Point p)
	{
		double bestDistance = Double.MAX_VALUE;
		BleedResult closestResult = null;
		for(BleedResult r : results)
		{
			if(r.getCenter().distance(p) < bestDistance)
			{
				bestDistance = r.getCenter().distance(p);
				closestResult = r;
			}
		}
		return closestResult;
	}
	
	private static BleedResult getResultFarthestFrom(ArrayList<BleedResult> results, Point p)
	{
		double bestDistance = Double.MIN_VALUE;
		BleedResult farthestResult = null;
		for(BleedResult r : results)
		{
			if(r.getCenter().distance(p) > bestDistance)
			{
				bestDistance = r.getCenter().distance(p);
				farthestResult = r;
			}
		}
		return farthestResult;
	}
	
	private static final int LABEL_THRESHOLD = 40;
	private static final Bleeder LABEL_BLEEDER = new Bleeder(5);
	public static ArrayList<BleedResult> getLabels(PWindow window)
	{
		IntBitmap screen = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT));
		RatioFilter.maintainRatio(screen, FilterType.STASH_LABEL);
		screen.lowPass(230);
		BinaryImage bin = screen.toGreyscale().doubleCutoff(30);
		bin.killLoners(1, BinaryImage.WHITE); //sometimes there's a lot of bad signal
		ArrayList<BleedResult> allResults =  LABEL_BLEEDER.find(bin);
		ArrayList<BleedResult> labels = new ArrayList<BleedResult>();
		
		for(BleedResult result : allResults)
		{
			if(result.getNumPixels() > LABEL_THRESHOLD)
			{
				labels.add(result);
			}
		}
		return labels;
	}
}
