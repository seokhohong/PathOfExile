package main;

import img.BinaryImage;
import img.Display;

import java.awt.Point;

public class TestLine 
{
	public static void main(String[] args)
	{
		new TestLine().go();
	}
	private static final int THICKNESS = 10;
	private void go()
	{
		boolean[][] imgData = new boolean[100][100];
		BinaryImage img = new BinaryImage(imgData);
		Point origin = new Point(7, 77);
		Point dest = new Point(16, 86);
		double perpendicular = Math.atan2( -(origin.x - dest.x), (origin.y - dest.y)); //angle perpendicular
		Point orthoDest = new Point((int) (origin.x + THICKNESS * Math.cos(perpendicular)), (int) (origin.y + THICKNESS * Math.sin(perpendicular)));
		img.drawLine(origin, dest, BinaryImage.WHITE);
		img.drawLine(origin, orthoDest, BinaryImage.WHITE);
		Display.showHang(img);
		for(int a = 0; a < Math.abs(origin.x - orthoDest.x); a++) //from each point along the line perpendicular to the bridge
		{
			
		}
	}
}
