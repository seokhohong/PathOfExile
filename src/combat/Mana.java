package combat;

import img.BinaryImage;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;
import macro.LogoutException;
import window.PWindow;
import window.ScreenRegion;
import data.Profile;

/**
 * TODO: Stop overusing potions
 * Reads the mana bar of the character and uses the appropriate potions to keep mana filled
 * 
 * @author Seokho
 *
 */
public class Mana 
{
	private Profile profile;
	//Number of health pixels in bar
	private int mana;
	
	private PWindow window;

	public Mana(PWindow window)
	{
		this.profile = window.getProfile();
		IntBitmap hpImg = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.MANA_RECT));
		RatioFilter.maintainRatio(hpImg, FilterType.MANA); //Extracts reddish pixels that correspond
		//simple conversion to a binary
		BinaryImage hpBinary = hpImg.toGreyscale().doubleCutoff(1);
		//counts the number of white pixels, effectively counting red pixels
		mana = hpBinary.countWhite();
		this.window = window;
	}
	private static final int NEED_POTION = 50;
	//This does nothing to stop overuse of potions
	//Shouldn't run places it needs full potions to live anyway
	public void usePotion() throws LogoutException
	{
		//System.out.println("Using Mana Potion");
		if(profile == null) throw new LogoutException("No Profile");
		profile.useManaPotion(window);
	}
	//Kept outside constructor because of LogoutException
	public void keepFilled() throws LogoutException
	{
		//Its mana, but whatever
		if(mana > NEED_POTION)
		{
			usePotion();
		}
	}
	//May have blood magic
	public boolean hasMana()
	{
		return mana > NEED_POTION;
	}
}
