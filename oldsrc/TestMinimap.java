package main;

import window.PWindow;
import macro.Macro;

import arena.Arena;

import img.ImageToolkit;
import img.IntBitmap;

public class TestMinimap 
{
	public static void main(String[] args)
	{
		new TestMinimap().go();
	}
	private void go()
	{
		Macro.macro.sleep(3000);
		PWindow window = PWindow.getWindows(IntBitmap.getInstance(ImageToolkit.takeScreenshot())).get(0);
		int a = 0; 
		while(true)
		{
			System.out.println(a);
			a++;
			IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(window.getMAP_RECT()));
			Arena arena = Arena.fromString("The Forest1");
			minimap.export("map/"+a+"Raw.bmp");
			arena.processMap(minimap).export("map/"+a+"Map.bmp");
			Macro.macro.sleep(1000);
		}
	}
}
