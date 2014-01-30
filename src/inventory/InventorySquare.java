package inventory;

import img.BinaryImage;
import img.IntBitmap;
import img.MidpassFilter;

import java.awt.Point;
import java.awt.Rectangle;

public class InventorySquare implements Comparable<InventorySquare>
{		
	private Point topLeftCorner;				Point getTLCorner() { return topLeftCorner; }
	private Point bottomRightCorner;			Point getBRCorner() { return bottomRightCorner; }
	private Point location; 					Point getLocation() { return location; }
	
	private Point windowClickCoordinate;		Point getWindowClickCoordinate() { return windowClickCoordinate; }
	private IntBitmap icon;						IntBitmap getIcon() { return icon; }
	
	private static final int CLICK_OFFSET = 15;
	
	InventorySquare(IntBitmap inventoryImage, Point location, Point topLeftCorner, Point bottomRightCorner, Inventory parentInventory)
	{
		this.topLeftCorner = topLeftCorner;
		this.bottomRightCorner = bottomRightCorner;
		this.location = location;
		windowClickCoordinate = new Point(parentInventory.getCornerX() + topLeftCorner.x + CLICK_OFFSET, parentInventory.getCornerY() + topLeftCorner.y + CLICK_OFFSET);

		int squareWidth = bottomRightCorner.x - topLeftCorner.x + 1; 	// +1 for fence post issue
		int squareHeight = bottomRightCorner.y - topLeftCorner.y + 1;
		icon = inventoryImage.subimage(new Rectangle(topLeftCorner.x, topLeftCorner.y, squareWidth, squareHeight));
	}
	InventorySquare(IntBitmap inventoryImage, Point topLeftCorner, Point bottomRightCorner, ItemGrid grid)
	{
		this.topLeftCorner = topLeftCorner;
		this.bottomRightCorner = bottomRightCorner;
		windowClickCoordinate = new Point(grid.getImageRectangle().x + topLeftCorner.x + CLICK_OFFSET, grid.getImageRectangle().y + topLeftCorner.y + CLICK_OFFSET);

		int squareWidth = bottomRightCorner.x - topLeftCorner.x + 1;	// +1 for fence post issue
		int squareHeight = bottomRightCorner.y - topLeftCorner.y + 1;
		icon = inventoryImage.subimage(new Rectangle(topLeftCorner.x, topLeftCorner.y, squareWidth, squareHeight));
	}
	public boolean isColor(HighlightColor hc)
	{
		IntBitmap image = IntBitmap.copy(icon);
		MidpassFilter.maintainRanges(image, hc.getFilter());
		BinaryImage filtered = image.toGreyscale().doubleCutoff(1);
		return filtered.countWhite() > hc.getMatchThreshold();
	}
	@Override
	public int compareTo(InventorySquare s1) 
	{
		InventorySquare s2 = this;
		int val1 = s1.getLocation().x * Inventory.INVENTORY_HEIGHT + s1.getLocation().y;
		int val2 = s2.getLocation().x * Inventory.INVENTORY_HEIGHT + s2.getLocation().y;
		return val2 - val1;
	}
}
