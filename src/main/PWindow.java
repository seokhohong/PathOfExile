package main;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import fourier.BandPass;
import fourier.FourierTransform;

import macro.*;
import map.LevelMap;

import img.*;

public class PWindow 
{
	private static Robot robot;
	private static final Random random = new Random();
	static
	{
		try
		{
			robot = new Robot();
		}
		catch(AWTException e) {}
	}
	
	private static final int WIDTH = 800;		public int getWidth() { return WIDTH; }
	private static final int HEIGHT = 600;		public int getHeight() { return HEIGHT; }
	
	//8, 31 is the offset of the graphics
	private static final Rectangle GRAPHICS_RECT = new Rectangle(8, 31, WIDTH, HEIGHT);
	
	private static final int X_OFFSET = 14 - GRAPHICS_RECT.x;
	private static final int Y_OFFSET = 603 - GRAPHICS_RECT.y; //of window picking image

	//EVERYTHING IS RELATIVE TO THE TOP LEFT OF THE GRAPHICS WINDOW
	private static final Rectangle HP_RECT = new Rectangle(332, 8, 10, 4); //relative to top left corner of graphics window (DO NOT USE)
	//Translates it with respect to the window
	private Rectangle getHP_RECT() { return new Rectangle(HP_RECT.x + x, HP_RECT.y + y, HP_RECT.width, HP_RECT.height); }
	private static final Rectangle MAP_RECT = new Rectangle(645, 4, 150, 150);
	Rectangle getMAP_RECT() { return new Rectangle(MAP_RECT.x + x, MAP_RECT.y + y, MAP_RECT.width, MAP_RECT.height); }
	private static final Rectangle ITEM_SCAN_RECT = new Rectangle(0, 0, 650 ,500);
	Rectangle getITEM_SCAN_RECT() { return new Rectangle(ITEM_SCAN_RECT.x + x, ITEM_SCAN_RECT.y + y, ITEM_SCAN_RECT.width, ITEM_SCAN_RECT.height); }
	private static final Rectangle WINDOW_SCAN_RECT = new Rectangle(X_OFFSET, Y_OFFSET, 27 ,20);
	private Rectangle getWINDOW_SCAN_RECT() { return new Rectangle(WINDOW_SCAN_RECT.x + x, WINDOW_SCAN_RECT.y + y, WINDOW_SCAN_RECT.width, WINDOW_SCAN_RECT.height); }
	
	private static final Point LOGOUT = new Point(400, 270);
	private Point getLOGOUT() { return new Point(LOGOUT.x + x, LOGOUT.y + y); }
	private static final Point LOGIN = new Point(570, 430);
	private Point getLOGIN() { return new Point(LOGIN.x + x, LOGIN.y + y); }
	
	private int x;	public int getX() { return x; }
	private int y;	public int getY() { return y; }
	private PWindow(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public static ArrayList<PWindow> getWindows(BufferedImage wholeScreen)
	{
		ArrayList<Point> windows = IntBitmap.getInstance(wholeScreen).findImages(Library.getWindowIcon());
		ArrayList<PWindow> pwindows = new ArrayList<PWindow>();
		for(Point p : windows)
		{
			pwindows.add(new PWindow(p.x - X_OFFSET, p.y - Y_OFFSET));
		}
		return pwindows;
	}
	private static final int OPTIONS_DELAY = 200;
	private static final int NUM_DEGREES = 360;
	private static final int MOVE_DIST = 200;
	private static final int WAIT_MULTIPLIER = 3;
	
	private static final int DIST_THRESHOLD = 20; //at 20 pixels we reevaluate destination
	
	private int currAngle = random.nextInt(NUM_DEGREES);
	private int lastAngle = currAngle;
	private int continuous = 0;

	public void execute()
	{
		//avoid waypoint....
		if(windowVisible())
		{
			move();
		}
		else
		{
			sleep(1000);
		}
	}
	private static final int WAIT_PER = 35; //the time the cursor stays on a given point to take a screenshot
	private static final int MAX_RADIAL_DIST = 200;
	private static final double INC_RAD = Math.PI / 8;
	private static final int INC_DIST = 5;
	private static final int INIT_ATTACK_DIST = 50;
	private static final int WEAPON_COOLDOWN = 800; //milliseconds (take into account the click)
	private double attackAng = 0; 	//angle of cursor spinning around center to identify enemies
	private int attackDist = 50; 	//distance of cursor away from center
	//Gets the whole screen
	public BufferedImage getMinimap(BufferedImage wholescreen)
	{
		return ImageToolkit.getSubimage(wholescreen, getMAP_RECT());
	}
	//Did I attack an enemy?
	private boolean killEnemies(int time)
	{
		for(int a = 0; a < time / WAIT_PER ; a++)
		{
			if(attackDist > MAX_RADIAL_DIST)
			{
				attackDist = INIT_ATTACK_DIST; //reset
			}
			mouseMove(WIDTH / 2 + attackDist * Math.cos(attackAng), HEIGHT / 2 + attackDist * Math.sin(attackAng));
			sleep(WAIT_PER);
			if(existsEnemyBar()) 
			{
				System.out.println("Attack the Enemy!");
				Macro.rightClick();
				sleep(WEAPON_COOLDOWN);
				return true;
			}
			attackAng += INC_RAD;
			attackDist += INC_DIST;
		}
		return false;
	}
	private static final float SHIFT_MAP = 0.3f; //how often it shifts the map
	private static final int STUCK_STREAK = 5;
	//Don't run this
	private void move()
	{
		BinaryImage finalMap;
		BufferedImage mapNorm = takeScreenshot(getMAP_RECT());
		BinaryImage bMapNorm = processMap(IntBitmap.getInstance(mapNorm));
		if(random.nextFloat() < SHIFT_MAP)
		{
			//ImageToolkit.exportImage(mapNorm, "export/OrigNorm.bmp");
			PoEMacro.openOptions();
			sleep(OPTIONS_DELAY);
			BufferedImage mapShift = takeScreenshot(getMAP_RECT());
			//ImageToolkit.exportImage(mapShift, "export/OrigShift.bmp");
			BinaryImage bMapShift = processMap(IntBitmap.getInstance(mapShift));
			//sleep(OPTIONS_DELAY);
			PoEMacro.closeOptions();
			finalMap = bMapNorm;
			finalMap.add(bMapShift);
		}
		else
		{
			finalMap = bMapNorm;
		}
		finalMap.export("export/fin.bmp");
		LevelMap map = new LevelMap(bMapNorm);
		//map.clearCenter();
		lastAngle = currAngle;
		//currAngle = map.bestMovement(currAngle);
		if(lastAngle == currAngle)
		{
			continuous ++ ;
			if(continuous > STUCK_STREAK) currAngle = random.nextInt(NUM_DEGREES);
		}
		else
		{
			continuous = 0;
		}
		sleep(100);
		//System.out.println("Moving at "+currAngle);
		PoEMacro.moveHero(this, currAngle, MOVE_DIST);
		while(killEnemies(MOVE_DIST * WAIT_MULTIPLIER / 2))
		{
			items(); //takes time
		}
		//sleep(MOVE_DIST * WAIT_MULTIPLIER);
	}
	private static final int ITEM_WAIT_MULTIPLIER = 7;
	private void items()
	{
		ArrayList<Point> items = findItem(IntBitmap.getInstance(takeScreenshot(getITEM_SCAN_RECT())));//, false);
		if(!items.isEmpty())
		{
			System.out.println(items.get(0));
			clickItem((int) items.get(0).getX(), (int) items.get(0).getY());
			sleep(MOVE_DIST * ITEM_WAIT_MULTIPLIER);
		}
	}
	private static final int DERIV_MULTIPLIER = 2;
	void experimentDelta(IntBitmap img1, IntBitmap img2)
	{
		img1.subtract(img2);
		img1.export("export/subtracted.bmp");
	}
	ArrayList<Point> findItem(IntBitmap iImage) 
	{
		ArrayList<Point> points = new ArrayList<Point>();
		BinaryImage isolated = isolateItems(iImage);	
		return points;
	}
	//Runs a bandpass filter through the rgb image with settings for detecting items
	private GreyscaleImage itemFFT(IntBitmap rgb)
	{
		FourierTransform ft = new FourierTransform(new BandPass(0.0d, 325.0d));
		GreyscaleImage grey = rgb.toGreyscale();
		return ft.transform(grey);    // Fourier transform
	}
	public BinaryImage isolateItems(IntBitmap rgb)
	{
		GreyscaleImage fft = itemFFT(rgb);
		ArrayList<BinaryImage> bImages = new ArrayList<BinaryImage>();
		int a = 0; 
		for(ItemType type : ItemType.values())
		{
			//multiply filtered with fourier to compound pixel color information with edge detection
			GreyscaleImage grey = type.filterMatch(rgb).toGreyscale();
			grey.export("export/colored2.bmp");
			//fft.export("export/fourier.bmp");
			//grey.export("export/colored.bmp");
			grey.multiply(fft);
			fft.export("export/4ier2.bmp");
			grey.export("export/greysum.bmp");

			// Blur once to eliminate single pixels
	 		grey.horizontalBlur(3); 
			
	 		// Iterate, relatively strengthening clusters and eliminating loners
	 		//   More iterations will cut down on false positives and may give false negatives
		    for(int i = 0; i < 8; i++) 
		    {
		    	grey.horizontalBlur(7);
		    	if(i%2 == 0) grey.blur(2);
		    	grey.multiply(1.02);
		    }
			BinaryImage bin = grey.doubleCutoff(10); // kill noise
			//bin.export("export/"+a+"itemType.bmp");
			bImages.add(bin); //Filters for pixels that match this item type
			a++;
		}		
		//Won't be summed up at the end, but is good for visuals now
		BinaryImage sum = bImages.get(0);
		for(int b = 0 ; b < bImages.size(); b++)
		{
			sum.add(bImages.get(b));
		}
		sum.export("export/summed.bmp");
		ArrayList<Point> points = ItemFinder.signatureToPoints(sum);
		for(Point p : points)
		{
			sum.getData()[p.x][p.y] = true;
		}
		sum.export("export/found.bmp");
	    return sum;
	}
	private BinaryImage getItemLines(GreyscaleImage grey, int id, boolean verbose)
	{
		//grey.export("export/firstDeriv"+id+".bmp");
		grey = ItemFinder.deltaVertical(grey);
		grey.multiply(DERIV_MULTIPLIER);
		if(verbose) grey.export("export/dVert"+id+".bmp");
		BinaryImage bin = grey.doubleCutoff(ImageToolkit.MAX_VAL - 1);
		if(verbose) bin.export("export/cutoff"+id+".bmp");
		bin.requireNeighbors(2, 3);
		bin.requireNeighbors(2, 3);
		if(verbose) bin.export("export/cleared"+id+".bmp");
		ItemFinder.repairHorizontal(bin);
		if(verbose) bin.export("export/repair"+id+".bmp");
		bin = ItemFinder.consistentHorizontal(bin);
		bin = ItemFinder.consistentHorizontal(bin);
		if(verbose) bin.export("export/horiz"+id+".bmp");
		return bin;
	}
	public void mouseMove(double x, double y)
	{
		robot.mouseMove((int) (x + this.x), (int) (y + this.y)); //transforms window coords into real coords
	}
	public void click(int x, int y)
	{
		Macro.click(x + this.x, y + this.y); //transforms window coords into real coords
	}
	private static final int TAKE_TIME_CLICKING = 100;
	public void clickItem(int x, int y)
	{
		sleep(TAKE_TIME_CLICKING);
		Macro.click(x + this.x, y + this.y + ItemFinder.getItemHeight() / 2);
	}
	private void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch(InterruptedException e) {}
	}
	//whole area
	public BufferedImage takeScreenshot()
	{
		return robot.createScreenCapture(new Rectangle(x, y, WIDTH, HEIGHT));
	}
	//part
	public BufferedImage takeScreenshot(Rectangle rect)
	{
		return robot.createScreenCapture(rect);
	}
	public BinaryImage processMap(IntBitmap map)
	{
		GreyscaleImage grey = map.bidirectionalDerivative();
		grey.multiply(15);
		grey.export("export/blurredDeriv.bmp");
		BinaryImage bin = grey.doubleCutoff(ImageToolkit.MAX_VAL - 1);
		bin.invert();
		grey = bin.toGreyscale();
		grey.export("export/cutoff.bmp");
		//grey.blur(3);
		bin = grey.doubleCutoff((int) (ImageToolkit.MAX_VAL - 1));
		//grey.export("export/"+name+"binary.bmp");
		return bin;
	}

	//detects if enemy hp bar is present in this window
	public boolean existsEnemyBar()
	{
		BufferedImage hp = takeScreenshot(getHP_RECT());
		IntBitmap hpData = IntBitmap.getInstance(hp);
		//ImageToolkit.exportImage(hp, "export/bar.bmp");
		GreyscaleImage grey = hpData.lineDerivative();
		//grey.export("export/hpc.bmp");
		return new HpFinder(grey).isHp();
	}
	private boolean windowVisible()
	{
		BufferedImage windowId = takeScreenshot(getWINDOW_SCAN_RECT());
		return IntBitmap.getInstance(windowId).findImage(Library.getWindowIcon())!=null;
	}
}