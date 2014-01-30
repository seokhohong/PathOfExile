package inventory;

import img.Display;
import img.IntBitmap;

import java.awt.Point;
import java.awt.Rectangle;

import macro.Macro;
import macro.PoEMacro;
import window.PWindow;
import window.ScreenRegion;

public class InventorySlot implements Comparable<InventorySlot>
{
	private static final int CLICK_OFFSET = 15;
	private static final int INVENTORY_GRID_HEIGHT = 5;
	
	private Inventory parentInventory;
	private PWindow parentWindow;
	private ItemGrid parentGrid;
	
	private IntBitmap icon;

	private Point tLCorner; //gives the pixel number of the top left corner of slot relative to inventory window
	private Point windowClickPoint;
	
	private InventoryItem contents;				public InventoryItem getContents() { return contents; }
												public void emptyContents() { contents = null; }

	private Rectangle definingRect; //defined by grid coordinates, not pixels
	
	private Rectangle screenshotRectangle;

	private SizeExtension ext;
	private InventoryItemType itemType;          public InventoryItemType getItemType() { return itemType; }
	
	private boolean wasWisdom = false;
	/**
	 * Should only be instantiated during instantiation of inventory.
	 * 
	 * @param location
	 * @param corner
	 */
	public InventorySlot(Rectangle definingRect, Inventory parentInventory, PWindow parentWindow, InventorySquare[][] inventorySquares)
	{
		this.definingRect = definingRect;
		this.parentInventory = parentInventory;
		this.parentWindow = parentWindow;
		
		tLCorner = inventorySquares[definingRect.x][definingRect.y].getTLCorner();
		windowClickPoint = new Point(parentInventory.getCornerX() + tLCorner.x + CLICK_OFFSET, parentInventory.getCornerY() + tLCorner.y + CLICK_OFFSET);
		
		Point bRCorner = inventorySquares[definingRect.x + definingRect.width - 1][definingRect.y + definingRect.height - 1].getBRCorner();
		int pixelWidth = bRCorner.x - tLCorner.x + 1;
		int pixelHeight = bRCorner.y - tLCorner.y + 1;
		ext = SizeExtension.getExt(pixelWidth, pixelHeight);
		
		screenshotRectangle = new Rectangle(parentInventory.getCornerX() + tLCorner.x, parentInventory.getCornerY() + tLCorner.y, pixelWidth, pixelHeight);
		
		//System.out.println("Slot Created with rectangle " + definingRect + ", width: " + pixelWidth + ", height: " + pixelHeight);
		
		itemType = findItemType(IntBitmap.getInstance(parentWindow.takeScreenshot(ScreenRegion.INVENTORY_ITEM_TYPE_ID_RECT)));
		parentWindow.mouseMove(new Point(200, 200));
		
		//System.out.println("Slot " + definingRect.x + ", " + definingRect.y + " is " + itemType);
		
		//setExtension();
		//ItemIdentifier.matchItem(icon, ext);
	}
	public InventorySlot(Rectangle definingRect, ItemGrid parentGrid, PWindow parentWindow, InventorySquare[][] inventorySquares)
	{
		this.definingRect = definingRect;
		this.parentGrid = parentGrid;
		this.parentWindow = parentWindow;
		
		tLCorner = inventorySquares[definingRect.x][definingRect.y].getTLCorner();
		windowClickPoint = new Point(parentGrid.getImageRectangle().x + tLCorner.x + CLICK_OFFSET, parentGrid.getImageRectangle().y + tLCorner.y + CLICK_OFFSET);
		
		Point bRCorner = inventorySquares[definingRect.x + definingRect.width - 1][definingRect.y + definingRect.height - 1].getBRCorner();
		int pixelWidth = bRCorner.x - tLCorner.x + 1;
		int pixelHeight = bRCorner.y - tLCorner.y + 1;
		ext = SizeExtension.getExt(pixelWidth, pixelHeight);
		
		screenshotRectangle = new Rectangle(parentGrid.getImageRectangle().x  + tLCorner.x, parentGrid.getImageRectangle().y + tLCorner.y, pixelWidth, pixelHeight);
		
		//System.out.println("Slot Created with rectangle " + definingRect + ", width: " + pixelWidth + ", height: " + pixelHeight);
		
		itemType = findItemType(IntBitmap.getInstance(parentWindow.takeScreenshot(ScreenRegion.INVENTORY_ITEM_TYPE_ID_RECT)));
		parentWindow.mouseMove(new Point(200, 200));
		
		//System.out.println("Slot " + definingRect.x + ", " + definingRect.y + " is " + itemType);
		
		//setExtension();
		//ItemIdentifier.matchItem(icon, ext);
	}
	public void findItem()
	{
		if(itemType == InventoryItemType.CURRENCY)
		{
			takeSlotPicture();
			contents = ItemIdentifier.matchItem(getIcon(), getExtension());
			if(contents == InventoryItem.SCROLL_OF_WISDOM)
			{
				wasWisdom = true;
			}
			//System.out.println("Slot " + definingRect.x + ", " + definingRect.y + " is " + contents);
		}
		else
		{
			//System.out.println("Slot " + definingRect.x + ", " + definingRect.y + " is " + itemType);
		}
	}

	public boolean shouldDeposit()
	{
		return contents != InventoryItem.SCROLL_FRAGMENT && 
				!wasWisdom && 
				contents != InventoryItem.TRANSMUTATION_SHARD &&
				contents != InventoryItem.ALTERATION_SHARD &&
				contents != InventoryItem.ALCHEMY_SHARD &&
				itemType != InventoryItemType.GARBAGE && 
				itemType != InventoryItemType.MAGIC && 
				itemType != InventoryItemType.RARE && 
				is1x1() &&
				contents != InventoryItem.ORB_OF_TRANSMUTATION;
	}
	public boolean shouldSell()
	{
		return itemType == InventoryItemType.MAGIC 
				|| itemType == InventoryItemType.RARE
				|| itemType == InventoryItemType.GARBAGE
				|| (itemType == InventoryItemType.UNIQUE && !is1x1()) 
				|| contents == InventoryItem.TRANSMUTATION_SHARD
				|| contents == InventoryItem.ORB_OF_TRANSMUTATION;
	}
	private boolean is1x1()
	{
		return definingRect.width == 1 && definingRect.height == 1;
	}
	public InventoryItemType findItemType(IntBitmap image)
	{
		for(InventoryItemType type : InventoryItemType.values())
		{
			IntBitmap copy = IntBitmap.copy(image);
			//Display.show(image);
			if(type.matchesType(copy))
			{
				return type;
			}
		}
		/*for(InventoryItemType type : InventoryItemType.values())
		{
			IntBitmap image = IntBitmap.getInstance(parentWindow.takeScreenshot(ScreenRegion.INVENTORY_ITEM_TYPE_ID_RECT));
			//Display.showHang(image);
			if(type.matchesType(image))
			{
				return type;
			}
		}*/
		System.out.println("Was considered magic by default.");
		return InventoryItemType.MAGIC;
	}
	void identify()
	{
		parentWindow.mouseMove(windowClickPoint);
		Macro.sleep(40);
		parentWindow.leftClickCarefully(windowClickPoint);
		Macro.sleep(40);
	}
	void takeSlotPicture()
	{
		icon = IntBitmap.getInstance(parentWindow.takeScreenshot(screenshotRectangle));
		//Display.show(icon);
		//************************* USED TO NAME IMAGES IN MASS -- PROBABLY WON'T NEED AGAIN BUT DON'T KILL
		
		/*Display.show(icon);
		System.out.println("Name this image: ");
		try{
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String s = bufferRead.readLine();
		    if(!s.equals(null))
		    {
		    	icon.export("imglib/Items/" + icon.getWidth() + "x" + icon.getHeight() + "/" + s + icon.getWidth() + "x" + icon.getHeight()+ ".bmp");
		    }
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}*/
		
		//***********************************************************************************************
	}
	public SizeExtension getExtension()
	{
		return ext;
	}
	public IntBitmap getIcon()
	{
		return icon;
	}
	
	//public void clearSlot()
	{
		//PoEMacro.clearInventorySlot(parentWindow, windowClickPoint);
	}
	public void move()
	{
		PoEMacro.slotToStash(parentWindow, windowClickPoint);
	}
	public Point getTLCorner()
	{
		return tLCorner;
	}
	public Point getWindowClickCoordinate()
	{
		return windowClickPoint;
	}
	public void mouseMoveSquare()
	{
		parentWindow.mouseMove(getWindowClickCoordinate());
	}
	@Override
	public int compareTo(InventorySlot s1) 
	{
		InventorySlot s2 = this;
		int val1 = s1.definingRect.x * INVENTORY_GRID_HEIGHT + definingRect.y;
		int val2 = s2.definingRect.x * INVENTORY_GRID_HEIGHT + definingRect.y;
		return val2 - val1;
	}
	
}
