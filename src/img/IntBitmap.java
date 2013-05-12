package img;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;

public class IntBitmap
{
	public static final int RGB = 3;
	
	private int[][][] data;			public int[][][] getData() { return data; }
	private int imgWidth;			public int getWidth() { return imgWidth; }
	private int imgHeight;			public int getHeight() { return imgHeight; }
	private IntBitmap(int[][][] data)
	{
		this.data = data;
		imgWidth = data.length;
		imgHeight = data[0].length;
	}
	public static IntBitmap getInstance(int[][][] data)
	{
		return new IntBitmap(data);
	}
	public static IntBitmap getInstance(BufferedImage img)
	{
		try
		{
			byte[] byteData = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
			return new IntBitmap(makeIntArray(byteData, img.getWidth(), img.getHeight()));
		}
		catch(ClassCastException e)
		{
			int[] intData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
			return new IntBitmap(makeIntArray(intData, img.getWidth(), img.getHeight()));
		}
	}
	private static int[][][] makeIntArray(int[] intArray, int imgWidth, int imgHeight)
	{
		int[][][] intArr = new int[imgWidth][imgHeight][RGB];
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				Color c = new Color(intArray[b*imgWidth + a]);
				intArr[a][b][0] = c.getRed();
				intArr[a][b][1] = c.getGreen();
				intArr[a][b][2] = c.getBlue();
			}
		}
		return intArr;
	}
	private static int[][][] makeIntArray(byte[] byteArray, int imgWidth, int imgHeight)
	{
		int[][][] intArr = new int[imgWidth][imgHeight][RGB];
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				for(int c = 0; c < RGB ; c++)
				{
					intArr[a][b][c] = (ImageToolkit.MAX_VAL + (int) byteArray[b * imgWidth * RGB + a * RGB + (2 - c)]) % ImageToolkit.MAX_VAL;
				}
			}
		}
		return intArr;
	}
	//BufferedImage -> int[][][]
	//Avoids having to expose data as public
	public static int[][][] ImgtoIntArr(BufferedImage img)
	{
		return getInstance(img).data;
	}
	
	/*
	 ***************************************
	 * Filters
	 ***************************************
	 */
	public void blur(int radius)
	{
		int[][][] blurred = new int[imgWidth][imgHeight][RGB];
		for(int a = 0; a < imgWidth ; a ++)
		{
			for(int b = 0; b < imgHeight ; b++)
			{
				int redVal = 0;
				int greenVal = 0;
				int blueVal = 0;
				int numVals = 0; //number of color values summed
				for(int c = -radius; c < radius; c++)
				{
					if(a + c < 0 || a + c >= imgWidth - 1) continue;
					for(int d = -radius ; d < radius; d++)
					{
						if(b + d < 0 || b + d >= imgHeight - 1) continue;
						redVal += data[a+c][b+d][0];
						greenVal += data[a+c][b+d][1];
						blueVal += data[a+c][b+d][2];
						numVals++;
					}
				}
				blurred[a][b][0] = redVal / numVals;
				blurred[a][b][1] = greenVal / numVals;
				blurred[a][b][2] = blueVal / numVals;
			}
		}
		data = blurred;
	}
	//All pixels with a color value above val will be blackened
	public void lowPass(int val)
	{
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				//if above, clamp
				if(data[a][b][0] > val || data[a][b][1] > val || data[a][b][2] > val)
				{
					data[a][b][0] = 0;
					data[a][b][1] = 0;
					data[a][b][2] = 0;
				}
			}
		}
	}
	//All pixels with a color value above val will be blackened
	public void highPass(int val)
	{
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				//if above, clamp
				if(data[a][b][0] < val || data[a][b][1] < val || data[a][b][2] < val)
				{
					data[a][b][0] = 0;
					data[a][b][1] = 0;
					data[a][b][2] = 0;
				}
			}
		}
	}
	public void subtract(IntBitmap img)
	{
		int[][][] otherData = img.getData();
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				//data[a][b] = minDiffWithNeighbors(otherData, a, b);
				
				for(int c = 0; c < RGB; c++)
				{
					data[a][b][c] = Math.abs(data[a][b][c] - otherData[a][b][c]);
				}
			}
		}
	}
	private int[] minDiffWithNeighbors(int[][][] otherData, int x, int y)
	{
		int[] min = new int[RGB];
		Arrays.fill(min, ImageToolkit.MAX_VAL);
		for(int a = 0; a < RGB; a++)
		{
			if(x > 0) min[a] = Math.min(min[a], Math.abs(data[x][y][a] - otherData[x-1][y][a]));
			if(x > 0 && y > 0) min[a] = Math.min(min[a], Math.abs(data[x][y][a] - otherData[x-1][y-1][a]));
			if(x > 0 && y < data[0].length - 1) min[a] = Math.min(min[a], Math.abs(data[x][y][a] - otherData[x-1][y+1][a]));
			if(x < data.length - 1) min[a] = Math.min(min[a], Math.abs(data[x][y][a] - otherData[x + 1][y][a]));
			if(x < data.length - 1 && y > 0) min[a] = Math.min(min[a], Math.abs(data[x][y][a] - otherData[x + 1][y - 1][a]));
			if(x < data.length - 1 && y < data[0].length - 1) min[a] = Math.min(min[a], Math.abs(data[x][y][a] - otherData[x + 1][y + 1][a]));
			if(y > 0) min[a] = Math.min(min[a], Math.abs(data[x][y][a] - otherData[x][y-1][a]));
			if(y < data[0].length - 1) min[a] = Math.min(min[a], Math.abs(data[x][y][a] - otherData[x][y + 1][a]));
		}
		return min;
	}
	public GreyscaleImage negHorizDerivative()
	{
		int[][] newData = new int[getWidth()][getHeight()];
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				int leftDeriv = a>0 ? ImageToolkit.colorDiff(data[a][b], data[a-1][b]) : 0;
				int rightDeriv = a<getWidth()-1 ? ImageToolkit.colorDiff(data[a][b], data[a+1][b]) : 0;
				newData[a][b] = ImageToolkit.MAX_VAL - (leftDeriv + rightDeriv);
			}
		}
		return new GreyscaleImage(newData);
	}
	private static final int CONTRAST = 3;
	public GreyscaleImage lineDerivative()
	{
		int[][] newData = new int[getWidth()][getHeight()];
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				int leftDeriv = a > 0 ? ImageToolkit.colorDiff(data[a][b], data[a-1][b]) : 0;
				int rightDeriv = a < getWidth()-1 ? ImageToolkit.colorDiff(data[a][b], data[a+1][b]) : 0;
				int upDeriv = b > 0 ? ImageToolkit.colorDiff(data[a][b], data[a][b-1]) : 0;
				int downDeriv = b < getHeight()-1 ? ImageToolkit.colorDiff(data[a][b], data[a][b+1]) : 0;
				newData[a][b] = CONTRAST * (upDeriv + downDeriv)/(leftDeriv + rightDeriv + 1); //avoid div by 0
			}
		}
		return new GreyscaleImage(newData);
	}
	/**
	 * 
	 * Gets whiter value for pixels that have greater contrast with the one above and are similar to the ones below
	 * Accentuates the top lines of items
	 * 
	 */
	public GreyscaleImage emphasisUpDerivative()
	{
		int[][] data = new int[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{		
				data[a][b] = Math.max(getDelta(a, b, 0, -1) - getDelta(a, b, 0, 1), 0);  //can't go negative
			}
		}
		return new GreyscaleImage(data);
	}
	public GreyscaleImage emphasisDownDerivative()
	{
		int[][] data = new int[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{
				data[a][b] = Math.max(getDelta(a, b, 0, 1) - getDelta(a, b, 0, -1), 0);  //can't go negative
			}
		}
		return new GreyscaleImage(data);
	}
	public GreyscaleImage bidirectionalDerivative()
	{
		int[][] data = new int[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{
				data[a][b] = (getDelta(a, b, 0, 1) + getDelta(a, b, 1, 0)) / 2;
			}
		}
		return new GreyscaleImage(data);
	}
	public GreyscaleImage fourdirectionalDerivative()
	{
		int[][] data = new int[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{
				data[a][b] = (getDelta(a, b, -1, 0) + 
							getDelta(a, b, 0, -1) + 
							getDelta(a, b, 0, 1) + 
							getDelta(a, b, 1, 0)) / 4;
			}
		}
		return new GreyscaleImage(data);
	}
	public GreyscaleImage omnidirectionalDerivative()
	{
		int[][] data = new int[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{
				data[a][b] = (getDelta(a, b, -1, -1) + 
						getDelta(a, b, -1, 0) + 
						getDelta(a, b, -1, 1) + 
						getDelta(a, b, 0, -1) + 
						getDelta(a, b, 0, 1) + 
						getDelta(a, b, 1, -1) + 
						getDelta(a, b, 1, 0) + 
						getDelta(a, b, 1, 1)) / 8;
			}
		}
		return new GreyscaleImage(data);
	}
	public GreyscaleImage verticalDerivative()
	{
		int[][] data = new int[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{
				data[a][b] = getDelta(a, b, 0, 1);
			}
		}
		return new GreyscaleImage(data);
	}
	private int getDelta(int x, int y, int dx, int dy)
	{
		int altX = x + dx;
		int altY = y + dy;
		if(altX < 0 || altY < 0 || altX > imgWidth - 1 || altY > imgHeight - 1) return 0;
		return (Math.abs(getRed(altX, altY) - getRed(x, y)) + 
					Math.abs(getGreen(altX, altY) - getRed(x, y)) +
					Math.abs(getRed(altX, altY) - getRed(x, y))) / 3 ; //properly scale
	}
	public GreyscaleImage toGreyscale()
	{
		int[][] data = new int[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{
				data[a][b] = (this.data[a][b][0] + this.data[a][b][1] + this.data[a][b][2]) / 3;
			}
		}
		return new GreyscaleImage(data);
	}
	private static final int COLOR_THRESHOLD = 10;
	public Point findImage(IntBitmap icon)
	{
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				if(ImageToolkit.colorDiff(data[a][b], icon.data[0][0]) < COLOR_THRESHOLD)
				{
					boolean valid = true;
					for(int c = 0; c < icon.getWidth(); c++)
					{
						for(int d = 0; d < icon.getHeight(); d++)
						{
							if(c+a < data.length && d+b < data[0].length 
									&& ImageToolkit.colorDiff(data[c + a][d + b], icon.data[c][d]) > COLOR_THRESHOLD)
							{
								valid = false;
								break;
							}
						}
					}
					if(valid)
					{
						return new Point(a, b);
					}
				}
			}
		}
		return null;
	}
	//Returns the points at which icon was found on the screen
	public ArrayList<Point> findImages(IntBitmap icon)
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				if(ImageToolkit.colorDiff(data[a][b], icon.data[0][0]) < COLOR_THRESHOLD)
				{
					boolean valid = true;
					for(int c = 0; c < icon.getWidth(); c++)
					{
						for(int d = 0; d < icon.getHeight(); d++)
						{
							if(c+a < data.length && d+b < data[0].length 
									&& ImageToolkit.colorDiff(data[c + a][d + b], icon.data[c][d]) > COLOR_THRESHOLD)
							{
								valid = false;
								break;
							}
						}
					}
					if(valid)
					{
						points.add(new Point(a, b));
					}
				}
			}
		}
		return points;
	}
	public void export(String filename)
	{
		BufferedImage toExport = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = toExport.createGraphics();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				g.setColor(new Color(data[a][b][0], data[a][b][1], data[a][b][2]));
				g.drawLine(a, b, a, b);
			}
		}
		ImageToolkit.exportImage(toExport, filename);
	}
	public int getRed(int x, int y) { return data[x][y][0]; }		
	public int getGreen(int x, int y) { return data[x][y][1]; }
	public int getBlue(int x, int y) { return data[x][y][2]; }
	public void setRed(int x, int y, int val) { data[x][y][0] = val; }		
	public void setGreen(int x, int y, int val) { data[x][y][1] = val; }
	public void setBlue(int x, int y, int val) { data[x][y][2] = val; }
	public void subtractRed(int val) {
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				int newval = getRed(a, b) - val;
				if(newval < 0) { newval = 0; }
				if(newval > 255) { newval = 255; }
				setRed(a, b, newval);
			}
		}
	}
	public void subtractBlue(int val) {
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				int newval = getBlue(a, b) - val;
				if(newval < 0) { newval = 0; }
				if(newval > 255) { newval = 255; }
				setBlue(a, b, newval);
			}
		}
	}
	public void subtractGreen(int val) {
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				int newval = getGreen(a, b) - val;
				if(newval < 0) { newval = 0; }
				if(newval > 255) { newval = 255; }
				setGreen(a, b, newval);
			}
		}
	}
}
