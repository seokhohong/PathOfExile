package inventory;

import img.BinaryImage;
import img.BleedResult;
import img.Bleeder;
import img.Display;
import img.FilterType;
import img.ImageLibrary;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;
import img.RatioFilter;

import java.awt.Point;
import java.util.ArrayList;

import window.PWindow;
import window.ScreenRegion;
import macro.LogoutException;
import macro.Macro;
import macro.Timer;
import map.GlobalMap;

@Deprecated
public enum Home 
{
	LIONEYE(0, 0, 33, 400)
	{
		public void findStore(PWindow window, ArrayList<BleedResult> results) throws LogoutException
		{
			BleedResult closest = getResultClosestTo(results, new Point(250, 100));
			if(closest == null) throw new LogoutException("Could not find Store");
			window.leftClick(new Point(closest.getCenter().x, closest.getCenter().y + 40));
		}
	},
	FOREST(2, 1, 55, 500)
	{
		public void findStore(PWindow window, ArrayList<BleedResult> results) throws LogoutException
		{
			BleedResult closest = getResultClosestTo(results, new Point(500, 100));
			if(closest != null)
			{
				window.leftClick(new Point(closest.getCenter().x, closest.getCenter().y + 40));
			}
		}
	},
	SARN(0, 0, 0, 0)
	{
		public void findStore(PWindow window, ArrayList<BleedResult> results)
		{
			System.err.println("Not in Sarn Please");
			System.exit(1);
		}
	};
	
	private Home(int indexOfStash, int indexOfStash2, int sellFromBottom, int storeDialogThreshold)
	{
		this.indexOfStash = indexOfStash;
		this.indexOfStash2 = indexOfStash2;
		this.sellFromBottom = sellFromBottom;
		this.storeDialogThreshold = storeDialogThreshold;
	}
	
	
	
	private int indexOfStash; //from store, what index is the BleedResult of the stash label?
	private int indexOfStash2 = 0; //if its not the first one, the second option
	
	private int storeDialogThreshold;
	
	/**
	 * Identifies the current Home environment, and adds all labels to the ArrayList<BleedResult> allLabels
	 * 
	 * @param window
	 * @return
	 * @throws LogoutException	: On failure to identify the environment
	 */
	public static Home getHome(PWindow window) throws LogoutException
	{
		//Always will be in a particular order because bleeder works from left to right
		switch(getNumDoors(window))
		{
		case 0: return SARN;
		case 1: return LIONEYE;
		case 3: return FOREST;
		default: 
			{
				throw new LogoutException("Could not identify Home");
			}
		}
	}
	
	boolean storeDialogOpen(PWindow window)
	{
		//if(processStoreDialog(window).countWhite() > DIALOGUE_WHITE_THRESHOLD)
		{
			//Display.showHang(processStoreDialog(window));
		}
		return InventoryMacro.processStoreDialog(window).countWhite() > storeDialogThreshold;
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
	
	//Orange doors
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
	/*
	static ArrayList<BleedResult> getLabels(PWindow window)
	{
		IntBitmap screen = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT));
		RatioFilter.maintainRatio(screen, FilterType.STASH_LABEL);
		BinaryImage bin = screen.toGreyscale().doubleCutoff(30);
		bin.killLoners(0, BinaryImage.WHITE); //sometimes there's a lot of bad signal
		//Display.show(bin);
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
	*/
	private static final int BELOW_LABEL = 25;
	private static final int STASH_WIDTH = 30;
	private void clickStash(PWindow window, BleedResult result)
	{
		for(int a = 0; a < 2; a++)
		{
			window.leftClick(new Point(result.getCenter().x, result.getCenter().y + BELOW_LABEL));
		}
		Macro.macro.sleep(1500);
	}
	public void findStash(PWindow window) throws LogoutException
	{
		Macro.macro.sleep(500); //for transaction to close
		Timer waitForDialog = new Timer(10 * 1000);
		while(!stashOpen(window) && waitForDialog.stillWaiting())
		{
			ArrayList<BleedResult> results = pollLabels(window);
			if(window.destroyItemVisible())
			{
				window.leftClick(ScreenRegion.DESTROY_ITEM_RECT.getCenter());
			}
			int resultWidth = results.get(indexOfStash).toRectangle().width;
			//System.out.println(results.get(indexOfStash).toRectangle()+" "+resultWidth);
			if(resultWidth <= STASH_WIDTH)
			{
				clickStash(window, results.get(indexOfStash));
			}
			else
			{
				resultWidth = results.get(indexOfStash2).toRectangle().width;
				//System.out.println(resultWidth);
				if(resultWidth <= STASH_WIDTH) //try again
				{
					clickStash(window, results.get(indexOfStash2));
				}
				else
				{
					throw new LogoutException("Not Labeling Correctly");
				}
			}
		}
		if(waitForDialog.hasExpired())
		{
			throw new LogoutException("No Stash");
		}
	}
	//Keeps looking for labels until it finds them
	private ArrayList<BleedResult> pollLabels(PWindow window) throws LogoutException
	{
		ArrayList<BleedResult> labels;
		Timer labelFinder = new Timer(3 * 1000);
		while(labelFinder.stillWaiting()) //Need clear labels
		{
			labels = GlobalMap.getLabels(window);
			if(labels.size() > indexOfStash)
			{
				return labels;
			}
			Macro.macro.sleep(100);
		}
		throw new LogoutException("Could not find Labels");
	}
	private static final int STASH_THRESHOLD = 300;
	private static boolean stashOpen(PWindow window)
	{
		IntBitmap region = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.STASH_OPEN_RECT));
		MidpassFilter.maintainRanges(region, MidpassFilterType.STASH_OPEN);
		BinaryImage bin = region.toGreyscale().doubleCutoff(20);
		bin.killLoners(2, true);
		//Display.showHang(bin);
		return bin.countWhite() > STASH_THRESHOLD; //Doesn't suck and it works
	}

	private int sellFromBottom;
	public void openSellWindow(PWindow window)
	{
		int bottom = getBottomOfMenu(window);
		window.leftClickCarefully(new Point(PWindow.getWidth() / 2, bottom - sellFromBottom));
		window.leftClickCarefully(new Point(PWindow.getWidth() / 2, bottom - sellFromBottom));
	}
	//Goes down the rows, finding the lowest completely black row after highpass
	private static final int MENU_HIGHPASS = 100;
	private static final int TOLERANCE = 1; //Number of non-black pixels tolerated for a completely black row
	private int getBottomOfMenu(PWindow window)
	{
		IntBitmap rect = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.STORE_OPEN_RECT));
		rect.highPassByAverage(MENU_HIGHPASS);
		int bottom = ScreenRegion.STORE_OPEN_RECT.getAbsoluteY();
		int[][][] data = rect.getData();
		for(int b = 0; b < rect.getHeight(); b++)
		{
			int numBlackPixels = 0;
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
				if(pixelIsBlack)
				{
					numBlackPixels ++;
				}
			}
			if(numBlackPixels > rect.getWidth() - TOLERANCE)
			{
				bottom = b + ScreenRegion.STORE_OPEN_RECT.getAbsoluteY();
			}
		}
		return bottom;
	}
	
	public abstract void findStore(PWindow window, ArrayList<BleedResult> results) throws LogoutException;

}
