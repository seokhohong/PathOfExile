package arena;

import img.BinaryImage;
import img.FilterType;
import img.GreyscaleImage;
import img.ImageToolkit;
import img.IntBitmap;
import img.RatioFilter;
import items.Item;
import items.ItemFinder;
import items.ItemType;

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
import map.GlobalMap;
import map.Label;
import message.Instruction;
import message.Message;
import party.Portal;
import party.SocialMacro;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
import combat.HealthBarFinder;
import control.MustaphaMond;
import data.Potion;

public class TheTwilightStrand extends Arena
{
	public static final String NAME = "The Twilight Strand";		
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
			arenas.add(new TheTwilightStrand(a));
		}
		return arenas;
	}
	TheTwilightStrand(String s, int level)
	{
		super(s, level, 1);
	}
	private TheTwilightStrand(int level) 
	{
		super("The Twilight Strand", level, 1);
		addFilter(FilterType.WAYPOINT);
		addFilter(FilterType.BROWN_WALL);
		addFilter(FilterType.NEXT_REGION);
	}
	
	private static final int MVMT_DELAY = 200; //between each movement click
	private static final int MVMT_DIST = 250; //how far to move each click
	private static final int MAX_ATK_DIST = 200; //don't click stray hp bars (particularly totem)
	
	//Hillock runner
	//private static final int PORTAL_CLOSE_ENOUGH = 7;
	private static final Point firstMovePoint = new Point(500, 400); //this is dumb
	@Override
	public void login(WindowThread thread) throws HaltThread, LogoutException, WindowException
	{
		PWindow window = thread.getWindow();
		window.login(thread);
		GlobalMap home = GlobalMap.findHome(window);
		PoEMacro.levelUpGems(thread.getWindow());
		thread.getWindow().mouseMove(firstMovePoint);
		new WaypointNavigator(window).go(Arena.fromString("The Twilight Strand2"), home, thread);
	}
	
	@Override
	public void openArena(WindowThread thread) throws LogoutException, HaltThread
	{

	}
	
	/** Returns true to continue without logging out*/
	@Override
	public boolean clearArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		while(true)
		{
			window.getProfile().processMemos(thread.getNetwork(), window);
			SocialMacro.invite(window);
			runToHillock(window, thread);
			
			window.getProfile().castDecoyTotem(window); //make a decoy totem to draw hillock to character and let it portal
			//This boolean is merely whether the character has the spell
			boolean hasPortal = openPortal(window, thread);
			while(!Portal.exists(window))
			{
				openPortal(window, thread);
			}
			sendPortalMessage(thread);

			initiateCombat(window, thread);
			killHillock(window, thread);
			
			Macro.sleep(500);
			//window.getProfile().turnOffAuras(window);

			if(!hasPortal)
			{
				return false;
			}
			else
			{
				//thread.checkHalt();
				/*
				if(!Portal.exists(window))
				{
					GlobalMap.TWILIGHT_STRAND.moveHero(window, Destination.PORTALS, PORTAL_CLOSE_ENOUGH);
				}
				*/
				thread.checkHalt();
				Timer portalTimer = new Timer(5000);
				while(Arena.finishedMap(window) && portalTimer.stillWaiting())
				{
					System.out.println("Clicking Portal!!");
					Portal.click(window);
				}
				if(portalTimer.hasExpired())
				{
					throw new LogoutException("Failed to leave Twilight");
				}
				if(GlobalMap.findHome(window) == GlobalMap.TWILIGHT_STRAND)
				{
					throw new LogoutException("Took the wrong portal");
				}
				thread.checkHalt();
				Timer waypointTimer = new Timer(5000);
				while(waypointTimer.stillWaiting() && !GlobalMap.clickClosestLabel(window, Label.WAYPOINT))
				{
					Macro.sleep(100);
				}
				
				thread.checkHalt();
				new WaypointNavigator(window).clickOnMap(Arena.fromString("The Twilight Strand2"));
			}
		}
	}
	
	private void sendPortalMessage(WindowThread thread)
	{
		Message msg = new Message(thread.getConfig().getComputer(), thread.getNetwork().getComputer(MustaphaMond.getHost()), Instruction.PORTAL_OPENED, new ArrayList<String>());
		thread.sendMessage(msg);
		System.out.println("Sent Portal Message");
	}
	
	private static final Point initMove = new Point(600, 500); 
	private void runToHillock(PWindow window, WindowThread thread) throws HaltThread, LogoutException
	{
		window.mouseMove(initMove); //this is incredibly dumb
		ArrayList<Integer> quicksilvers = window.getProfile().getPotions(Potion.QUICKSILVER);
		Timer emergencyTimer = new Timer(45000);
		Timer speed = new Timer(0); //time before next quicksilver flask required
		int numQuick = 0; //number of quicksilver doses used
		while(!Arena.finishedMap(window))
		{
			thread.checkHalt();
			if(speed.hasExpired())
			{
				useQuicksilver(window, quicksilvers, numQuick);
				numQuick ++ ;
				speed.reset(Potion.getQuicksilverDuration());
			}
			if(emergencyTimer.hasExpired())
			{
				throw new LogoutException("Failed to find Hillock");
			}
			IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
			PoEMacro.moveHero(window, polarSweep(processMap(minimap)), MVMT_DIST);
			Macro.sleep(MVMT_DELAY);
		}
		//GlobalMap.TWILIGHT_STRAND.moveHero(window, thread, Destination.PORTALS, 7);
		Macro.sleep(MVMT_DELAY);
	}
	private void initiateCombat(PWindow window, WindowThread thread) throws HaltThread, LogoutException
	{	
		window.getProfile().castAttackTotem(window);
		window.getProfile().useAuras(window);
		Timer combat = new Timer(20000);
		while(combat.stillWaiting())
		{
			//window.getProfile().attack(window, PWindow.getWindowCenter());
			thread.checkHalt();
			HealthBarFinder hpFinder = new HealthBarFinder(window);
			if(hpFinder.getHealthBars().size() != 0)
			{
				System.out.println("FoundHpBar");
				break;
			}
			Macro.sleep(1000);
		}
	}
	
	private boolean openPortal(PWindow window, WindowThread thread) throws LogoutException, HaltThread
	{
		//GlobalMap.TWILIGHT_STRAND.moveHero(window, thread, Destination.PORTALS, PORTAL_CLOSE_ENOUGH);
		//Macro.sleep(100);
		return window.getProfile().castPortal(window);
	}
	private Point adjustHillockBar(Point bar)
	{
		return new Point(bar.x + 10, bar.y + 20);
	}
	private static final int CEASE_COMBAT = 2; //stop fighting at this number of items
	private void killHillock(PWindow window, WindowThread thread) throws HaltThread
	{	
		Timer fighting = new Timer(20000); //combat should be very quick, but we'll give it some time
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
				attackWaiter = attackHillock(window, adjustHillockBar(permaBar), attackWaiter);
			}
			thread.checkHalt();
			
			ArrayList<Item> items = ItemFinder.findItems(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT)), magic);
			if(items.size() > CEASE_COMBAT)
			{
				return;
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
	private static final double HEAL_FREQ = 0.3;
	private Waiter attackHillock(PWindow window, Point hpBar, Waiter attackWaiter)
	{
		if(new Random().nextDouble() < HEAL_FREQ)
		{
			window.getProfile().useHealingPotion(window);
		}
		if(attackWaiter != null)
		{
			attackWaiter.waitFully();
		}
		return new Waiter(window.getProfile().attack(window, hpBar));
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
				if(rad == 0 && ang == Math.PI && data[pixX][pixY] == BinaryImage.WHITE) //we're in water here
				{
					return Math.PI / 2d;
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
