package fourier;

/**
 * 
 * Performs a kind of edge detection by allowing only certain bands to pass through
 * 
 * lowCutoff: raise to blur
 * highCutoff: lower to sharpen
 * @author Seokho
 *
 */
public class BandPass extends FourierOperation 
{
	//Do not modify variables after they have been set
	private double lowCutoff;
	private double highCutoff;
	public BandPass(double lowCutoff, double highCutoff)
	{
		super();
		this.lowCutoff = lowCutoff;
		this.highCutoff = highCutoff;
	}

	@Override
	void run(double[][] im) 
	{
    	int centerX = im.length/2;
    	int centerY = (im[0].length/2)/2; //The input matrix is twice as big so we can store im#
    	double lowCutoff = Math.pow(this.lowCutoff, 2);
    	double highCutoff = Math.pow(this.highCutoff, 2);
 		
    	for(int x = 0; x < im.length; x++)
        {
        	for(int y = 0; y < (im[0].length / 2); y++)
        	{
        		double distance = Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2);
        		if(distance < lowCutoff || highCutoff < distance)
        		{
        			im[x][y] = 0; // Suppress it
        			im[x][y+im[0].length/2] = 0; // Suppress it
        		}
            }
        }
	}
}
