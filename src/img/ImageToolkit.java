package img;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * 
 * A collection of static methods making it more convenient to deal with images, particularly with image files
 * 
 * @author Seokho
 *
 */
public class ImageToolkit 
{
	private static Robot robot;
	static
	{
		try
		{
			robot = new Robot();
		}
		catch(AWTException e) {}
	}
	public static final int MAX_VAL = 255;
	public static BufferedImage loadImage(String filename)
	{
		try
		{
			return ImageIO.read(new File(filename));
		}
		catch(IOException e)
		{
			System.err.println("Failed to read "+filename);
			System.exit(1);
			return null;
		}
	}
	public static BufferedImage takeScreenshot(Rectangle screenRect)
	{
		return robot.createScreenCapture(screenRect);
	}
	public static BufferedImage takeScreenshot()
	{
		return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}
	//Deep copies to produce a new copy of the area (this isn't cheap)
	//IntBitmap probably is cheaper?
	public static BufferedImage getSubimage(BufferedImage orig, Rectangle rect)
	{
		int[][][] origData = IntBitmap.getInstance(orig).getData();
		BufferedImage sub = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = sub.createGraphics();
		for(int a = rect.x; a < rect.x + rect.width; a++)
		{
			for(int b = rect.y; b < rect.y + rect.height; b++)
			{
				g.setColor(new Color(origData[a][b][0], origData[a][b][1], origData[a][b][2]));
				g.drawLine(a - rect.x, b - rect.y, a - rect.x, b - rect.y);
			}
		}
		g.dispose();
		return sub;
	}
	//Returns the whiteness of this pixel
	public static int brightness(int[] pixel)
	{
		return (pixel[0] + pixel[1] + pixel[2])/3;
	}
	public static BufferedImage deepCopy(BufferedImage bi) 
	{
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	public static void exportImage(BufferedImage img, String filename)
	{
		try
		{
			ImageIO.write(img, filename.substring(filename.lastIndexOf('.') + 1), new File(filename));
		}
		catch(Exception e)
		{
			System.err.println("Failed to write "+filename);
		}
	}
	public static int colorDiff(int[] one, int[] two) //rgb array
	{
		return Math.abs(one[0] - two[0]) + Math.abs(one[1] - two[1]) + Math.abs(one[2] - two[2]);
	}
	public static IntBitmap splice(IntBitmap base, IntBitmap toAdd, int xOffset, int yOffset)
	{
		int[][][] baseData = base.getData();
		int[][][] toAddData = toAdd.getData();
		int combinedWidth = Math.max(base.getWidth() + Math.max(-xOffset, 0), toAdd.getWidth() + Math.max(xOffset, 0));
		int combinedHeight = Math.max(base.getHeight() + Math.max(-yOffset, 0), toAdd.getHeight() + Math.max(yOffset, 0));
		int[][][] combined = new int[combinedWidth][combinedHeight][IntBitmap.RGB];
		for(int a = 0; a < base.getWidth(); a ++)
		{
			for(int b = 0; b < base.getHeight(); b++)
			{
				for(int c = 0; c < IntBitmap.RGB; c++)
				{
					combined[a + Math.max(-xOffset, 0)][b + Math.max(-yOffset, 0)][c] = baseData[a][b][c];
				}
			}
		}
		for(int a = 0; a < toAdd.getWidth(); a ++)
		{
			for(int b = 0; b < toAdd.getHeight(); b++)
			{
				for(int c = 0; c < IntBitmap.RGB; c++)
				{
					combined[a + Math.max(xOffset, 0)][b + Math.max(yOffset, 0)][c] = toAddData[a][b][c];
				}
			}
		}
		return IntBitmap.getInstance(combined);
	}
	public static GreyscaleImage splice(GreyscaleImage base, GreyscaleImage toAdd, int xOffset, int yOffset)
	{
		int[][] baseData = base.getData();
		int[][] toAddData = toAdd.getData();
		int[][] combined = new int[Math.min(base.getWidth(), toAdd.getWidth()) + Math.abs(xOffset)]
											[Math.min(base.getHeight(), toAdd.getHeight()) + Math.abs(yOffset)];
		for(int a = 0; a < base.getWidth(); a ++)
		{
			for(int b = 0; b < base.getHeight(); b++)
			{
				combined[a + Math.max(-xOffset, 0)][b + Math.max(-yOffset, 0)] = baseData[a][b];
			}
		}
		for(int a = 0; a < toAdd.getWidth(); a ++)
		{
			for(int b = 0; b < toAdd.getHeight(); b++)
			{
				combined[a + Math.max(xOffset, 0)][b + Math.max(yOffset, 0)] = toAddData[a][b];
			}
		}
		return new GreyscaleImage(combined);
	}
	//offset is from base's top left corner
	public static BinaryImage splice(BinaryImage base, BinaryImage toAdd, int xOffset, int yOffset)
	{
		boolean[][] baseData = base.getData();
		boolean[][] toAddData = toAdd.getData();
		boolean[][] combined = new boolean[Math.max(base.getWidth(), toAdd.getWidth()) + Math.abs(xOffset)]
											[Math.max(base.getHeight(), toAdd.getHeight()) + Math.abs(yOffset)];
		for(int a = 0; a < base.getWidth(); a ++)
		{
			for(int b = 0; b < base.getHeight(); b++)
			{
				combined[a + Math.max(-xOffset, 0)][b + Math.max(-yOffset, 0)] = baseData[a][b];
			}
		}
		for(int a = 0; a < toAdd.getWidth(); a ++)
		{
			for(int b = 0; b < toAdd.getHeight(); b++)
			{
				combined[a + Math.max(xOffset, 0)][b + Math.max(yOffset, 0)] = toAddData[a][b];
			}
		}
		return new BinaryImage(combined);
	}
	
	public static void ratioTool(IntBitmap image, ArrayList<Point> points)
	{
		double red;
		double green;
		double blue;
		double RG;
		double GB;
		for(int a = 0; a < points.size(); a++)
		{
			red = image.getRed(points.get(a).x, points.get(a).y);
			green = image.getGreen(points.get(a).x, points.get(a).y);
			blue = image.getBlue(points.get(a).x, points.get(a).y);
			RG = red/green;
			GB = green/blue;
			//System.out.println(RG + ", " + GB);
			System.out.println(red + ", " + green + ", " + blue);
		}
	}
	
}
