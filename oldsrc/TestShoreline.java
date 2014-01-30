package main;

import java.awt.Point;
import java.util.ArrayList;

import process.AHKBridge;
import process.Quittable;
import arena.*;
import window.*;
import geom.CoordToolkit;
import geom.PolarPoint;
import img.*;
import macro.*;
import window.ScreenRegion;

public class TestShoreline implements Quittable
{
	private boolean halt = false;
	public static void main(String[] args)
	{
		new TestShoreline().go();
	}
	private void go()
	{
		AHKBridge.runExitHook(this);
		WindowManager winMgr = new WindowManager();
		PWindow window = winMgr.getWindows().get(0);
		
		while(!halt)
		{
			double bestMvt = bestMovementAngle(window);
			System.out.println("Best Angle "+bestMvt);
			//PoEMacro.moveHero(window, bestMvt, 150);
			Macro.macro.sleep(2000);
		}
		
		/*
		IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		BinaryImage bin = Arena.fromString("The Forest3").processMap(minimap);
		Display.showHang(bin);
		*/
		/*
		//Spin a circle from PI to 5PI/2
		boolean[][] binData = bin.getData();
		for(int a = 0; a < 24; a++)
		{
			double angle = Math.PI + Math.PI * a / 16d;
			int dist = 0;
			for(int b = 0; b < 75; b++)
			{
				int pixX = (int) (Math.cos(angle) * b) + 75;
				int pixY = (int) (Math.sin(angle) * b) + 75;
				if(binData[pixX][pixY])
				{
					dist++;
				}
				else
				{
					break;
				}
			}
			System.out.println("For angle "+angle+" dist "+dist);
		}
		Display.showHang(bin);
		*/
		
	}

	private static final int NUM_SEARCH_LINES = 24;
	//Polar sweep to determine what angle it can move at, it picks the one at the first major block by water
	private double polarSweep(BinaryImage map)
	{
		boolean[][] data = map.getData();
		boolean hasSeenClear = false;
		for(double ang = -Math.PI / 4; ang < Math.PI ; ang += Math.PI / 16)
		{
			boolean maxed = true;
			for(int rad = 0; rad < map.getWidth() / 2; rad++)
			{
				if(rad > map.getWidth() / 2 - 10)
				{
					hasSeenClear = true;
				}
				int pixX = (int) (Math.cos(ang) * rad) + data.length / 2;
				int pixY = (int) (-Math.sin(ang) * rad) + data.length / 2;
				if(data[pixX][pixY] == BinaryImage.WHITE)
				{
					if(hasSeenClear && rad < map.getWidth() / 2 - 20)
					{
						ang -= Math.PI / 16d; //to compensate for the stripping of some ocean
						return ang;
					}
					maxed = false;
					System.out.println("Angle "+ang+" Has "+rad);
					break;
				}
			}
			if(maxed) System.out.println("Angle "+ang+" Has 75");
		}
		return Math.PI;
	}
	public double bestMovementAngle(PWindow window)
	{
		IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		BinaryImage bin = Arena.fromString("The Forest3").processMap(minimap);
		return polarSweep(bin);
	}
	private int getClarity(boolean[][] binData, double angle)
	{
		int radius = 75;
		int dist = 0;
		for(int b = 0; b < radius; b++) 
		{
			int pixX = (int) (Math.cos(angle) * b) + radius;
			int pixY = (int) (-Math.sin(angle) * b) + radius;
			if(binData[pixX][pixY])
			{
				dist++;
			}
			else
			{
				break;
			}
		}
		return dist;
	}
	private static final int TOO_LITTLE_WATER = 30; //Not enough points to clearly form an outline of the shore (probably noise)
	private static final double DIST_FROM_SHORE = 20;
	private static final int BLACK_THRESHOLD = 15;
	private static final int SEARCH_RADIUS = 2;
	private static boolean inBounds(boolean[][] data, int x, int y)
	{
		return x >= 0 && y >=0 && x < data.length && y < data[0].length;
	}
	private static boolean lotOfBlack(boolean[][] data, int x, int y)
	{
		int count = 0;
		for(int a = -SEARCH_RADIUS; a < SEARCH_RADIUS; a++)
		{
			for(int b = -SEARCH_RADIUS; b < SEARCH_RADIUS; b++)
			{
				if(inBounds(data, a + x, b + y))
				{
					if(data[a + x][b + y] == BinaryImage.BLACK)
					{
						count++;
					}
				}
			}
		}
		return count > BLACK_THRESHOLD;
	}
	//Marks the boolean array at coordinate (a, b) offset a certain distance towards lower-right corner
	private void markPoint(boolean[][] data, int a, int b, ArrayList<Point> linePoints)
	{
		//if(data[a][b] == BinaryImage.BLACK)
		{
			//double angleToCorner = Math.atan2(b - data[0].length, a - data.length);
			//System.out.println(angleToCorner);
			//double newX = a + (Math.sin(angleToCorner) * DIST_FROM_SHORE);
			//double newY = b + (Math.cos(angleToCorner) * DIST_FROM_SHORE);
			int newX = (int) (a + DIST_FROM_SHORE);
			int newY = b;
			if(newX >= 0 && newY >= 0 && newX < data.length && newY < data[0].length)
			{
				linePoints.add(new Point(newX, newY));
				data[newX][newY] = BinaryImage.BLACK;
			}
		}
	}
	private PolarPoint furthestPoint(ArrayList<Point> linePoints)
	{
		ArrayList<Point> centered = CoordToolkit.centerAll(linePoints, PWindow.getMinimapSize(), PWindow.getMinimapSize());
		ArrayList<PolarPoint> polars = CoordToolkit.polarAll(centered);
		double farthest = 0;
		PolarPoint bestPoint = null;
		for(PolarPoint pp : polars)
		{
			if(pp.getAngle() > 0) //top half of screen
			{
				if(pp.getMagnitude() > farthest)
				{
					bestPoint = pp;
					farthest = pp.getMagnitude();
				}
			}
		}
		if(bestPoint == null) return null;
		return bestPoint;
	}
	@Override
	public void exitProgram() 
	{
		halt = true;
	}
}
