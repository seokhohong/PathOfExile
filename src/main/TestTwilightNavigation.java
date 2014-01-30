package main;

import img.*;

import java.util.ArrayList;

import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import process.AHKBridge;
import process.Quittable;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;
import data.Config;

public class TestTwilightNavigation implements Quittable
{
	private boolean halt = false;
	public static void main(String[] args)
	{
		new TestTwilightNavigation().go();
	}
	private void go()
	{
		Config config = new Config();
		AHKBridge.runExitHook(this, config);
		PWindow window = new WindowManager(config).getWindows().get(0);
		Timer quick = new Timer(0);
		int numExpiration = 0;
		
		double[] idealAdjust = new double[1]; //pass by reference
		idealAdjust[0] = 0.35d;
		
		while(!halt)
		{
			if(quick.hasExpired())
			{
				quick = new Timer(4500);
				window.type(new Integer(numExpiration / 2 + 1).toString());
				numExpiration ++;
			}
			PoEMacro.moveHero(window, getAngle(window, idealAdjust), 300);
			Macro.sleep(200);
		}
	}
	private static final double ADJ_AMOUNT = 0.02d;
	private double getAngle(PWindow window, double[] idealAdjust)
	{
		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		filters.add(FilterType.BROWN_WALL);
		filters.add(FilterType.WATER);
		filters.add(FilterType.TWILIGHT_GARBAGE);
		
		IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		
		BinaryImage waterBin = waterBin(minimap);
		BinaryImage landBin = landBin(minimap);
		BinaryImage garbageBin = garbageBin(minimap);
		
		if(waterBin.countWhite() > landBin.countWhite() && garbageBin.countWhite() < 50)
		{
			System.out.println("More Water");
			if(idealAdjust[0] > 0)
			{
				idealAdjust[0] -= ADJ_AMOUNT;
			}
		}
		else
		{
			System.out.println("More Land");
			idealAdjust[0] += ADJ_AMOUNT;
		}
		System.out.println(idealAdjust[0]);
		
		waterBin.add(landBin);
		waterBin.add(garbageBin);
		
		double[] clarity = scanPolar(waterBin);
		weightClarity(clarity, idealAdjust[0]);
		
		return indexToAngle(clearest(clarity));
	}
	
	private BinaryImage waterBin(final IntBitmap minimap)
	{
		IntBitmap forWater = IntBitmap.copy(minimap);
		RatioFilter.maintainRatio(forWater, FilterType.WATER);
		GreyscaleImage grey = forWater.toGreyscale();
		grey.blur(2);
		BinaryImage bin = grey.doubleCutoff(10);
		return bin;
	}
	
	private BinaryImage landBin(final IntBitmap minimap)
	{
		IntBitmap forWater = IntBitmap.copy(minimap);
		RatioFilter.maintainRatio(forWater, FilterType.BROWN_WALL);
		GreyscaleImage grey = forWater.toGreyscale();
		grey.blur(3);
		BinaryImage bin = grey.doubleCutoff(10);
		return bin;
	}
	
	private BinaryImage garbageBin(IntBitmap minimap)
	{
		RatioFilter.maintainRatio(minimap, FilterType.TWILIGHT_GARBAGE);
		GreyscaleImage grey = minimap.toGreyscale();
		grey.blur(1);
		BinaryImage bin = grey.doubleCutoff(10);
		return bin;
	}
	
	private double indexToAngle(int index)
	{
		return index * Math.PI / NUM_BARS - Math.PI / 4;
	}
	
	private static final int NUM_BARS = 32;
	private double[] scanPolar(BinaryImage bin)
	{
		boolean[][] data = bin.getData();
		double[] clarity = new double[NUM_BARS];
		for(int a = 0; a < NUM_BARS; a++)
		{
			double angle = indexToAngle(a);
			for(int b = 0; b < bin.getWidth() / 2; b++)
			{
				int x = (bin.getWidth() / 2) + (int) (b * Math.cos(angle));
				int y = (bin.getHeight() / 2) - (int) (b * Math.sin(angle));
				if(data[x][y] == BinaryImage.BLACK)
				{
					clarity[a] += (bin.getWidth() / 2 - b) + bin.getWidth() / 2;
				}
			}
		}
		return clarity;
	}

	private static final double weight = 0.05d;
	private void weightClarity(double[] clarity, double idealAdjust)
	{
		//some clarities may be negative because of ideal angle adjustment
		//punishes edges of bottom more than top
		for(int a = 0; a < NUM_BARS / 2; a++)
		{
			clarity[a] = (double) clarity[a] * (Math.sin(a * Math.PI / NUM_BARS + idealAdjust) * weight * 2 + (1 - weight * 2));
		}
		for(int a = NUM_BARS - 1; a > NUM_BARS / 2; a--)
		{
			clarity[a] = (double) clarity[a] * (Math.sin(a * Math.PI / NUM_BARS + idealAdjust) * weight + (1 - weight));
		}
	}
	
	private int clearest(double[] clarity)
	{
		int maxIndex = 0;
		double maxValue = 0;
		for(int a = 0; a < clarity.length; a++)
		{
			if(clarity[a] > maxValue)
			{
				maxValue = clarity[a];
				maxIndex = a;
			}
		}
		return maxIndex;
	}
	
	@Override
	public void exitProgram() 
	{
		halt = true;
	}
}
