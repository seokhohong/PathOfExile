package img;

public class HpFinder 
{
	public static final int HP_ROW = 2;
	public static final int WHITENESS_THRESHOLD = 50;
	private boolean isHp;
	public HpFinder(GreyscaleImage img)
	{
		int[][] data = img.getData();
		int whiteness = 0;
		for(int a = 0; a < data.length; a++)
		{
			whiteness += data[a][HP_ROW];
		}
		whiteness /= img.getWidth(); //average it
		isHp = whiteness > WHITENESS_THRESHOLD;
	}
	public boolean isHp()
	{
		return isHp;
	}
}
