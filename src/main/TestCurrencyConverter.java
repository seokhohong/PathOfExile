package main;

import inventory.CurrencyConverter;
import process.AHKBridge;
import process.Quittable;
import window.HaltThread;
import window.PWindow;
import window.WindowManager;
import window.WindowThread;
import data.Config;

public class TestCurrencyConverter implements Quittable
{
	public static void main(String[] args)
	{
		new TestCurrencyConverter().go();
	}
	private void go()
	{
		Config config = new Config();
		AHKBridge.runExitHook(this, config);
		WindowManager winMgr = new WindowManager(config);
		PWindow window = winMgr.getWindows().get(0);
		WindowThread thread = new WindowThread(config, winMgr, null, window);
		try {
			CurrencyConverter.run(thread);
		} catch (HaltThread e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void exitProgram()
	{
		
	}
}
