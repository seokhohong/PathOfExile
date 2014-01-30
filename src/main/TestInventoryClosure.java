package main;

import data.Config;
import window.PWindow;
import window.WindowManager;

public class TestInventoryClosure 
{
	public static void main(String[] args)
	{
		new TestInventoryClosure().go();
	}
	private void go()
	{
		Config config = new Config();
		PWindow window = new WindowManager(config).getWindows().get(0);
		System.out.println(window.inventoryVisible());
	}
}
