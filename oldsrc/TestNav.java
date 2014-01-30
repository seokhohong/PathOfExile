package main;

import java.awt.Point;

import img.ImageLibrary;
import img.IntBitmap;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;

public class TestNav 
{
	public static void main(String[] args)
	{
		new TestNav().go();
	}
	private void go()
	{
		PWindow window = new WindowManager().getWindows().get(0);
		IntBitmap nav = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.NAV_RECT));
		Point p = nav.findImage(ImageLibrary.NORMAL_UNHIGHLIGHT.get(), 10);
		System.out.println(p);
	}
}
