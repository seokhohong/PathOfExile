package main;

import window.PWindow;
import window.WindowManager;

public class TestLogout 
{
	public static void main(String[] args)
	{
		new TestLogout().go();
	}
	private void go()
	{
		WindowManager winMgr = new WindowManager();
		PWindow window = winMgr.getWindows().get(0);
		
		window.logout();
	}
}
