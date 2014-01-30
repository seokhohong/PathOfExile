package main;

import img.BinaryImage;
import img.BleedResult;
import img.Bleeder;
import img.Display;
import img.ImageToolkit;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;
import inventory.Inventory;

import java.util.ArrayList;

import macro.LogoutException;
import map.GlobalMap;
import process.AHKBridge;
import process.Quittable;
import window.HaltThread;
import window.PWindow;
import window.WindowManager;
import window.WindowThread;
import data.Config;

public class TestInventory
{
	public static void main(String[] teehee)
	{
		new TestInventory().go();
	}
	private void go()
	{
		Config config = new Config();
		WindowManager winmgr = new WindowManager(config);
		PWindow window = winmgr.getWindows().get(0);
		window.select();
		WindowThread thread = new WindowThread(config, winmgr, null, window);
		try {
			new Inventory(thread, GlobalMap.FOREST_CAMP).buildSpeedy(0.99);		} catch (HaltThread e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LogoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
