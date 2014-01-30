package fourier;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import img.Display;
import img.GreyscaleImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * This class is a mess, and doesn't work reliably. Its not broken, but FourierTransform is quite weak.
 * Many commented out regions are past attempts to improve its strength/hide warnings
 * 
 * @author Seokho
 *
 */
public class FourierTransform 
{
	//Stores all operations related to this transform
	private ArrayList<FourierOperation> ops = new ArrayList<FourierOperation>();
	public FourierTransform(FourierOperation fo) //By default loaded with no operations
	{
		addOperation(fo);
	}
	/**
	 * Adds a new FourierOperation to the list of operations to perform
	 * @param fo
	 */
	public void addOperation(FourierOperation fo)
	{
		ops.add(fo);
	}

	/**
	 * 
	 * Performs the fast Fourier transform on two images to get their translation
	 * 
	 * @param im1: first Image
	 * @param im2: second Image
	 * @return	{x offset, y offset}. Values will be negative if the first should be overlaid on the second.
	 */

	public static int[] on(GreyscaleImage gimg1, GreyscaleImage gimg2, int maxGap)
	{
        List<Peak> peaks = getOffset(gimg1, gimg2, maxGap);
        Peak best = null;
        double bestRating = 0;
        int a = 0;
        for(Peak p : peaks)
        {
        	System.out.println(p.getRating()+" at "+p.getX()+", "+p.getY());
        	double rating = p.getRating();
        	a+=4;
        	if(rating > bestRating)
        	{
        		bestRating = rating;
        		best = p;
        	}
        }
        //ImageToolkit.splice(gimg1, gimg2, best.getX(), best.getY()).display("best");
        int[] offset = { peaks.get(0).getX(), peaks.get(0).getY() };
        return offset;
	}
/*
	private static Peak bestPeak(GreyscaleImage img1, GreyscaleImage img2, Peak peak, int a)
	{
		ArrayList<Peak> peaks = peak.allPermutations();
		for(Peak p : peaks)
		{
			p.setRating(matchRating(img1, img2, p, a));
	        //ImageToolkit.splice(img1, img2, p.getX(), p.getY()).expand(10).display(Integer.toString(a));
			//System.out.println(a+": "+p.getRating());
			a++;
		}
		Collections.sort(peaks);
		return peaks.get(0);
	}
	*/
	//private static final int[][] neighbors = { { -1, 1 } , { -1 , 1 } , { 1 , -1 } , { 1 , 1}, { -1, 0 } , { 0 , 1 } , { 1 , 0 } , { 0 , -1}, { 0 , 0} };
	/**
	 * Scores a particular overlap based on the image produced after the operation
	 * 
	 * @param img1
	 * @param img2		: overlaps img1
	 * @param peak		: point of overlap
	 * @return			: Higher indicates a weaker match (WTF STRONGER?)
	 */
	/*
	private static double matchRating(GreyscaleImage img1, GreyscaleImage img2, Peak peak, int numFrame)
	{
		int rating = 0;		//Increased for every pixel that doesn't overlap well
		int numPoints = 0;	//points of overlap
		int[][] data1 = img1.getData();
		int[][] data2 = img2.getData();
		int[][] overlap = new int[img1.getWidth()][img2.getWidth()];
		for(int a = 0; a < Math.max(img1.getWidth(), img2.getWidth()); a++)
		{
			for(int b = 0; b < Math.max(img1.getHeight(), img2.getHeight()); b++)
			{
				/*
				int altX = a + Math.abs(peak.getX());
				int altY = b + Math.abs(peak.getY());
				int d1X = peak.getX() >= 0 ? a : altX;
				int d2X = peak.getX() >= 0 ? altX : a;
				int d1Y = peak.getY() >= 0 ? b : altY;
				int d2Y = peak.getY() >= 0 ? altY : b;
				if(d2X >= 0 && d2Y >=0 && d2X < data2.length && d2Y < data2[0].length &&
						d1X >= 0 && d1Y >=0 && d1X < data1.length && d1Y < data1[0].length)
				{
					rating += Math.abs(data1[d1X][d1Y] - data2[d2X][d2Y]);
					numPoints++;
				}
				//
				
				int min = ImageToolkit.MAX_VAL + 1;
				for(int[] offset : neighbors)
				{
					//alternate X and Y coordinate which gets applied to either img1 or img2 based on the sign of peak.getX()
					int altX = a + Math.abs(peak.getX()) + offset[0];
					int altY = b + Math.abs(peak.getY()) + offset[1];
					//default settings for data retrieval coordinates
					int d1X = peak.getX() >= 0 ? a : altX;
					int d2X = peak.getX() >= 0 ? altX : a;
					int d1Y = peak.getY() >= 0 ? b : altY;
					int d2Y = peak.getY() >= 0 ? altY : b;
					if(d2X >= 0 && d2Y >=0 && d2X < data2.length && d2Y < data2[0].length &&
							d1X >= 0 && d1Y >=0 && d1X < data1.length && d1Y < data1[0].length)
					{
						min = Math.min(min, Math.abs(data1[d1X][d1Y] - data2[d2X][d2Y]));
						numPoints ++ ;
					}
				}
				int add = ((min == ImageToolkit.MAX_VAL + 1) ? 0 : min);
				overlap[a][b] = add;
				rating += add;
			}
		}
		//new GreyscaleImage(overlap).display(Integer.toString(numFrame));
		if(numPoints == 0)
		{
			rating = Integer.MAX_VALUE;
		}
		return (rating / (double) numPoints);
	}
	*/
	//Will try to splice the top NUM_TRIES peaks
	private static final int NUM_TRIES = 5;
    public static List<Peak> getOffset(GreyscaleImage im1, GreyscaleImage im2, int maxGap)
    {
    	int [] offset = new int [2];
    	 
    	double [][] pixelarr1 = new double[im1.getWidth()][im1.getHeight()*2];
        double [][] pixelarr2 = new double[im2.getWidth()][im2.getHeight()*2];
        for(int x = 0; x < im1.getWidth(); x++) {
         	for(int y = 0; y < im1.getHeight(); y++) {
         		pixelarr1[x][y] = im1.getData()[x][y];
         		pixelarr2[x][y] = im2.getData()[x][y];
         	}
        }
         
        // Now transform that shit
        DoubleFFT_2D fft1 = new DoubleFFT_2D(im1.getWidth(), im1.getHeight());
        //double time = System.currentTimeMillis();
        fft1.complexForward(pixelarr1);
        fft1.complexForward(pixelarr2);
        //System.out.println("Complex Forward: " + (System.currentTimeMillis() - time));
    
        // pixelarr1 = Ga o Gb*
        for(int x = 0; x < im1.getWidth(); x++)
        {
         	for(int y = 0; y < im1.getHeight(); y++)
         	{
         		// Ga[x][y] = a + bi
         		// Gb[x][y] = c + di
         		double a = pixelarr1[x][y];
         		double b = pixelarr1[x][y + im1.getHeight()];
         		double c = pixelarr2[x][y];
         		double d = pixelarr2[x][y + im2.getHeight()];
         		double norm = Math.sqrt(Math.pow(a*c + b*d, 2.0) + Math.pow(b*c - a*d, 2.0));
         		pixelarr1[x][y] = (a*c + b*d)/norm;
         		pixelarr1[x][y + im1.getHeight()] = (b*c - a*d)/norm;
         	}
        }
    
 		fft1.complexInverse(pixelarr1, true);
 		//Creating a normalized image to represent the results of the transform
 		double highest = 0;
 		for(int a = 0; a < pixelarr1.length; a++)
 		{
 			for(int b = 0; b < pixelarr1[a].length / 2; b++)
 			{
 				highest = Math.max(pixelarr1[a][b], highest);
 			}
 		}
 		//Creating the integer array that will form the basis for the image
 		int[][] intArr = new int[pixelarr1[0].length][pixelarr1.length];
 		for(int a = 0; a < pixelarr1.length; a++)
 		{
 			for(int b = 0; b < pixelarr1[a].length; b++)
 			{
 				intArr[b][a] = (int) (pixelarr1[a][b]);
 			}
 		}
 		GreyscaleImage grey = new GreyscaleImage(intArr);
 		Display.showHang(grey);
 		//return grey;
 		// Calculate peak
 		ArrayList<Peak> peaks = new ArrayList<Peak>();
 		double max = 0;
 		//Needs a match with enough overlap
 		for(int x = 0; x < Math.min(maxGap, pixelarr1.length); x++)
 		{
        	for(int y = 0; y < Math.min(maxGap, pixelarr1[0].length); y++)
         	{
         		if(pixelarr1[x][y] > 10)
         		{
         			peaks.add(new Peak(x, y, pixelarr1[x][y]));
         		}
         		if(max < pixelarr1[x][y]) 
         		{
         			max = pixelarr1[x][y]; offset[0] = x; offset[1] = y;
         		}
            }
        }
 		Collections.sort(peaks);
 		if(peaks.isEmpty()) return peaks;
 		return peaks.subList(0, Math.min(peaks.size() - 1, NUM_TRIES));

    }
    public GreyscaleImage transform(GreyscaleImage im)
    {
    	// Transform
        double [][] pixelarr = new double[im.getWidth()][im.getHeight()*2];
        for(int x = 0; x < im.getWidth(); x++) {
        	for(int y = 0; y < im.getHeight(); y++) {
        		pixelarr[x][y] = im.getData()[x][y];
        	}
        }
         
        // Now transform that shit
        DoubleFFT_2D fft1 = new DoubleFFT_2D(im.getWidth(), im.getHeight());
        fft1.complexForward(pixelarr);
         
        for(FourierOperation op : ops)
        {
        	op.run(pixelarr);
        }
        
        fft1.complexInverse(pixelarr, true);
        int [][] result = new int[im.getWidth()][im.getHeight()];
    	for(int x = 0; x < im.getWidth(); x++) {
    		for(int y = 0; y < im.getHeight(); y++) {
    			result[x][y] = (int) pixelarr[x][y];
    		}
    	}
    	return new GreyscaleImage(result);
    }
}
//Location of a highly rated overlap point
class Peak implements Comparable<Peak>
{
	private int x;
	private int y;
	private double rating;
	Peak(int x, int y, double rating)
	{
		this.x = x;
		this.y = y;
		this.rating = rating;
	}
	@Override
	public int compareTo(Peak oPeak)
	{
		if(rating > oPeak.rating)
		{
			return -1;
		}
		else if(rating < oPeak.rating)
		{
			return 1;
		}
		return 0;
	}
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	public double getRating()
	{
		return rating;
	}
	public void setRating(double rating)
	{
		this.rating = rating;
	}
	public ArrayList<Peak> allPermutations()
	{
		ArrayList<Peak> allPeaks = new ArrayList<Peak>();
		allPeaks.add(new Peak(x, y, rating));
		allPeaks.add(new Peak(-x, y, rating));
		allPeaks.add(new Peak(x, -y, rating));
		allPeaks.add(new Peak(-x, -y, rating));
		return allPeaks;
	}
}
