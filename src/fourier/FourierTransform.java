package fourier;

import org.jlinalg.Matrix;
import org.jlinalg.complex.Complex;
import org.jlinalg.complex.ComplexMatrix;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import img.GreyscaleImage;
import img.ImageToolkit;

import java.util.ArrayList;

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
	 * @return	{x offset, y offset}. Values will be negative if the first should be overlayed on the second.
	 */
	public static int[] on(GreyscaleImage gimg1, GreyscaleImage gimg2)
	{
        int[] offset = getOffset(gimg1, gimg2);
        GreyscaleImage spliced1 = ImageToolkit.splice(gimg1, gimg2, offset[0], offset[1]);
        GreyscaleImage spliced2 = ImageToolkit.splice(gimg2, gimg1, offset[0], offset[1]);
        if(matchRating(spliced1, offset) > matchRating(spliced2, offset))
        {
        	int[] reverse = new int[2];
        	reverse[0] = -offset[0];
        	reverse[1] = -offset[1];
        	return reverse;
        }
        else
        {
        	return offset;
        }		
	}
	public static int[] on(GreyscaleImage gimg1, GreyscaleImage gimg2, String filename)
	{
        int[] offset = getOffset(gimg1, gimg2);
        GreyscaleImage spliced1 = ImageToolkit.splice(gimg1, gimg2, offset[0], offset[1]);
        GreyscaleImage spliced2 = ImageToolkit.splice(gimg2, gimg1, offset[0], offset[1]);
        if(matchRating(spliced1, offset) < matchRating(spliced2, offset))
        {
        	spliced1.export(filename);
        	int[] reverse = new int[2];
        	reverse[0] = -offset[0];
        	reverse[1] = -offset[1];
        	return reverse;
        }
        else
        {
        	spliced2.export(filename);
        	return offset;
        }		
	}
	//Higher indicates a weaker match
	private static float matchRating(GreyscaleImage img, int[] offset)
	{
        GreyscaleImage deriv1 = img.bidirectionalDerivative();
        int[][] deriv1Data = deriv1.getData();
        int deriv1Intensity = 0;
        //go across, adding the intensity of the derivative
        if(offset[1] > 0)
        {
	        for(int a = 0; a < deriv1.getWidth(); a++)
	        {
	        	deriv1Intensity += Math.abs(deriv1Data[a][offset[1] - 1] - deriv1Data[a][offset[1]]);
	        }
        }
        //go across, adding the intensity of the derivative
        if(offset[0] > 0)
        {
	        for(int a = 0; a < deriv1.getHeight(); a++)
	        {
	        	deriv1Intensity += Math.abs(deriv1Data[offset[0]][a] - deriv1Data[offset[0] - 1][a]);
	        }
        }
        return (float) deriv1Intensity / (deriv1.getWidth() + deriv1.getHeight());
	}
    private static int[] getOffset(GreyscaleImage im1, GreyscaleImage im2)
    {
    	int [] offset = new int [2];
    	 
    	double [][] pixelarr1 = new double[im1.getWidth()][im1.getHeight()*2];
        double [][] pixelarr2 = new double[im2.getWidth()][im2.getHeight()*2];
        for(int x = 0; x < im1.getWidth(); x++) {
         	for(int y = 0; y < im1.getHeight(); y++) {
         		pixelarr1[x][y] = (double) im1.getData()[x][y];
         		pixelarr2[x][y] = (double) im2.getData()[x][y];
         	}
        }
         
        // Now transform that shit
        DoubleFFT_2D fft1 = new DoubleFFT_2D(im1.getWidth(), im1.getHeight());
        fft1.complexForward(pixelarr1);
        fft1.complexForward(pixelarr2);
        Complex [][] ft1 = new Complex[im1.getWidth()][im1.getHeight()];
        Complex [][] ft2 = new Complex[im2.getWidth()][im2.getHeight()];
         

        // Place it into a complex matrix
        for(int x = 0; x < im1.getWidth(); x++)
        {
         	for(int y = 0; y < im1.getHeight(); y++)
         	{
         		ft1[x][y] = Complex.FACTORY.get(pixelarr1[x][y], pixelarr1[x][y + im1.getHeight()]);
         		ft2[x][y] = Complex.FACTORY.get(pixelarr2[x][y], pixelarr2[x][y + im2.getHeight()]);
         	}
        }
        
        ComplexMatrix complexft1 = new ComplexMatrix(ft1);  // Ga
        ComplexMatrix complexft2 = new ComplexMatrix(ft2);  // Gb
        ComplexMatrix inverse2 = new ComplexMatrix(ft2);    // will be Gb*
         
        for(int x = 0; x < im1.getWidth(); x++)           // Elementwise calculating Gb*
        {
         	for(int y = 0; y < im1.getHeight(); y++)
         	{
         		inverse2.set(x+1,  y+1, complexft2.get(x+1, y+1).conjugate());
         	}
        }
         
        // Calculate the product of the two transforms
        Matrix<Complex> result = complexft1.arrayMultiply(inverse2);  // Ga .* Gb*
        for(int x = 0; x < im1.getWidth(); x++)           // Elementwise calculating |GaGb*|
        {
         	for(int y = 0; y < im1.getHeight(); y++)
         	{
         		result.set(x+1,  y+1, result.get(x+1, y+1).divide(
         				Complex.FACTORY.get( 
         				Math.sqrt(result.get(x+1, y+1).norm().getReal().doubleValue()) 
         				)));
         	}
        }
         
        // Put this into a 2d array and find inverse
        double[][] finalArray = new double[im1.getWidth()][im1.getHeight() * 2];
     	for(int x = 0; x < im1.getWidth(); x++)
        {
         	for(int y = 0; y < im1.getHeight(); y++)
         	{
         		finalArray[x][y] = result.get(x+1, y+1).getReal().doubleValue();
         		finalArray[x][y+im1.getHeight()] = result.get(x+1, y+1).getImaginary().doubleValue();
            }
        }
 		fft1.complexInverse(finalArray, false);
 		
 		
 		// Calculate peak
 		double max = 0;
 		for(int x = 0; x < im1.getWidth(); x++)
        {
         	for(int y = 0; y < im1.getHeight(); y++)
         	{
         		if(max < finalArray[x][y]) 
         		{
         			max = finalArray[x][y]; offset[0] = x; offset[1] = y;
         		}
            }
        }
 		
 		return offset;
    }
    public GreyscaleImage transform(GreyscaleImage im)
    {
    	// Transform
        double [][] pixelarr = new double[im.getWidth()][im.getHeight()*2];
        for(int x = 0; x < im.getWidth(); x++) {
        	for(int y = 0; y < im.getHeight(); y++) {
        		pixelarr[x][y] = (double) im.getData()[x][y];
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
