package img;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
	public static BufferedImage takeScreenshot()
	{
		return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}
	//Deep copies to produce a new copy of the area (this isn't cheap)
	public static BufferedImage getSubimage(BufferedImage orig, Rectangle rect)
	{
		int[][][] origData = IntBitmap.ImgtoIntArr(orig);
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
	public static IntBitmap splice(IntBitmap original, IntBitmap toAdd, int xOffset, int yOffset)
	{
		int[][][] origData = original.getData();
		int[][][] toAddData = toAdd.getData();
		//new dimensions
		int[][][] combined = new int[xOffset + toAdd.getWidth()][yOffset + toAdd.getHeight()][];
		int origWidth = original.getWidth();
		int origHeight = original.getHeight();
		int toAddWidth = toAdd.getWidth();
		int toAddHeight = toAdd.getHeight();
		for(int a = 0; a < xOffset + toAddWidth; a++)
		{
			for(int b = 0; b < yOffset + toAddHeight; b++)
			{
				if(a - xOffset > 0 && b - yOffset > 0) //if the second image has the right pixels
				{
					combined[a][b] = toAddData[a - xOffset][b - yOffset];
				}
				else if(a < origWidth && b < origHeight) //else the first fills in
				{
					combined[a][b] = origData[a][b];
				}
				else //the corners
				{
					combined[a][b] = blackPixel();
				}
			}
		}
		return IntBitmap.getInstance(combined);
	}
	public static GreyscaleImage splice(GreyscaleImage original, GreyscaleImage toAdd, int xOffset, int yOffset)
	{
		int[][] origData = original.getData();
		int[][] toAddData = toAdd.getData();
		int[][] combined = new int[xOffset + toAdd.getWidth()][yOffset + toAdd.getHeight()];
		int origWidth = original.getWidth();
		int origHeight = original.getHeight();
		int toAddWidth = toAdd.getWidth();
		int toAddHeight = toAdd.getHeight();
		for(int a = 0; a < xOffset + toAddWidth; a++)
		{
			for(int b = 0; b < yOffset + toAddHeight; b++)
			{
				if(a - xOffset > 0 && b - yOffset > 0) //if the second image has the right pixels
				{
					combined[a][b] = toAddData[a - xOffset][b - yOffset];
				}
				else if(a < origWidth && b < origHeight) //else the first fills in
				{
					combined[a][b] = origData[a][b];
				}
				else //the corners
				{
					combined[a][b] = 0;
				}
			}
		}
		return new GreyscaleImage(combined);
	}

	//returns an array of 0, 0, 0
	private static int[] blackPixel()
	{
		int[] black = {0, 0, 0};
		return black;
	}
}
