package inventory;

import java.awt.Point;
import java.util.Arrays;

public class SquareStateMatrix 
{
	private SquareState[][] grid;
	
	SquareStateMatrix(ItemGrid itemGrid)
	{
		grid = new SquareState[itemGrid.getWidth()][itemGrid.getHeight()];
		for(SquareState[] arr : grid)
		{
			Arrays.fill(arr, SquareState.UNOCCUPIED);
		}
	}
	public void setSlot(Point p, SquareState state)
	{
		grid[p.x][p.y] = state;
	}
	private Point getTopLeft()
	{
		for(int a = 0; a < grid.length; a++)
		{
			for(int b = 0; b < grid[0].length; b++)
			{
				if(grid[a][b] == SquareState.OCCUPIED)
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
				if(grid[a][b] == SquareState.OCCUPIED)
				{
					return new Point(a, b);
				}
			}
		}
		return new Point(0, 0);
	}
}
