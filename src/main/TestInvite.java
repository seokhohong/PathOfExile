package main;

import img.Display;
import img.IntBitmap;

import java.util.ArrayList;

import memo.MemoProcessor;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;
import data.Config;

public class TestInvite 
{
	public static void main(String[] args)
	{
		new TestInvite().go();
	}
	private void go()
	{
		Config config = new Config();
		PWindow window = new WindowManager(config).getWindows().get(0);
		Display.showHang(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT)));
	}
}
