package arena;

import java.awt.Point;
import java.util.*;

import combat.Healer;
import data.Profile;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
import macro.LogoutException;
import macro.HealWaiter;
import macro.Timer;
import macro.Waiter;
import geom.CoordToolkit;
import geom.PolarPoint;
import img.BinaryImage;
import img.FilterType;
import img.GreyscaleImage;
import img.ImageToolkit;
import img.IntBitmap;
import img.RatioFilter;

public class TheForest extends Arena
{
	public static ArrayList<Arena> getArenas()
	{
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		for(int a = 0; a <= Arena.getNumDifficulties(); a++)
		{
			arenas.add(new TheForest(a));
		}
		return arenas;
	}
	private TheForest(int level) 
	{
		super("The Forest", level, 2);
		addFilter(FilterType.WATER);
		addFilter(FilterType.FOREST_SHORELINE);
	}
	@Override
	public double startAngle()
	{
		return Math.PI;
	}
	@Override
	public int getMaxLevelTime()
	{
		return 60000 * 2; //2 min
	}
	private static final int INITIAL_WAIT = 10000;
	private static final int COMBAT_IDLE = 25000;
	
	private static final int RUSH_TIME = 10000;
	
	private static final int MVMT_WAIT = 700; //How long to wait after moving
	//Forest plays differently because its not navigable in the same way other arenas are
	@Override
	public void clearArena(PWindow window, WindowThread thread) throws LogoutException, HaltThread
	{
		Healer healer = null;
		try
		{
			Profile profile = window.getProfile();
			healer = new Healer(window);
			rush(window, thread, healer);	//Dive into the arena quickly, don't waste time
			profile.shootEverywhere(window, healer);
			profile.fight(window, this, thread, healer); //Come out fighting
			
			Timer wholeLevelTimer = new Timer(getMaxLevelTime());
			Timer combatTimer = new Timer(INITIAL_WAIT);
			HealWaiter moveTime = null;
			while(wholeLevelTimer.stillWaiting()) //fights for a certain amount of time
			{
				thread.checkHalt();	//Make sure halting occurs cleanly
				
				if(moveTime!=null) moveTime.waitFully(); //Wait the set amount of time before fighting
				
				if(profile.fight(window, this, thread, healer))
				{
					combatTimer.reset(COMBAT_IDLE);
				}
				if(isIdle(combatTimer)) break;
				
				thread.checkHalt();
				double time = System.currentTimeMillis();
				profile.pickUpItems(window, thread, healer);
				System.out.println(time - System.currentTimeMillis());
				thread.checkHalt();
				//Move
				window.getProfile().getCombatStyle().move(window, bestMovementAngle(window), 275);
				
				thread.checkHalt();
				moveTime = new HealWaiter(MVMT_WAIT, healer); //make sure movement is done by combat time
				
				healer.checkHealth();
				profile.pickUpItems(window, thread, healer);
				
				thread.checkHalt();
			}
		}
		catch(LogoutException e)
		{
			healer.stop();
			throw e;
		}
		catch(HaltThread e)
		{
			healer.stop();
			throw e;
		}
	}
	private void rush(PWindow window, WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		Profile profile = window.getProfile();
		Timer rushTimer = new Timer(RUSH_TIME);
		while(rushTimer.stillWaiting())
		{
			thread.checkHalt();
			if(profile!=null)
			{
				profile.getCombatStyle().rush(window, bestMovementAngle(window), 275);
			}
			Waiter moveTime = new Waiter(MVMT_WAIT / 2);
			if(!healer.isFullHealth()) //If provoked
			{
				break;
			}
			moveTime.waitFully();
		}
	}
	@Override
	public BinaryImage processMap(IntBitmap map)
	{
		RatioFilter.maintainRatio(map, getObstacles());
		GreyscaleImage grey = map.bidirectionalDerivative();
		grey.blur(4);
		grey.multiply(45);
		
		BinaryImage bin = grey.doubleCutoff(ImageToolkit.MAX_VAL - 1);

		bin.invert();
		grey = bin.toGreyscale();
		grey.blur(2);
		bin = grey.doubleCutoff(1);
		grey = bin.toGreyscale();
		bin = grey.doubleCutoff(ImageToolkit.MAX_VAL - 1);
		
		return bin;
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
	public double bestMovementAngle(PWindow window)
	{
		IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		BinaryImage bin = processMap(minimap);
		ArrayList<Point> linePoints = new ArrayList<Point>();
		boolean[][] data = bin.getData();
		for(int a = 0; a < bin.getHeight(); a++)
		{
			for(int b = bin.getWidth() - 1; b > 0; b--)
			{
				if(lotOfBlack(data, b, a))
				{
					markPoint(data, b, a, linePoints);
					break;
				}
			}
		}
		//from lower-right corner
		/*
		for(double angle = Math.PI / 2d; angle < Math.PI; angle += (Math.PI / 80d))
		{
			for(int radius = 0; radius < bin.getWidth() * 2; radius++)
			{
				//clean this up
				int rectX = (int) (Math.cos(angle) * radius + bin.getWidth());
				int rectY = (int) ( - Math.sin(angle) * radius + bin.getHeight());
				if(rectX >= 0 && rectX < data.length - 1 && rectY >= 0 && rectY < data[0].length - 1 && data[rectX][rectY] == BinaryImage.BLACK)
				{
					markPoint(data, rectX, rectY, linePoints);
					break;
				}
			}
		}
		*/
		PolarPoint furthestPoint = furthestPoint(linePoints);
		//System.out.println(furthestPoint);
		if(linePoints.size() < TOO_LITTLE_WATER || furthestPoint == null)
		{
			//System.out.println("NULL or Too Little Water");
			return startAngle();
		}
		return furthestPoint.getAngle();
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
	public void clickLocation(PWindow window) 
	{
		window.leftClick(230, 240);
	}
}
