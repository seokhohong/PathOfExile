package inventory;

import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

import java.util.Random;

import macro.HomeNavigator;
import macro.LogoutException;
import macro.Macro;
import macro.Timer;
import map.GlobalMap;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
public class InventoryMacro
{
	private static final int TOO_MUCH_STUFF = 10;
	public static void depositStash(Inventory inventory, GlobalMap global, WindowThread thread) throws LogoutException, HaltThread
	{
		if(inventory.hasUnique() || 
				(inventory.getSlots().size() - inventory.numMagics() - inventory.numWisdoms() > TOO_MUCH_STUFF))
		{
			HomeNavigator navigator = new HomeNavigator(thread, global);
			navigator.goToStash();
			for(InventorySlot slot : inventory.getSlots())
			{
				if(slot.shouldDeposit())
				{
					slot.move();
				}
			}
			Timer escapeTimer = new Timer(3 * 1000);
			while(navigator.stashOpen() && escapeTimer.stillWaiting())
			{
				thread.getWindow().pressEscape(); //clear screen of stash
				Macro.sleep(500);
			}
		}
	}
	//Because spare wisdoms accumulate in the back of the list, use those first
	private static InventorySlot wisdomSlot(Inventory inventory)
	{
		InventorySlot lastWisdom = null;
		for(InventorySlot slot : inventory.getSlots())
		{
			if(slot.getContents() == InventoryItem.SCROLL_OF_WISDOM)
			{
				lastWisdom = slot;
			}
		}
		return lastWisdom;
	}
	private static final Random rnd = new Random();

	public static void identifyItems(Inventory inventory, double chanceIdMagic)
	{
		PWindow window = inventory.getPWindow();
		InventorySlot wisdoms = wisdomSlot(inventory);
		if(!existUnidentified(inventory))
		{
			return;
		}
		if(wisdoms == null)
		{
			System.out.println("No Wisdoms");
			return;
		}
		window.rightClick(wisdoms.getWindowClickCoordinate());
		Macro.sleep(100);
		window.pressShift();
		//Prioritize Rares/Uniques over magics
		identifyRaresAndUniques(inventory, window, wisdoms);
		identifyMagics(inventory, window, wisdoms, chanceIdMagic);
		window.releaseShift();
	}
	//Not actually as smart as it sounds, it merely checks whether there are magics/rares/uniques regardless of identification status
	private static boolean existUnidentified(Inventory inventory)
	{
		for(InventorySlot slot : inventory.getSlots())
		{
			if(slot.getItemType() == InventoryItemType.MAGIC 
					|| slot.getItemType() == InventoryItemType.RARE 
					|| slot.getItemType() == InventoryItemType.UNIQUE)
			{
				return true;
			}
		}
		return false;
	}
	private static void identifyRaresAndUniques(Inventory inventory, PWindow window, InventorySlot wisdoms)
	{
		int numRares = 0;
		for(InventorySlot slot : inventory.getSlots())
		{
			if(slot.getItemType() == InventoryItemType.RARE || slot.getItemType() == InventoryItemType.UNIQUE)
			{
				numRares ++;
				identify(window, slot);
			}
		}
		System.out.println(numRares+" Rares");
	}
	private static void identifyMagics(Inventory inventory, PWindow window, InventorySlot wisdoms, double chance)
	{
		for(InventorySlot slot : inventory.getSlots())
		{
			if(slot.getItemType() == InventoryItemType.MAGIC && rnd.nextDouble() < chance)
			{
				identify(window, slot);
			}
		}
	}
	private static void identify(PWindow window, InventorySlot slot)
	{
		window.mouseMove(slot.getWindowClickCoordinate());
		Macro.sleep(40);
		window.leftClickCarefully(slot.getWindowClickCoordinate());
		Macro.sleep(40);
	}
	
	
	private static final int MENU_HIGHPASS = 100;
	private static final int THRESHOLD = 10; //Number of non-black pixels tolerated for a completely black row
	public static int getBottomOfMenu(PWindow window)
	{
		IntBitmap rect = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.STORE_DIALOG_RECT));
		rect.highPassByAverage(MENU_HIGHPASS);
		RatioFilter.maintainRatio(rect, FilterType.GREUST_DIALOGUE_TEXT);
		int bottom = ScreenRegion.STORE_OPEN_RECT.getAbsoluteY();
		int[][][] data = rect.getData();
		for(int b = 0; b < rect.getHeight(); b++)
		{
			int numColorPixels = 0;
			for(int a = 0; a < rect.getWidth(); a++)
			{
				boolean pixelIsBlack = true;
				for(int c = 0; c < IntBitmap.RGB; c++)
				{
					if(data[a][b][c] != 0)
					{
						pixelIsBlack = false;
					}
				}
				if(!pixelIsBlack)
				{
					numColorPixels ++;
				}
			}
			if(numColorPixels > THRESHOLD)
			{
				bottom = b + ScreenRegion.STORE_OPEN_RECT.getAbsoluteY();
			}
		}
		return bottom;
	}
}