package inventory;

import img.ImageLibrary;
import img.IntBitmap;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import macro.LogoutException;
import map.GlobalMap;
import window.PWindow;
import window.WindowThread;
import window.HaltThread;

public class Stash extends ItemGrid
{
	private static final int WIDTH = 12;				public int getWidth() { return WIDTH; }
	private static final int HEIGHT = 12; 				public int getHeight() { return HEIGHT; }
	
	private static final Rectangle IMAGE_RECTANGLE 
		= new Rectangle(9, 90, 351, 351); 				public Rectangle getImageRectangle() { return IMAGE_RECTANGLE; }
			//unverified...may not be perfect
		
	private static final int[] columnData = { 0, 29, 30, 58, 59, 87, 88, 116, 117, 146, 147, 175, 176, 204, 205, 233, 234, 236, 264, 292, 293, 321, 322, 350}; 
		public int[] getColumnData() { return columnData; }
		
	private static final int[] rowData = { 0, 29, 31, 58, 59, 87, 88, 116, 117, 145, 146, 175, 176, 204, 205, 233, 234, 262, 263, 292, 293, 321, 322, 350};
		public int[] getRowData() { return rowData; }
	
		
	protected Stash(WindowThread thread, GlobalMap global) throws LogoutException, HaltThread
	{
		super(thread, global);
	}
	@Override
	public void build()
	{
		
	}
	//public boolean tabFull()
	{
		
	}
	public void gotoStashTab(int tab)
	{
		//if(findCurrentTab() == tab)
		{
			//System.out.println("Already on this tab, buddy.");
		}
		//else
		{
			IntBitmap stash = IntBitmap.getInstance(window.takeScreenshot());
			Point p = stash.findImage(ImageLibrary.stashUnhighlight(tab));
			if(p != null) //actually found the thing
			{
				window.clickItem(p.x, p.y);
			}
			else
			{
				System.out.println("Already on this tab.");
			}
		}
	}
	public int findCurrentTab()
	{
		int matches = 0;
		int tabMatch = 0; 
		IntBitmap stash = IntBitmap.getInstance(window.takeScreenshot());
		for(int i = 1; i <= 4; i++)
		{
			if(stash.findImage(ImageLibrary.stashHighlight(i), 1) != null);
			{
				matches++;
				return i;
			}
		}
		System.out.println(matches);
		//if(matches == 1)
		{
			return tabMatch;
		}
		//System.out.println("Couldn't find tab properly. Check yo shit.");
		//return 0;
	}
}
