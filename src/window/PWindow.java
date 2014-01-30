package window;

import img.BinaryImage;
import img.Display;
import img.FilterType;
import img.ImageLibrary;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;
import img.RatioFilter;
import items.ItemFinder;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import macro.AccountNavigator;
import macro.LogoutException;
import macro.Macro;
import macro.Timer;
import macro.WindowException;
import combat.Mana;
import data.Profile;
import data.Config;

/**
 * 
 * Represents the Path of Exile window on the screen. Position refers to the top left corner of the graphics rectangle
 * 
 * @author Seokho
 *
 */
public class PWindow 
{
	private static Robot robot;

	static
	{
		try
		{
			robot = new Robot();
		}
		catch(AWTException e) {}
	}
	
	//Screen resolution should be WIDTH x HEIGHT
	private static final int WIDTH = 800;			public static int getWidth() { return WIDTH; }
	private static final int HEIGHT = 600;			public static int getHeight() { return HEIGHT; }
	
	private static final int MINIMAP_SIZE = 150;	public static int getMinimapSize() { return MINIMAP_SIZE; }
	
	//EVERYTHING IS RELATIVE TO THE TOP LEFT OF THE GRAPHICS WINDOW
	//Offset from the Windows coordinates to the PWindow coordinate system (absolute -> graphical window)
	private static final int X_OFFSET = 8;
	private static final int Y_OFFSET = 31;
	
	private Profile profile;	public Profile getProfile() { return profile; }
								public void setProfile(Profile p) { profile = p; }
								public void clearProfile() { profile = null; }
	
	//To protect from LogoutExceptions if PoE is still loading
	private boolean hasSeenLogin = false; 	public boolean hasSeenLogin() { return hasSeenLogin; }
											public void sawLogin() { hasSeenLogin = true; }
											
	private static final int LOGIN_ERROR = 10000;			public static int getLoginErrorThreshold() { return LOGIN_ERROR; }
	
	private int x;				public int getX() { return x; }
	private int y;				public int getY() { return y; }
	
	private AccountNavigator acctNav;			public void logout() { acctNav.logout(); }
	
	public static Point getWindowCenter() { return new Point(WIDTH / 2, HEIGHT / 2); }
	public static Point selectPoint() { return new Point(PWindow.getWidth() / 2, PWindow.getHeight() - 10); }
	public Point getCenter() { return new Point(x + WIDTH, y + HEIGHT); }
	public Point getLocation() { return new Point(x, y); }
	
	private Macro macro;
	
	private PWindow(Config config, int x, int y)
	{
		this.macro = config.getMacro();
		acctNav = new AccountNavigator(config, this);
		this.x = x;
		this.y = y;
	}
	
	//Constructs a PWindow from a string in the format: "# #"
	
	public static PWindow fromString(Config config, String s)
	{
		String[] split = s.split(" ");
		int x = Integer.parseInt(split[0]) + X_OFFSET;
		int y = Integer.parseInt(split[1]) + Y_OFFSET;
		return new PWindow(config, x, y);
	}
	
	//Returns whether the image exists anywhere in the PWindow
	public boolean imageExists(IntBitmap icon)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot());
		return imageExists(icon, regionImage);
	}
	@Deprecated
	public boolean imageExists(IntBitmap icon, ScreenRegion region)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot(region));
		return imageExists(icon, regionImage);
	}
	/** 
	 * Bruteforce match check
	 * @param icon
	 * @param searchArea
	 * @return
	 */
	public boolean imageExists(IntBitmap icon, IntBitmap searchArea)
	{
		return searchArea.findImage(icon) != null;
	}
	public boolean imageExists(IntBitmap icon, IntBitmap searchArea, int threshold)
	{
		return searchArea.findImage(icon, threshold) != null;
	}
	public boolean imageMatches(IntBitmap icon, ScreenRegion region)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot(region));
		return imageMatches(icon, regionImage);
	}
	private static final int GENERIC_MATCH_THRESHOLD = 2000; 	//Generally speaking anything that doesn't match will be far above, matches far under
	public boolean imageMatches(IntBitmap icon, IntBitmap searchArea)
	{
		//System.out.println(searchArea.matchError(icon));
		return searchArea.matchError(icon) < GENERIC_MATCH_THRESHOLD;
		
	}
	/**
	 * 
	 * Clicks on the center of the image using the IntBitmap.isMatch function that looks for coloring
	 * 
	 * @param icon		:	The image to click on
	 * @param region	: 	Area the icon should be
	 * @return			:	Returns whether the image was found or not
	 */
	
	public boolean clickOnImageMatch(IntBitmap icon, ScreenRegion region)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot(region));
		if(icon.isMatch(regionImage))
		{
			leftClick(region.getCenter());
			return true;
		}
		return false;
	}
	public boolean existsImageMatch(IntBitmap icon, ScreenRegion region, int error)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot(region));
		return icon.matchError(regionImage) < error;
	}
	public boolean clickOnImageMatch(IntBitmap icon, ScreenRegion region, int error)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot(region));
		//System.out.println(icon.matchError(regionImage));
		if(icon.matchError(regionImage) < error)
		{
			leftClick(region.getCenter());
			return true;
		}
		return false;
	}
	/**
	 * 
	 * Clicks on the center of the image using IntBitmap.findImage which does a bruteforce match
	 * 
	 * @param icon		:	The image to click on
	 * @param region	: 	Area the icon should be
	 * @return			:	Returns whether the image was found or not
	 */
	
	public boolean clickOnImageFound(IntBitmap icon, ScreenRegion region)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot(region));
		Point p = regionImage.findImage(icon, 20);
		if(p != null)
		{
			leftClick(region.getAbsoluteX() + p.x, region.getAbsoluteY() + p.y);
		}
		return p != null;
	}
	public boolean clickOnImageFound(IntBitmap icon, ScreenRegion region, int threshold)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot(region));
		//Display.show(regionImage);
		Point p = regionImage.findImage(icon, threshold);
		if(p != null)
		{
			leftClick(region.getAbsoluteX() + p.x, region.getAbsoluteY() + p.y);
		}
		return p != null;
	}
	@Deprecated
	public boolean clickOnImage(IntBitmap icon)
	{
		IntBitmap regionImage = IntBitmap.getInstance(takeScreenshot());
		Point toClick = regionImage.findImage(icon);
		if(toClick != null)
		{
			leftClick(toClick.x, toClick.y);
		}
		return toClick != null;
	}
	/**
	 * 
	 * Logs in, selects the character specified by prof, and uses the waypoint to navigate to an arena.
	 * 
	 * @param prof				: Specified character
	 * @return					: The arena to which this character navigated to
	 * @throws LogoutException	: Something bad happened
	 * @throws WindowException	: Something even worse happened including non-visible window
	 * @throws HaltThread 
	 */
	/*public Arena openArena(Profile prof, WindowThread thread) throws LogoutException, WindowException, HaltThread
	{
		this.profile = prof;
		acctNav.login(thread);
		System.out.println("Logged In");
		thread.checkHalt();
		acctNav.selectCharacter(prof.getIndexOnList(), thread);
		thread.checkHalt();
		waitForHpBar(thread);
		PoEMacro.levelUpGems(this);
		Unfriender.unfriendAll(this);
		profile.reparty(this);
		//If unsuccessful return null
		if(new Mana(this).hasMana()) //if we can find a mana bar
		{
			profile.foundManaBar();
		}
		Arena destination = prof.getAvailableArena();
		thread.checkHalt();
		wayptNav.go(destination, thread);
		thread.checkHalt();
		return destination;
	}
*/
	/** Runs the whole login sequence 
	 * @throws WindowException 
	 * @throws LogoutException 
	 * @throws HaltThread */
	public void login(WindowThread thread) throws HaltThread, LogoutException, WindowException
	{
		setProfile(thread.getProfile());
		acctNav.login(thread);
		System.out.println("Logged In");
		thread.checkHalt();
		acctNav.selectCharacter(thread.getProfile().getIndexOnList(), thread);
		thread.checkHalt();
		waitForHpBar(thread);
	}
	
	private void checkBounds(int x, int y)
	{
		if(x < -8 || y < -31 || x > 800 || y > 600)
		{
			System.err.println("Clicking at "+x+", "+y);
			for(StackTraceElement st : Thread.currentThread().getStackTrace())
			{
				System.out.println(st);
			}
			System.exit(1);
		}
	}
	public void mouseMove(Point p)
	{
		checkBounds(p.x, p.y);
		robot.mouseMove(p.x + this.x, p.y + this.y); //transforms window coords into real coords
	}
	public void mouseMove(double x, double y)
	{
		robot.mouseMove((int) (x + this.x), (int) (y + this.y)); //transforms window coords into real coords
	}
	public void leftClick(Point p)
	{
		checkBounds(p.x, p.y);
		macro.click(p.x + this.x, p.y + this.y); //transforms window coords into real coords
	}
	public void leftClickCarefully(Point p)
	{
		checkBounds(p.x, p.y);
		macro.clickCarefully(p.x + this.x, p.y + this.y); //transforms window coords into real coords
	}
	public void doubleClick(Point p)
	{
		checkBounds(p.x, p.y);
		macro.doubleClick(p.x + this.x, p.y + this.y);
	}
	public void rightClick(Point p)
	{
		checkBounds(p.x, p.y);
		macro.rightClick(p.x + this.x, p.y + this.y); //transforms window coords into real coords
	}
	public void middleClick(Point p)
	{
		checkBounds(p.x, p.y);
		macro.middleClick(p.x + this.x, p.y + this.y); //transforms window coords into real coords
	}
	public void leftClick(int x, int y)
	{
		checkBounds(x, y);
		macro.click(x + this.x, y + this.y);
	}
	public void rightClick(int x, int y)
	{
		checkBounds(x, y);
		macro.rightClick(x + this.x, y + this.y); //transforms window coords into real coords
	}
	
	public void scrollDown() { macro.scrollDown(); }
	public void pressControl() { macro.pressControl(); }
	public void releaseControl() { macro.releaseControl(); }
	public void pressShift() { macro.pressShift(); }
	public void releaseShift() { macro.releaseShift(); }
	
	public void type(String s)
	{
		macro.type(this, s);
	}
	public void typeInField(String s, Point fieldLocation)
	{
		macro.typeInField(this, s, fieldLocation);
	}
	
	public void pressEscape()
	{
		macro.pressEscape(this);
	}
	public void toggleInventory()
	{
		macro.type('i');
	}
	public void select()
	{
		macro.selectWindow(this);
	}
	public void eraseField()
	{
		macro.eraseField();
	}
	
	public void clickItem(int x, int y)
	{
		checkBounds(x, y);
		macro.click(x + this.x, y + this.y + ItemFinder.getItemHeight() / 2);
	}
	//whole area
	public BufferedImage takeScreenshot()
	{
		return robot.createScreenCapture(new Rectangle(x, y, WIDTH, HEIGHT));
	}
	//part
	public BufferedImage takeScreenshot(ScreenRegion region)
	{
		return robot.createScreenCapture(region.get(this));
	}
	//part by rectangle
	public BufferedImage takeScreenshot(Rectangle rect)
	{
		return robot.createScreenCapture(new Rectangle(x + rect.x, y + rect.y, rect.width, rect.height));
	}
	
	public void checkMana()
	{
		if(new Mana(this).hasMana()) //if we can find a mana bar
		{
			profile.foundManaBar();
		}
	}
	
	private static final int HP_WAIT = 20000;
	//Sleeps until the Health Bar in the lower left hand corner is visible
	private void waitForHpBar(WindowThread thread) throws LogoutException, HaltThread, WindowException
	{
		Timer maxWait = new Timer(HP_WAIT);
		while(maxWait.stillWaiting()) //wait until I can select a character
		{
			IntBitmap screen = IntBitmap.getInstance(takeScreenshot());
			acctNav.resolveLoginScreenError(screen, thread);		//Can happen if there is an "Abnormal Disconnection"
			if(myHealthVisible()) 
			{
				return;
			}
			//System.out.println(Thread.currentThread()+" does not see Life bar");
			Macro.sleep(500);
		}
	}
	/** 
	 * 
	 * Is the character's HP bar visible?
	 * 
	 * @return
	 */
	public boolean myHealthVisible()
	{
		IntBitmap lifeBar = IntBitmap.getInstance(takeScreenshot(ScreenRegion.LIFE_RECT));
		RatioFilter.maintainRatio(lifeBar, FilterType.LIFE);
		BinaryImage bin = lifeBar.toGreyscale().doubleCutoff(30);
		return bin.countWhite() > 1000;
		//return imageMatches(IntBitmap.getInstance(takeScreenshot(ScreenRegion.FIND_HEALTH_RECT)), ImageLibrary.MY_HEALTH_ICON.get());
	}
	private static final int INVENTORY_THRESHOLD = 200;
	public boolean inventoryVisible()
	{
		IntBitmap invent = IntBitmap.getInstance(takeScreenshot(ScreenRegion.INVENTORY_OPEN_RECT));
		MidpassFilter.maintainRanges(invent, MidpassFilterType.STASH_OPEN);
		BinaryImage bin = invent.toGreyscale().doubleCutoff(30);
		//Display.showHang(bin);
		bin.killLoners(1, BinaryImage.WHITE);
		return bin.countWhite() > INVENTORY_THRESHOLD;
	}
	
	public boolean minimapWaypointVisible()
	{
		IntBitmap map = IntBitmap.getInstance(takeScreenshot(ScreenRegion.MAP_RECT));
		MidpassFilter.maintainRanges(map, MidpassFilterType.WAYPOINT);
		BinaryImage bin = map.toGreyscale().doubleCutoff(10);
		return bin.countWhite() > 1;
	}

	public boolean loginVisible()
	{
		IntBitmap img = IntBitmap.getInstance(takeScreenshot(ScreenRegion.LOGIN_RECT));
		return img.isMatch(ImageLibrary.LOGIN_BUTTON.get()) || img.matchError(ImageLibrary.LOGIN_BUTTON.get()) < LOGIN_ERROR;
	}
	public boolean charSelectionVisible()
	{
		boolean charSelection = imageMatches(IntBitmap.getInstance(takeScreenshot(ScreenRegion.CHAR_SELECT_RECT)), ImageLibrary.CHARACTER_SELECT.get());
		//System.out.println("CharSelection "+charSelection);
		return charSelection;
	}
	public boolean worldNavVisible()
	{
		IntBitmap map = IntBitmap.getInstance(takeScreenshot(ScreenRegion.WORLD_RECT));
		//System.out.println("Nav Error: "+ map.matchError(ImageLibrary.WORLD_LABEL.get()));
		//boolean worldNav = imageMatches(map, ImageLibrary.WORLD_LABEL.get());
		//System.out.println("Nav visibility "+worldNav);
		return map.matchError(ImageLibrary.WORLD_LABEL.get()) < 500000; //Eww
	}
	public boolean logoutVisible()
	{
		return imageMatches(IntBitmap.getInstance(takeScreenshot(ScreenRegion.LOGOUT_RECT)), ImageLibrary.LOGOUT_BUTTON.get());
	}
	
	public boolean destroyItemVisible()
	{
		return imageMatches(IntBitmap.getInstance(takeScreenshot(ScreenRegion.DESTROY_ITEM_RECT)), ImageLibrary.DESTROY_ITEM.get());
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof PWindow)
		{
			PWindow wind = (PWindow) o;
			if(wind.x == x && wind.y == y)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return x * WIDTH +y;
	}
	
	@Override
	public String toString()
	{
		return "PWindow("+x+", "+y+")";
	}
}