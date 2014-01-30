package map;

import java.awt.Point;

import img.*;

public class MapImage 
{
	//This first image is the anchor for this MapImage object
	//Any checks against this image to verify that a given map is this map will be performed against the anchor image
	private BinaryImage anchor;
	private BinaryImage lastImage;	//allows the calculation of offsets for newly added images
	private int lastOffsetX;
	private int lastOffsetY;
	private BinaryImage world; 		//the whole world
	public MapImage(BinaryImage anchor)
	{
		this.anchor = anchor;
		lastImage = anchor;
		world = anchor;
		lastOffsetX = 0;
		lastOffsetY = 0;
	}
	//Takes the result of the fourier image and mirrors them to create an image 2x x 2x the original size;
	private GreyscaleImage mirrorFourier(GreyscaleImage fourier)
	{
		int[][] finalData = new int[fourier.getWidth() * 2][fourier.getHeight() * 2];
		int[][] fourierData = fourier.getData();
		//quadrant 2
		for(int a = 0; a < fourier.getWidth(); a++)
			for(int b = 0; b < fourier.getHeight(); b++)
				finalData[a][b] = fourierData[fourier.getWidth() - a - 1][fourier.getHeight() - b - 1];
		//quadrant 1
		for(int a = 0; a < fourier.getWidth(); a++)
			for(int b = 0; b < fourier.getHeight(); b++)
				finalData[a + fourier.getWidth()][b] = fourierData[a][fourier.getHeight() - b - 1];
		//quadrant 3
		for(int a = 0; a < fourier.getWidth(); a++)
			for(int b = 0; b < fourier.getHeight(); b++)
				finalData[a][b + fourier.getHeight()] = fourierData[fourier.getWidth() - a - 1][b];
		//quadrant 4
		for(int a = 0; a < fourier.getWidth(); a++)
			for(int b = 0; b < fourier.getHeight(); b++)
				finalData[a + fourier.getWidth()][b + fourier.getHeight()] = fourierData[a][b];
		return new GreyscaleImage(finalData);
	}
	/**
	 * 
	 * Adds an image to the database
	 * 
	 * @param add
	 * @return		: Whether the image connected well enough to the rest of the map
	 */
	public boolean addImage(BinaryImage add)
	{
		/*
		double time = System.currentTimeMillis();
		GreyscaleImage fourier = FourierTransform.getOffset(lastImage.toGreyscale(), add.toGreyscale(), 0);
		GreyscaleImage mirror = mirrorFourier(fourier);
		GreyscaleImage ratings = getRatings(lastImage.toGreyscale(), add.toGreyscale(), 50);
		ratings.invert();
		lastImage.toGreyscale().display("LastImage");
		add.toGreyscale().display("Add");
		mirror.expand(10).display();
		ratings.expand(10).display();
		mirror.multiply(ratings);
		mirror.multiply(ratings);
		mirror.expand(10).display("Combined", true);
		
		 */
		Point offset = getOffset(lastImage.toGreyscale(), add.toGreyscale(), (add.getWidth() - 4) * (add.getHeight() - 4));
		if(offset == null) return false;
		lastImage = add;
		world = ImageToolkit.splice(add, world, -(offset.x - lastImage.getWidth()), - (offset.y - lastImage.getHeight()));
		System.out.println(offset.x + " " + offset.y);
		return true;
		//System.out.println("Splice Time: " + (System.currentTimeMillis() - time));
	}
	private Point getOffset(GreyscaleImage img1, GreyscaleImage img2, int minOverlap)
	{
		GreyscaleImage ratings = getRatings(img1, img2, minOverlap);
		ratings.invert();
		//ratings.display(true);
		
		//img1.display("img1");
		//img2.display("img2");
		//ratings.display("MatchRatings");
		return brightestPoint(ratings);
	}
	private Point brightestPoint(GreyscaleImage img)
	{
		int[][] data = img.getData();
		int brightest = 0;
		Point brightestPoint = null;
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				if(data[a][b] > brightest)
				{
					brightestPoint = new Point(a, b);
					brightest = data[a][b];
				}
			}
		}
		System.out.println("Brightest Rating: "+brightest);
		return brightestPoint;
	}
	//Maybe move this into GreyscaleImage class?
	private int CONST_DIST = 4;
	private void accentCenter(GreyscaleImage img)
	{
		int[][] data = img.getData();
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				data[a][b] = (int) (data[a][b] / Math.log10(distance(a, b, img.getWidth() / 2, img.getHeight() / 2) + CONST_DIST));
			}
		}
	}
	private double distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	private GreyscaleImage getRatings(GreyscaleImage img1, GreyscaleImage img2, int minOverlap)
	{
		int[][] matchRatings = new int[img1.getWidth() + img2.getWidth()][img1.getHeight() + img2.getHeight()];
		int[][] data1 = img1.getData();
		int[][] data2 = img2.getData();
		for(int a = 0; a < matchRatings.length; a++)
		{
			for(int b = 0; b < matchRatings[0].length; b++)
			{
				int xOffset = a - img2.getWidth();
				int yOffset = b - img2.getHeight();
				int match = 0;
				int numPoints = 1;
				for(int c = 0; c < img2.getWidth(); c++)
				{
					for(int d = 0; d < img2.getHeight(); d++)
					{
						int data1x = xOffset + c;
						int data1y = yOffset + d;
						if(data1x >= 0 && data1x < data1.length && data1y >= 0 && data1y < data1[0].length)
						{
							match += Math.abs(data1[data1x][data1y] - data2[c][d]);
							numPoints++;
						}
					}
				}
				if(numPoints > minOverlap)
				{
					match /= numPoints;
					match *= 2;
					matchRatings[a][b] = match;
				}
				else
				{
					matchRatings[a][b] = ImageToolkit.MAX_VAL;
				}
			}
		}
		return new GreyscaleImage(matchRatings);
	}
	public void export(String filenameWhole, String filenameAnchor)
	{
		world.export(filenameWhole);
		anchor.export(filenameAnchor);
	}
}
