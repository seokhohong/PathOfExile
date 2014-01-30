package main;

import process.AHKBridge;
import process.Quittable;
import data.Config;
import window.HaltThread;
import window.WindowManager;
import window.WindowThread;
import macro.HomeNavigator;
import macro.LogoutException;
import map.*;
public class TestStoreNav implements Quittable
{
	public static void main(String[] args)
	{
		new TestStoreNav().go();
	}
	private void go()
	{
		Config config = new Config();
		AHKBridge.runExitHook(this, config);
		WindowManager winmgr = new WindowManager(config);
		WindowThread thread = new WindowThread(config, winmgr, null, winmgr.getWindows().get(0));
		try {
			new HomeNavigator(thread, GlobalMap.SARN).goToStore();
		} catch (HaltThread e) {
			e.printStackTrace();
		} catch (LogoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void exitProgram() 
	{
		
	}
}
