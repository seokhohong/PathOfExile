package arena;

import img.BinaryImage;
import img.FilterType;
import img.GreyscaleImage;
import img.ImageToolkit;
import img.IntBitmap;
import img.RatioFilter;
import inventory.Inventory;
import items.Item;
import items.ItemFinder;
import items.ItemType;

import java.awt.Point;
import java.util.ArrayList;

import macro.LogoutException;
import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import macro.Waiter;
import macro.WaypointNavigator;
import macro.WindowException;
import map.Destination;
import map.GlobalMap;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
import combat.HealthBarFinder;
import data.Potion;

public class TheTwilightStrandSolo extends Arena
{
	public static final String NAME = "The Twilight Strand Solo";		
	@Override
	public String getName() { return NAME; }
	
	static ArrayList<ItemType> types = new ArrayList<ItemType>();
	static ArrayList<ItemType> magic = new ArrayList<ItemType>();
	static ArrayList<ItemType> priorityItem = new ArrayList<ItemType>();

	static
	{
		types.add(ItemType.MAGIC);
		types.add(ItemType.RARE);
		types.add(ItemType.UNIQUE);
		types.add(ItemType.CURRENCY);
		magic.add(ItemType.MAGIC);
		priorityItem.add(ItemType.CURRENCY);
		priorityItem.add(ItemType.RARE);
		priorityItem.add(ItemType.UNIQUE);
	}
	
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new TheTwilightStrandSolo(a));
		}
		return arenas;
	}
	private TheTwilightStrandSolo(int level) 
	{
		super(NAME, level, 1);
		addFilter(FilterType.WAYPOINT);
		addFilter(FilterType.BROWN_WALL);
		addFilter(FilterType.NEXT_REGION);
	}
	
	private static final double MVMT_SPEED = 2.0d; //for item pickup speed
	private static final int MVMT_DELAY = 200; //between each movement click
	private static final int MVMT_DIST = 250; //how far to move each click
	private static final int MAX_ATK_DIST = 200; //don't click stray hp bars (particularly totem)
	
	@Override
	public void login(WindowThread thread) throws HaltThread, LogoutException, WindowException
	{
		PWindow window = thread.getWindow();
		window.login(thread);
		GlobalMap foundHome = GlobalMap.findHome(window);
		if(foundHome != GlobalMap.LION_EYES_WATCH)
		{
			foundHome.moveHero(thread, Destination.WAYPOINT, 10); //arbitrary distance
			new WaypointNavigator(window).go(Arena.fromString("Lion Eyes Watch2"), foundHome, thread);
		}
	}
	
	private static final double CHANCE_ID_MAGIC = 0.175d;
	public void openArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		GlobalMap myHome = GlobalMap.LION_EYES_WATCH;
		
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
		window.getProfile().processMemos(thread.getNetwork(), window);
		window.getProfile().useAuras(window);
		runToHillock(thread);
		initiateCombat(thread);
		pickUpItems(thread);
		window.getProfile().usePotion(window, Potion.QUICKSILVER);
		try
		{
			GlobalMap.TWILIGHT_STRAND.moveHero(thread, Destination.WAYPOINT, 2);
			GlobalMap.TWILIGHT_STRAND.moveHero(thread, Destination.WAYPOINT, 2);
		}
		catch(LogoutException e)
		{
			
		}
		waitForHealthBar(window);
		System.out.println("Now In Lion Eyes Watch");
		//GlobalMap.LION_EYES_WATCH.blindMove(thread, 0.0d); //to the right
		return true;
	}
	
	private void waitForHealthBar(PWindow window) throws LogoutException
	{
		Timer maxWait = new Timer(10000);
		while(!window.myHealthVisible())
		{
			Macro.sleep(300);
			if(maxWait.hasExpired())
			{
				throw new LogoutException("Too long at Transition");
			}
		}
	}
	
	private static final Point initMove = new Point(600, 500); 
	private void runToHillock(WindowThread thread) throws HaltThread
	{
		PWindow window = thread.getWindow();
		window.mouseMove(initMove); //this is incredibly dumb
		ArrayList<Integer> quicksilvers = window.getProfile().getPotions(Potion.QUICKSILVER);
		Timer emergencyTimer = new Timer(2 * 60000);
		Timer speed = new Timer(0); //time before next quicksilver flask required
		int numQuick = 0; //number of quicksilver doses used
		while(emergencyTimer.stillWaiting() && !Arena.finishedMap(window))
		{
			thread.checkHalt();
			IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
			double angle = polarSweep(processMap(minimap));
			PoEMacro.moveHero(window, angle, MVMT_DIST);
			if(speed.hasExpired())
			{
				useQuicksilver(window, quicksilvers, numQuick);
				numQuick ++ ;
				speed.reset(Potion.getQuicksilverDuration());
			}
			Macro.sleep(MVMT_DELAY);
		}
		Macro.sleep(MVMT_DELAY);
	}
	private void initiateCombat(WindowThread thread) throws HaltThread
	{
		PWindow window = thread.getWindow();
		window.getProfile().castAttackTotem(window); //make a totem to identify
		Timer combat = new Timer(20000);
		while(combat.stillWaiting())
		{
			thread.getProfile().attack(window, PWindow.getWindowCenter());
			thread.checkHalt();
			HealthBarFinder hpFinder = new HealthBarFinder(window);
			if(hpFinder.getHealthBars().size() != 0)
			{
				break;
			}
			Macro.sleep(1000);
		}
	}
	private static final int PICKUP_ATTEMPTS = 25;
	private void pickUpItems(WindowThread thread) throws HaltThread
	{
		PWindow window = thread.getWindow();
		ArrayList<ItemType> searchLevel = priorityItem; //drops it after it can't find any
		
		boolean seesItems = false;
		
		Timer fighting = new Timer(30000); //combat should be very quick, but we'll give it some time
		Waiter attackWaiter = null;
		Point permaBar = null;
		while(fighting.stillWaiting())
		{
			thread.checkHalt();
			Point hillockBar = getHillockHpBar(window);
			if(hillockBar == null) //always have a bar to attack
			{
				hillockBar = permaBar;
			}
			else
			{
				permaBar = hillockBar; 
			}
			if(permaBar!=null)
			{
				attackWaiter = attackHillock(window, permaBar, attackWaiter);
			}
			thread.checkHalt();
			ArrayList<Item> items = ItemFinder.findItems(window, ScreenRegion.ITEM_SCAN_RECT, magic);
			if(items.size() > 1)
			{
				permaBar = null;
				//Timer itemTime = new Timer(6000); //max time to pick up items
				//while(itemTime.stillWaiting())
				for(int a = 0; a < PICKUP_ATTEMPTS; a++)
				{
					thread.checkHalt();
					items = ItemFinder.findItems(window, ScreenRegion.ITEM_SCAN_RECT, searchLevel);
					if(items.isEmpty())
					{
						if(searchLevel == priorityItem)
						{
							searchLevel = types;
						}
						else
						{
							return;
						}
					}
					else
					{
						if(items.get(0).getX() > 100) //dumb thing thinks there's an item off to the left (thunder?)
						{
							pickUpItem(window, items.get(0));
						}
					}
					if(a == PICKUP_ATTEMPTS - 1) //inventory full
					{
						return;
					}
				}

				if(seesItems) //may be a fluke
				{
					break;
				}
				seesItems = true;
			}

			Macro.sleep(100); //give program a break
		}
	}
	private Point getHillockHpBar(PWindow window)
	{
		HealthBarFinder hpFinder = new HealthBarFinder(window);
		for(Point hpBar : hpFinder.getHealthBars())
		{
			if(hpBar.distance(PWindow.getWindowCenter()) < MAX_ATK_DIST)
			{
				return hpBar;
			}
		}
		return null;
	}
	private Waiter attackHillock(PWindow window, Point hpBar, Waiter attackWaiter)
	{
		window.getProfile().useHealingPotion(window);
		if(attackWaiter != null)
		{
			attackWaiter.waitFully();
		}
		return new Waiter(window.getProfile().attack(window, hpBar));
	}
	private void pickUpItem(PWindow window, Item item)
	{
		double distToItem = item.toPoint().distance(PWindow.getWindowCenter());	//how far away the item is
		window.leftClick(item.getCenter()); //don't click on the top left corner!
		Macro.sleep((int) (distToItem * MVMT_SPEED));
	}
	//Each Quicksilver flask contains two doses
	private void useQuicksilver(PWindow window, ArrayList<Integer> quicksilvers, int numDosesUsed)
	{
		if(numDosesUsed / 2 < quicksilvers.size())
		{
			window.type(Integer.toString(quicksilvers.get(numDosesUsed / 2)));
		}
	}
	//Polar sweep to determine what angle it can move at, it picks the one at the first major block by water
	private double polarSweep(BinaryImage map)
	{
		boolean[][] data = map.getData();
		for(double ang = Math.PI; ang > -Math.PI / 4; ang -= Math.PI / 8)
		{
			for(int rad = 0; rad < map.getWidth() / 2; rad++)
			{
				int pixX = (int) (Math.cos(ang) * rad) + data.length / 2;
				int pixY = (int) (-Math.sin(ang) * rad) + data.length / 2;
				if(rad < 30 && data[pixX][pixY] == BinaryImage.WHITE)
				{
					ang += Math.PI / 4d; //to compensate for the stripping of some ocean
					return ang;
				}
			}
		}
		return 0d;
	}
	@Override
	public double startAngle()
	{
		return Math.PI * 3d / 2d;
	}
	@Override
	public BinaryImage processMap(IntBitmap map)
	{
		RatioFilter.maintainRatio(map, FilterType.WATER);
		GreyscaleImage grey = map.toGreyscale();
		grey.blur(4);
		grey = grey.doubleCutoff(10).toGreyscale(); //solidify grainy water bits
		grey.blur(10); //bite off a chunk of water
		return grey.doubleCutoff(ImageToolkit.MAX_VAL - 1); 
		/*
		//water processing
		IntBitmap waterMap = IntBitmap.copy(map);
		RatioFilter.maintainRatio(waterMap, FilterType.WATER);
		GreyscaleImage greyWater = waterMap.toGreyscale();
		//wipes out narrow clusters of water, i.e streams
		greyWater.blur(4);
		BinaryImage binWater = greyWater.doubleCutoff(40);
		
		//everything else
		RatioFilter.maintainRatio(map, getObstacles());
		GreyscaleImage grey = map.bidirectionalDerivative();
		grey.multiply(15);
		BinaryImage bin = grey.doubleCutoff(ImageToolkit.MAX_VAL - 1);
		bin.add(binWater);
		bin.invert();
		grey = bin.toGreyscale();
		bin = grey.doubleCutoff(ImageToolkit.MAX_VAL - 1);
		return bin;
		*/
	}
	@Override
	public void clickLocation(PWindow window) 
	{
		window.leftClick(105, 280);
	}

}
