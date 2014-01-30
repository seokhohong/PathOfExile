package main;

import arena.Arena;
import macro.LogoutException;
import macro.WaypointNavigator;
import window.PWindow;
import window.WindowManager;
import data.Config;

public class TestWorldNav 
{
	public static void main(String[] args)
	{
		new TestWorldNav().go();
	}
	private void go()
	{
		Config config = new Config();
		PWindow window = new WindowManager(config).getWindows().get(0);
		WaypointNavigator wpNav = new WaypointNavigator(window);
		try {
			wpNav.clickOnMap(Arena.fromString("The Forest3"));
		} catch (LogoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
