package map;

import fourier.BandPass;
import fourier.FourierTransform;
import img.BinaryImage;
import img.GreyscaleImage;
import img.IntBitmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.*;
import java.awt.Point;

public class LevelMap
{
	enum State 
	{ 
		HIDDEN(' ', false), 
		BLOCKED('X', false), 
		VISITED_PATH('.', true),	 	//no need to differentiate between visited and unvisited for exporting
		UNVISITED_PATH('.', true);
		char symbol; //for exporting
		boolean pathable;
		State(char symbol, boolean pathable)
		{
			this.symbol = symbol;
		}
		char getSymbol() { return symbol; }
		boolean isPathable() { return pathable; }
	}
	private static final int RESOLUTION = 5; //number of map pixels that correspond to a given state
	
	//Stored by rows of columns
	private ArrayList<ArrayList<State>> map = new ArrayList<ArrayList<State>>();
	private int top = 0;	//will go negative as map is explored above
	private int left = 0;
	private int width;
	private int height;
	
	private static final int SIGHT_RADIUS = 7; //how many states we can confirm are visible if we are in the center
	
	//the grid coordinate of the top left corner of the last image to be processed
	private int lastX = 0;
	private int lastY = 0;
	private int lastWidth = 0; //width of the last image spliced into LevelMap (probably should be 150). Map images must be square.
	BinaryImage lastMap; //the last image to be processed (keeps fourier computations small because its not the whole map that is processed)
	
	//Takes an IntBitmap of the minimap dimensions and preprocesses for acceptance to the LevelMap database
	public static BinaryImage preprocessMap(IntBitmap rawMap)
	{
		// Note: 80, 140 is very good for minimaps
		//FourierTransform isolateBadSignal = new FourierTransform(new BandPass(90.0d, 105.0d));
		FourierTransform ft = new FourierTransform(new BandPass(60.0d, 100d));
		FourierTransform ft2 = new FourierTransform(new BandPass(80.0d, 140d));
		//bidirectional is good
		GreyscaleImage grey = rawMap.toGreyscale();//rawMap.bidirectionalDerivative();
		grey = ft2.transform(grey);    // Fourier transform
		//GreyscaleImage remove = isolateBadSignal.transform(rawMap.toGreyscale());
		//grey.subtract(remove);
 		//Iterate, strengthening clusters and eliminating loners
		//remove.display();
		//grey.horizontalBlur(10);
		//grey.display();
	    for(int i = 0; i < 2; i++)
	    {
		   	grey.blur(3);
	    	grey.multiply(1.02); // Experimentally: this is the smallest value that keeps items 'alive' indefinitely.
	    }
	    for(int j = 0; j < 10; j ++)
	    {
	    	grey = grey.add(rawMap.toGreyscale(), .5);
	    	grey = ft2.transform(grey);    // Fourier transform
	    	for(int i = 0; i < 4; i++)
	    	{
	    		grey.blur(2);
	    		grey.multiply(1.02); // Experimentally: this is the smallest value that keeps items 'alive' indefinitely.
	    	}
	    }
	    BinaryImage bin = grey.doubleCutoff(230);
	    bin.toGreyscale().display();
	    return bin;
	}
	public LevelMap(BinaryImage mapImg) //initial part of the world
	{
		width = mapImg.getWidth() / RESOLUTION;
		height = mapImg.getHeight() / RESOLUTION;
		for(int a = 0; a < width; a ++) //init
		{
			map.add(newColumn());
		}
		addImage(mapImg);
		lastMap = mapImg;
	}
	public void addImage(BinaryImage img)
	{
		boolean[][] grid = imgToGrid(img);
		lastWidth = imgToGridCoord(img.getWidth());
		int[] offset = {0, 0};
		if(lastMap!=null)
		{
			offset = FourierTransform.on(lastMap.toGreyscale(), img.toGreyscale());
			//ImageToolkit.splice(lastMap.toGreyscale(), img.toGreyscale(), offset[0], offset[1]).display();
		}
		addGrid(grid, offset);
	}
	//Returns the best coordinate to click on (with respect to the grid not the screen)
	//Assumes current position is with respect to lastX, lastY
	public Point move()
	{
		Point gridCoord = bestDestination();
		if(gridCoord==null)
		{
			System.out.println("Done Exploring");
			System.exit(0);
		}
		//Run AStar here
		return gridCoord; //change this!
	}
	//Runs a bfs to find closest unexplored path
	private Point bestDestination()
	{
		Set<Point> visited = new HashSet<Point>();
		int currX = lastX + lastWidth / 2;
		int currY = lastY + lastWidth / 2;
		return bfsFrom(visited, currX, currY);
	}
	/**
	 * Runs a breadth first search from the coordinates x, y to find the nearest UNVISITED_PATH
	 */
	private Point bfsFrom(Set<Point> visited, int x, int y)
	{
		if(getState(x, y) == State.UNVISITED_PATH)
		{
			return new Point(x, y);
		}
		visited.add(new Point(x, y));
		if(x > left && getState(x - 1, y).isPathable()) { bfsFrom(visited, x - 1, y); }
		if(x < width - 1 && getState(x + 1, y).isPathable()) { bfsFrom(visited, x + 1, y); }
		if(y > top && getState(x, y - 1).isPathable()) { bfsFrom(visited, x, y - 1); }
		if(y > height - 1 && getState(x, y + 1).isPathable()) { bfsFrom(visited, x, y + 1); }
		return null;
	}
	/**
	 * Returns the state at the x, y grid coordinate. Does no bounds checking.
	 */
	private State getState(int x, int y)
	{
		return map.get(x - left).get(y - top);
	}
	private void addGrid(boolean[][] grid, int[] offset)
	{
		for(int a = lastX; a < grid.length; a++)
		{
			for(int b = lastY; b < grid[0].length; b++)
			{
				if(grid[a][b])
				{
					if(Math.abs(a - grid.length / 2) <= SIGHT_RADIUS)
					{
						addData(a, b, State.VISITED_PATH);
					}
					else
					{
						addData(a, b, State.UNVISITED_PATH);
					}
				}
				else
				{
					addData(a, b, State.BLOCKED);
				}
			}
		}
		lastX = offset[0];
		lastY = offset[1];
	}
	//number of pixels out of RESOLUTION ^ 2 that have to be white to consider this tile pathable
	private static final int WHITE_THRESHOLD = 15;
	private static boolean[][] imgToGrid(BinaryImage bin)
	{
		boolean[][] data = bin.getData();
		boolean[][] gridData = new boolean[imgToGridCoord(bin.getWidth())][imgToGridCoord(bin.getHeight())];
		for(int a = 0; a < bin.getWidth(); a += RESOLUTION) //loop through all big blocks
		{
			for(int b = 0; b < bin.getHeight(); b += RESOLUTION)
			{
				//loop through pixels within each large block
				int numWhite = 0;
				for(int c = 0; c < RESOLUTION; c++)
				{
					for(int d = 0; d < RESOLUTION; d++)
					{
						if(data[a + c][b + d] == BinaryImage.WHITE)
						{
							numWhite++;
						}
					}
				}
				gridData[imgToGridCoord(a)][imgToGridCoord(b)] = numWhite > WHITE_THRESHOLD;
			}
		}
		return gridData;
	}
	//Image coordinate to grid coordinate conversion
	private static int imgToGridCoord(int x)
	{
		return x / RESOLUTION;
	}
	private void addData(int x, int y, State state)
	{
		if(x < left) { extendLeft(left - x); }
		if(x > left + width) { extendRight(x - (left + width)); }
		if(y < top) { extendUp(top - y); }
		if(y > top + height) { extendDown(y - (top + height)); }
		map.get(x - left).set(y - top, state);
	}
	//Makes a new column with the proper number of hidden states
	private ArrayList<State> newColumn()
	{
		ArrayList<State> col = new ArrayList<State>();
		for(int a = 0; a < height; a++)
		{
			col.add(State.HIDDEN);
		}
		return col;
	}
	//Extend methods add more ArrayLists or states to accomodate more information
	//left/top/width/height variables are adjusted accordingly.
	private void extendUp(int numRows)
	{
		for(int a = 0; a < numRows; a++)
		{
			top -- ;
			height ++ ;
			for(ArrayList<State> col : map)
			{
				insertFront(col, State.HIDDEN);
			}
		}
	}
	private void extendDown(int numRows)
	{
		for(int a = 0; a < numRows; a++)
		{
			height ++ ;
			for(ArrayList<State> col : map)
			{
				col.add(State.HIDDEN);
			}
		}
	}
	private void extendLeft(int numCols)
	{
		for(int a = 0; a < numCols ; a++)
		{
			insertFront(map, newColumn());
			left --;
			width ++ ;
		}
	}
	private void extendRight(int numCols)
	{
		for(int a = 0; a < numCols ; a++)
		{
			map.add(newColumn());
			width ++ ;
		}
	}
	
	public void export(String filename)
	{
		try
		{
			BufferedWriter buff = new BufferedWriter(new FileWriter(new File(filename)));
			//Should use StringBuilder here, but this exists only for debugging 
			ArrayList<String> rows = new ArrayList<String>();
			for(int a = 0; a < map.get(0).size(); a++)
			{
				rows.add("");
			}
			for(ArrayList<State> col : map)
			{
				for(int a = 0; a < col.size(); a++)
				{
					rows.set(a, rows.get(a) + col.get(a).getSymbol());
				}
			}
			for(String row : rows)
			{
				buff.write(row);
				buff.newLine();
			}
			buff.close();
		}
		catch(IOException e)
		{
			
		}
	}
	
	//Could have used two ArrayLists instead of inserting, but shouldn't be too significant. Runs in O(n) time
	private static void insertFront(ArrayList<State> list, State element)
	{
		list.add(list.get(list.size() - 1));
		for(int a = 1 ; a < list.size() - 1; a++)
		{
			list.set(a, list.get(a - 1));
		}
		list.set(0, element);
	}
	private static void insertFront(ArrayList<ArrayList<State>> list, ArrayList<State> element)
	{
		list.add(list.get(list.size() - 1));
		for(int a = 1 ; a < list.size() - 1; a++)
		{
			list.set(a, list.get(a - 1));
		}
		list.set(0, element);
	}
}
/*
public class LevelMap extends BinaryImage
{
	public LevelMap(boolean[][] data) 
	{
		super(data);
	}
	public LevelMap(BinaryImage bin)
	{
		super(bin.getData());
	}
	private static final int BLACK_RADIUS = 2;
	public void expandBlack()
	{
		boolean[][] data = getData();
		boolean[][] newData = new boolean[getWidth()][getHeight()];
		for(boolean[] col : newData)
		{
			Arrays.fill(col, BinaryImage.WHITE);
		}
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0 ; b < getHeight(); b++)
			{
				//If any neighbors are black, make this pixel black;
				for(int c = -BLACK_RADIUS; c < BLACK_RADIUS; c++)
				{
					if(a + c < 0 || a + c >= getWidth() - 1) continue;
					for(int d = -BLACK_RADIUS ; d < BLACK_RADIUS; d++)
					{
						if(b + d < 0 || b + d >= getHeight() - 1) continue;
						if(data[a+c][b+d] == BinaryImage.BLACK) 
						{
							newData[a][b] = BinaryImage.BLACK;
						}
					}
				}
			}
		}
		setData(newData);
	}
	private static final int CLEAR_RADIUS = 5;
	//unfortunately the yellow marker icon appears as a blocker on the processed map
	public void clearCenter()
	{
		boolean[][] data = getData();
		for(int a = -CLEAR_RADIUS; a < CLEAR_RADIUS; a++)
		{
			for(int b = -CLEAR_RADIUS ; b < CLEAR_RADIUS; b++)
			{
				data[a+getWidth()/2][b+getHeight()/2] = BinaryImage.WHITE;
			}
		}
	}
	private static final int DEG_PER_INC = 5; //degrees per increment
	private static final int DEG_PER_2PI = 360;
	private int[] polarSearch()
	{
		boolean data[][] = getData();
		int[] radius = new int[DEG_PER_2PI/DEG_PER_INC];
		for(int a = 0; a < radius.length; a++)
		{
			int dist = 0;
			while(true)
			{
				int x = (int) (dist * Math.cos(Math.toRadians(a*DEG_PER_INC))) + getWidth() / 2;
				int y = (int) (dist * Math.sin(Math.toRadians(a*DEG_PER_INC))) + getHeight() / 2;
				if(x < 0 || y < 0 || x > getWidth()- 1 || y > getHeight() - 1) //out of bounds
				{
					break;
				}
				if(data[x][y] == BinaryImage.BLACK) break;
				dist++;
			}
			radius[a] = dist;
		}
		return radius;
	}
	//returns angle and distance that can be traveled at this angle
	public int bestMovement(int currAngle)
	{
		int[] polar = polarSearch();
		int bestAngle = 0;
		int bestScore = 0;
		for(int a = 0; a < polar.length; a++)
		{
			//0 through 180
			int angleScore = getShortestAngle(currAngle, a * DEG_PER_INC) / 2;
			if(polar[a] - angleScore > bestScore)
			{
				bestScore = polar[a] - angleScore;
				bestAngle = a * DEG_PER_INC;
			}
		}
		return bestAngle;
	}
	private static int getShortestAngle(int ang1, int ang2)
	{
		int angle = Math.abs(ang1 - ang2) %360;
		if(angle > DEG_PER_2PI / 2)
		{
			angle = DEG_PER_2PI - angle;
		}
		return angle;
	}
}
*/