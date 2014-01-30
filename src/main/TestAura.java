package main;

import window.*;
import data.Config;
import macro.*;
public class TestAura 
{
	public static void main(String[] args)
	{
		new TestAura().go();
	}
	private void go()
	{
		PWindow window = new WindowManager(new Config()).getWindows().get(0);
		PoEMacro.auraOn(window, Hotkey.T);
	}
}
