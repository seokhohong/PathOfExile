package main;

import combat.Healer;

import window.PWindow;
import window.WindowManager;
import macro.Macro;

public class TestHp 
{
	public static void main(String[] dominique_is_hot)
	{
		new TestHp().go();
	}
	private void go()
	{		
		//Doesn't want to work

		WindowManager winMgr = new WindowManager();
		PWindow window = winMgr.getWindows().get(0);
		Healer h = new Healer(window);
		while(true)
		{
			System.out.println(h.isFullHealth());
			System.out.println(h.overallChange());
			Macro.macro.sleep(200);
		}
	}
}
