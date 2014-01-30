package main;
import java.awt.Point;

import window.PWindow;

import geom.CoordToolkit;
import img.ImageToolkit;
import img.IntBitmap;
import combat.HealthBarFinder;

public class TestHealthBarAngleFinding 
{
	public static void main(String[] balls)
	{
		new TestHealthBarAngleFinding().go();
	}
	private void go()
	{
		IntBitmap image = IntBitmap.getInstance(ImageToolkit.loadImage("img/angle3.bmp"));
		PWindow window = PWindow.getWindows(image).get(0);
		IntBitmap region = image.subimage(window.getITEM_SCAN_RECT());
		region.toGreyscale().display();
		HealthBarFinder bars = new HealthBarFinder(region);
		double a = bars.getShootingAngle();
		Point p = new Point();
		p.x = (int) (150d*Math.cos(a));
		p.y = (int) (150d*Math.sin(a));
		System.out.println(p.x+" "+p.y);
		System.out.println(CoordToolkit.toUncentered(p, PWindow.getWidth(), PWindow.getHeight()));
		//Point p = new Point(100, 200); 
		//System.out.println(Math.atan2(-200,100));
		//System.out.println(CoordToolkit.toUncentered(p, PWindow.getWidth(), PWindow.getHeight()));
	}
}
