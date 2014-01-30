package main;

import img.ImageLibrary;
import img.IntBitmap;
import macro.Timer;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;

public class TestDestroyItem 
{
	public static void main(String[] TestDestroyItem)
	{
		new TestDestroyItem().go();
	}
	private void go()
	{
		WindowManager winMgr = new WindowManager();
		PWindow window = winMgr.getWindows().get(0);
		System.out.println(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.DESTROY_ITEM_RECT)).matchError(ImageLibrary.DESTROY_ITEM.get()));
	}
}
