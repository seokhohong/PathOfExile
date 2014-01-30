package macro;

import java.awt.Point;

import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;
import img.RatioFilter;
import inventory.Inventory;
import inventory.InventoryMacro;
import inventory.InventorySlot;
import map.Destination;
import map.GlobalMap;
import map.Label;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
/**
 * Contains public methods which may take hero to stash or store once class has been instantiated using window thread.
 * 
 * @author Jamison
 *
 */
public class HomeNavigator 
{
	private WindowThread thread;
	private PWindow window;
	private GlobalMap global;
	
	public HomeNavigator(WindowThread thread, GlobalMap global) throws LogoutException
	{
		this.thread = thread;
		window = thread.getWindow();
		this.global = global;
 	}
	
	public void goToStore() throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		System.out.println("Going to store.");
		Macro.sleep(50);
		global.blindMove(thread, 0.0d);
		try
		{
			Timer storeTimer = new Timer(15000);
			while(!global.moveHeroOnceStore(thread))
			{
				thread.checkHalt();
				global.checkDestroyItem(window);
				if(storeDialogOpen())
				{
					System.out.println("Dialog is Open");
					break;
				}
				if(storeTimer.hasExpired())
				{
					throw new LogoutException("Failed to move");
				}
			}
		} 
		catch(LogoutException e)
		{
			goStoreBlindly(thread);
		}
		waitForStoreDialog(thread);
		Macro.sleep(50);
	}
	//Made for Sarn
	private void goStoreBlindly(WindowThread thread) throws LogoutException, HaltThread
	{
		Timer tryStore = new Timer(3000);
		while(true)
		{
			thread.checkHalt();
			global.clickClosestStoreLabel(this, thread.getWindow());
			Macro.sleep(300);
			if(storeDialogOpen())
			{
				return;
			}
			if(tryStore.hasExpired())
			{
				throw new LogoutException("Ok Really Can't see Waypoint");
			}
		}
	}
	private static final Point ACCEPT_LOCATION = new Point(80, 450);
	public void sellShit(Inventory inventory, PWindow window) throws LogoutException
	{
		Timer checkInventTimer = new Timer(1000);
		while(!window.inventoryVisible())
		{
			if(checkInventTimer.hasExpired())
			{
				throw new LogoutException("Not actually at store");
			}
		}
		if(stashOpen())
		{
			throw new LogoutException("At Stash not Store");
		}
		for(InventorySlot slot : inventory.getSlots())
		{
			if(slot.shouldSell())
			{
				slot.move();
				Macro.sleep(50);
			}
		}
		Macro.sleep(100);
		window.leftClickCarefully(ACCEPT_LOCATION); //accept
		window.leftClickCarefully(ACCEPT_LOCATION);
	}
	public void goToStash() throws HaltThread, LogoutException
	{
		System.out.println("Going to stash.");
		global.blindMove(thread, 0.0d);
		try
		{
			Timer stashTimer = new Timer(15000);
			while(!global.moveHeroOnce(thread, Destination.STASH, 10))
			{
				thread.checkHalt();
				global.checkDestroyItem(window);
				if(stashOpen() && thread.getWindow().inventoryVisible())
				{
					System.out.println("Stash is Open");
					break;
				}
				if(storeDialogOpen())
				{
					window.pressEscape();
					Macro.sleep(500);
				}
				if(stashTimer.hasExpired())
				{
					throw new LogoutException("Failed to move");
				}
			}
		}
		catch(LogoutException e)
		{
			if(!stashOpen())
			{
				throw new LogoutException("Ok Really Can't see Waypoint");
			}
		}
		
		if(global == GlobalMap.LION_EYES_WATCH)
		{
			Macro.sleep(300);
			if(!stashOpen())
			{
				GlobalMap.clickClosestStashLabel(window);
			}
		}
		else
		{
			Macro.sleep(400); //stop moving
			GlobalMap.clickClosestLabel(window, Label.STASH);
			//GlobalMap.clickClosestLabel(window, Label.STASH);
		}
		waitForStash();
	}
	
	private void waitForStash() throws LogoutException
	{
		Macro.sleep(20); //for transaction to close
		Timer waitForStash = new Timer(4 * 1000);
		while(!stashOpen() && waitForStash.stillWaiting())
		{
			if(window.destroyItemVisible())
			{
				window.leftClick(ScreenRegion.DESTROY_ITEM_RECT.getCenter());
			}
			if(waitForStash.elapsedTime() > 2 * 1000)
			{
				GlobalMap.clickClosestLabel(window, Label.STASH);
				Macro.sleep(500);
			}
			Macro.sleep(50);
			
		}
		if(waitForStash.hasExpired())
		{
			throw new LogoutException("No Stash.");
		}
	}
	private static final int STASH_THRESHOLD = 300;
	public boolean stashOpen()
	{
		IntBitmap region = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.STASH_OPEN_RECT));
		MidpassFilter.maintainRanges(region, MidpassFilterType.STASH_OPEN);
		BinaryImage bin = region.toGreyscale().doubleCutoff(20);
		bin.killLoners(2, true);
		return bin.countWhite() > STASH_THRESHOLD; //Doesn't suck and it works
	}
	private void waitForStoreDialog(WindowThread thread) throws LogoutException, HaltThread
	{
		Timer waitForDialog = new Timer(5 * 1000);
		boolean firstClick = false; //first attempt at the store
		while(!storeDialogOpen() && !window.inventoryVisible() && waitForDialog.stillWaiting())
		{
			thread.checkHalt();
			global.clickClosestStoreLabel(this, window);
			if(firstClick)
			{
				Macro.sleep(1000);
			}
			if(window.destroyItemVisible())
			{
				window.leftClick(ScreenRegion.DESTROY_ITEM_RECT.getCenter());
			}
			firstClick = true;
		}
		if(waitForDialog.hasExpired())
		{
			throw new LogoutException("No Store Dialogue");
		}
	}
	private static final int DIALOGUE_WHITE_THRESHOLD = 70;
	private static final int FAR_DOWN = 100;
	public boolean storeDialogOpen()
	{
		IntBitmap region = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.STORE_OPEN_RECT));
		RatioFilter.maintainRatio(region, FilterType.GREUST_DIALOGUE_TEXT);
		//MidpassFilter.maintainRanges(region, MidpassFilterType.GREUST_DIALOGUE_TEXT);
		if(region.toGreyscale().doubleCutoff(20).countWhite() > DIALOGUE_WHITE_THRESHOLD && InventoryMacro.getBottomOfMenu(window) > FAR_DOWN)
		{
			//Display.showHang(region.toGreyscale().doubleCutoff(20));
			return true;
		}
		return false;
	}
}
