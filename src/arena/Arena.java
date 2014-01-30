package arena;

import img.BinaryImage;
import img.BleedResult;
import img.Bleeder;
import img.FilterType;
import img.GreyscaleImage;
import img.ImageLibrary;
import img.ImageToolkit;
import img.IntBitmap;
import img.RatioFilter;
import inventory.Inventory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import macro.LogoutException;
import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import macro.Waiter;
import macro.WaypointNavigator;
import macro.WindowException;
import map.Destination;
import map.GlobalMap;
import map.PolarMap;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
import combat.Healer;
import data.Profile;

/**
 * 
 * Contains data necessary for every arena
 * 
 * Would be an enum type if it didn't require an ArrayList as part of its definition
 * 
 * Each location is represented by its own class, inheriting from the abstract Arena type here
 * 
 * @author Seokho
 *
 */
public abstract class Arena
{
	//Number of repeats of the acts
	private static final int NUM_DIFFICULTIES = 3;
	public static int getNumDifficulties() { return NUM_DIFFICULTIES; }
	
	private static final ArrayList<Arena> arenas = new ArrayList<Arena>();
	
	//Name of the arena
	private String name;			public abstract String getName();
	
	//What constitutes an obstacle (RatioFilter)
	private ArrayList<FilterType> obstacles = new ArrayList<FilterType>();
	ArrayList<FilterType> getObstacles() { return obstacles; }
	
	//Level of difficulty
	private int level;
	public int getLevel() { return level; }
	public IntBitmap getLevelUnhighlighted() { return ImageLibrary.levelUnhighlight(level); } 
	
	//Which Act
	private int act;
	public int getAct() { return act; }
	public IntBitmap getActUnhighlighted() { return ImageLibrary.actUnhighlight(act); }
	
	Arena(String name, int level, int act) 
	{
		this.name = name;
		this.level = level;
		this.act = act;
	}
	
	void addFilter(FilterType f) { obstacles.add(f); }
	
	public static Arena fromString(String arena)
	{
		int level = Integer.parseInt(Character.toString(arena.charAt(arena.length() - 1))); //extract the last character from arena
		arena = arena.substring(0, arena.length() - 1); //cut off that character
		for(Arena a : arenas)
		{
			if(a.name.equals(arena) && a.level == level) //correct name and level
			{
				return a;
			}
		}
		System.err.println("Could not convert "+arena+" to an Arena");
		System.exit(1);
		return null;
	}
	
	public double startAngle()
	{
		return new Random().nextDouble(); //arbitrary
	}
	
	/**
	 * 
	 * Processes a map with respect to a given arena
	 * 
	 * @param map		: Image of Map
	 * @param arena		: Current arena
	 * @return
	 */
	public abstract BinaryImage processMap(IntBitmap map);
	
	//ProcessMap filters the map and then processedFiltered is common to 
	BinaryImage processFiltered(IntBitmap filtered)
	{
		GreyscaleImage grey = filtered.bidirectionalDerivative();
		grey.multiply(15);
		BinaryImage bin = grey.doubleCutoff(ImageToolkit.MAX_VAL - 1);
		bin.invert();
		grey = bin.toGreyscale();
		bin = grey.doubleCutoff(ImageToolkit.MAX_VAL - 1);
		return bin;
	}
	
	/**
	 * Clicks on the correct coordinates to travel to this arena
	 * @param window
	 */
	public abstract void clickLocation(PWindow window);
	
	/**
	 * Checks for the orange door
	 * 
	 * @param map	: Map of minimap
	 * @return
	 */
	private static final int NR_NUM_PIXELS = 5; //Number of pixels to mark as Waypoint
	private static final Bleeder FINISHED_BLEEDER = new Bleeder(2);
	public static boolean finishedMap(PWindow window)
	{
		IntBitmap map = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		RatioFilter.maintainRatio(map, FilterType.NEXT_REGION);
		for(BleedResult result : FINISHED_BLEEDER.find(map.toGreyscale().doubleCutoff(50)))
		{
			if(result.getNumPixels() > NR_NUM_PIXELS)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * If a Waypoint is present according to the minimap, it will be returned as a point object.
	 * If it is not, null will be returned
	 * 
	 * @param map	: minimap
	 * @return
	 */
	private static final int WP_NUM_PIXELS = 2; //Number of pixels to mark as Waypoint
	public static Point findWaypoint(IntBitmap map)
	{
		RatioFilter.maintainRatio(map, FilterType.WAYPOINT);
		Bleeder waypointBleeder = new Bleeder(5);
		for(BleedResult result : waypointBleeder.find(map.toGreyscale().doubleCutoff(25)))
		{
			if(result.getNumPixels() >= WP_NUM_PIXELS)
			{
				return result.getCenter();
			}
		}
		return null;
	}
	
	public void clickDestination(PWindow window) throws LogoutException
	{
		window.pressControl();
		clickLocation(window);
		window.releaseControl();
		Macro.sleep(400);
		boolean clicked = false;
		Timer scrollTimer = new Timer(3000);
		while(true)
		{
			//scroll down until new instance
			if(scrollTimer.hasExpired())
			{
				throw new LogoutException("Timed out at Scrolling");
			}
			Point newInstance = IntBitmap.getInstance(window.takeScreenshot()).findImage(ImageLibrary.NEW_INSTANCE.get(), 30);
			if(newInstance!=null)
			{
				window.leftClick((int) (newInstance.getX() - 20), (int) newInstance.getY());
				clicked = true;
			}
			else
			{
				if(clicked) break;
				window.leftClick(PWindow.getWidth() / 2, PWindow.getHeight() / 2); //select window
				window.scrollDown();
			}
			Macro.sleep(100);
		}
	}

	public void login(WindowThread thread) throws HaltThread, LogoutException, WindowException
	{
		PWindow window = thread.getWindow();
		window.login(thread);
	}
	private static final double CHANCE_ID_MAGIC = 0.15d;
	public void openArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		GlobalMap myHome = GlobalMap.findHome(window);
		
		PoEMacro.levelUpGems(window);
		
		window.checkMana();
		thread.checkHalt();
		
		new Inventory(thread, myHome).build(CHANCE_ID_MAGIC);
		thread.checkHalt();

		new WaypointNavigator(window).go(this, myHome, thread);
		thread.checkHalt();
	}
	
	private static final int INITIAL_WAIT = 30000;
	private static final int COMBAT_IDLE = 20000;
	private static final int MAX_LEVEL_TIME = 60000 * 3; //max time on a given level
	
	//Generic combat routine
	public boolean clearArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		Profile profile = thread.getProfile();
		
		double currAngle = startAngle();
		Timer wholeLevelTimer = new Timer(getMaxLevelTime());
		Timer combatTimer = new Timer(INITIAL_WAIT); //Might take some time to meet something at first
		Waiter moveTime = null;
		int numMoves = 0;
		while(wholeLevelTimer.stillWaiting()) //fights for a certain amount of time
		{
			thread.checkHalt();
			Healer healer = new Healer(window);
			IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
			if(finishedMap(window))
			{
				return false;
			}
			if(isIdle(combatTimer)) break;
			
			if(moveTime!=null) moveTime.waitFully();
			if(profile.fight(window, this, thread, healer))
			{
				combatTimer.reset(COMBAT_IDLE);
			}
			profile.pickUpItems(window, thread, healer);
			//Move
			PolarMap pMap = new PolarMap(processMap(minimap));
			currAngle = pMap.bestMovement(currAngle);
			
			window.getProfile().getCombatStyle().move(window, currAngle, 250, numMoves);
			numMoves ++;
			
			thread.checkHalt();
			moveTime = new Waiter(500); //make sure movement is done by combat time
			healer.checkHealth();
			moveTime.waitFully();
		}
		return false;
	}
	//Returns whether the character has not seen combat for a significant amount of time
	boolean isIdle(Timer combatTimer)
	{
		return combatTimer.hasExpired();
	}
	public int getMaxLevelTime() 
	{
		return MAX_LEVEL_TIME;
	}
	@Override
	public String toString()
	{
		return name;
	}
	static
	{
		arenas.addAll(TheTwilightStrandSolo.getArenas());
		arenas.addAll(Crossroads.getArenas());
		arenas.addAll(LionEyesWatch.getArenas());
		arenas.addAll(ForestEncampment.getArenas());
		arenas.addAll(SarnEncampment.getArenas());
		arenas.addAll(TheForest.getArenas());
		//arenas.addAll(MudFlats.getArenas());
		//arenas.addAll(TheBrokenBridge.getArenas());
	}
}
