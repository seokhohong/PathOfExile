package inventory;

import img.BinaryImage;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;

import java.awt.Point;
import java.awt.Rectangle;

import macro.Macro;
import macro.Timer;
import window.HaltThread;
import window.PWindow;
import window.WindowThread;

public class CurrencyConverter 
{
	public static void run(WindowThread thread) throws HaltThread
	{
		PWindow window = thread.getWindow();
		for(Item item : Item.values())
		{
			Timer maxTimer = new Timer(120000);
			while(item.buyable(window) && maxTimer.stillWaiting())
			{
				Timer buyTime = new Timer(5000);
				while(true)
				{
					if(buyTime.hasExpired())
					{
						break;
					}
					thread.checkHalt();
					window.pressControl();
					item.click(window);
					Macro.sleep(100);
					window.releaseControl();
				}
				//Refresh buyable label
				window.mouseMove(PWindow.getWindowCenter());
				Macro.sleep(500);
			}
		}
	}
}
//Keep them prioritized in the correct order
enum Item
{
	//Location on the screen
	ALTERATION(new Point(225, 300)),
	JEWELLER(new Point(250, 140)),
	FUSING(new Point(220, 440));
	
	
	private Point sarn1Location;
	
	private Item(Point sarn1Location)
	{
		this.sarn1Location = sarn1Location;
	}
	
	private Rectangle infoRectangle()
	{
		int x = sarn1Location.x - 50;
		int y = sarn1Location.y - 50;
		int width = 100;
		int height = 30;
		return new Rectangle(x, y, width, height);
	}
	
	private static final int RED_TEXT_THRESHOLD = 100;
	boolean buyable(PWindow window)
	{
		window.mouseMove(sarn1Location);
		Macro.sleep(50);
		IntBitmap info = IntBitmap.getInstance(window.takeScreenshot(infoRectangle()));
		MidpassFilter.maintainRanges(info, MidpassFilterType.CANNOT_BUY);
		BinaryImage bin = info.toGreyscale().doubleCutoff(50);
		return bin.countWhite() < RED_TEXT_THRESHOLD;
	}
	
	void click(PWindow window)
	{
		window.leftClick(sarn1Location);
	}
	
}