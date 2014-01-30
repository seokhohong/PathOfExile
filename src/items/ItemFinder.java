package items;

import img.BinaryImage;
import img.BleedResult;
import img.Bleeder;
import img.IntBitmap;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Point;

import window.PWindow;
import window.ScreenRegion;

public class ItemFinder 
{
	private static final int ITEM_HEIGHT_BASE = 12;		public static int getItemHeight() { return ITEM_HEIGHT_BASE; }
	/**
	 * 
	 * Finds all the items of a particular type
	 * 
	 * @param rgb
	 * @param type
	 * @return
	 */
	
	private static final int NO_SCALING = 1;
	
	static ArrayList<ItemType> magicType = new ArrayList<ItemType>();
	static ArrayList<ItemType> priorityType = new ArrayList<ItemType>();

	static
	{
		magicType.add(ItemType.MAGIC);
		priorityType.add(ItemType.CURRENCY);
		priorityType.add(ItemType.RARE);
		priorityType.add(ItemType.UNIQUE);
	}
	
	/**
	 * 
	 * Looks for Unique/Currency/Rares first then picks up Magics
	 * 
	 * @param rgb
	 * @return
	 */
	private static final int NORMAL_NAME_SIZE = 50;
	private static final int INCLUDE_SMALL_NAMES = 30;
	public static ArrayList<Item> findPrioritizedItems(PWindow window, ScreenRegion region)
	{
		IntBitmap itemImage = IntBitmap.getInstance(window.takeScreenshot(region));
		
		Point offset = region.getTopLeft();		//Offset to change from image coordinates to screen coordinates for items
		
		ArrayList<Item> priority = processBleederResults(ItemType.filterMatch(IntBitmap.copy(itemImage), priorityType), offset, NO_SCALING, INCLUDE_SMALL_NAMES);
		Collections.sort(priority);
		//System.out.println("Priority Items "+priority.size());
		
		ArrayList<Item> magic = processBleederResults(ItemType.filterMatch(itemImage, magicType), offset, NO_SCALING, INCLUDE_SMALL_NAMES);
		Collections.sort(magic);
		//System.out.println("Magic Items "+magic.size());
		priority.addAll(magic);
		
		return priority;
	}

	public static ArrayList<Item> findItems(PWindow window, ScreenRegion region, ArrayList<ItemType> types)
	{
		IntBitmap itemImage = IntBitmap.getInstance(window.takeScreenshot(region));
		Point offset = region.getTopLeft();
		
		ArrayList<Item> items = processBleederResults(ItemType.filterMatch(itemImage, types), offset, NO_SCALING, NORMAL_NAME_SIZE);
		Collections.sort(items);
		return items;
	}
	
	private static final int MAX_ITEM_HEIGHT = 10;
	private static ArrayList<Item> processBleederResults(BinaryImage combined, Point offset, int scaling, int threshold)
	{
		ArrayList<Item> items = new ArrayList<Item>();
		Bleeder itemBleeder = new Bleeder(4);
		ArrayList<BleedResult> results = itemBleeder.find(combined);
		for(BleedResult br : results)
		{
			if(br.getNumPixels() > threshold)
			{
				Rectangle result = br.toRectangle();
				if(result.height < MAX_ITEM_HEIGHT)
				{
					items.add(new Item(new Rectangle(result.x * scaling + offset.x, result.y * scaling + offset.y, result.width * scaling, result.height * scaling)));
				}
			}
		}
		return items;
	}
}
