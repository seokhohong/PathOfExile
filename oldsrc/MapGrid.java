package map;

import java.awt.Point;
import java.util.*;
import java.awt.Graphics2D;

public class MapGrid implements Pathable
{
	
	//Stored by rows of columns
	private ArrayList<ArrayList<Tile>> map = new ArrayList<ArrayList<Tile>>();
	
	private HashMap<Point, Point> bridgePortals = new HashMap<Point, Point>();
	
	private int top = 0;	//will go negative as map is explored above
	private int left = 0;
	private int width;			public int getWidth() { return width; }
	private int height;			public int getHeight() { return height; }
	
	private int resolution;
	
	//Center of the last matrix added to map
	private Point currentCenter;	public Point getCenter() { setTile(currentCenter, Tile.MARKER); return currentCenter; }
									public void setCenter(Point p) { currentCenter = p; }  
	
	public void markBlocked(Point p) { setTile(p, Tile.UNPATHABLE); }
									
	MapGrid(int initWidth, int initHeight, int resolution)
	{
		this.width = initWidth;
		this.height = initHeight;
		currentCenter = new Point(width / 2, height / 2);
		this.resolution = resolution;
		for(int a = 0; a < width; a ++) //init
		{
			map.add(newColumn());
		}
	}

	//Add neighboring points
	void addNeighbors(Queue<Point> open, Set<Point> visited, int x, int y)
	{
		ArrayList<Point> neighbors = new ArrayList<Point>();
		if(x - 1 > this.left) neighbors.add(new Point(x - 1, y));
		if(y - 1 > top) neighbors.add(new Point(x, y - 1));
		if(x + 1 > this.left + width) neighbors.add(new Point(x + 1, y));
		if(y + 1 > top + height) neighbors.add(new Point(x, y + 1));
		for(Point p : neighbors)
		{
			if(!visited.contains(p) && getTile(p.x, p.y).isPathable())
			{
				open.add(p);
			}
		}
		visited.addAll(neighbors);
	}
	//fractions of pixels that have to be white to consider this tile pathable
	/*
	private static final double WHITE_THRESHOLD = 0.1f;
	private boolean[][] imgToGrid(BinaryImage bin)
	{
		boolean[][] data = bin.getData();
		boolean[][] gridData = new boolean[imgToGridCoord(bin.getWidth())][imgToGridCoord(bin.getHeight())];
		for(int a = 0; a < bin.getWidth(); a += resolution) //loop through all big blocks
		{
			for(int b = 0; b < bin.getHeight(); b += resolution)
			{
				//loop through pixels within each large block
				int numWhite = 0;
				for(int c = 0; c < resolution; c++)
				{
					for(int d = 0; d < resolution; d++)
					{
						if(data[a + c][b + d] == BinaryImage.WHITE)
						{
							numWhite++;
						}
					}
				}
				gridData[imgToGridCoord(a)][imgToGridCoord(b)] = (numWhite / (resolution * resolution) > WHITE_THRESHOLD);
			}
		}
		return gridData;
	}*/
	//Image coordinate to grid coordinate conversion
	int imgToGridCoord(int x)
	{
		return x / resolution;
	}
	//Operates on a resolution of 1
	void addMatrix(TileMatrix tileMatrix, Point offset)
	{
		Tile[][] tiles = tileMatrix.getMatrix();
		addPortals(tileMatrix.getBridgePortals());
		clearMarked();
		for(int a = 0; a < tileMatrix.getWidth(); a++)
		{
			for(int b = 0; b < tileMatrix.getHeight(); b++)
			{
				int x = a + offset.x;
				int y = b + offset.y;
				extendMap(x, y);
				if(getTile(x, y) == Tile.UNWRITTEN)
				{
					setTile(x, y, tiles[a][b]);
				}
			}
		}
		currentCenter = new Point(currentCenter.x + offset.x, currentCenter.y + offset.y);
	}
	private void clearMarked()
	{
		for(ArrayList<Tile> tiles : map)
		{
			for(int a = 0; a < tiles.size(); a++)
			{
				if(tiles.get(a) == Tile.MARKER)
				{
					tiles.set(a, Tile.PATHABLE);
				}
			}
		}
	}
	private void addPortals(HashMap<Point, Point> portals)
	{
		bridgePortals.clear(); //reset old portal locations
		for(Point key : portals.keySet())
		{
			if(!bridgePortals.containsKey(key))
			{
				bridgePortals.put(key, portals.get(key));
			}
		}
	}
	//expands the map so the coordinates are valid
	private void extendMap(int x, int y)
	{
		if(x < left) { extendLeft(left - x); }
		if(x >= left + width) { extendRight(x - (left + width) + 1); }
		if(y < top) { extendUp(top - y); }
		if(y >= top + height) { extendDown(y - (top + height) + 1); }
	}
	void setTile(int x, int y, Tile tile)
	{
		map.get(x - left).set(y - top, tile);
	}
	void setTile(Point p, Tile tile)
	{
		map.get(p.x - left).set(p.y - top, tile);
	}
	/**
	 * Returns the Tile at the x, y grid coordinate. Does no bounds checking.
	 */
	Tile getTile(int x, int y)
	{
		return map.get(x - left).get(y - top);
	}
	Tile getTile(Point p)
	{
		return map.get(p.x - left).get(p.y - top);
	}
	public int hScore(Node n)
	{
		//How close it is to edge (consider arena exit later)
		int xDist = n.x < width / 2 ? n.x : width - n.x;
		int yDist = n.y < height / 2 ? n.y : height - n.y;
		return Math.min(xDist, yDist);
		//return n.x + n.y;
	}
	public boolean isDestination(Node n)
	{
		//If edge, good. Later implement making the destination the arena exit
		return n.x == 0 || n.x == width - 1 || n.y == 0 || n.y == height - 1;
	}
	//Inserts neighbors into the list
	public ArrayList<Node> getNeighbors(Node currNode)
	{
		ArrayList<Node> nodes = new ArrayList<Node>();
		Tile thisTile = getTile(currNode);
		if(thisTile == Tile.BRIDGE_PORTAL)	//add bridge portals if there are any
		{
			Node portalNode = new Node(bridgePortals.get(currNode.toPoint()));
			portalNode.setParent(currNode);
			nodes.add(portalNode);
		}
		addAdjacentNeighbor(nodes, currNode.x - 1, currNode.y, currNode);
		addAdjacentNeighbor(nodes, currNode.x + 1, currNode.y, currNode);
		addAdjacentNeighbor(nodes, currNode.x, currNode.y - 1, currNode);
		addAdjacentNeighbor(nodes, currNode.x, currNode.y + 1, currNode);
		return nodes;
	}
	private void addAdjacentNeighbor(ArrayList<Node> nodes, int newX, int newY, Node parent)
	{
		if(newX >= 0 && newY >=0 && map.size() > newX - left && map.get(newX - left).size() > newY - top && getTile(newX, newY).isPathable())
		{
			Node newNode = new Node(newX, newY);
			newNode.setParent(parent);
			nodes.add(newNode);
		}
	}
	//Makes a new column with the proper number of hidden Tiles
	private ArrayList<Tile> newColumn()
	{
		ArrayList<Tile> col = new ArrayList<Tile>();
		for(int a = 0; a < height; a++)
		{
			col.add(Tile.UNWRITTEN);
		}
		return col;
	}
	//Extend methods add more ArrayLists or Tiles to accommodate more information
	//left/top/width/height variables are adjusted accordingly.
	private void extendUp(int numRows)
	{
		for(int a = 0; a < numRows; a++)
		{
			top -- ;
			height ++ ;
			for(ArrayList<Tile> col : map)
			{
				insertFront(col, Tile.UNWRITTEN);
			}
		}
	}
	private void extendDown(int numRows)
	{
		for(int a = 0; a < numRows; a++)
		{
			height ++ ;
			for(ArrayList<Tile> col : map)
			{
				col.add(Tile.UNWRITTEN);
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
	private static final int RECT_DIM = 2;
	public void drawImage(Graphics2D g2d)
	{
		ArrayList<StringBuilder> rows = new ArrayList<StringBuilder>();
		for(int a = 0; a < map.get(0).size(); a++)
		{
			rows.add(new StringBuilder(""));
		}
		for(int a = 0; a < map.size(); a++)
		{
			for(int b = 0; b < map.get(a).size(); b++)
			{
				g2d.setColor(map.get(a).get(b).getColor());
				g2d.fillRect(a * RECT_DIM, b * RECT_DIM, RECT_DIM, RECT_DIM);
			}
		}
	}
	
	public void export(String filename)
	{
		/*
		try
		{
			BufferedWriter buff = new BufferedWriter(new FileWriter(new File(filename)));
			//Should use StringBuilder here, but this exists only for debugging 
			ArrayList<String> rows = new ArrayList<String>();
			for(int a = 0; a < map.get(0).size(); a++)
			{
				rows.add("");
			}
			for(ArrayList<Tile> col : map)
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
		*/
	}
	
	//Could have used two ArrayLists instead of inserting, but shouldn't be too significant. Runs in O(n) time
	private static void insertFront(ArrayList<Tile> list, Tile element)
	{
		ArrayList<Tile> dumpHere = new ArrayList<Tile>();
		dumpHere.addAll(list);
		list.clear();
		list.add(element);
		list.addAll(dumpHere);
	}
	private static void insertFront(ArrayList<ArrayList<Tile>> list, ArrayList<Tile> element)
	{
		ArrayList<ArrayList<Tile>> dumpHere = new ArrayList<ArrayList<Tile>>();
		dumpHere.addAll(list);
		list.clear();
		list.add(element);
		list.addAll(dumpHere);
	}
}
