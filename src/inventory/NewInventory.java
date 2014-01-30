package inventory;

import img.BinaryImage;
import img.ImageLibrary;
import img.IntBitmap;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import macro.HomeNavigator;
import macro.LogoutException;
import macro.Macro;
import macro.Timer;
import map.GlobalMap;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;

public class NewInventory extends ItemGrid
{
	private static final int WIDTH = 12;				public int getWidth() { return WIDTH; }
	private static final int HEIGHT = 5; 				public int getHeight() { return HEIGHT; }
	
	private static final Rectangle IMAGE_RECTANGLE		
		= new Rectangle(0, 0, 0, 0); 					public Rectangle getImageRectangle() { return IMAGE_RECTANGLE; }
			//fix this later^
		
	private static final int[] columnData = {0, 28, 29, 57, 58, 86, 87, 116, 117, 145, 146, 174, 175, 203, 204, 233, 234, 262, 263, 291, 292, 320, 321, 350 };
		public int[] getColumnData() { return columnData; }
		
	private static final int[] rowData = {0, 28, 29, 58, 59, 87, 88, 116, 117, 145 };
		public int[] getRowData() { return rowData; }
	
	public NewInventory(WindowThread thread, GlobalMap global) throws LogoutException, HaltThread 		
	{
		super(thread, global);
	}
	
	@Override 
	public void build() throws LogoutException, HaltThread
	{
		//GlobalMap global = GlobalMap.findHome(window);
		//long t0 = System.currentTimeMillis();
		IntBitmap inventoryPic = openInventory();
		//BinaryImage processedImage = processInventory();
		//columns = getColumns(processedImage);
		//rows = getRows(processedImage);

		//System.out.println(columns.size() + " " + rows.size());
		CornerMatrix cornerMatrix = new CornerMatrix();
		if(cornerMatrix.properlyBuilt())
		{
			InventorySquare[][] inventorySquares = createSquares(inventoryPic, cornerMatrix);
			findSlots(inventoryPic, cornerMatrix, inventorySquares);
			//Macro.macro.sleep(20);
			super.moveCursorAway();
			//createSlots();
			Collections.sort(slots);
			Macro.sleep(50);
			for(InventorySlot slot : slots)
			{
				slot.findItem();
			}

			InventoryMacro.identifyItems(this);
		
			closeInventory();

			window.mouseMove(PWindow.getWindowCenter());
			Macro.sleep(100);

			if(hasStuffToSell())
			{
				System.out.println("Selling");
				new HomeNavigator(thread, global).goToStore();
				InventoryMacro.sellShit(this, window);
			}
			GlobalMap.LION_EYES_WATCH.waitForWaypoint(thread);
			//System.out.println("Done Selling");
			InventoryMacro.depositStash(this, global, thread);
			//System.out.println((System.currentTimeMillis() - t0));
		}
		else
		{
			System.out.println("Sufficient data not found.");
		}

	}
	
	private void closeInventory() throws LogoutException
	{
		Timer maxToggle = new Timer(2000);
		while(window.inventoryVisible())
		{
			window.toggleInventory();
			Macro.sleep(300);
			if(maxToggle.hasExpired())
			{
				throw new LogoutException("Could not close Inventory");
			}
		}
	}

	private IntBitmap openInventory() throws LogoutException
	{
		IntBitmap inventoryImage = ImageLibrary.INVENTORY.get();
		//Display.showHang(screen);
		Timer openInventTimer = new Timer(1000);
		Point p = ScreenRegion.INVENTORY_ICON.getTopLeft();
		while(!window.inventoryVisible())
		{
			window.toggleInventory();
			Macro.sleep(300);
			if(openInventTimer.hasExpired())
			{
				throw new LogoutException("Failed to open inventory");
			}
		}
		IntBitmap screen = IntBitmap.getInstance(window.takeScreenshot());
		inventoryRect = new Rectangle(p.x + inventoryImage.getWidth(), p.y + inventoryImage.getHeight(), INVENTORY_PIXEL_WIDTH, INVENTORY_PIXEL_HEIGHT);
		return screen.subimage(inventoryRect);
	}
	/*
	private BinaryImage processInventory()
	{
		corner_x = p.x + SQUARE_IDENTIFIER_OFFSET; //accounts for offset from top left corner of ID pic and top left corner of actual inventory
		corner_y = p.y + SQUARE_IDENTIFIER_OFFSET; //
		inventoryRect = new Rectangle(corner_x, corner_y, INVENTORY_PIXEL_WIDTH, INVENTORY_PIXEL_HEIGHT);
		inventoryPic = screen.subimage(inventoryRect);
		//Display.showHang(IntBitmap.getInstance(window.takeScreenshot(inventoryRect)));
		IntBitmap picForProcessing = IntBitmap.copy(inventoryPic);	
		IntBitmap otherPicForProcessing = IntBitmap.copy(inventoryPic);
		picForProcessing.lowPass(50);
		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		filters.add(FilterType.INVENT_BROWN_WALL);
		filters.add(FilterType.INVENT_BLUE_WALL);
		RatioFilter.maintainRatio(picForProcessing, filters);
		BinaryImage passed = picForProcessing.toGreyscale().doubleCutoff(20);
		MidpassFilter.maintainRanges(otherPicForProcessing, MidpassFilterType.INVENTORY_RED_LINE);
		BinaryImage bin = otherPicForProcessing.toGreyscale().doubleCutoff(20);
		passed.add(bin);
		passed.killLoners(1, true ); //eliminates any isolated white pixels to clean up image
		//Display.showHang(passed);

		return passed;
	}
	*/
	static final int VERTICAL_THRESHOLD = 25;
	private ArrayList<Integer> getColumns(BinaryImage passed)
	{
		ArrayList<Integer> sigColumns = new ArrayList<Integer>();
		boolean[][] data = passed.getData();
		
		for(int i=0; i<data.length; i++) //vertical true counter
		{
			int count = 0;
			for(int k=0; k<data[0].length; k++)
			{
				if(data[i][k] == true)
				{
					count++;
				}
			}
			//System.out.println(count);
			if(count >= VERTICAL_THRESHOLD)
			{
				sigColumns.add(i);
				//passed.fillColumn(i);
			}
			
		}
		//Display.showHang(passed);
		return sigColumns;
	}
	
	static final int HORIZONTAL_THRESHOLD = 50;
	private ArrayList<Integer> getRows(BinaryImage passed)
	{
		ArrayList<Integer> sigRows = new ArrayList<Integer>();
		boolean[][] data = passed.getData();
		
		for(int i=0; i<data[0].length; i++) //horizontal true counter
		{
			int count = 0;
			for(int k=0; k<data.length; k++)
			{
				if(data[k][i] == true)
				{
					count++;
				}
			}
			if(count >= HORIZONTAL_THRESHOLD)
			{
				sigRows.add(i);
				//passed.fillRow(i);
			}
		}
		return sigRows;
	}
	
	private InventorySquare[][] createSquares(IntBitmap image, CornerMatrix cornerMatrix)
	{
		InventorySquare[][] squares = new InventorySquare[INVENTORY_WIDTH][INVENTORY_HEIGHT];
		for(int a=0; a < INVENTORY_WIDTH; a++)
		{
			for(int b=0; b < INVENTORY_HEIGHT; b++)
			{
				squares[a][b] = new InventorySquare(image, cornerMatrix.getTLCorner(a, b), cornerMatrix.getBRCorner(a, b), this);
			}
		}
		return squares;
	}
	private void findSlots(IntBitmap inventoryPic, CornerMatrix cornerMatrix, InventorySquare[][] inventorySquares)
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
	private IntBitmap takeInventoryScreenshot()
	{
		return IntBitmap.getInstance(window.takeScreenshot(inventoryRect));
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
	int getCornerX()
	{
		return inventoryRect.x;
	}
	int getCornerY()
	{
		return inventoryRect.y;
	}
	PWindow getInventoryWindow()
	{
		return window;
	}

}
