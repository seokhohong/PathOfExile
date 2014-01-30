package img;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * 
 * An image of shades of grey. Each pixel is represented by a value between 0 and 255
 * 
 * Class contains generic operations on greyscale images
 * 
 * @author Seokho
 *
 */
public class GreyscaleImage 
{
	private int[][] data;		public int[][] getData() { return data; }
	private int imgWidth;		public int getWidth() { return imgWidth; }
	private int imgHeight;		public int getHeight() { return imgHeight; }
	public GreyscaleImage(int[][] data)
	{
		this.data = data;
		imgWidth = data.length;
		imgHeight = data[0].length;
	}
	
	public static GreyscaleImage copy(GreyscaleImage img)
	{
		int[][] oldData = img.data;
		int[][] newData = new int[img.imgWidth][img.imgHeight];
		for(int a = 0; a < img.imgWidth; a++)
		{
			for(int b = 0; b < img.imgHeight; b++)
			{
				newData[a][b] = oldData[a][b];
			}
		}
		return new GreyscaleImage(newData);
	}
	
	public void export(String filename)
	{
		BufferedImage toExport = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = toExport.createGraphics();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				int color = Math.max(Math.min(data[a][b], 255), 0);
				g.setColor(new Color(color, color, color));
				g.drawLine(a, b, a, b);
			}
		}
		ImageToolkit.exportImage(toExport, filename);
	}
	/**
	 * Multiplies all pixels by a constant factor.
	 * 
	 * @param factor : The amount by which all pixel values are multiplied
	 */
	public void multiply(double factor)
	{
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				data[a][b] *= factor;
				data[a][b] = Math.min(data[a][b], ImageToolkit.MAX_VAL);
			}
		}
	}
	public void invert()
	{
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				data[a][b] = ImageToolkit.MAX_VAL - data[a][b];
			}
		}
	}
	//Simple blur (not gaussian)
	public void blur(int radius)
	{
		if(radius <=0 ) return;
		int[][] blurred = new int[imgWidth][imgHeight];
		for(int a = 0; a < imgWidth ; a ++)
		{
			for(int b = 0; b < imgHeight ; b++)
			{
				int greyVal = 0;
				int numVals = 0; //number of color values summed
				for(int c = -radius; c < radius; c++)
				{
					if(a + c < 0 || a + c >= imgWidth - 1) continue;
					for(int d = -radius ; d < radius; d++)
					{
						if(b + d < 0 || b + d >= imgHeight - 1) continue;
						greyVal += data[a+c][b+d];
						numVals++;
					}
				}
				blurred[a][b] = greyVal / numVals;
			}
		}
		data = blurred;
	}
	/**
	 * Returns a deep-copy subimage of the original image
	 * @param rect	: Delineates the rectangle
	 * @return
	 */
	public GreyscaleImage subimage(Rectangle rect)
	{
		int[][] newData = new int[rect.width][rect.height];
		for(int a = 0; a < rect.width; a++)
		{
			for(int b = 0; b < rect.height; b++)
			{
				if(a + rect.x < newData.length && b + rect.y < newData[0].length)
				{
					newData[a][b] = data[a + rect.x][b + rect.y];
				}
			}
		}
		return new GreyscaleImage(newData);
	}
	public GreyscaleImage shrink(int factor)
	{
		int[][] shrunk = new int[imgWidth / factor][imgHeight / factor];
		for(int a = 0; a < imgWidth / factor ; a ++)
		{
			for(int b = 0; b < imgHeight / factor ; b ++)
			{
				int val = 0;
				for(int c = 0; c < factor ; c ++)
				{
					for(int d = 0; d < factor ; d++)
					{
						val += data[a * factor + c][b * factor + d];
					}
				}
				shrunk[a][b] = val;
			}
		}
		return new GreyscaleImage(shrunk);
	}
	public GreyscaleImage expand(int factor)
	{
		int[][] expand = new int[imgWidth * factor][imgHeight * factor];
		for(int a = 0; a < imgWidth; a ++)
		{
			for(int b = 0; b < imgHeight; b ++)
			{
				for(int c = 0; c < factor ; c ++)
				{
					for(int d = 0; d < factor ; d++)
					{
						expand[a * factor + c][b * factor + d] = data[a][b];
					}
				}
			}
		}
		return new GreyscaleImage(expand);
	}
	/**
	 * Any pixel value below val will be sent to black
	 * @param val
	 */
	public void highPass(int val)
	{
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				if(data[a][b] < val)
				{
					data[a][b] = 0;
				}
			}
		}
	}
	public void lowPass(int val)
	{
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				if(data[a][b] > val)
				{
					data[a][b] = 0;
				}
			}
		}
	}

	/**
	 * 
	 * Pixels above divide become white, the rest become black
	 * 
	 * @param divide
	 */
	public BinaryImage doubleCutoff(int divide)
	{
		boolean[][] newData = new boolean[imgWidth][imgHeight];
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				newData[a][b] = data[a][b] > divide;
			}
		}
		return new BinaryImage(newData);
	}

	public void multiply(GreyscaleImage toAdd)
	{
		int[][] toAddData = toAdd.getData();
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				data[a][b] *= toAddData[a][b];
			}
		}
		
		int max  = 0;
		for(int x = 0; x < this.getWidth(); x++)
		{
			for(int y = 0; y < this.getHeight(); y++)
			{
				if(max < data[x][y]) { max  = data[x][y]; }
			}
		}
		
		for(int x = 0; x < this.getWidth(); x++)
		{
			for(int y = 0; y < this.getHeight(); y++)
			{
				data[x][y] = (int) (data[x][y] * (((double)ImageToolkit.MAX_VAL)/((double)max)));
			}
		}
		
	}
	public void subtract(GreyscaleImage img)
	{
		int[][] otherData = img.getData();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				data[a][b] -= otherData[a][b];
				data[a][b] = Math.max(data[a][b], 0);
			}
		}
	}
	public GreyscaleImage add(GreyscaleImage im, double weight)
	{
		if(getWidth() > im.getWidth() || getHeight() > im.getHeight()) System.out.println("Boned.");
		int[][] summed = new int[this.getWidth()][this.getHeight()];
		for(int x = 0; x < this.getWidth(); x++)
		{
			for(int y = 0; y < this.getHeight(); y++)
			{
				summed[x][y] = data[x][y] + (int)((im.getData()[x][y]) * weight);
			}
		}
		
		int max  = 0;
		for(int x = 0; x < this.getWidth(); x++)
		{
			for(int y = 0; y < this.getHeight(); y++)
			{
				if(max < summed[x][y]) { max  = summed[x][y]; }
			}
		}
		
		for(int x = 0; x < this.getWidth(); x++)
		{
			for(int y = 0; y < this.getHeight(); y++)
			{
				summed[x][y] = (int) (summed[x][y] * (((double)ImageToolkit.MAX_VAL)/((double)max)));
			}
		}
		
		return new GreyscaleImage(summed);
	}
	public double averageIntensity()
	{
		int count = 0;
		for(int x = 0; x < this.getWidth(); x++)
		{
			for(int y = 0; y < this.getHeight(); y++)
			{
				count = count + data[x][y];
			}
		}
		return count/((double) this.getWidth()*(double) this.getHeight());
	}
	public double[][] getDoubleData()
	{
		double[][] result = new double[imgWidth][imgHeight];
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				result[a][b] = data[a][b];
			}
		}
		return result;
	}
	
}
