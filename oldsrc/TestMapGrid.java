package main;

import img.BinaryImage;
import img.BleedResult;
import img.Bleeder;
import img.Display;
import img.FilterType;
import img.ImageToolkit;
import img.IntBitmap;
import img.RatioFilter;

import java.awt.Point;
import java.util.ArrayList;

import map.GlobalMap;
import math.PhaseCorrelation;

public class TestMapGrid 
{
	public static void main(String[] args)
	{
		new TestMapGrid().go();
	}
	private void go()
	{
		GlobalMap globalMap = new GlobalMap();
		
		IntBitmap map1 = IntBitmap.getInstance(ImageToolkit.loadImage("img/MinimapA5.bmp"));
		//IntBitmap map2 = IntBitmap.getInstance(ImageToolkit.loadImage("img/MinimapA4.bmp"));
		
		globalMap.addImage(map1, new Point(0, 0));
		//globalMap.addImage(map2, PhaseCorrelation.getOffset(map1.toGreyscale(), map2.toGreyscale()));
		
		globalMap.findPath();
	}
	/*
	private IntBitmap processImage(IntBitmap img)
	{
		IntBitmap bridges = IntBitmap.copy(img);

		ArrayList<FilterType> filters = new ArrayList<FilterType>();
		filters.add(FilterType.CAVE_WATER);
		filters.add(FilterType.CAVE_JUNK);
		//filters.add(FilterType.CAVE_BRIDGE);
		
		RatioFilter.eliminateRatio(img, filters);
		
		int[][][] mapData = img.getData();
		
		RatioFilter.maintainRatio(bridges, FilterType.CAVE_BRIDGE);
		Bleeder bridgeBleeder = new Bleeder(1);
		for(BleedResult result : bridgeBleeder.find(bridges.toGreyscale().doubleCutoff(50)))
		{
			if(result.getNumPixels() < 10)
			{
				continue;
			}
			Point origin = result.getOrigin();
			Point dest = result.getDest();
			for(int a = 0; a < IntBitmap.RGB; a++)
			{
				mapData[origin.x][origin.y][a] = 255;
			}
			for(int a = 0; a < IntBitmap.RGB; a++)
			{
				mapData[dest.x][dest.y][a] = 255;
			}
		}
		
		Display.showHang(img);
		
		//Display.showHang(map);
		BinaryImage bin = img.toGreyscale().doubleCutoff(50);
		bin.invert();
		//bin.reconstructGaps(8, BinaryImage.WHITE);
		

		
		
		Display.showHang(bin);
		return bin;
	}
	private static final double THICKNESS = 200;
	private void drawBridge(BinaryImage bin, BleedResult br)
	{
		Point origin = br.getOrigin().x < br.getDest().x ? br.getOrigin() : br.getDest();
		Point dest = br.getOrigin().x < br.getDest().x ? br.getDest() : br.getOrigin();
		double perpendicular = Math.atan2( -(origin.x - br.getDest().x), (origin.y - br.getDest().y)); //angle perpendicular
		Point orthoDest = new Point((int) (origin.x + THICKNESS * Math.cos(perpendicular)), (int) (origin.y + THICKNESS * Math.sin(perpendicular)));
		//line perpendicular to the bridge
		
		int dx = orthoDest.x - origin.x; //invisible line marking the starting points of all lines
		int dy = orthoDest.y - origin.y;
		double error = 0;
		double deltaError = Math.abs((double) dy / dx);
		System.out.println(deltaError);
		int y = origin.y;
		for(int a = origin.x; a < origin.x + Math.abs(origin.x - orthoDest.x); a++) //from each point along the line perpendicular to the bridge
		{
			//draw a line across the bridge
			System.out.println(a+" "+y);
			bin.drawLine(new Point(a, y), new Point(a + (dest.x - origin.x), y + (dest.y - origin.y)), BinaryImage.BLACK);
			error += deltaError;
			if(error >= 0.5d)
			{
				
				y ++;
				//error -=;
			}
		}
	}
	*/
}
