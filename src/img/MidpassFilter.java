package img;
/**
 * Midpass filter which keeps or blackens pixels depending on whether on not they fall within color ranges set in enum MidpassFilterType. Extremely effective for 
 * 
 * picking out color patches which have relatively constant values of one or more color content.
 * 
 * Ugly because its optimized (Seokho)
 * 
 * @author Jamison
 *
 */
public class MidpassFilter 
{
	public static void maintainRanges(IntBitmap image, MidpassFilterType ft)
	{
		int[][][] data = image.getData();
		for(int a = 0; a < image.getWidth(); a++)
		{
			int[][] dataA = data[a];
			for(int b = 0; b < image.getHeight(); b++)
			{
				int[] dataB = dataA[b];
				if(!((dataB[0] >= ft.lowerR && dataB[0] <= ft.upperR) && 
						(dataB[1] >= ft.lowerG && dataB[1] <= ft.upperG) && 
						(dataB[2] >= ft.lowerB && dataB[2] <= ft.upperB)))
				{
					dataB[0] = 0;
					dataB[1] = 0;
					dataB[2] = 0;
				}
			}
		}
	}
	public static void killInRanges(IntBitmap image, MidpassFilterType ft)
	{
		int[][][] data = image.getData();
		for(int a = 0; a < image.getWidth(); a++)
		{
			int[][] dataA = data[a];
			for(int b = 0; b < image.getHeight(); b++)
			{
				int[] dataB = dataA[b];
				if(((dataB[0] >= ft.lowerR && dataB[0] <= ft.upperR) && 
						(dataB[1] >= ft.lowerG && dataB[1] <= ft.upperG) && 
						(dataB[2] >= ft.lowerB && dataB[2] <= ft.upperB)))
				{
					dataB[0] = 0;
					dataB[1] = 0;
					dataB[2] = 0;
				}
			}
		}
	}
}
