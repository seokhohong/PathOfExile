package img;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
	public void horizontalBlur(int radius)
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
					greyVal += data[a+c][b];
					numVals++;
				}
				blurred[a][b] = greyVal / numVals;
			}
		}
		data = blurred;
	}
	public GreyscaleImage bidirectionalDerivative()
	{
		int[][] newData = new int[getWidth()][getHeight()];
		for(int a = 0 ; a < getWidth() ; a++)
		{
			for(int b = 0; b < getHeight() ; b++)
			{
				newData[a][b] = (getDelta(a, b, 0, 1) + getDelta(a, b, 1, 0)) / 2;
			}
		}
		return new GreyscaleImage(newData);
	}
	private int getDelta(int x, int y, int dx, int dy)
	{
		int altX = x + dx;
		int altY = y + dy;
		if(altX < 0 || altY < 0 || altX > imgWidth - 1 || altY > imgHeight - 1) return 0;
		return (Math.abs(data[altX][altY] - data[x][y])); //properly scale
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
	//anything below min gets sent to 0
	public BinaryImage floor(GreyscaleImage img, int min)
	{
		boolean[][] newData = new boolean[imgWidth][imgHeight];
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				if(data[a][b] < min)
				{
					newData[a][b] = BinaryImage.BLACK;
				}
			}
		}
		return new BinaryImage(newData);
	}
	//anything above max gets capped
	public BinaryImage ceiling(GreyscaleImage img, int max)
	{
		boolean[][] newData = new boolean[imgWidth][imgHeight];
		for(int a = 0; a < getWidth(); a++)
		{
			for(int b = 0; b < getHeight(); b++)
			{
				if(data[a][b] > max)
				{
					newData[a][b] = BinaryImage.WHITE;
				}
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
				data[x][y] = (int) ((double)data[x][y] * (((double)ImageToolkit.MAX_VAL)/((double)max)));
			}
		}
		
	}
	public void display()
	{
		BufferedImage img = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				int color = Math.max(Math.min(data[a][b], 255), 0);
				g.setColor(new Color(color, color, color));
				g.drawLine(a, b, a, b);
			}
		}
		JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
				summed[x][y] = data[x][y] + (int)(((double)im.getData()[x][y]) * weight);
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
				summed[x][y] = (int) ((double)summed[x][y] * (((double)ImageToolkit.MAX_VAL)/((double)max)));
			}
		}
		
		return new GreyscaleImage(summed);
	}
}
