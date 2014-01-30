package inventory;

import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;
import img.RatioFilter;

public enum InventoryItemType 
{
	CURRENCY(60)
	{
		@Override
		public boolean matchesType(IntBitmap image)
		{
			RatioFilter.maintainRatio(image, FilterType.CURRENCY_TEXT);
			MidpassFilter.maintainRanges(image, MidpassFilterType.CURRENCY_TEXT);
			BinaryImage result = image.toGreyscale().doubleCutoff(10);
			return exceedsThreshold(CURRENCY, result);
		}
	},
	MAGIC(30)
	{
		@Override
		public boolean matchesType(IntBitmap image)
		{
			MidpassFilter.maintainRanges(image, MidpassFilterType.MAGIC_BOX);
			BinaryImage result = image.toGreyscale().doubleCutoff(10);
			return exceedsThreshold(MAGIC, result);
		}
	},
	RARE(90)
	{
		@Override
		public boolean matchesType(IntBitmap image)
		{
			MidpassFilter.maintainRanges(image, MidpassFilterType.RARE_TEXT);
			BinaryImage result = image.toGreyscale().doubleCutoff(10);
			result.killLoners(0, true);
			return exceedsThreshold(RARE, result);
		}
	},
	GEM(90)
	{
		@Override
		public boolean matchesType(IntBitmap image)
		{
			MidpassFilter.maintainRanges(image, MidpassFilterType.TEAL_TEXT);
			BinaryImage result = image.toGreyscale().doubleCutoff(10);
			return exceedsThreshold(GEM, result);
		}
	},
	UNIQUE(90)
	{
		@Override
		public boolean matchesType(IntBitmap image)
		{
			RatioFilter.maintainRatio(image, FilterType.ORANGE_TEXT);
			BinaryImage result = image.toGreyscale().doubleCutoff(10);
			return exceedsThreshold(UNIQUE, result);
		}
	},
	GARBAGE(90)
	{
		@Override
		public boolean matchesType(IntBitmap image)
		{
			//RatioFilter.maintainRatio(image, FilterType.GARBAGE_TEXT);
			MidpassFilter.maintainRanges(image, MidpassFilterType.GARBAGE_BOX);
			BinaryImage result = image.toGreyscale().doubleCutoff(10);
			return exceedsThreshold(GARBAGE, result);
		}
	};
	
	private InventoryItemType(int threshold)
	{
		whiteThreshold = threshold;
	}
	
	private int whiteThreshold;
	public abstract boolean matchesType(IntBitmap image);
	
	private static boolean exceedsThreshold(InventoryItemType type, BinaryImage binary)
	{
		return binary.countWhite() >= type.whiteThreshold;
	}
}
