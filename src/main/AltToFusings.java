package main;

import inventory.CurrencyConverter;
import inventory.InventoryMacro;
import arena.Arena;
import process.AHKBridge;
import process.Quittable;
import macro.HomeNavigator;
import macro.LogoutException;
import macro.WaypointNavigator;
import map.Destination;
import map.GlobalMap;
import window.*;
import data.Config;

/**
 * 
 * Run in NORMAL Sarn!!
 * 
 * @author HONG
 *
 */
public class AltToFusings implements Quittable
{
	private boolean halt = false;
	public static void main(String[] args)
	{
		new AltToFusings().go();
	}
	private void go()
	{
		Config config = new Config();
		AHKBridge.runExitHook(this, config);
		WindowManager winMgr = new WindowManager(config);
		PWindow window = winMgr.getWindows().get(0);
		WindowThread thread = new WindowThread(config, winMgr, null, window);
		try
		{
			GlobalMap home = GlobalMap.findHome(window);
			if(home != GlobalMap.SARN)
			{
				home.moveHero(thread, Destination.WAYPOINT, 10);
				new WaypointNavigator(window).go(Arena.fromString("Sarn Encampment1"), home, thread);;
			}
			HomeNavigator homeNav = new HomeNavigator(thread, GlobalMap.SARN);
			homeNav.goToStore();
			home.openBuyWindow(window);
			CurrencyConverter.run(thread);
		}
		catch(LogoutException e) {}
		catch(HaltThread e) {}
	}
	public void exitProgram()
	{
		halt = true;
	}
}
