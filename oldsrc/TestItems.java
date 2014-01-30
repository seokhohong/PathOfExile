package main;

import geom.MathTools;
import img.IntBitmap;
import items.Item;
import items.ItemFinder;
import items.ItemType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import macro.Macro;
import macro.Timer;
import combat.CombatStyle;
import combat.HealthBarFinder;
import combat.SpellInstance;
import window.PWindow;
import window.WindowManager;

public class TestItems 
{
	public static void main(String[] dominique_is_hot)
	{
		new TestItems().go();
	}
	private void go()
	{		
		//Doesn't want to work

		WindowManager winMgr = new WindowManager();
		PWindow window = winMgr.getWindows().get(0);
		CombatStyle person = CombatStyle.fromString("RANGER", new ArrayList<SpellInstance>());
		
		ArrayList<ItemType> types = new ArrayList<ItemType>();
		types.add(ItemType.MAGIC);
		types.add(ItemType.RARE);
		types.add(ItemType.UNIQUE);
		
		Timer itemAttemptTimer = new Timer(5000);
		while(itemAttemptTimer.stillWaiting()) //while not taking too long . . .
		{
			IntBitmap itemScreen = IntBitmap.getInstance(window.takeScreenshot());
			ArrayList<Item> items = ItemFinder.findItems(itemScreen, types);
			
			Collections.sort(items);
			if(items.isEmpty())
			{
				break;
			}
			double distToItem = items.get(0).toPoint().distance(new Point(PWindow.getWindowCenter()));	//how far away the item is
			window.leftClick(items.get(0).getX() + items.get(0).getWidth() / 4, items.get(0).getY() + 4); //don't click on the top left corner!
			Timer walkTime = new Timer((int) (distToItem * 1.6d) + 200);
			while(walkTime.stillWaiting())
			{
				HealthBarFinder hpBars = new HealthBarFinder(window);
				if(items.size() == 0 || !hpBars.isSafe()) 
				{
					break;
				}
				//heal(window);
				Macro.macro.sleep(100); //some breathing room here
			}
		}
		System.out.println("Done");
	}
}
