package main;

import img.BinaryImage;
import img.FilterType;
import img.GreyscaleImage;
import img.IntBitmap;
import img.RatioFilter;
import items.Item;
import items.ItemFinder;
import items.ItemType;

import java.util.ArrayList;

import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import process.AHKBridge;
import process.Quittable;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;
import arena.Arena;

import combat.HealthBarFinder;

public class TestHillock implements Quittable
{
	boolean halt = false;
	public static void main(String[] args)
	{
		new TestHillock().go();
	}
	private void go()
	{
		AHKBridge.runExitHook(this);
		WindowManager winMgr = new WindowManager();
		Timer speed = new Timer(0);
		int numQuick = 0;
		PWindow window = winMgr.getWindows().get(0);

		while(!halt && !Arena.finishedMap(window))
		{
			if(speed.hasExpired())
			{
				Macro.macro.type(window, Integer.toString(numQuick / 2 + 1) );
				numQuick ++ ;
				speed.reset(4500);
			}
			IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
			//IntBitmap minimap = IntBitmap.getInstance(ImageToolkit.loadImage("img/minimap.bmp"));
			RatioFilter.maintainRatio(minimap, FilterType.WATER);
			GreyscaleImage grey = minimap.toGreyscale();
			grey.blur(4);
			grey = grey.doubleCutoff(10).toGreyscale();
			grey.blur(10);
			
			PoEMacro.moveHero(window, polarSweep(grey.doubleCutoff(254)), 150);
			Macro.macro.sleep(200);
		}
		//PoEMacro.castTotem(window);
		Timer combat = new Timer(20000);
		while(combat.stillWaiting())
		{
			
			HealthBarFinder hpFinder = new HealthBarFinder(window);
			if(hpFinder.getHealthBars().size() != 0)
			{
				System.out.println("FoundHpBar");
				break;
			}
			Macro.macro.sleep(1000);
		}
		Timer fighting = new Timer(20000);
		while(fighting.stillWaiting())
		{
			ArrayList<ItemType> types = new ArrayList<ItemType>();
			types.add(ItemType.MAGIC);
			types.add(ItemType.RARE);
			types.add(ItemType.UNIQUE);
			ArrayList<Item> items = ItemFinder.findItems(IntBitmap.getInstance(window.takeScreenshot()), types);
			if(items.size() >= 3)
			{
				System.out.println("Found Items");
				break;
			}
			HealthBarFinder hpFinder = new HealthBarFinder(window);
			if(hpFinder.getHealthBars().size() != 0)
			{
				window.rightClick(hpFinder.getHealthBars().get(0));
			}
			Macro.macro.sleep(1000);
		}
		window.logout();
	}
	private double polarSweep(BinaryImage map)
	{
		//Display.showHang(map);
		boolean[][] data = map.getData();
		for(double ang = Math.PI; ang > -Math.PI / 4; ang -= Math.PI / 8)
		{
			for(int rad = 0; rad < map.getWidth() / 2; rad++)
			{
				int pixX = (int) (Math.cos(ang) * rad) + data.length / 2;
				int pixY = (int) (-Math.sin(ang) * rad) + data.length / 2;
				if(rad < 30 && data[pixX][pixY] == BinaryImage.WHITE)
				{
					ang += Math.PI / 4d;
					//System.out.println(ang);
					return ang;
				}
			}
		}
		
		return 0d;
	}
	@Override
	public void exitProgram() 
	{
		halt = true;
	}
}
