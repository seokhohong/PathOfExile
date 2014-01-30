package main;

import window.PWindow;
import window.WindowManager;

public class TestImageMatcher 
{
	public static void main(String[] args)
	{
		new TestImageMatcher().go();
	}
	private void go()
	{
		WindowManager winMgr = new WindowManager();
		PWindow window = winMgr.getWindows().get(0);
		while(true)
		{
			System.out.println(window.charSelectionVisible());
		}
	}
}
