package main;

import img.ImageToolkit;
import img.IntBitmap;

import java.awt.Point;
import java.util.ArrayList;

import macro.Macro;
import macro.PoEMacro;
import map.GlobalMap;
import math.PhaseCorrelation;
import window.PWindow;
import window.ScreenRegion;
import window.WindowManager;
import process.AHKBridge;
import process.Quittable;

public class TestCaveNav implements Quittable
{
	private boolean halt = false;
	
	public static void main(String[] args)
	{
		new TestCaveNav().go();
	}
	private void go()
	{
		AHKBridge.runExitHook(this);
		
		WindowManager winMgr = new WindowManager();
		PWindow window = winMgr.getWindows().get(0);
		
		GlobalMap globalMap = new GlobalMap();
		
		double clickAngle = 0; //angle at which we attempted to move
		
		IntBitmap totalImage = null;
		IntBitmap lastImage = null;
		Point totalOffset = new Point(0, 0);
		int a = 0;
		while(a < 10 && !halt)
		{
			IntBitmap minimap = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MAP_RECT));
			
			if(lastImage == null)
			{
				a++;
				totalImage = minimap;
				lastImage = minimap;
				continue;
			}
			Point offset = PhaseCorrelation.getOffset(lastImage.toGreyscale(), minimap.toGreyscale());
			totalOffset = new Point(totalOffset.x + offset.x, totalOffset.y + offset.y);
			System.out.println("Offset: "+offset+" TotalOffset: "+totalOffset+" Angle of Offset: "+Math.atan2(offset.y, offset.x));

			globalMap.addImage(minimap, totalOffset);
			totalImage = ImageToolkit.splice(totalImage, minimap, totalOffset.x, totalOffset.y);
			totalOffset = new Point(Math.max(totalOffset.x, 0), Math.max(totalOffset.y, 0));
			Point centerPoint = new Point(totalOffset.x + 75, totalOffset.y + 75);
			globalMap.setCenter(centerPoint);
			double moveAngle = Math.atan2(offset.y, offset.x);
			//if(Math.abs(Math.atan2(offset.y, offset.x)) < 0.001d) //stuck
			if(clickAngle - moveAngle > 0.1 || Math.abs(moveAngle) < 0.001d)
			{
				Point blockedPoint = new Point(centerPoint.x + (int) Math.cos(clickAngle) * 2,centerPoint.y + (int) Math.sin(clickAngle) * 2);
				System.out.println("Blocked at "+blockedPoint);
				globalMap.markBlocked(blockedPoint);
			}
			lastImage = minimap;
			a++;
			
			ArrayList<Point> path = globalMap.findPath();
			double angle = 0;
			int dist = 0;
			int pointIndex = 0;
			Point currPoint = null;
			while(dist < 200 && pointIndex < path.size() - 1)
			{
				pointIndex ++ ;
				currPoint = path.get(pointIndex);
				//Uncenter this
				double xDist = currPoint.x - globalMap.getCenter().x;
				double yDist = -(currPoint.y - globalMap.getCenter().y);
				angle = Math.atan2(yDist, xDist);
				
				dist = (int) Math.sqrt(xDist * xDist + yDist * yDist) * 10;
			}
			System.out.println("Moved at Angle "+angle+" Dist "+dist);
			clickAngle = angle;
			PoEMacro.moveHero(window, angle, Math.min(dist, 200));
			Macro.macro.sleep(1000);
		}
		
	}
	@Override
	public void exitProgram() 
	{
		halt = true;	
	}
}
