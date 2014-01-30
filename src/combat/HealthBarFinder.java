package combat;

import geom.CoordToolkit;
import geom.PolarCone;
import geom.PolarPoint;
import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import window.PWindow;
import window.ScreenRegion;

public class HealthBarFinder 
{
	private ArrayList<Point> healthBars = new ArrayList<Point>();
	public ArrayList<Point> getHealthBars() { return healthBars; }
	
	public HealthBarFinder(IntBitmap image)
	{
		processScreen(image);
	}
	
	public HealthBarFinder(PWindow window)
	{
		//expand rectangle?
		processScreen(IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.ITEM_SCAN_RECT)));
	}
	private static final int SAFETY_BARS = 3; //max number of healthbars on screen to consider safety
	public boolean isSafe()
	{
		return healthBars.size() <= SAFETY_BARS;
	}
	
	private static final double SHOOTING_SPREAD = Math.PI/6; //angle which is assumed for arrow firing spray IN RADIANS
	private static final int ANGLE_CHECKS = 50; //number of iterations which angle optimizer performs
	/**
	 * Creates a large number of polar cones which sweep around the ranger, and then returns the angle for
	 * which the number HP bar intersections with cones is greatest.
	 * 
	 * @return
	 */
	public double getShootingAngle()
	{
		double increment = 2*Math.PI/ANGLE_CHECKS;
		int bestCount = 0;
		double bestAngle = 0;
		ArrayList<PolarPoint> polarBars = CoordToolkit.polarAll(CoordToolkit.centerAll(healthBars, PWindow.getWidth(), PWindow.getHeight()));
		for(int i=0; i<ANGLE_CHECKS; i++)
		{
			PolarCone cone = new PolarCone(i * increment - Math.PI, SHOOTING_SPREAD);
			int count = cone.overlapCount(polarBars);
			//System.out.println("At "+(i*increment)+" Count is "+count);
			if(count > bestCount)
			{
				bestCount = count;
				bestAngle = i * increment + SHOOTING_SPREAD/2;
			}
		}
		if(bestCount <= 2)
		{
			PolarPoint rndBar = polarBars.get(new Random().nextInt(polarBars.size()));
			return rndBar.getAngle();
		}
		return bestAngle - Math.PI;
	}
	
	private static final int SIZE_THRESHOLD = 10; //number of pixels that have to be found contiguous in order to mark it as an hp bar
	private static final int HEALTH_BAR_HEIGHT = 40; //Health bars are quite a ways above the actual targete
	private static final int HEALTH_BAR_WIDTH = 50;
	private void processScreen(IntBitmap screen)
	{
		RatioFilter.maintainRatio(screen, FilterType.HEALTH_BAR);
		BinaryImage whiteBars = screen.toGreyscale().doubleCutoff(1);
		boolean[][] data = whiteBars.getData();
		for(int a = 0; a < whiteBars.getWidth(); a++)
		{
			for(int b = 0; b < whiteBars.getHeight(); b++)
			{
				if(data[a][b] == BinaryImage.WHITE)
				{
					if(whiteBars.fillBlack(a, b) > SIZE_THRESHOLD)
					{
						healthBars.add(new Point(a + HEALTH_BAR_WIDTH / 2, Math.min(PWindow.getHeight(), b + HEALTH_BAR_HEIGHT)));
					}
				}
			}
		}
	}
}
