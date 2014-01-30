package main;

import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;
import data.Config;

public class TestStore 
{
	public static void main(String[] args)
	{
		new TestStore().go();
	}
	private void go()
	{
		Config config = new Config();
		WindowManager winMgr = new WindowManager(config);
		PWindow window = winMgr.getWindows().get(0);
		int bottom = getBottomOfMenu(window);
		window.mouseMove(PWindow.getWindowCenter().x, bottom);
	}
	/*
	//Goes down the rows, finding the lowest completely black row after highpass
	private static final int MENU_HIGHPASS = 100;
	private static final int TOLERANCE = 1; //Number of non-black pixels tolerated for a completely black row
	private int getBottomOfMenu(PWindow window)
	{
		IntBitmap rect = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.STORE_DIALOG_RECT));
		rect.highPassByAverage(MENU_HIGHPASS);
		RatioFilter.maintainRatio(rect, FilterType.GREUST_DIALOGUE_TEXT);
		Display.showHang(rect);
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
	*/
	//Don't weak in forest
	private static final int MENU_HIGHPASS = 100;
	private static final int THRESHOLD = 15; //Number of non-black pixels tolerated for a completely black row
	private int getBottomOfMenu(PWindow window)
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
