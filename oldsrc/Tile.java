package map;

import java.awt.Color;

public enum Tile 
{
	UNWRITTEN(Color.gray, false),	//Initialized default
	PATHABLE(Color.white, true),
	UNPATHABLE(Color.black, false),
	MARKER(Color.cyan, true),
	BRIDGE_PORTAL(Color.red, true);
	private Color color;			public Color getColor() { return color; }
	private boolean isPathable; 	public boolean isPathable() { return isPathable; }
	private Tile(Color color, boolean isPathable)
	{
		this.color = color;
		this.isPathable = isPathable;
	}
}
