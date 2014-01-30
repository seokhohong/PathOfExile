package inventory;

import img.BinaryImage;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashSet;

public class InventoryGrid 
{
	public static final boolean OCCUPIED = true;
	public static final boolean UNOCCUPIED = false;
	//Whether a particular inventory grid slot is occupied
	private boolean[][] grid;
	
	InventoryGrid()
	{
		grid = new boolean[Inventory.INVENTORY_WIDTH][Inventory.INVENTORY_HEIGHT];
		for(boolean[] arr : grid)
		{
			Arrays.fill(arr, UNOCCUPIED);
		}
	}
	public void setSlot(Point p, boolean state)
	{
		grid[p.x][p.y] = state;
	}
	private Point getTopLeft()
	{
		for(int a = 0; a < grid.length; a++)
		{
			for(int b = 0; b < grid[0].length; b++)
			{
				if(grid[a][b] == OCCUPIED)
				{
					return new Point(a, b);
				}
			}
		}
		return new Point(0, 0);
	}
	private Point getBottomRight()
	{
		for(int a = grid.length - 1; a >= 0; a--)
		{
			for(int b = grid[0].length - 1; b >= 0; b--)
			{
				if(grid[a][b] == OCCUPIED)
				{
					return new Point(a, b);
				}
			}
		}
		return new Point(0, 0);
	}
	public HashSet<Point> getWhites()
	{
		HashSet<Point> points = new HashSet<Point>();
		for(int a = 0; a < grid.length; a++)
		{
			for(int b = 0; b < grid[0].length; b++)
			{
				if(grid[a][b] == OCCUPIED)
				{
					points.add(new Point(a, b));
				}
			}
		}
		return points;
	}
	public static InventoryGrid add(InventoryGrid g1, InventoryGrid g2)
	{
		InventoryGrid result = new InventoryGrid();
		g1.getWhites().addAll(g2.getWhites());
		for(Point p : g1.getWhites())
		{
			result.setSlot(p, true);
		}
		return result;
	}
	
	public static InventoryGrid getColorGrid(InventorySquare[][] squares, HighlightColor hc)
	{
		InventoryGrid grid = new InventoryGrid();
		for(int a = 0; a < Inventory.INVENTORY_WIDTH; a++)
		{
			for(int b = 0; b < Inventory.INVENTORY_HEIGHT; b++)
			{
				if(squares[a][b].isColor(hc))
				{
					grid.setSlot(new Point(a, b), InventoryGrid.OCCUPIED);
				}
			}
		} 
		return grid;
	}

	
	public Rectangle getRectangle()
	{
		return new Rectangle(getTopLeft().x, getTopLeft().y, getBottomRight().x - getTopLeft().x + 1, getBottomRight().y - getTopLeft().y + 1);
	}
	public BinaryImage toImage()
	{
		return new BinaryImage(grid);
	}
	public boolean isEmpty()
	{
		return toImage().isAllBlack();
	}
}
