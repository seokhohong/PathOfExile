package inventory;

import img.IntBitmap;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import macro.HomeNavigator;
import macro.LogoutException;
import macro.Macro;
import map.GlobalMap;
import window.HaltThread;
import window.PWindow;
import window.WindowThread;

public abstract class ItemGrid 
{
	protected PWindow window;								PWindow getPWindow() { return window; }
	protected GlobalMap global;
	protected WindowThread thread;
	protected CornerMatrix cornerMatrix;

	ArrayList<InventorySlot> slots = new ArrayList<InventorySlot>(); 				ArrayList<InventorySlot> getSlots(){ return slots; }

	public abstract int getWidth();
	public abstract int getHeight();
	public abstract Rectangle getImageRectangle();
	public abstract int[] getColumnData();
	public abstract int[] getRowData();
	
	public abstract void build();
	
	private static final Point MOVE_CURSOR_AWAY = new Point(400, 20);
	
	/**
	 * Should call method "build" upon instantiation of inventory class to initialize inventory.
	 * 
	 * @param window
	 * @throws LogoutException 
	 * @throws HaltThread 
	 */
	protected ItemGrid(WindowThread thread, GlobalMap global) throws LogoutException, HaltThread
	{
		this.thread = thread;
		this.global = global;
		window = thread.getWindow();
		initialize();
	}
	
	public void initialize() throws LogoutException, HaltThread
	{
		cornerMatrix = new CornerMatrix(this);
	}
	
	protected void moveCursorAway()
	{
		window.mouseMove(MOVE_CURSOR_AWAY);
	}
	protected InventorySquare[][] createSquares(IntBitmap image, CornerMatrix cornerMatrix)
	{
		InventorySquare[][] squares = new InventorySquare[getWidth()][getHeight()];
		for(int a=0; a < getWidth(); a++)
		{
			for(int b=0; b < getHeight(); b++)
			{
				squares[a][b] = new InventorySquare(image, cornerMatrix.getTLCorner(a, b), cornerMatrix.getBRCorner(a, b), this);
			}
		}
		return squares;
	}
	protected void findSlots(IntBitmap inventoryPic, CornerMatrix cornerMatrix, InventorySquare[][] inventorySquares)
	{
		HashSet<Point> blockedPoints = new HashSet<Point>(); //Avoids concurrent modification
		HashSet<Point> validPoints = new HashSet<Point>();
		validPoints.addAll(getColoredSquares(inventoryPic, cornerMatrix, HighlightColor.BLUE));
		validPoints.addAll(getColoredSquares(inventoryPic, cornerMatrix, HighlightColor.RED));
		for(Point p : validPoints)
		{
			if(!blockedPoints.contains(p))
			{
				window.mouseMove(inventorySquares[p.x][p.y].getWindowClickCoordinate());
				Macro.sleep(30);
				IntBitmap screenshot = takeInventoryScreenshot();
				InventorySquare[][] squares = createSquares(screenshot, cornerMatrix);
				InventoryGrid grid = InventoryGrid.getColorGrid(squares, HighlightColor.GREEN);
				if(!grid.isEmpty())
				{
					for(Point itemPoints : grid.getWhites())
					{
						blockedPoints.add(itemPoints);
					}
					slots.add(new InventorySlot(grid.getRectangle(), this, window, inventorySquares));
				}
			}
		}
		Macro.sleep(20);
	}
	private HashSet<Point> getColoredSquares(IntBitmap inventoryPic, CornerMatrix cornerMatrix, HighlightColor hc)
	{
		InventorySquare[][] squares = createSquares(inventoryPic, cornerMatrix);
		InventoryGrid grid = InventoryGrid.getColorGrid(squares, hc);
		return grid.getWhites();
	}
	protected IntBitmap takeInventoryScreenshot()
	{
		return IntBitmap.getInstance(window.takeScreenshot(getImageRectangle()));
	}
	boolean hasStuffToSell()
	{
		for(InventorySlot slot : slots)
		{
			if(slot.shouldSell())
			{
				return true;
			}
		}
		return false;
	}
	boolean hasUnique()
	{		
		for(InventorySlot slot : slots)
		{
			if(slot.getItemType() == InventoryItemType.UNIQUE)
			{
				return true;
			}
		}
		return false;
	}
	int numWisdoms()
	{		
		int numWisdoms = 0;
		for(InventorySlot slot : slots)
		{
			if(slot.getContents() == InventoryItem.SCROLL_OF_WISDOM)
			{
				numWisdoms ++;
			}
		}
		return numWisdoms;
	}
	int numMagics()
	{		
		int numMagics = 0;
		for(InventorySlot slot : slots)
		{
			if(slot.getItemType() == InventoryItemType.MAGIC)
			{
				numMagics ++;
			}
		}
		return numMagics;
	}
}
