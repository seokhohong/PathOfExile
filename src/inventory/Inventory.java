package inventory;

import img.BinaryImage;
import img.ImageLibrary;
import img.IntBitmap;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import macro.HomeNavigator;
import macro.LogoutException;
import macro.Macro;
import macro.Timer;
import map.GlobalMap;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;

public class Inventory 
{	
	static final int INVENTORY_WIDTH = 12;				//height in inventory grid squares
	static final int INVENTORY_HEIGHT = 5;				//
	static final int INVENTORY_PIXEL_WIDTH = 351;
	static final int INVENTORY_PIXEL_HEIGHT = 146;
	static final int SQUARE_IDENTIFIER_OFFSET = 6;		//size of the top left corner of the inventory that's matched against in the img library
	
	private PWindow window;								PWindow getPWindow() { return window; }
	private GlobalMap global;
	private WindowThread thread;
	private Rectangle inventoryRect;												Point getInventoryCorner() { return new Point(inventoryRect.x, inventoryRect.y);} //corner of inventory w/ respect to PWindow

	ArrayList<InventorySlot> slots = new ArrayList<InventorySlot>(); 				public ArrayList<InventorySlot> getSlots(){ return slots; }

	/**
	 * Should call method "build" upon instantiation of inventory class to initialize inventory.
	 * 
	 * @param window
	 * @throws LogoutException 
	 */
	public Inventory(WindowThread thread, GlobalMap global) throws LogoutException
	{
		this.thread = thread;
		window = thread.getWindow();
		this.global = global;
		
	}
	
	private static final Point MOVE_CURSOR_AWAY = new Point(20, 20);
	
	public void build(double chanceIdMagic) throws LogoutException, HaltThread
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

			window.mouseMove(MOVE_CURSOR_AWAY);

			Collections.sort(slots);
			Macro.sleep(50);
			for(InventorySlot slot : slots)
			{
				slot.findItem();
			}

			InventoryMacro.identifyItems(this, chanceIdMagic);
		
			closeInventory();

			if(hasStuffToSell())
			{
				HomeNavigator homeNav = new HomeNavigator(thread, global);
				homeNav.goToStore();
				global.openSellWindow(window);
				System.out.println("Opened Sell Window");
				homeNav.sellShit(this, window);
			}
			global.waitForWaypoint(thread);
			System.out.println("Done Selling");
			InventoryMacro.depositStash(this, global, thread);
		}
		else
		{
			System.out.println("Sufficient data not found.");
		}

	}
	public void buildSpeedy(double chanceIdMagic) throws LogoutException, HaltThread
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
			findSlotsQuickly(inventoryPic, cornerMatrix, inventorySquares, chanceIdMagic);

			window.mouseMove(MOVE_CURSOR_AWAY);
			closeInventory();
			
			if(hasStuffToSell())
			{
				HomeNavigator homeNav = new HomeNavigator(thread, global);
				homeNav.goToStore();
				global.openSellWindow(window);
				System.out.println("Opened Sell Window");
				homeNav.sellShit(this, window);
			}
			GlobalMap.LION_EYES_WATCH.waitForWaypoint(thread);
			System.out.println("Done Selling");
			InventoryMacro.depositStash(this, global, thread);
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
			System.out.println("Closed Inventory");
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
				squares[a][b] = new InventorySquare(image, new Point(a, b), cornerMatrix.getTLCorner(a, b), cornerMatrix.getBRCorner(a, b), this);
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
	@SuppressWarnings("unchecked")
	private void findSlotsQuickly(IntBitmap inventoryPic, CornerMatrix cornerMatrix, InventorySquare[][] inventorySquares, double chanceIdMagic)
	{
		HashSet<InventorySquare> blockedSquares = new HashSet<InventorySquare>(); //Avoids concurrent modification
		for(int a = 0; a < 2; a++)
		{
			for(int b = 0; b < INVENTORY_HEIGHT; b++)
			{
				blockedSquares.add(inventorySquares[a][b]);
			}
		}
		HashSet<Point> validPoints = new HashSet<Point>();
		validPoints.addAll(getColoredSquares(inventoryPic, cornerMatrix, HighlightColor.BLUE));
		validPoints.addAll(getColoredSquares(inventoryPic, cornerMatrix, HighlightColor.RED));
		ArrayList<InventorySquare> validSquares = new ArrayList<InventorySquare>();
		for(Point v : validPoints)
		{
			validSquares.add(inventorySquares[v.x][v.y]);
		}
		Collections.sort(validSquares);
		boolean hasWisdom = false;
		boolean hasClickedWisdom = false;
		window.pressShift();
		for(InventorySquare sq : validSquares)
		{
			if(!blockedSquares.contains(sq))
			{
				window.mouseMove(sq.getWindowClickCoordinate());
				Macro.sleep(30);
				IntBitmap screenshot = takeInventoryScreenshot();
				InventorySquare[][] squares = createSquares(screenshot, cornerMatrix);
				InventoryGrid grid = InventoryGrid.getColorGrid(squares, HighlightColor.GREEN);
				if(!grid.isEmpty())
				{
					for(Point itemPoints : grid.getWhites())
					{
						blockedSquares.add(inventorySquares[itemPoints.x][itemPoints.y]);
					}
					
					InventorySlot s = new InventorySlot(grid.getRectangle(), this, window, inventorySquares);
					slots.add(s);
					window.mouseMove(MOVE_CURSOR_AWAY);
					Macro.sleep(70);
					s.findItem();
					//s.identify();
					//Macro.sleep(999999);
					if(s.getContents() == InventoryItem.SCROLL_OF_WISDOM)
					{
						hasWisdom = true;
						System.out.println("Found a wisdom, guys!");
						//Macro.sleep(9999999);
						window.rightClick(s.getWindowClickCoordinate());
						hasClickedWisdom = true;
						//Macro.sleep(9999999);
					}
					if(hasWisdom && s.getContents() != InventoryItem.SCROLL_OF_WISDOM)
					{
						s.identify();
						identifyIfAppropriate(s, chanceIdMagic);
						Macro.sleep(999999);
					}
				}
			}
		}
		window.releaseShift();
		Macro.sleep(20);
	}
	private void identifyIfAppropriate(InventorySlot slot, double chanceIdMagic)
	{
		if(slot.getItemType() == InventoryItemType.RARE || slot.getItemType() == InventoryItemType.UNIQUE)
		{
			slot.identify();
		}
		if(slot.getItemType() == InventoryItemType.MAGIC && new Random().nextDouble() < chanceIdMagic)
		{
			slot.identify();
		}
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
	private static final int MIN_SELL_COUNT = 3;
	boolean hasStuffToSell()
	{
		int sellCount = 0;
		for(InventorySlot slot : slots)
		{
			if(slot.shouldSell())
			{
				sellCount ++;
				if(sellCount >= MIN_SELL_COUNT)
				{
					return true;
				}
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
