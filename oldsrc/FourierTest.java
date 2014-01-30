package main;
import fourier.BandPass;
import fourier.FourierTransform;
import img.BinaryImage;
import img.ImageToolkit;
import img.IntBitmap;
import img.GreyscaleImage;

import java.util.ArrayList;

import window.PWindow;

public class FourierTest 
{
	private String firstImgFile = "Image1.png";
	private String secondImgFile = "Image2.png";
	private String itemFile = "img/ScreenHunter_430.bmp";
	private String rawMapFile = "export/2.bmp";
	private String sliceExportFile = "export/spliced.bmp";
	public static void main(String[] args)
	{
		new FourierTest().spliceTest();
	}
	private void spliceTest()
	{
		/*
		GreyscaleImage im1 = IntBitmap.getInstance(ImageToolkit.loadImage(firstImgFile)).toGreyscale();
		GreyscaleImage im2 = IntBitmap.getInstance(ImageToolkit.loadImage(secondImgFile)).toGreyscale();
		int[] translation = FourierTransform.on(im1, im2, sliceExportFile);
		System.out.println("Estimated translation: (" + translation[0] + ", " + translation[1] + ")");
		*/
	}
	private void lowPassFilterTest() {
		
		ArrayList<PWindow> windows = PWindow.getWindows(IntBitmap.getInstance(ImageToolkit.loadImage(itemFile)));
		//windows.get(0).findItem(IntBitmap.getInstance(ImageToolkit.loadImage(itemFile)));//, true);
		// The .operations function requires an array of ArrayLists
		//   where each ArrayList is the function to use followed by 
		//   the arguments to pass to the function
		// Note: 80, 140 is very good for minimaps
		// Follow by blur
		FourierTransform ft = new FourierTransform(new BandPass(0.0d, 320.0d));
		IntBitmap rgbmap = IntBitmap.getInstance(ImageToolkit.loadImage(itemFile));
		GreyscaleImage grey = rgbmap.toGreyscale();
		grey = ft.transform(grey);    // Fourier transform
		
		// Check against the edge detection technique
		grey.multiply(IntBitmap.getInstance(ImageToolkit.loadImage("export/filtered.bmp")).toGreyscale());
		
		// Blur once to eliminate single pixels
 		grey.horizontalBlur(2); 
		
 		// Iterate, strengthening clusters and eliminating loners
	    for(int i = 0; i < 20; i++)
	    {
	    	grey.horizontalBlur(10);
		   	grey.blur(2);
	    	grey.multiply(1.125); // Experimentally: this is the smallest value that keeps items 'alive' indefinitely.
	    }
	   
		BinaryImage bin = grey.doubleCutoff(5);
	    grey = bin.toGreyscale();
	}
	private void bandPassFilterTest() 
	{
		// The .operations function requires an array of ArrayLists
		//   where each ArrayList is the function to use followed by 
		//   the arguments to pass to the function
		// Note: 80, 140 is very good for minimaps
		// Follow by blur
		FourierTransform ft = new FourierTransform(new BandPass(80.0d, 110.0d));
		IntBitmap rgbmap = IntBitmap.getInstance(ImageToolkit.loadImage(rawMapFile));
		GreyscaleImage grey = rgbmap.toGreyscale();
		grey = ft.transform(grey);    // Fourier transform
		
 		// Iterate, strengthening clusters and eliminating loners
	    for(int i = 0; i < 2; i++)
	    {
		   	grey.blur(3);
	    	grey.multiply(1.02); // Experimentally: this is the smallest value that keeps items 'alive' indefinitely.
	    }
	    for(int j = 0; j < 5; j ++)
	    {
	    	grey = grey.add(rgbmap.toGreyscale(), .3);
	    	grey = ft.transform(grey);    // Fourier transform

	    	for(int i = 0; i < 4; i++)
		    {
			   	grey.blur(2);
		    	grey.multiply(1.02); // Experimentally: this is the smallest value that keeps items 'alive' indefinitely.
		    }
	    }
		BinaryImage bin = grey.doubleCutoff(230);
	    grey = bin.toGreyscale();
	}
	/*
	private void run()
	{
		// Read in the image
		IntBitmap img1 = IntBitmap.getInstance(ImageToolkit.loadImage(firstImgFile));
		IntBitmap img2 = IntBitmap.getInstance(ImageToolkit.loadImage(secondImgFile));
	    GreyscaleImage gimg1 = img1.toGreyscale();
	    GreyscaleImage gimg2 = img2.toGreyscale();
	        
	    // Put it into an array
	    // Each one has to be twice as wide as it should be to store complex values 
	    // from the FFT
	    double [][] pixelarr1 = new double[gimg1.getWidth()][gimg1.getHeight()*2];
	    double [][] pixelarr2 = new double[gimg2.getWidth()][gimg2.getHeight()*2];
        for(int x = 0; x < gimg1.getWidth(); x++) {
        	for(int y = 0; y < gimg1.getHeight(); y++) {
        		pixelarr1[x][y] = (double) gimg1.getData()[x][y];
        		pixelarr2[x][y] = (double) gimg2.getData()[x][y];
           	}
        }
		        
        // Now transform that shit
        DoubleFFT_2D fft1 = new DoubleFFT_2D(gimg1.getWidth(), gimg1.getHeight());
        fft1.complexForward(pixelarr1);    
        fft1.complexInverse(pixelarr1, true);
	        
	        
        int[][] realphase = new int[gimg1.getWidth()][gimg1.getHeight()];
	        
        for(int x = 0; x < gimg1.getWidth(); x++)
        {
        	for(int y = 0; y < gimg1.getHeight(); y++)
        	{
        		realphase[x][y] = (int) (finalArray[x][y]);
           	}
        }
	        
        GreyscaleImage img = new GreyscaleImage(realphase);
        img.display();
        
        
	}
*/
}
