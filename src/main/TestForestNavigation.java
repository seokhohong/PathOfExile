package main;

import data.Config;
import img.BinaryImage;
import img.Display;
import img.GreyscaleImage;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;
import macro.Macro;
import macro.PoEMacro;
import process.AHKBridge;
import process.Quittable;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;

public class TestForestNavigation implements Quittable
{
	private boolean halt = false;
	public static void main(String[] args)
	{
		new TestForestNavigation().go();
	}
	private void go()
	{
		Config config = new Config();
		AHKBridge.runExitHook(this, config);
		PWindow win = new WindowManager(config).getWindows().get(0);
		while(!halt)
		{	
			double angle = bestMovementAngle(win);
			System.out.println(angle);
			PoEMacro.moveHero(win, angle, 200);
			Macro.sleep(1000);
		}
	}
	//private Orientation getOrientation(BinaryImage map)
	{
		
	}
	private static final double OFFSET_ANGLE = Math.PI / 30; 
	private static double getAngle(BinaryImage map)
	{
		boolean[][] data = map.getData();
		boolean existsWhite = false;
		for(int r = 1; r < map.getWidth(); r++)
		{
			int xPix = (int) (map.getWidth() - r*Math.cos(OFFSET_ANGLE) - 1);
			int yPix = (int) ((double) map.getHeight()/2 - r*Math.sin(OFFSET_ANGLE) - 1);
			if(xPix >= 0 && yPix >= 0 && data[xPix][yPix] == BinaryImage.WHITE)
			{
				existsWhite = true;
				//System.out.println("here");
				if(r > 3/8 * map.getWidth())
				{
					//System.out.println("here");
					double a = (double) map.getWidth()/2;
					double b = r;
					double c = Math.sqrt(a*a + b*b - 2*a*b*Math.cos(OFFSET_ANGLE));
					if(xPix > map.getWidth() / 2)
					{
						System.out.println("Pos");
						return Math.asin(b/c * Math.sin(OFFSET_ANGLE));
					}
					else
					{
						return Math.asin(b/c * Math.sin(OFFSET_ANGLE)) + Math.PI / 2;
					}
				}
				else break;
			}
		}
		if(!existsWhite)
		{
			return Math.PI;
		}
		System.out.println("Entered mode 2");
		for(int r = 0; r < map.getHeight()/2; r++)
		{
			int xPix = (int) ((double) map.getWidth()/2 + r*Math.sin(OFFSET_ANGLE) - 1);
			int yPix = (int) (map.getHeight() - r*Math.cos(OFFSET_ANGLE) - 1);
			if(xPix >= 0 && yPix >= 0 && data[xPix][yPix] == BinaryImage.WHITE)
			{
				
				if(r > 3/8 * map.getHeight())
				{
					double a = (double) map.getHeight()/2;
					double b = r;
					double c = Math.sqrt(a*a + b*b - 2*a*b*Math.cos(OFFSET_ANGLE));
					if(yPix > map.getHeight()/2)
					{
						return Math.asin(b/c * Math.sin(OFFSET_ANGLE));
					}
					else
					{
						return Math.asin(b/c * Math.sin(OFFSET_ANGLE)) - Math.PI / 2;
					}
				}
				return Math.PI/8;
			}
		}
		return Math.PI;
	}
	public BinaryImage processMap(IntBitmap map)
	{
		MidpassFilter.maintainRanges(map, MidpassFilterType.WATER_TEST);
		GreyscaleImage grey = map.bidirectionalDerivative();
		grey.blur(12);
		grey.multiply(35);
		BinaryImage bin = grey.doubleCutoff(254);
		//bin.clearCenter(10, BinaryImage.BLACK);
		Display.show(bin);
		return bin;
	}
	public double bestMovementAngle(PWindow window)
	{
		IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		BinaryImage bin = processMap(minimap);
		return getAngle(bin);
	}
	@Override
	public void exitProgram() 
	{
		halt = true;
	}
}
enum Orientation {LEFT, UP;}
