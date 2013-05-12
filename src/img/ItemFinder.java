package img;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class ItemFinder 
{
	private static final int NUM_H_NEIGHBORS = 12; //the number of neighbors required to be significantly considered aligned
	private static final int NUM_V_NEIGHBORS = 1;
	
	private static final int THRESHOLD = 1;
	
	private static final int ITEM_HEIGHT_BASE = 12;		public static int getItemHeight() { return ITEM_HEIGHT_BASE; }
	private static final int ITEM_HEIGHT_VAR = 5;
	
	public static ArrayList<Point> signatureToPoints(BinaryImage binary)
	{
		ArrayList<Point> allPoints = new ArrayList<Point>();
		boolean[][] data = binary.getData();
		for(int a = 0; a < binary.getWidth(); a++)
		{
			for(int b = 0; b < binary.getHeight(); b++)
			{
				if(data[a][b] == BinaryImage.WHITE)
				{
					allPoints.add(new Point(a, b));
					binary.fillBlack(binary, a, b);
				}
			}
		}
		return allPoints;
	}
	/*
	public static BinaryImage filterPixels(IntBitmap img)
	{
		int[][][] data = img.getData();
		boolean[][] newData = new boolean[img.getWidth()][img.getHeight()];
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				//scale it up
				newData[a][b] = ItemType.bestMatch(data[a][b]);
			}
		}
		return new BinaryImage(newData);
	}
	*/
	//Highlights pixels very different from vertical neighbors
	public static GreyscaleImage deltaVertical(GreyscaleImage img)
	{
		int[][] newData = new int[img.getWidth()][img.getHeight()];
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				newData[a][b] = verticalDistinction(img.getData(), a, b);
			}
		}
		return new GreyscaleImage(newData);
	}
	//Highlights pixels with consistent neighbors
	public static BinaryImage consistentHorizontal(BinaryImage img)
	{
		boolean[][] data = img.getData();
		boolean[][] newData = new boolean[img.getWidth()][img.getHeight()];
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				newData[a][b] = horizontalSimilarity(data, a, b);
			}
		}
		return new BinaryImage(newData);
	}
	private static final double WHITE_MULTIPLIER = 10.0f;
	private static final double WHITE_THRESHOLD = 10.0f;
	private static final double WHITE_REPAIR_COST = 2.0f;
	private static final double WHITE_DEFAULT = 1.0f;
	public static void repairHorizontal(BinaryImage img)
	{
		boolean[][] data = img.getData();
		for(int a = 0; a < img.getHeight(); a++)
		{
			double whiteStrength = WHITE_DEFAULT;
			int consecutiveRepair = 0;
			for(int b = 0; b < img.getWidth(); b++)
			{
				if(data[b][a] == BinaryImage.WHITE)
				{
					if((b < 0 || data[b-1][a]) && (b >= img.getWidth() || data[b+1][a]))
					{
						whiteStrength *= WHITE_MULTIPLIER;
						consecutiveRepair = 0;
					}
				}
				else
				{
					if(whiteStrength > WHITE_THRESHOLD)
					{
						data[b][a] = BinaryImage.WHITE;
						consecutiveRepair++;
						whiteStrength /= Math.pow(WHITE_REPAIR_COST, consecutiveRepair);
					}
					else
					{
						data[b][a] = BinaryImage.BLACK;
						whiteStrength = WHITE_DEFAULT;
						consecutiveRepair = 0;
					}
				}
			}
		}
	}
	public static boolean horizontalSimilarity(boolean[][] data, int x, int y)
	{
		if(x > NUM_H_NEIGHBORS && x < data.length - NUM_H_NEIGHBORS - 1 && data[x][y] == BinaryImage.WHITE)
		{
			boolean rightScore = false;
			boolean leftScore = false;
			for(int a = 0; a < NUM_H_NEIGHBORS; a++)
			{
				if(a == 0) continue;
				//find a black pixel if there is one
				rightScore = rightScore || !data[x+a][y];
				leftScore = leftScore || !data[x-a][y];
			}
			return !leftScore || !rightScore; //if there is a row without a black pixel
		}
		return BinaryImage.BLACK;
	}
	private static int verticalDistinction(int[][] data, int x, int y)
	{
		if(y > NUM_V_NEIGHBORS && y < data[0].length - NUM_V_NEIGHBORS - 1)
		{
			int topScore = 255;
			int botScore = 255;
			for(int a = -NUM_V_NEIGHBORS; a < 0; a++)
			{
				if(a == 0) continue;
				topScore = Math.min(data[x][y] - data[x][y+a], topScore);
				botScore = Math.min(data[x][y] - data[x][y-a], botScore);
			}
			//*10 otherwise a bit too dark in most cases
			return Math.max(10 * Math.max(topScore, botScore), 0);
		}
		return 0;
	}
	public static GreyscaleImage selectLetters(IntBitmap bitmap)
	{
		int[][] data = new int[bitmap.getWidth()][bitmap.getHeight()];
		for(int a = 0 ; a < bitmap.getWidth() ; a++)
		{
			for(int b = 0; b < bitmap.getHeight() ; b++)
			{
				data[a][b] = isRGBSame(bitmap, a, b) && bitmap.getRed(a, b)!=0 ? 255 : 0;
			}
		}
		return new GreyscaleImage(data);
	}
	private static boolean isRGBSame(IntBitmap bitmap, int x, int y)
	{
		return Math.abs(bitmap.getRed(x, y) - bitmap.getGreen(x, y)) < THRESHOLD && Math.abs(bitmap.getGreen(x, y) - bitmap.getBlue(x, y)) < THRESHOLD;
	}
	//Scans for rows
	public static ArrayList<Point> findItems(BinaryImage top, BinaryImage bot)
	{
		ArrayList<Point> items = addItems(top);
		items.addAll(addItems(bot));
		return items;
	}
	private static ArrayList<Point> addItems(BinaryImage bin)
	{
		ArrayList<Point> items = new ArrayList<Point>();
		boolean[][] data = bin.getData();
		int[] rows = getRows(bin);
		for(int a = 0; a < rows.length; a++)
		{
			if(rows[a]==0) continue;
			items.add(new Point(findLongestSegment(a, data), a));
		}
		return items;
	}
	private static int findLongestSegment(int row, boolean[][] data)
	{
		int locationOfMax = 0;
		int maxStreak = 0;
		int currStreak = 0;
		for(int col = 0; col < data.length; col++) // go across columns
		{
			currStreak ++;
			if(data[col][row] == BinaryImage.BLACK)
			{
				if(currStreak > maxStreak)
				{
					locationOfMax = col - currStreak / 2; //decent approximation: not perfect
					maxStreak = currStreak;
				}
				currStreak = 0;
			}
		}
		return locationOfMax;
	}
	//Finds tops of items
	private static int[] getRows(BinaryImage img)
	{
		boolean[][] data = img.getData();
		int[] rows = new int[img.getHeight()];
		for(int h = ITEM_HEIGHT_BASE; h < ITEM_HEIGHT_BASE + ITEM_HEIGHT_VAR; h++)
		{
			for(int a = 0; a < img.getHeight() - h; a++)
			{
				for(int b = 0; b < img.getWidth(); b++)
				{
					if(data[b][a] == data[b][a+h] && data[b][a] == BinaryImage.WHITE)
					{
						rows[a]++;
					}
				}
			}
		}
		return rows;
	}
	public static ArrayList<Point> findBlocks(IntBitmap img)
	{
		ArrayList<Point> blocks = new ArrayList<Point>();
		blocks.addAll(img.findImages(Library.getBlueSquareIcon()));
		blocks.addAll(img.findImages(Library.getYellowSquareIcon()));
		blocks.addAll(img.findImages(Library.getRedSquareIcon()));
		return blocks;
	}
	public static void removeBlockedItems(ArrayList<Point> items, ArrayList<Point> blocks)
	{
		Iterator<Point> iIter = items.iterator();
		while(iIter.hasNext())
		{
			Point i = iIter.next();
			if(closeBlockExists(i, blocks))
			{
				iIter.remove();
			}
		}
	}
	private static final int X_LIMIT = 100; //max pixels away
	private static final int Y_LIMIT = 10;
	private static boolean closeBlockExists(Point i, ArrayList<Point> blocks)
	{
		for(Point b : blocks)
		{
			int dx = b.x - i.x;
			int dy = b.y - i.y;
			if(dx > 0 && dy > 0 && dx < X_LIMIT && dy < Y_LIMIT) //block will always be below and to the right of thi item
			{
				return true;
			}
		}
		return false;
	}
}
