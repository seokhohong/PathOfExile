package party;

import java.awt.Point;

import window.PWindow;

public enum SocialTab 
{
	FRIENDS(100),
	PARTY(160);
	
	private static final int Y_VALUE = 100; //of tabs
	private Point clickPoint;
	
	private SocialTab(int xLoc)
	{
		clickPoint = new Point(xLoc, Y_VALUE);
	}
	
	public void click(PWindow window)
	{
		window.leftClick(clickPoint);
		window.leftClick(clickPoint);
	}
}
