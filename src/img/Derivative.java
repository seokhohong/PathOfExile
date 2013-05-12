package img;

public class Derivative 
{
	public static enum Direction { HORIZONTAL, VERTICAL, NEG_HORIZONTAL, NEG_VERTICAL };
	public static enum ColorType { RED, GREEN, BLUE };
	/*
	public static GreyscaleImage verticalFilter(ImageData img)
	{
		int[][] data = new int[img.getWidth()][img.getHeight()];
		for(int a = 0 ; a < img.getWidth() ; a++)
		{
			for(int b = 0; b < img.getHeight() ; b++)
			{
				data[a][b] = getDelta(img, a, b, Direction.VERTICAL);
			}
		}
		return new GreyscaleImage(data);
	}
	public static GreyscaleImage filter(IntBitmap bitmap)
	{
		int[][] data = new int[bitmap.getWidth()][bitmap.getHeight()];
		for(int a = 0 ; a < bitmap.getWidth() ; a++)
		{
			for(int b = 0; b < bitmap.getHeight() ; b++)
			{
				data[a][b] = (getDelta(bitmap, a, b, Direction.HORIZONTAL) + getDelta(bitmap, a, b, Direction.VERTICAL)) / 2;
			}
		}
		return new GreyscaleImage(data);
	}
	//takes the derivative but maintains the color
	public static IntBitmap colorFilter(ImageData img)
	{
		int[][][] bitmap = new int[img.getWidth()][img.getHeight()][ImageData.RGB];
		for(int a = 0 ; a < img.getWidth() ; a++)
		{
			for(int b = 0; b < img.getHeight() ; b++)
			{
				for(int c = 0; c < ImageData.RGB; c++)
				{
					bitmap[a][b][c] = (getDelta(img, a, b, Direction.HORIZONTAL, ColorType.values()[c]) + getDelta(img, a, b, Direction.VERTICAL, ColorType.values()[c])) / 2;
				}
			}
		}
		return IntBitmap.getInstance(bitmap);
	}
	public static void filter(GreyscaleImage img)
	{
		int[][] data = img.getData();
		for(int a = 0 ; a < img.getWidth() ; a++)
		{
			for(int b = 0; b < img.getHeight() ; b++)
			{
				data[a][b] = (getDelta(data, a, b, Direction.HORIZONTAL) + getDelta(data, a, b, Direction.VERTICAL)) / 2;
			}
		}
	}
	//Returns a scaled value based off of (x-1, y) - (x, y) or (x, y-1) - (x, y), depending on the boolean
	//If x or y is 0, this will return 0
	private static int getDelta(ImageData imgData, int x, int y, Direction dir) //delta in X?
	{
		int altX = dir == Direction.HORIZONTAL ? x - 1 : x;
		int altY = dir == Direction.VERTICAL ? y - 1 : y;
		if(altX < 0 || altY < 0) return 0;
		return (Math.abs(imgData.getRed(altX, altY) - imgData.getRed(x, y)) + 
					Math.abs(imgData.getGreen(altX, altY) - imgData.getRed(x, y)) +
					Math.abs(imgData.getRed(altX, altY) - imgData.getRed(x, y))) / 3 ; //properly scale
	}
	//Preserves color
	private static int getDelta(ImageData imgData, int x, int y, Direction dir, ColorType c) //delta in X?
	{
		int altX = dir == Direction.HORIZONTAL ? x - 1 : x;
		int altY = dir == Direction.VERTICAL ? y - 1 : y;
		if(altX < 0 || altY < 0) return 0;
		switch(c)
		{
		case RED : return Math.abs(imgData.getRed(altX, altY) - imgData.getRed(x, y));
		case GREEN : return Math.abs(imgData.getGreen(altX, altY) - imgData.getGreen(x, y));
		case BLUE : return Math.abs(imgData.getBlue(altX, altY) - imgData.getBlue(x, y));
		default : return 0; //not possible
		}
	}
	//Adjusted for greyscale images
	private static int getDelta(int[][] data, int x, int y, Direction dir) //delta in X?
	{
		int altX = dir == Direction.HORIZONTAL ? x - 1 : x;
		int altY = dir == Direction.VERTICAL ? y - 1 : y;
		if(altX < 0 || altY < 0) return 0;
		return Math.abs(data[altX][altY] - data[x][y]); //properly scale
	}
	*/
}
