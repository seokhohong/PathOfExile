package main;

import img.IntBitmap;
import items.Item;
import items.ItemFinder;
import items.ItemType;

import java.util.ArrayList;

import process.AHKBridge;
import data.Config;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;

public class TestItems 
{
	public static void main(String[] args)
	{
		new TestItems().go();
	}
	private void go()
	{
		ArrayList<ItemType> types = new ArrayList<ItemType>();

		types.add(ItemType.MAGIC);
		types.add(ItemType.RARE);
		types.add(ItemType.UNIQUE);
		types.add(ItemType.CURRENCY);
		
		Config config = new Config();
		WindowManager winMgr = new WindowManager(config);
		PWindow window = winMgr.getWindows().get(0);
		ArrayList<Item> items = ItemFinder.findItems(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT)), types);
		System.out.println(items.size());
		for(Item i : items)
		{
			System.out.println(i);
		}
		System.out.println();
	}
}
