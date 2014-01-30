package map;

import fourier.BandPass;
import fourier.FourierTransform;
import img.BinaryImage;
import img.GreyscaleImage;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.awt.Point;

public class LevelMap
{

	private static final int RESOLUTION = 1; //number of map pixels that correspond to a given state.
	
	private static final int SIGHT_RADIUS = 3; //how many states we can confirm are visible if we are in the center
	private static final int MAX_TRAVEL_DIST = 2; //number of states we can travel per click
	
	//the grid coordinate of the top left corner of the last image to be processed
	private int lastX = 0;
	private int lastY = 0;
	private int lastWidth = 0; //width of the last image spliced into LevelMap (probably should be 150). Map images must be square.
	BinaryImage lastMap; //the last image to be processed (keeps fourier computations small because its not the whole map that is processed)
	
	MapGrid mapGrid;
	
	//Takes an IntBitmap of the minimap dimensions and preprocesses for acceptance to the LevelMap database
	public static BinaryImage preprocessMap(BinaryImage rawMap)
	{
		// Note: 80, 140 is very good for minimaps
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
	    for(int j = 0; j < 5; j ++)
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
	    return bin;
	}
	public LevelMap(BinaryImage mapImg) //initial part of the world
	{
		mapGrid = new MapGrid(mapImg.getWidth() / RESOLUTION, mapImg.getHeight() / RESOLUTION, RESOLUTION);
		addImage(mapImg);
		lastMap = mapImg;
	}
	public void addImage(BinaryImage img)
	{
		boolean[][] grid = mapGrid.imgToGrid(img);
		lastWidth = mapGrid.imgToGridCoord(img.getWidth());
		int[] offset = {0, 0};
		
		if(lastMap!=null)
		{
			//double time = System.currentTimeMillis();
			//offset = FourierTransform.on(lastMap.toGreyscale(), img.toGreyscale(), 10);
			//System.out.println("Transform Time: "+(System.currentTimeMillis() - time));
			//offset[0] *= RESOLUTION;
			//offset[1] *= RESOLUTION;
			//System.out.println(offset[0]+" "+offset[1]);
			//img.toGreyscale().display();
			//ImageToolkit.splice(lastMap.toGreyscale(), img.toGreyscale(), offset[0], offset[1]).display();	
		}
		addGrid(grid, offset);
		lastMap = img;
	}
	
	
	/*Returns the best coordinate to click on (with respect to the grid not the screen)
	Assumes current position is with respect to lastX, lastY*/
	public Point move()
	{
		Point bestDest = bestDestination();
		if(bestDest == null)
		{
			System.out.println("Done Exploring");
			System.exit(0);
		}
		//AStar uses RELATIVE positioning
		Path path = mapGrid.getPath(new PathFrame(currentPosition(), bestDest));
		System.out.println("Current Position " + currentPosition());
		//Best clicking destination, which will be part way through the path
		Point clickDest = path.getPath().get(Math.min(MAX_TRAVEL_DIST, path.getLength() - 1));
		System.out.println("Click Destination " + clickDest);
		return new Point(clickDest.x - currentPosition().x, clickDest.y - currentPosition().y);
	}
	//Runs a bfs to find closest unexplored path
	private Point bestDestination()
	{
		Set<Point> visited = new HashSet<Point>();
		int currX = lastWidth / 2;
		int currY = lastWidth / 2;
		Queue<Point> open = new LinkedList<Point>();
		mapGrid.addNeighbors(open, visited, currX, currY);
		while(!open.isEmpty())
		{
			Point currPoint = open.poll();
			mapGrid.addNeighbors(open, visited, currPoint.x, currPoint.y);
			//?? Check coordinates here
			if(mapGrid.getState(currPoint.x, currPoint.y) == State.UNVISITED_PATH)
			{
				return currPoint;
			}
		}
		return null;
	}
	//Returns the PoE character's current position based on the last map added to the database
	private Point currentPosition()
	{
		return new Point(lastX + (lastWidth / 2), lastY + (lastWidth / 2));
		//return new Point(lastWidth / 2, lastWidth / 2);
	}
	/**
	 * 
	 * Splices a grid of booleans into the whole map. Ensure that offsets are calibrated properly
	 * 
	 * @param grid		: Grid of data
	 * @param offset	: Measured in grid units not pixel units (currently equal)
	 */
	private void addGrid(boolean[][] grid, int[] offset)
	{
		for(int a = 0; a < grid.length; a++)
		{
			for(int b = 0; b < grid[0].length; b++)
			{
				int x = a + offset[0] + lastX;
				int y = b + offset[1] + lastY;
				if(grid[a][b])
				{
					//Just distance formula with adjustment (with how the camera works, it sees north more than south)
					if((a - grid.length / 2) * (a - grid.length / 2) + (b + 1 - grid.length / 2) * (b + 1 - grid.length / 2) <= SIGHT_RADIUS * SIGHT_RADIUS)
					{
						mapGrid.addData(x, y, State.VISITED_PATH);
					}
					else
					{
						mapGrid.addData(x, y, State.UNVISITED_PATH);
					}
				}
				else
				{
					mapGrid.addData(x, y, State.BLOCKED);
				}
			}
		}
		lastX = offset[0];
		lastY = offset[1];
	}
	public void export(String filename)
	{
		mapGrid.export(filename);
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