package map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import img.BleedResult;
import img.Bleeder;
import img.Display;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GlobalMap
{
	//InitWidth, InitHeight, Resolution
	private MapGrid grid = new MapGrid(150, 150, 1);
	private JFrame frame;

	private Draw drawPanel;
	
	public void setCenter(Point p) { grid.setCenter(p); }
	public Point getCenter() { return grid.getCenter(); }
	public void markBlocked(Point p) { grid.markBlocked(p); }
	
	public GlobalMap()
	{
		buildGui();
	}
	public void buildGui() 
	{
		frame = new JFrame();
		
		drawPanel = new Draw();
		drawPanel.setSize(300, 300);
		
		frame.getContentPane().add(drawPanel);
		frame.setSize(300, 300);
		frame.setLocation(800, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	/**
	 * 
	 * Adds img at the point Offset which is an offset with respect to the global coordinate system.
	 * 
	 * @param img
	 * @param offset
	 */
	public void addImage(IntBitmap img, Point offset)
	{
		HashMap<Point, Point> bridgePortals = new HashMap<Point, Point>();
		IntBitmap processedMap = processMap(img, bridgePortals);
		grid.addMatrix(toMatrix(processedMap, bridgePortals), offset);
		frame.setSize(grid.getWidth() * 2, grid.getHeight() * 2);
		drawPanel.repaint();
	}
	
	public ArrayList<Point> findPath()
	{
		ArrayList<Point> path = AStar.getPath(grid, grid.getCenter());
		for(Point p : path)
		{
			grid.setTile(p.x, p.y, Tile.MARKER);
		}
		drawPanel.repaint();
		return path;
	}
	
	private TileMatrix toMatrix(IntBitmap img, HashMap<Point, Point> bridgePortals)
	{
		Tile[][] matrix = new Tile[img.getWidth()][img.getHeight()];
		int[][][] imgData = img.getData();
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				int[] pixel = imgData[a][b];
				//bridgepixel
				if(pixel[0] == 255 && pixel[1] == 255 && pixel[2] == 255) //should use color objects for style, but could be slow
				{
					matrix[a][b] = Tile.BRIDGE_PORTAL;
				}
				else if(pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0)
				{
					matrix[a][b] = Tile.PATHABLE;
				}
				else
				{
					matrix[a][b] = Tile.UNPATHABLE;
				}
			}
		}
		return new TileMatrix(matrix, bridgePortals);
	}
	//The processed map should probably be a class of its own, but that will be a change to make later, and after profiling
	//Separating it into its own class will probably be time consuming
	private IntBitmap processMap(IntBitmap img, HashMap<Point, Point> bridgePortals)
	{
		IntBitmap bridges = IntBitmap.copy(img);
		
		RatioFilter.maintainRatio(bridges, FilterType.CAVE_BRIDGE);
		Bleeder bridgeBleeder = new Bleeder(1);
		ArrayList<BleedResult> results = new ArrayList<BleedResult>();
		for(BleedResult result : bridgeBleeder.find(bridges.toGreyscale().doubleCutoff(50)))
		{
			if(result.getNumPixels() > 10) killArch(result, img);
			/*
			if(result.getNumPixels() > 10 && hasNearbyWater(result, img))
			{
				results.add(result);
			}
			else
			{
				//RatioFilter.eliminateRatio(img, FilterType.CAVE_BRIDGE, result.toRectangle());
				killArch(result, img);
			}
			*/
		}
		clearCenter(img);
		for(BleedResult result : results)
		{
			markBridgePortals(result, img, bridgePortals);
		}
		
		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		filters.add(FilterType.CAVE_WATER);
		filters.add(FilterType.CAVE_JUNK);
		
		RatioFilter.eliminateRatio(img, filters);	//Has all obstacles colored
		
		img.highPassByAverage(50);
		//Display.showHang(img);
		return img;
	}
	private static final int WATER_SEARCH_RADIUS = 5;
	private boolean hasNearbyWater(BleedResult result, IntBitmap image)
	{
		Rectangle bleedRect = result.toRectangle();
		int[][][] imageData = image.getData();
		//Display.showHang(image);
		for(int a = bleedRect.x - WATER_SEARCH_RADIUS; a < bleedRect.x + bleedRect.width + WATER_SEARCH_RADIUS; a++)
		{
			for(int b = bleedRect.y - WATER_SEARCH_RADIUS; b < bleedRect.y + bleedRect.height + WATER_SEARCH_RADIUS; b++)
			{
				if(a >= 0 && b >= 0 && a < imageData.length - 1 && b < imageData[0].length - 1 && 
						RatioFilter.matchesRatio(imageData[a][b], FilterType.CAVE_WATER))
				{
					return true;
				}
			}
		}
		return false;
	}
	private static final int ARCH_MARGIN = 1;
	private void killArch(BleedResult result, IntBitmap image)
	{		
		Rectangle bleedRect = result.toRectangle();
		int[][][] imageData = image.getData();
		for(int a = bleedRect.x - ARCH_MARGIN; a < bleedRect.x + bleedRect.width + ARCH_MARGIN; a++)
		{
			for(int b = bleedRect.y - ARCH_MARGIN; b < bleedRect.y + bleedRect.height + ARCH_MARGIN; b++)
			{
				if(a >= 0 && b >= 0 && a < imageData.length - 1 && b < imageData[0].length - 1)
				{
					imageData[a][b][0] = 0;
					imageData[a][b][1] = 0;
					imageData[a][b][2] = 0;
				}
			}
		}
	}
	private static final int CENTER_RADIUS = 2;
	private void clearCenter(IntBitmap image)
	{
		int[][][] imageData = image.getData();
		for(int a = image.getWidth() / 2 - CENTER_RADIUS; a < image.getWidth() / 2 + CENTER_RADIUS; a++)
		{
			for(int b = image.getHeight() / 2 - CENTER_RADIUS; b < image.getHeight() / 2 + CENTER_RADIUS; b++)
			{
				imageData[a][b][0] = 0;
				imageData[a][b][1] = 0;
				imageData[a][b][2] = 0;
			}
		}
	}
	private void markBridgePortals(BleedResult result, IntBitmap image, HashMap<Point, Point> allPortals)
	{
		int[][][] mapData = image.getData();
		Point origin = result.getOrigin();
		Point dest = result.getDest();
		ArrayList<Point> origPortals = markBridgePortal(origin, mapData);
		ArrayList<Point> destPortals = markBridgePortal(dest, mapData);
		hashPortals(origPortals, destPortals, allPortals);
	}
	//Tie all of one end to the first point of the other
	private void hashPortals(ArrayList<Point> origPortals, ArrayList<Point> destPortals, HashMap<Point, Point> allPortals)
	{
		for(Point orig : origPortals)
		{
			allPortals.put(orig, destPortals.get(0));
		}
		for(Point dest : destPortals)
		{
			allPortals.put(dest, origPortals.get(0));
		}
	}
	private static final int PORTAL_RADIUS = 3;
	//Marks one bridge portal and puts all points into an ArrayList
	private ArrayList<Point> markBridgePortal(Point origin, int[][][] mapData)
	{
		ArrayList<Point> portalPoints = new ArrayList<Point>();
		for(int x = -PORTAL_RADIUS; x < PORTAL_RADIUS; x++)
		{
			for(int y = -PORTAL_RADIUS; y < PORTAL_RADIUS; y++)
			{
				int mapX = origin.x + x;
				int mapY = origin.y + y;
				if(mapX >= 0 && mapY >=0 && mapX < mapData.length && mapY < mapData[0].length)
				{
					for(int a = 0; a < IntBitmap.RGB; a++)
					{
						mapData[mapX][mapY][a] = 255;
					}
				}
				portalPoints.add(new Point(mapX, mapY));
			}
		}
		return portalPoints;
	}
	class Draw extends JPanel
	{
		@Override
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			grid.drawImage(g2d);
		}
	}
	
}
