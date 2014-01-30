package main;

import window.PWindow;
import data.Profile;

import macro.Macro;
import macro.Waiter;
import arena.Arena;
import arena.TheForest;

import img.BinaryImage;
import img.ImageToolkit;
import img.IntBitmap;

public class TestNavigation 
{
	public static void main(String[] args)
	{
		new TestNavigation().go();
	}
	private void go()
	{
		Macro.macro.sleep(2000);
		PWindow window = PWindow.getWindows(IntBitmap.getInstance(ImageToolkit.takeScreenshot())).get(0);
		int a = 0; 
		while(true)
		{
			//Use an ExitHook
			System.out.println(a);
			a++;
			TheForest arena = (TheForest) Arena.fromString("The Forest1");
			IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(window.getMAP_RECT()));
			BinaryImage processed = arena.processMap(minimap);
			double mvtAngle = arena.bestMovementAngle(processed);
			processed.export("map\\Nav"+a+".bmp");
			Profile.getProfiles().get(0).getCombatStyle().move(window, mvtAngle, 250);
			Waiter moveTime = new Waiter(1500); //make sure movement is done by combat time
			
			moveTime.waitFully();
		}
	}
}
