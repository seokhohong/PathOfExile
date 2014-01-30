package inventory;

import img.MidpassFilterType;

public enum HighlightColor 
{
	GREEN(MidpassFilterType.INVENTORY_GREEN, 30),
	BLUE(MidpassFilterType.INVENTORY_BLUE, 30),
	RED(MidpassFilterType.INVENTORY_RED, 30);
	
	private MidpassFilterType midFilter;		public MidpassFilterType getFilter() { return midFilter; }
	
	private int matchThreshold;					public int getMatchThreshold() { return matchThreshold; }
	
	private HighlightColor(MidpassFilterType midFilter, int matchThreshold)
	{
		this.midFilter = midFilter;
		this.matchThreshold = matchThreshold;
	}
}
