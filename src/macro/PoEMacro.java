package macro;

import main.PWindow;

//High level macros
public class PoEMacro 
{
	//Opens options menu (Used for shifting map)
	public static void openOptions() { Macro.type('o'); }
	public static void closeOptions() { Macro.type('o'); }
	private static final int WAIT_FOR_CLICK = 100;
	//walks dist distance at angle angle
	public static void moveHero(PWindow window, int angle, int dist)
	{
		int centerX = window.getWidth() / 2;
		int centerY = window.getHeight() / 2;
		int dx = (int) (dist * Math.cos(Math.toRadians(angle)));
		int dy = (int) (dist * Math.sin(Math.toRadians(angle)));
		//System.out.println("Clicking "+(centerX+dx) + ", "+(centerY + dy));
		window.click(centerX+dx, centerY+dy);
		sleep(WAIT_FOR_CLICK);
	}
	private static void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch(InterruptedException e) {}
	}
}