package main;

import java.awt.Rectangle;

import window.PWindow;

import macro.LogoutException;
import macro.Macro;
import arena.Arena;

import combat.HealthBarFinder;
import combat.ShadowHealer;

import img.ImageToolkit;
import img.IntBitmap;

public class TestHealer 
{
	public static void main(String[] args)
	{
		new TestHealer().go();
	}
	private void go()
	{
		Macro.macro.sleep(3000);
		PWindow window = PWindow.getWindows(IntBitmap.getInstance(ImageToolkit.takeScreenshot())).get(0);
		while(true)
		{
			try {
				new ShadowHealer(window).stayAlive();
			} catch (LogoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
