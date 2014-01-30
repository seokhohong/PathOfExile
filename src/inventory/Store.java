package inventory;

import img.IntBitmap;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import macro.LogoutException;
import macro.Macro;
import map.GlobalMap;
import window.HaltThread;
import window.WindowThread;

public class Store extends ItemGrid
{
	private static final int WIDTH = 12;				public int getWidth() { return WIDTH; }
	private static final int HEIGHT = 12; 				public int getHeight() { return HEIGHT; }
	
	private static final Rectangle IMAGE_RECTANGLE 
		= new Rectangle(9, 90, 351, 351); 				public Rectangle getImageRectangle() { return IMAGE_RECTANGLE; }
			//need to change this later
		
	private static final int[] columnData = { 0, 29, 30, 58, 59, 87, 88, 116, 117, 146, 147, 175, 176, 204, 205, 233, 234, 236, 264, 292, 293, 321, 322, 350}; 
		public int[] getColumnData() { return columnData; } //all this data is unverified
		
	private static final int[] rowData = { 0, 29, 31, 58, 59, 87, 88, 116, 117, 145, 146, 175, 176, 204, 205, 233, 234, 262, 263, 292, 293, 321, 322, 350};
		public int[] getRowData() { return rowData; }		//all this data is unverified
	
	private static final InventoryItem[] conversionItems = {InventoryItem.ORB_OF_ALTERATION, InventoryItem.JEWELLERS_ORB, InventoryItem.ORB_OF_FUSING};
	//private static final ArrayList<InventoryItem> currencyConversionItems = new ArrayList<InventoryItem>(Arrays.asList(conversionItems));
		
	protected Store(WindowThread thread, GlobalMap global) throws LogoutException, HaltThread 
	{
		super(thread, global);
	}
	
	public void build() 
	{
		IntBitmap storePic = super.takeInventoryScreenshot();
		InventorySquare[][] inventorySquares = createSquares(storePic, cornerMatrix);
		findSlots(storePic, cornerMatrix, inventorySquares);

		super.moveCursorAway();
		Collections.sort(slots);
		Macro.sleep(50);
		for(InventorySlot slot : slots)
		{
			slot.findItem();
		}
		HashMap map = new HashMap();
		for(InventoryItem item : conversionItems)
		{
			map.put(item, slotWithItem(item).getWindowClickCoordinate()); //maps items for conversion to points that you have to click
		}
	}
	private InventorySlot slotWithItem(InventoryItem item)
	{
		int count = 0;
		InventorySlot foundSlot = null;
		for(InventorySlot slot : slots)
		{
			if(slot.getContents() == item)
			{
				count++;
				foundSlot = slot;
			}
		}
		if(count == 0)
		{
			System.out.println(item + " not found here.");
		}
		return foundSlot;
	}
	
}
