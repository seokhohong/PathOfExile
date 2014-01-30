package inventory;

import img.IntBitmap;

import java.awt.Color;
import java.awt.Rectangle;

public class ItemIdentifier 
{
	private static final double ERROR_THRESHOLD = 2d;
	public static InventoryItem matchItem(IntBitmap icon, SizeExtension ext)
	{
		InventoryItem bestMatch = null;
		double minError = Double.MAX_VALUE;
		for(InventoryItem item : InventoryItem.values())
		{
			if(item.getIcon(ext) != null)
			{
				double error = matchError(icon, item.getIcon(ext));
				//System.out.println(item + ": " + error);
				if(error < minError)
				{
					bestMatch = item;
					minError = error;
				}
			}
		}
		if(minError < ERROR_THRESHOLD)
		{
			return bestMatch;
		}

		return null;
	}
	public static double matchError(IntBitmap img1, IntBitmap img2)
	{
		img1.blackRectangle(new Rectangle(0, 0, 13, 10));
		img2.blackRectangle(new Rectangle(0, 0, 13, 10));
		
		double r1 = img1.averageColor(Color.red);
		double g1 = img1.averageColor(Color.green);
		double b1 = img1.averageColor(Color.blue);
		
		double r2 = img2.averageColor(Color.red);
		double g2 = img2.averageColor(Color.green);
		double b2 = img2.averageColor(Color.blue);
		
		double dr = Math.abs(r1 - r2);
		double dg = Math.abs(g1 - g2);
		double db = Math.abs(b1 - b2);

		return dr*dr + dg*dg + db*db;
	}
}
