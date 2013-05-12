package img;

//Static library
public class Library 
{
	private static IntBitmap windowIcon;			public static IntBitmap getWindowIcon() { return windowIcon; }
	private static IntBitmap blueSquareIcon;		public static IntBitmap getBlueSquareIcon() { return blueSquareIcon; }
	private static IntBitmap redSquareIcon;			public static IntBitmap getRedSquareIcon() { return redSquareIcon; }
	private static IntBitmap yellowSquareIcon;		public static IntBitmap getYellowSquareIcon() { return yellowSquareIcon; }
	static
	{
		windowIcon = IntBitmap.getInstance(ImageToolkit.loadImage("imglib/windowIcon.bmp"));
		blueSquareIcon = IntBitmap.getInstance(ImageToolkit.loadImage("imglib/blueSquare.bmp"));
		redSquareIcon = IntBitmap.getInstance(ImageToolkit.loadImage("imglib/redSquare.bmp"));
		yellowSquareIcon = IntBitmap.getInstance(ImageToolkit.loadImage("imglib/yellowSquare.bmp"));
	}
}
