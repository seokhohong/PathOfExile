package img;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

/**
 * 
 * A bitmap representing each pixel with an int[] of rgb values from 0 to 255
 * 
 * Contains generic operations on bitmaps
 * 
 * @author Seokho
 *
 */
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
	//Copy constructor
	public static IntBitmap copy(IntBitmap img)
	{
		int[][][] oldData = img.data;
		int[][][] newData = new int[img.imgWidth][img.imgHeight][RGB];
		for(int a = 0; a < img.imgWidth; a++)
		{
			for(int b = 0; b < img.imgHeight; b++)
			{
				for(int c = 0; c < RGB; c++)
				{
					newData[a][b][c] = oldData[a][b][c];
				}
			}
		}
		return new IntBitmap(newData);
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
					intArr[a][b][c] = (ImageToolkit.MAX_VAL + byteArray[b * imgWidth * RGB + a * RGB + (2 - c)]) % ImageToolkit.MAX_VAL;
				}
			}
		}
		return intArr;
	}
	
	public IntBitmap subimage(Rectangle rect)
	{
		int[][][] newData = new int[rect.width][rect.height][RGB];
		for(int a = 0; a < rect.width && a + rect.x < data.length; a++)
		{
			for(int b = 0; b < rect.height && b + rect.y < data[0].length; b++)
			{
				for(int c = 0; c < RGB ; c++)
				{
					newData[a][b][c] = data[a + rect.x][b + rect.y][c];
				}
			}
		}
		return new IntBitmap(newData);
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
	
	
	//All pixels with a color value above val will be blackened
	public void highPassByAverage(int val)
	{
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				//if above, clamp
				if((data[a][b][0] + data[a][b][1] + data[a][b][2]) / 3 < val)
				{
					data[a][b][0] = 0;
					data[a][b][1] = 0;
					data[a][b][2] = 0;
				}
			}
		}
	}
	//Values above this amount will be capped
	public void cap(int red, int green, int blue)
	{
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				Math.min(data[a][b][0], red);
				Math.min(data[a][b][1], green);
				Math.min(data[a][b][2], blue);
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
	public BinaryImage doubleCutoff(int cutoff)
	{
		boolean[][] data = new boolean[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{
				data[a][b] = (this.data[a][b][0] + this.data[a][b][1] + this.data[a][b][2]) / 3 > cutoff;
			}
		}
		return new BinaryImage(data);
	}
	//Returns whether data and iconData have matching information given the a, b offset in data
	private boolean foundMatch(int[][][] data, int[][][] iconData, int a, int b, int threshold)
	{
		boolean valid = true;
		for(int c = 0; c < iconData.length; c++)
		{
			for(int d = 0; d < iconData[0].length; d++)
			{
				if(c+a < data.length && d+b < data[0].length)
				{
					if(ImageToolkit.colorDiff(data[c + a][d + b], iconData[c][d]) > threshold)
					{
						valid = false; break;
					}
				}
				else
				{
					valid = false; break;
				}
				
			}
		}
		return valid;
	}
	/**
	 * 
	 * Dumbest matching function ever
	 * 
	 * @param icon
	 * @return
	 */
	private static final int DEFAULT_THRESHOLD = 10;
	public Point findImage(IntBitmap icon)
	{
		return findImage(icon, DEFAULT_THRESHOLD);
	}
	public Point findImage(IntBitmap icon, int threshold)
	{
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				if(ImageToolkit.colorDiff(data[a][b], icon.data[0][0]) < threshold)
				{
					if(foundMatch(data, icon.data, a, b, threshold)) return new Point(a, b);
				}
			}
		}
		return null;
	}
	private boolean foundItemMatch(int[][][] data, int[][][] iconData, int a, int b, double selectivityRatio)
	{
		double trueCount = 0;
		for(int c = 0; c < iconData.length; c++)
		{
			for(int d = 0; d < iconData[0].length; d++)
			{
				if(c+a < data.length && d+b < data[0].length)
				{
					if(ImageToolkit.colorDiff(data[c + a][d + b], iconData[c][d]) < 60)
					{
						trueCount++;
						//System.out.println("Got one.");
					}
				}
			}
		}
		//System.out.println("We got here");
		//System.out.println((double)trueCount/((double)iconData.length * (double)iconData[0].length));
		if(trueCount/((double)iconData.length * (double)iconData[0].length) >= selectivityRatio)
		{
			return true;
		}
		return false;
	}
	public Point findItemImage(IntBitmap icon, double selectivityRatio)
	{
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				if(ImageToolkit.colorDiff(data[a][b], icon.data[0][0]) < 40)
				{
					if(foundItemMatch(data, icon.data, a, b, selectivityRatio)) return new Point(a, b);
				}
			}
		}
		return null;
	}
	//Returns the points at which icon was found on the screen
	public ArrayList<Point> findImages(IntBitmap icon)
	{
		return findImages(icon, DEFAULT_THRESHOLD);
	}
	//Returns the points at which icon was found on the screen
	public ArrayList<Point> findImages(IntBitmap icon, int threshold)
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				if(ImageToolkit.colorDiff(data[a][b], icon.data[0][0]) < threshold)
				{
					if(foundMatch(data, icon.data, a, b, threshold)) points.add(new Point(a, b));
				}
			}
		}
		return points;
	}
	public BufferedImage toBufferedImage()
	{
		BufferedImage buff = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = buff.createGraphics();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				g.setColor(new Color(data[a][b][0], data[a][b][1], data[a][b][2]));
				g.drawLine(a, b, a, b);
			}
		}
		g.dispose();
		return buff;
	}
	public void export(String filename)
	{
		ImageToolkit.exportImage(toBufferedImage(), filename);
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
	
	public void isolate(Color rgb)
	{
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				if(rgb == Color.red)
				{
					setGreen(a, b, 0);
					setBlue(a, b, 0);
				}
				else if(rgb == Color.green)
				{
					setRed(a, b, 0);
					setBlue(a, b, 0);
				}
				else if(rgb == Color.blue)
				{
					setRed(a, b, 0);
					setGreen(a, b, 0);
				}
			}
		}
	}
	public double averageColor(Color rgb)
	{
		double result = 0;
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				if(rgb == Color.red)
				{
					result = result + getRed(a, b);
				}
				else if(rgb == Color.green)
				{
					result = result + getGreen(a, b);
				}
				else if(rgb == Color.blue)
				{
					result = result + getBlue(a, b);
				}
			}
		}
		return result/(getWidth()*getHeight());
	}
	private void blackPixel(Point p)
	{
		setRed(p.x, p.y, 0);
		setGreen(p.x, p.y, 0);
		setBlue(p.x, p.y, 0);
	}
	
	public void blackRectangle(Rectangle r)
	{
		for(int a = r.x; a < r.x + r.width; a++)
		{
			for(int b = r.y; b < r.y + r.height; b++)
			{
				blackPixel(new Point(a, b));
			}
		}
	}
	
	public String averageSetToString()
	{
		double r = averageColor(Color.red);
		double g = averageColor(Color.green);
		double b = averageColor(Color.blue);
		//double z = toGreyscale().averageIntensity(); //can be obtained just by averaging r,g,b -- thus no new information
		return new String(r + ", " + g + ", " + b);
	}
	public double matchError(IntBitmap icon)
	{		
		//this.blackRectangle(new Rectangle(0, 0, 13, 10));
		//icon.blackRectangle(new Rectangle(0, 0, 13, 10));
		
		double r1 = this.averageColor(Color.red);
		double g1 = this.averageColor(Color.green);
		double b1 = this.averageColor(Color.blue);
		
		double r2 = icon.averageColor(Color.red);
		double g2 = icon.averageColor(Color.green);
		double b2 = icon.averageColor(Color.blue);
		
		double dr = Math.abs(r1 - r2);
		double dg = Math.abs(g1 - g2);
		double db = Math.abs(b1 - b2);

		ArrayList<Double> brightErrors = this.getBrightnessErrors(icon);
		double brightnessError = 0;
		for(int i = 0; i < 10; i++)
		{
			brightnessError = brightnessError + brightErrors.get(i)*brightErrors.get(i);
		}
		return dr*dr + dg*dg + db*db + brightnessError*brightnessError;
	}
	
	private ArrayList<Double> getBrightnessErrors(IntBitmap icon)
	{
		final int INCREMENT = 50;
		ArrayList<Double> errors = new ArrayList<Double>();
		//icon.blackRectangle(new Rectangle(0, 0, 13, 10));
		//this.blackRectangle(new Rectangle(0, 0, 13, 10));
		
		for(int i = 1; i <= 5; i++)
		{
			errors.add(Math.abs(icon.brightContent(INCREMENT * i) - this.brightContent(INCREMENT * i)));
		}
		for(int i = 1; i <= 5; i++)
		{
			errors.add(Math.abs(icon.darkContent(INCREMENT * i) - this.darkContent(INCREMENT * i)));
		}
		return errors;
		
	}
	public double brightContent(int cutoff)
	{
		GreyscaleImage grey = this.toGreyscale();
		grey.highPass(cutoff);
		return grey.averageIntensity();
	}
	public double darkContent(int cutoff)
	{
		GreyscaleImage grey = this.toGreyscale();
		grey.lowPass(cutoff);
		return grey.averageIntensity();
	}
	
	
	private static final double MATCH_ERROR_THRESHOLD = 7;
	public boolean isMatch(IntBitmap icon)
	{
		double error = this.matchError(icon);
		if(error <= MATCH_ERROR_THRESHOLD)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Changes this image to an IntBitmap of width / dim x height / dim dimensions
	 * 
	 * Each dim x dim square in the original will be replaced with the top-left pixel
	 * 
	 * @param dim
	 */
	public void sample(int dim)
	{
		int[][][] sampled = new int[imgWidth / dim][imgHeight / dim][RGB];
		for(int a = 0; a < imgWidth / dim; a++)
		{
			for(int b = 0; b < imgHeight / dim; b++)
			{
				sampled[a][b] = data[a * dim][b * dim];
			}
		}
		imgWidth /= dim;
		imgHeight /= dim;
		data = sampled;
	}
	
}
