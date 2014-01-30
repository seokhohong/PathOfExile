package map;

public enum Label 
{
	NESSA(24, 25, 7),
	GREUST(32, 34, 10),
	CLARISSA(36, 38, 15),
	STASH(23, 29, 7),
	WAYPOINT(43, 46, 10);
	
	private int min; 	//widths
	private int max;
	
	private int closeEnough;		public int closeEnough() { return closeEnough; }
	
	private Label(int min, int max, int closeEnough)
	{
		this.min = min;
		this.max = max;
		this.closeEnough = closeEnough;
	}
	
	public boolean withinRange(int width)
	{
		return width >= min && width <= max;
	}
}
