package main;

import java.awt.Rectangle;

import window.PWindow;

import combat.HealthBarFinder;

import img.ImageToolkit;
import img.IntBitmap;

public class TestHealthBars 
{
	public static void main(String[] args)
	{
		new TestHealthBars().go();
	}
	private void go()
	{
		IntBitmap screen = IntBitmap.getInstance(ImageToolkit.loadImage("img/HealthBars.bmp"));
		PWindow window = PWindow.getWindows(screen).get(0);
		HealthBarFinder hpFinder = new HealthBarFinder(screen.subimage(new Rectangle(window.getX(), window.getY(), 700, 500)));
		System.out.println(hpFinder.getHealthBars().size());	
	}
}
