package arena;

import img.BinaryImage;
import img.IntBitmap;
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
import macro.WaypointNavigator;
import map.Destination;
import map.GlobalMap;
import map.Label;
import party.Portal;
import party.SocialMacro;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;

public class WaiterRoutine extends Arena
{
	public static final String NAME = "Waiter";		@Override public String getName() { return NAME; }
	
	private static final ArrayList<ItemType> highPriority = new ArrayList<ItemType>();
	private static final ArrayList<ItemType> lowPriority = new ArrayList<ItemType>();
	
	static
	{
		highPriority.add(ItemType.UNIQUE);
		highPriority.add(ItemType.RARE);
		highPriority.add(ItemType.CURRENCY);
		lowPriority.addAll(highPriority);
		lowPriority.add(ItemType.MAGIC);
	}
	
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 1; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new WaiterRoutine(a));
		}
		return arenas;
	}
	private WaiterRoutine(int level) 
	{
		super("Waiter", level, 3);
	}
	//Doesn't check inventory
	@Override
	public void openArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		//window.login(prof, thread);
		
		PoEMacro.levelUpGems(window);
		
		thread.checkHalt();
		thread.getProfile().reparty(window);
		
		window.checkMana();
		thread.checkHalt();
		
		if(GlobalMap.findHome(window) != GlobalMap.LION_EYES_WATCH)
		{
			new WaypointNavigator(window).go(Arena.fromString("Lion Eyes Watch2"), GlobalMap.SARN, thread);
		}
		//Will end up at Sarn
	}
	
	@Override
	public boolean clearArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		//Starts in Sarn
		while(true)
		{
			new Inventory(thread, GlobalMap.LION_EYES_WATCH).build(0.0);
			thread.checkHalt();
			GlobalMap.LION_EYES_WATCH.moveHero(thread, Destination.WAYPOINT, 15);
			new WaypointNavigator(window).go(Arena.fromString("Sarn Encampment1"), GlobalMap.LION_EYES_WATCH, thread);
			waitForRunnerPortal(thread);
			thread.checkHalt();
			waitForItems(window, thread);
			System.out.println("Picking Items");
			pickUpItems(window, thread);
			thread.checkHalt();
			//GlobalMap.TWILIGHT_STRAND.moveHero(thread, Destination.PORTALS, 20);
			thread.checkHalt();
			System.out.println("Going Home");
			if(Portal.exists(window))
			{
				waitForHomePortal(thread);
			}
			else
			{
				GlobalMap.TWILIGHT_STRAND.moveHero(thread, Destination.WAYPOINT, 10); //go through the door
				thread.checkHalt();
				new WaypointNavigator(window).go(Arena.fromString("Sarn Encampment1"), GlobalMap.LION_EYES_WATCH, thread);
				thread.checkHalt();
			}
		}
	}
	
	private void waitForHomePortal(WindowThread thread) throws LogoutException
	{
		PWindow window = thread.getWindow();
		while(WaiterRoutine.finishedMap(window))
		{
			Portal.clickOnce(window);
			Macro.sleep(500);
		}
		
		Timer homeTimer = new Timer(10000);
		while(homeTimer.stillWaiting())
		{
			if(window.myHealthVisible() && GlobalMap.findHome(window) == GlobalMap.SARN)
			{
				break;
			}
			Macro.sleep(200);
		}
		if(!GlobalMap.clickClosestLabel(window, Label.WAYPOINT))
		{
			throw new LogoutException("Couldn't find Waypoint in Sarn");
		}
		new WaypointNavigator(window).clickOnMap(Arena.fromString("Lion Eyes Watch2"));

	}
	
	private static final int PARTY_FREQ = 5;
	private void waitForRunnerPortal(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		System.out.println("Waiting for Runner portal");
		Timer portalTimer = new Timer(90 * 1000);
		int numSeconds = 0;
		while(true)
		{
			thread.checkHalt();
			if(window.getProfile().isPortalOpen())
			{
				Portal.clickOnce(window);
				Timer checkNext = new Timer(2000);
				while(checkNext.stillWaiting())
				{
					if(Arena.finishedMap(window)) //need to get through
					{
						System.out.println("I see the light!");
						window.getProfile().markPortalClosed();
						return;
					}
				}
				
			}
			numSeconds ++;
			if(numSeconds % PARTY_FREQ == 0)
			{
				SocialMacro.acceptPartyRequests(window);
			}
			Macro.sleep(1000);
			if(portalTimer.hasExpired())
			{
				throw new LogoutException("Can't find Portal. Is there a Runner?");
			}
		}
	}
	
	private static final int ENOUGH_ITEMS = 4;
	private void waitForItems(PWindow window, WindowThread thread)
	{
		Timer itemTime = new Timer(30000); //max time to wait for items
		while(itemTime.stillWaiting())
		{
			ArrayList<Item> items = ItemFinder.findItems(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT)), lowPriority);
			if(items.size() > ENOUGH_ITEMS)
			{
				return;
			}
		}
	}
	
	private void pickUpItems(PWindow window, WindowThread thread) throws LogoutException, HaltThread
	{
		ArrayList<ItemType> searchLevel = highPriority;
		
		Timer itemTime = new Timer(5000); //max time to pick up items
		while(itemTime.stillWaiting())
		{
			thread.checkHalt();
			ArrayList<Item> items = ItemFinder.findItems(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT)), searchLevel);
			if(items.isEmpty())
			{
				if(searchLevel == highPriority)
				{
					searchLevel = lowPriority;
				}
				else
				{
					return;
				}
			}
			else
			{
				if(items.get(0).getX() > 100) //dumb thing thinks there's an item off to the left (thunder)
				{
					pickUpItem(window, items.get(0));
				}
			}
		}
		if(itemTime.hasExpired()) //inventory full
		{
			return;
		}
	}
	private static final double MVMT_SPEED = 1.3d; //for item pickup speed
	private void pickUpItem(PWindow window, Item item)
	{
		double distToItem = item.toPoint().distance(PWindow.getWindowCenter());	//how far away the item is
		window.leftClick(item.getX() + item.getWidth() / 4, item.getY() + 4); //don't click on the top left corner!
		Macro.sleep((int) (distToItem * MVMT_SPEED));
	}

	
	@Override
	public BinaryImage processMap(IntBitmap map)
	{
		System.out.println("Does not use this");
		return null;
	}
	@Override
	public void clickLocation(PWindow window) 
	{
		window.leftClick(new Point(180, 230));
	}
}
