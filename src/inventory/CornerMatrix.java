package inventory;

import java.awt.Point;
import java.util.ArrayList;

public class CornerMatrix 
{
	private ArrayList<Integer> rows = new ArrayList<Integer>();
	private ArrayList<Integer> columns = new ArrayList<Integer>();
		
	private Point[][] topLeftCorners;			public Point getTLCorner(int x, int y) { return topLeftCorners[x][y]; }
	private Point[][] bottomRightCorners;		public Point getBRCorner(int x, int y) { return bottomRightCorners[x][y]; }
	
	CornerMatrix()
	{
		presetRowsAndColumns();
		topLeftCorners = findTopLeftCorners();
		bottomRightCorners = findBottomRightCorners();
	}
	CornerMatrix(ItemGrid grid)
	{
		presetRowsAndColumns(grid.getColumnData(), grid.getRowData());
		topLeftCorners = findTopLeftCorners();
		bottomRightCorners = findBottomRightCorners();
	}
	
	/**
	 * 
	 * Later will reflect whether the image analysis was properly done
	 * 
	 * @return
	 */
	public boolean properlyBuilt()
	{
		return columns.size() == 2 * Inventory.INVENTORY_WIDTH && rows.size() == 2 * Inventory.INVENTORY_HEIGHT;
	}
	public void presetRowsAndColumns(int[] columnData, int[] rowData)
	{
		for(int r : rowData)
		{
			rows.add(r);
		}
		for(int c : columnData)
		{
			columns.add(c);
		}
	}
	private void presetRowsAndColumns()
	{
		//WTF?! -- far more confusing than crop circles or stonehenge
		int[] columnNums = { 0, 28, 29, 57, 58, 86, 87, 116, 117, 145, 146, 174, 175, 203, 204, 233, 234, 262, 263, 291, 292, 320, 321, 350 };
		int[] rowNums = { 0, 28, 29, 58, 59, 87, 88, 116, 117, 145 };
		for(int c : columnNums)
		{
			columns.add(c);
		}
		for(int r : rowNums)
		{
			rows.add(r);
		}
	}
	
	private Point[][] findTopLeftCorners()
	{	
		Point[][] corners = new Point[Inventory.INVENTORY_WIDTH][Inventory.INVENTORY_HEIGHT];
		for(int a=0; a < Inventory.INVENTORY_WIDTH ; a++)
		{
			for(int b=0; b < Inventory.INVENTORY_HEIGHT ; b++)
			{
				//System.out.println(new Point(a,b));
				corners[a][b] = new Point(columns.get(2*a) + 1, rows.get(2*b) + 1);
			}
		}
		return corners;
	}
	
	private Point[][] findBottomRightCorners()
	{
		Point[][] corners = new Point[Inventory.INVENTORY_WIDTH][Inventory.INVENTORY_HEIGHT];
		for(int a = 0; a < Inventory.INVENTORY_WIDTH ; a++)
		{
			for(int b = 0; b < Inventory.INVENTORY_HEIGHT ; b++)
			{
				corners[a][b] = new Point(columns.get(2*a + 1) - 1, rows.get(2*b + 1) - 1);
			}
		}
		return corners;
	}
}
