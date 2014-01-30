package macro;

import img.ImageLibrary;
import img.IntBitmap;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import data.Config;
import process.AHKBridge;
import window.PWindow;
import img.*;

/**
 * 
 * Collection of GUI level macros
 * 
 * @author Seokho
 *
 */
public class PoEMacro 
{
	private static Robot robot;
    
    static
    {
    	try
    	{
    		robot = new Robot();
    	}
    	catch(AWTException e) {}
    }
	
	//Opens options menu (Used for shifting map)
	public static void openOptions(Macro macro) { macro.type('o'); }
	public static void closeOptions(Macro macro) { macro.type('o'); }
	//walks dist distance at angle angle
	//Angle is in degrees
	public static void moveHero(PWindow window, double angle, int dist)
	{
		window.leftClick(rectFromPolar(angle, dist));
	}
	public static void moveHeroCarefully(PWindow window, double angle, int dist)
	{
		Point movePoint = rectFromPolar(angle, dist);
		window.mouseMove(movePoint);
		Macro.sleep(30);
		window.leftClickCarefully(movePoint);
	}
	//Clicks on every instance of the level up icon
	private static final int GEM_OFFSET_X = 38;
	private static final int GEM_OFFSET_Y = 0;
	public static void levelUpGems(PWindow window)
	{
		IntBitmap gemRegion = IntBitmap.getInstance(window.takeScreenshot()); //can reduce if necessary
		ArrayList<Point> levelUpPoints = gemRegion.findImages(ImageLibrary.LEVEL_UP_GEM.get());
		for(int a = levelUpPoints.size() - 1; a >= 0; a --)
		{
			window.leftClickCarefully(new Point(levelUpPoints.get(a).x + GEM_OFFSET_X, levelUpPoints.get(a).y + GEM_OFFSET_Y));
		}
	}
	public static Point rectFromPolar(double angle, int dist)
	{
		int centerX = PWindow.getWidth() / 2;
		int centerY = PWindow.getHeight() / 2;
		int dx = (int) (dist * Math.cos(angle));
		int dy = (int) (dist * Math.sin(angle));
		return new Point(centerX + dx, centerY - dy); //minus because of centralized polar to screen coordinate changes
	}
	//Not really a "Macro" action
	public static void openPoE(Config config)
	{
		AHKBridge.runScript("ahk\\RunPoE"+config.getBitArchitecture()+".exe");
	}
	//Move to InventoryMacro
	public static void slotToStash(PWindow window, Point p)
	{
		robot.keyPress(KeyEvent.VK_CONTROL);
		window.clickItem(p.x, p.y);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		Macro.sleep(20);
	}
	private static final int AURA_ON_THRESHOLD = 8; 
	public static boolean auraOn(PWindow window, Hotkey key)
	{
		IntBitmap img = IntBitmap.getInstance(window.takeScreenshot(key.getRegion()));
		//Display.showHang(img);
		MidpassFilter.maintainRanges(img, MidpassFilterType.AURA_ON);
		BinaryImage bin = img.toGreyscale().doubleCutoff(40);
		//System.out.println(bin.countWhite());
		//Display.showHang(bin);
		return bin.countWhite() > AURA_ON_THRESHOLD;	
	}
}