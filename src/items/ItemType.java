package items;

import img.BinaryImage;
import img.IntBitmap;

import java.util.ArrayList;

public enum ItemType 
{
	//inclusive
	NORMAL(0.97d, 1.03d, 0.97d, 1.03d),
	MAGIC(0.97d, 1.03d, 0.52d, 0.57d),
	RARE(0.98d, 1.02d, 2.12d, 2.17d),
	UNIQUE(1.75d, 1.85d, 2.55d, 2.65d),
	CURRENCY(1.05d, 1.08d, 1.21d, 1.24d),
	NOTABLE(0.90d, 1.1d, 0.85d, 0.96d),
	GEM(0.15d, 0.18d, 1.02d, 1.06d);
	
	private double rgLower;
	private double rgUpper;
	private double gbLower;
	private double gbUpper;
	private static final int MIN_VALUE = 10;
	private ItemType(double rgLower, double rgUpper, double gbLower, double gbUpper)
	{
		this.rgLower = rgLower;
		this.rgUpper = rgUpper;
		this.gbLower = gbLower;
		this.gbUpper = gbUpper;
	}
	private boolean matchRating(int[] pixel)
	{
		if(pixel[0] >= MIN_VALUE &&			//pixels like (1, 1, 1) may pass filter, generally just contributing to noise
				pixel[1] >= MIN_VALUE &&
				pixel[2] >= MIN_VALUE
				)
		{
			return (double) pixel[0]/(double) pixel[1] >= rgLower && //pixel within bounds?
			(double) pixel[0]/(double) pixel[1] <= rgUpper &&
			(double) pixel[1]/(double) pixel[2] >= gbLower &&
			(double) pixel[1]/(double) pixel[2] <= gbUpper;
		}
		return false;
	}
	//If it matches any of the item types
	@Deprecated
	public static boolean bestMatch(int[] pixel)
	{
		for(ItemType type : values())
		{
			if(type.matchRating(pixel))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns a BinaryImage with only matching pixels as white.
	 *  
	 * @param img
	 * @return 
	 */
	public BinaryImage filterMatch(IntBitmap img)
	{
		int[][][] imgData = img.getData();
		boolean[][] data = new boolean[img.getWidth()][img.getHeight()];
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				data[a][b] = matchRating(imgData[a][b]); //does it match?
			}
		}
		return new BinaryImage(data);
	}
	public static BinaryImage filterMatch(IntBitmap img, ArrayList<ItemType> itemTypes)
	{
		int[][][] imgData = img.getData();
		boolean[][] data = new boolean[img.getWidth()][img.getHeight()];
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				data[a][b] = false;
				for(ItemType t : itemTypes)
				{
					data[a][b] |= t.matchRating(imgData[a][b]); //does it match?
				}
			}
		}
		return new BinaryImage(data);
	}
}
