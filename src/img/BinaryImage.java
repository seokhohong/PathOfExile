package img;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class BinaryImage
{
	public static final boolean WHITE = true;
	public static final boolean BLACK = false;
	//Limit exposure?
	private boolean[][] data;		public boolean[][] getData() { return data; }		
									void setData(boolean[][] data) { this.data = data; }
	private int imgWidth;			public int getWidth() { return imgWidth; }
	private int imgHeight;			public int getHeight() { return imgHeight; }
	public BinaryImage(boolean[][] data)
	{
		this.data = data;
		imgWidth = data.length;
		imgHeight = data[0].length;
	}
	public void invert()
	{
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				data[a][b] = !data[a][b];
			}
		}
	}
	public GreyscaleImage toGreyscale()
	{
		int[][] newData = new int[imgWidth][imgHeight];
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				newData[a][b] = data[a][b] ? ImageToolkit.MAX_VAL : 0;
			}
		}
		return new GreyscaleImage(newData);
	}
	public void add(BinaryImage otherImg)
	{
		assert(imgWidth == otherImg.imgWidth && imgHeight == otherImg.imgHeight);
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0 ; b < imgHeight; b++)
			{
				data[a][b] |= otherImg.data[a][b];
			}
		}
	}
	//Fills in white pixels with black, starting the spread at x, y
	public void fillBlack(BinaryImage img, int x, int y)
	{
		if(x < data.length - 1 && y < data[0].length - 1)
		{
			fillBlack(img.getData(), x + 1, y + 1);
		}
	}
	private void fillBlack(boolean[][] data, int x, int y)
	{
		data[x][y] = BinaryImage.BLACK;
		if(x > 0 && data[x-1][y]) fillBlack(data, x - 1, y);
		if(x < data.length - 1 && data[x + 1][y]) fillBlack(data, x + 1, y);
		if(y > 0 && data[x][y - 1]) fillBlack(data, x, y - 1);
		if(y < data[0].length - 1 && data[x][y + 1]) fillBlack(data, x, y + 1);
	}
	//If there isn't a neighboring pixel that is sufficiently similar to a given pixel, it dies
	public void requireNeighbors(int inclusiveMin, int inclusiveMax)
	{
		boolean[][] newData = new boolean[imgWidth][imgHeight];
		for(int a = 0; a < imgWidth; a++)
		{
			for(int b = 0; b < imgHeight; b++)
			{
				int neighbors = Neighbors.numNeighbors(data, a, b);
				newData[a][b] = neighbors >= inclusiveMin && neighbors <= inclusiveMax && data[a][b] == BinaryImage.WHITE;
			}
		}
		data = newData;
	}
	public void export(String filename)
	{
		BufferedImage toExport = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = toExport.createGraphics();
		for(int a = 0; a < data.length; a++)
		{
			for(int b = 0; b < data[0].length; b++)
			{
				int color = data[a][b] ? 255 :  0;
				g.setColor(new Color(color, color, color));
				g.drawLine(a, b, a, b);
			}
		}
		ImageToolkit.exportImage(toExport, filename);
	}
}
