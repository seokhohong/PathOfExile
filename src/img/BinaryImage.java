package img;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * An image of booleans, storing either white or black
 * 
 * Class contains generic operations on binary images
 * 
 * @author Seokho
 *
 */
public class BinaryImage
{
	public static final boolean WHITE = true;
	public static final boolean BLACK = false;
	//Limit exposure?
	private boolean[][] data;		public boolean[][] getData() { return data; }		
									void setData(boolean[][] data) { this.data = data; }
	private int imgWidth;			public int getWidth() { return imgWidth; }
	private int imgHeight;			public int getHeight() { return imgHeight; }
	public BinaryImage(boolean[][] data)
	{
		this.data = data;
		imgWidth = data.length;
		imgHeight = data[0].length;
	}
	public void invert()
	{
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				data[a][b] = !data[a][b];
			}
		}
	}
	public GreyscaleImage toGreyscale()
	{
		int[][] newData = new int[imgWidth][imgHeight];
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				newData[a][b] = data[a][b] ? ImageToolkit.MAX_VAL : 0;
			}
		}
		return new GreyscaleImage(newData);
	}
	public void add(BinaryImage otherImg)
	{
		assert(imgWidth == otherImg.imgWidth && imgHeight == otherImg.imgHeight);
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0 ; b < imgHeight; b++)
			{
				data[a][b] |= otherImg.data[a][b];
			}
		}
	}
	/**
	 * 
	 * If a given pixel has numNeighbors or more black pixels, it will be filled as black
	 * 
	 * @param numNeighbors	: number of black neighbors required
	 */
	public void fillCrowded(boolean color, int numNeighbors)
	{
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0 ; b < imgHeight; b++)
			{
				if(numNeighbors(a, b, color) >= numNeighbors)
				{
					data[a][b] = color;
				}
			}
		}
	}
	/**
	 * Fills in white pixels with black, starting the spread at x, y
	 * Breadth-first implementation (Recursive one blows up the stack)
	 * 
	 * @param img
	 * @param x
	 * @param y
	 * @return	: Number of pixels filled
	 */
	public int fillBlack(int x, int y)
	{
		Queue<Point> whites = new LinkedList<Point>();
		int numFilled = 0;
		if(x < imgWidth - 1 && y < imgHeight - 1)
		{
			whites.add(new Point(x + 1, y + 1));
		}
		while(!whites.isEmpty())
		{
			Point white = whites.poll();
			addNeighbors(white, whites);
			numFilled ++ ;
		}
		return numFilled;
	}
	private void addNeighbors(Point p, Queue<Point> points)
	{
		if(p.x > 0 && data[p.x - 1][p.y]) 
		{
			points.add(new Point(p.x - 1, p.y));
			data[p.x - 1][p.y] = BinaryImage.BLACK;
		}
		if(p.y > 0 && data[p.x][p.y - 1])
		{
			points.add(new Point(p.x, p.y - 1));
			data[p.x][p.y - 1] = BinaryImage.BLACK;
		}
		if(p.x < data.length - 1 && data[p.x + 1][p.y])
		{
			points.add(new Point(p.x + 1, p.y));
			data[p.x + 1][p.y] = BinaryImage.BLACK;
		}
		if(p.y < data[0].length - 1 && data[p.x][p.y + 1])
		{
			points.add(new Point(p.x, p.y + 1));
			data[p.x][p.y + 1] = BinaryImage.BLACK;
		}
	}
	/**
	 * 
	 * Reconstructs pixels of a given color
	 * Requires at least numNeighbors neighbors of the opposite color to reconstruct 
	 * 
	 * @param numNeighbors
	 * @param color
	 */
	public void reconstructGaps(int numNeighbors, boolean color)
	{
		for(int a = 1; a < imgWidth -1; a ++)
		{
			for(int b = 1; b < imgHeight-1; b ++)
			{
				if(numNeighbors(a, b, color) >= numNeighbors)
				{
					data[a][b] = color;
				}
			}
		}	
	}
	/**
	 * 
	 * A pixel can have up to numNeighbors neighbors before pixel is changed
	 * Kills neighbors of the specified color
	 * 
	 * @param numNeighbors
	 */
	public void killLoners(int numNeighbors, boolean color)
	{
		for(int a = 1; a < imgWidth -1; a ++)
		{
			for(int b = 1; b < imgHeight-1; b ++)
			{
				if(numNeighbors(a, b, color) <= numNeighbors)
				{
					data[a][b] = !color;
				}
			}
		}	
	}
	public void fillBlackHoles()
	{
		for(int a = 1; a < imgWidth -1; a ++)
		{
			for(int b = 1; b < imgHeight-1; b ++)
			{
				if(data[a][b] == BinaryImage.BLACK && numNeighbors(a, b, true) == 8)
				{
					data[a][b] = BinaryImage.WHITE;
				}
			}
		}		
	}
	
	public void fillRow(int r)
	{
		for(int i=0; i < imgWidth; i++)
		{
			data[i][r] = BinaryImage.WHITE;
		}
	}
	public void fillColumn(int r)
	{
		for(int i=0; i < imgHeight; i++)
		{
			data[r][i] = BinaryImage.WHITE;
		}
	}
	
	public int countWhite()
	{
		int numWhite = 0;
		for(int a = 0; a < imgWidth; a ++)
		{
			for(int b = 0; b < imgHeight; b ++)
			{
				if(data[a][b] == BinaryImage.WHITE)
				{
					numWhite ++;
				}
			}
		}
		return numWhite;
	}
	public BinaryImage shrink(int factor)
	{
		boolean[][] shrunk = new boolean[imgWidth / factor][imgHeight / factor];
		for(int a = 0; a < imgWidth / factor ; a ++)
		{
			for(int b = 0; b < imgHeight / factor ; b ++)
			{
				int val = 0;
				for(int c = 0; c < factor ; c ++)
				{
					for(int d = 0; d < factor ; d++)
					{
						val += data[a * factor + c][b * factor + d] ? 1 : 0;
					}
				}
				shrunk[a][b] = val > (factor * factor / 2) ? BinaryImage.WHITE : BinaryImage.BLACK;
			}
		}
		return new BinaryImage(shrunk);
	}
	public BinaryImage expand(int factor)
	{
		boolean[][] expanded = new boolean[imgWidth * factor][imgHeight * factor];
		for(int a = 0; a < imgWidth; a ++)
		{
			for(int b = 0; b < imgHeight; b ++)
			{
				for(int c = 0; c < factor ; c ++)
				{
					for(int d = 0; d < factor ; d++)
					{
						expanded[a * factor + c][b * factor + d] = data[a][b];
					}
				}
			}
		}
		return new BinaryImage(expanded);
	}
	private int numNeighbors(int x, int y, boolean color)
	{
		int numNeighbors = 0;
		if(x > 0 && y > 0 && x < data.length - 1 && y < data[0].length - 1)
		{
			if(data[x-1][y-1] == color) numNeighbors ++ ;
			if(data[x][y-1] == color) numNeighbors ++ ;
			if(data[x+1][y-1] == color) numNeighbors ++ ;		
			if(data[x-1][y] == color) numNeighbors ++ ;
			if(data[x+1][y] == color) numNeighbors ++ ;
			if(data[x-1][y+1] == color) numNeighbors ++ ;
			if(data[x][y+1] == color) numNeighbors ++ ;
			if(data[x+1][y+1] == color) numNeighbors ++ ;
		}
		return numNeighbors;
	}
	/**
	 * 
	 * Makes a white box in the center of the image (used for eliminating the character)
	 * Does no bounds checking
	 * 
	 */
	public void clearCenter(int radius, boolean color)
	{
		for(int a = -radius; a < radius; a++)
		{
			for(int b = -radius; b < radius; b++)
			{
				data[a + imgWidth / 2][b + imgHeight / 2] = color;
			}
		}
	}
	public BinaryImage subimage(Rectangle rect)
	{
		boolean[][] newData = new boolean[rect.width][rect.height];
		for(int a = 0; a < rect.width && a + rect.x < data.length; a++)
		{
			for(int b = 0; b < rect.height && b + rect.y < data[0].length; b++)
			{
				newData[a][b] = data[a + rect.x][b + rect.y];
			}
		}
		return new BinaryImage(newData);
	}
	public void export(String filename)
	{
		BufferedImage toExport = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = toExport.createGraphics();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				int color = data[a][b] ? 255 :  0;
				g.setColor(new Color(color, color, color));
				g.drawLine(a, b, a, b);
			}
		}
		ImageToolkit.exportImage(toExport, filename);
	}
	public boolean isAllBlack()
	{
		for(boolean[] row : data)
		{
			for(boolean val : row)
			{
				if(val == BinaryImage.WHITE)
				{
					return false;
				}
			}
		}
		return true;
	}
}
