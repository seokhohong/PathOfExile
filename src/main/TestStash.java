package main;

import macro.LogoutException;
import macro.Macro;
import map.GlobalMap;
import process.AHKBridge;
import window.HaltThread;
import window.PWindow;
import window.WindowManager;
import window.WindowThread;
import data.Config;
import inventory.*;

public class TestStash 
{
	public static void main(String[] args)
	{
		new TestStash().go();
	}
	private void go()
	{
		Config config = new Config();
		WindowManager winMgr = new WindowManager(config);
		PWindow window = winMgr.getWindows().get(0);
		WindowThread thread = new WindowThread(config, winMgr, null, window);
		try {
			Stash stash = new Stash(thread, GlobalMap.LION_EYES_WATCH);
			for(int i = 0; i < 10; i++)
			{
			stash.gotoStashTab(1);
			Macro.sleep(10);
			stash.gotoStashTab(2);
			Macro.sleep(10);
			stash.gotoStashTab(3);
			Macro.sleep(10);
			stash.gotoStashTab(4);
			}
		} catch (HaltThread e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LogoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
