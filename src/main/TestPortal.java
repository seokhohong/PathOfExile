package main;

import macro.LogoutException;
import party.Portal;
import window.PWindow;
import window.WindowManager;
import data.Config;

public class TestPortal 
{
	public static void main(String[] args)
	{
		new TestPortal().go();
	}
	private void go()
	{
		Config config = new Config();
		PWindow window = new WindowManager(config).getWindows().get(0);
		System.out.println(Portal.exists(window));
		try {
			Portal.click(window);
		} catch (LogoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
