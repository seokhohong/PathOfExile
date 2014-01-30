package arena;

import img.*;

import java.awt.Point;
import java.util.ArrayList;

import macro.HealWaiter;
import macro.LogoutException;
import macro.Macro;
import macro.Timer;
import macro.Waiter;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
import combat.Healer;
import data.Potion;
import data.Profile;
import geom.*;

public class TheForest extends Arena
{
	public static final String NAME = "The Forest";		
	@Override
	public String getName() { return NAME; }
	
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
		super(NAME, level, 2);
		addFilter(FilterType.WATER);
		//addFilter(FilterType.FOREST_SHORELINE);
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
	
	private static final int RUSH_TIME = 7000;
	
	private static final int MVMT_WAIT = 700; //How long to wait after moving
	//Forest plays differently because its not navigable in the same way other arenas are
	@Override
	public boolean clearArena(WindowThread thread) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		Profile profile = window.getProfile();
		profile.useAuras(window);
		Healer healer = new Healer(window);
		rush(thread, healer);	//Dive into the arena quickly, don't waste time
		profile.shootEverywhere(window, healer);
		profile.fight(window, this, thread, healer); //Come out fighting
		
		Timer wholeLevelTimer = new Timer(getMaxLevelTime());
		Timer combatTimer = new Timer(INITIAL_WAIT);
		HealWaiter moveTime = null;
		int numMoves = 0; //for counting backwards shooting
		while(wholeLevelTimer.stillWaiting()) //fights for a certain amount of time
		{
			thread.checkHalt();	//Make sure halting occurs cleanly
			healer = new Healer(window);
			
			if(moveTime!=null) moveTime.waitFully(); //Wait the set amount of time before fighting
			
			profile.pickUpItems(window, thread, healer);
			
			if(finishedMap(window))
			{
				return false;
			}
			
			//System.out.println("(Picking up Items2 Complete) Is Fighting");
			if(profile.fight(window, this, thread, healer))
			{
				combatTimer.reset(COMBAT_IDLE);
			}
			//System.out.println("Fighting Complete, Picking up Items");
			if(isIdle(combatTimer)) break;
			
			thread.checkHalt();
			
			profile.pickUpItems(window, thread, healer);
			
			thread.checkHalt();
			//Move
			//System.out.println("Items Complete, Moving");
			window.getProfile().getCombatStyle().move(window, bestMovementAngle(window), 275, numMoves);
			numMoves ++;
			
			thread.checkHalt();
			moveTime = new HealWaiter(MVMT_WAIT, healer); //make sure movement is done by combat time
			
			healer.checkHealth();
			//System.out.println("Moving Complete, Picking up Items2");
			profile.pickUpItems(window, thread, healer);
			
			thread.checkHalt();
		}
		return false;
	}
	private void rush(WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		PWindow window = thread.getWindow();
		Profile profile = window.getProfile();
		Timer rushTimer = new Timer(RUSH_TIME);
		window.getProfile().usePotion(window, Potion.QUICKSILVER);
		while(rushTimer.stillWaiting())
		{
			thread.checkHalt();
			if(profile!=null)
			{
				moveRush(window);

			}
			Waiter moveTime = new Waiter(MVMT_WAIT / 2);
			if(healer.checkHealth()) //If provoked
			{
				break;
			}
			moveTime.waitFully();
		}
	}
	private void moveRush(PWindow window)
	{
		Profile profile = window.getProfile();
		Point waypoint = Arena.findWaypoint(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT)));
		if(waypoint != null && waypoint.y < PWindow.getMinimapSize() / 2)
		{
			profile.getCombatStyle().rush(window, 7 * Math.PI / 8, 275);
		}
		else
		{
			profile.getCombatStyle().rush(window, bestMovementAngle(window), 275);
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
	/*
	@Override
	public BinaryImage processMap(IntBitmap map)
	{
		MidpassFilter.maintainRanges(map, MidpassFilterType.WATER_TEST);
		GreyscaleImage grey = map.bidirectionalDerivative();
		grey.blur(10);
		grey.multiply(35);
		BinaryImage bin = grey.doubleCutoff(254);
		return bin;
	}
	public double bestMovementAngle(PWindow window)
	{
		IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
		BinaryImage bin = processMap(minimap);
		double ang = getAngle(bin);
		System.out.println(ang);
		return ang;
		
	}
	private static final double OFFSET_ANGLE = Math.PI / 16; 
	private static double getAngle(BinaryImage map)
	{
		boolean[][] data = map.getData();
		boolean existsWhite = false;
		for(int r = 1; r < map.getWidth(); r++)
		{
			
			int xPix = (int) (map.getWidth() - r*Math.cos(OFFSET_ANGLE) - 1);
			int yPix = (int) ((double) map.getHeight()/2 - r*Math.sin(OFFSET_ANGLE) - 1);
			//System.out.println(xPix + " " + yPix + " " + map.getWidth() + " " + map.getHeight());
			//System.out.println(data[149][74]);
			
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
	*/
	@Override
	public void clickLocation(PWindow window) 
	{
		window.leftClick(230, 240);
	}
}
