package img;

import java.awt.Rectangle;
import java.util.ArrayList;
/**
 * 
 * Contains static methods to run images through filters
 * 
 * Code is messy because it has been optimized
 * 
 * @author Seokho
 *
 */
public class RatioFilter 
{
	//Returns whether a given color pixel matches the ratios of the specified FilterType
	public static boolean matchesRatio(int[] pixel, FilterType ft)
	{
		if(pixel[0] >= ft.minValue && pixel[1] >= ft.minValue && pixel[2] >= ft.minValue)
		{
			//Divide by 1 if necessary to avoid division by zero
			double rg = pixel[1] != 0 ? (double) pixel[0] / (double) pixel[1] : pixel[0];
			double gb = pixel[2] != 0 ? (double) pixel[1] / (double) pixel[2] : pixel[1];
			return (rg >= ft.rgMin && rg <= ft.rgMax && gb >= ft.gbMin && gb <= ft.gbMax);
		}
		return false;
	}
	public static void eliminateRatio(IntBitmap img, FilterType ft)
	{
		int[][][] data = img.getData();
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				int[] pixel = data[a][b];
				if(ft.minValue == 0 || (pixel[0] >= ft.minValue && pixel[1] >= ft.minValue && pixel[2] >= ft.minValue))
				{
					double rg = pixel[1] != 0 ? (double) pixel[0] / (double) pixel[1] : pixel[0];
					double gb = pixel[2] != 0 ? (double) pixel[1] / (double) pixel[2] : pixel[1];
					if(rg >= ft.rgMin && rg <= ft.rgMax && gb >= ft.gbMin && gb <= ft.gbMax)
					{
						pixel[0] = 0;
						pixel[1] = 0;
						pixel[2] = 0;
					}
				}
				else
				{
					pixel[0] = 0;
					pixel[1] = 0;
					pixel[2] = 0;
				}
			}
		}
	}
	/**
	 * 
	 * Eliminates the specified ratio from the specified image, but only within the rectangle
	 * 
	 * @param img
	 * @param ft
	 * @param rect
	 */
	public static void eliminateRatio(IntBitmap img, FilterType ft, Rectangle rect)
	{
		int[][][] data = img.getData();
		for(int a = rect.x; a < rect.x + rect.width; a++)
		{
			for(int b = rect.y; b < rect.y + rect.height; b++)
			{
				int[] pixel = data[a][b];
				if(ft.minValue == 0 || (pixel[0] >= ft.minValue && pixel[1] >= ft.minValue && pixel[2] >= ft.minValue))
				{
					double rg = pixel[1] != 0 ? (double) pixel[0] / (double) pixel[1] : pixel[0];
					double gb = pixel[2] != 0 ? (double) pixel[1] / (double) pixel[2] : pixel[1];
					if(rg >= ft.rgMin && rg <= ft.rgMax && gb >= ft.gbMin && gb <= ft.gbMax)
					{
						pixel[0] = 0;
						pixel[1] = 0;
						pixel[2] = 0;
					}
				}
				else
				{
					pixel[0] = 0;
					pixel[1] = 0;
					pixel[2] = 0;
				}
			}
		}
	}
	public static void maintainRatio(IntBitmap img, FilterType ft)
	{
		int[][][] data = img.getData();
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				int[] pixel = data[a][b];
				if(ft.minValue == 0 || (pixel[0] >= ft.minValue && pixel[1] >= ft.minValue && pixel[2] >= ft.minValue))
				{
					double rg = pixel[1] != 0 ? (double) pixel[0] / (double) pixel[1] : pixel[0];
					double gb = pixel[2] != 0 ? (double) pixel[1] / (double) pixel[2] : pixel[1];
					if(!(rg >= ft.rgMin && rg <= ft.rgMax && gb >= ft.gbMin && gb <= ft.gbMax))
					{
						pixel[0] = 0;
						pixel[1] = 0;
						pixel[2] = 0;
					}
				}
				else
				{
					pixel[0] = 0;
					pixel[1] = 0;
					pixel[2] = 0;
				}
			}
		}
	}
	public static void maintainRatio(IntBitmap img, ArrayList<FilterType> fts)
	{
		int[][][] data = img.getData();
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				boolean matches = false;
				int[] pixel = data[a][b];
				for(FilterType ft : fts)
				{
					if(ft.minValue == 0 || (pixel[0] >= ft.minValue && pixel[1] >= ft.minValue && pixel[2] >= ft.minValue))
					{
						double rg = pixel[1] != 0 ? (double) pixel[0] / (double) pixel[1] : pixel[0];
						double gb = pixel[2] != 0 ? (double) pixel[1] / (double) pixel[2] : pixel[1];
						if(rg >= ft.rgMin && rg <= ft.rgMax && gb >= ft.gbMin && gb <= ft.gbMax)
						{
							matches = true;
							break;
						}
					}
				}
				if(!matches) 
				{
					pixel[0] = 0;
					pixel[1] = 0;
					pixel[2] = 0;
				}
			}
		}
	}
	public static void eliminateRatio(IntBitmap img, ArrayList<FilterType> fts)
	{
		int[][][] data = img.getData();
		for(int a = 0; a < img.getWidth(); a++)
		{
			for(int b = 0; b < img.getHeight(); b++)
			{
				boolean matches = false;
				int[] pixel = data[a][b];
				for(FilterType ft : fts)
				{
					if(ft.minValue == 0 || (pixel[0] >= ft.minValue && pixel[1] >= ft.minValue && pixel[2] >= ft.minValue))
					{
						double rg = pixel[1] != 0 ? (double) pixel[0] / (double) pixel[1] : pixel[0];
						double gb = pixel[2] != 0 ? (double) pixel[1] / (double) pixel[2] : pixel[1];
						if(rg >= ft.rgMin && rg <= ft.rgMax && gb >= ft.gbMin && gb <= ft.gbMax)
						{
							matches = true;
							break;
						}
					}
					else
					{
						matches = true;
						break;
					}
				}
				if(matches) 
				{
					pixel[0] = 0;
					pixel[1] = 0;
					pixel[2] = 0;
				}
			}
		}
	}
}
