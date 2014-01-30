package combat;

import img.BinaryImage;
import img.BleedResult;
import img.Bleeder;
import img.FilterType;
import img.IntBitmap;
import img.RatioFilter;

import java.util.ArrayList;

import macro.LogoutException;
import window.PWindow;
import window.ScreenRegion;
import data.Profile;

/**
 * 
 * Reads the health bar of the character and uses the appropriate potions to stay alive.
 * Upon threat of death, it will throw a LogoutException
 * 
 * @author Seokho
 *
 */
public class Healer
{
	
	private Profile profile;
	private PWindow window;
	
	private int health;		//current health levels
	
	public Healer(PWindow window)
	{
		this.window = window;
		this.profile = window.getProfile();
	}
	public int measureHealth()
	{
		IntBitmap hp = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.LIFE_RECT));
		RatioFilter.maintainRatio(hp, FilterType.LIFE);
		BinaryImage bin = hp.toGreyscale().doubleCutoff(50);
		Bleeder hpBleeder = new Bleeder(1);
		ArrayList<BleedResult> results = hpBleeder.find(bin);
		Bleeder.removeWeakResults(results, 100);
		if(results.size() > 0)	//if we can see health bar
		{
			return results.get(0).toRectangle().y;
		}
		else
		{
			return health; //unchanged
		}
	}
	//This does nothing to stop the overuse of potions
	public void healSelf() throws LogoutException
	{
		if(profile == null) throw new LogoutException("No Profile");
		profile.useHealingPotion(window);
	}
	//Kept outside constructor because of LogoutException
	//Returns whether healing was necessary
	public boolean checkHealth() throws LogoutException
	{
		health = measureHealth();
		if(health > window.getProfile().getCriticalHealth())
		{
			throw new LogoutException("Dying");
		}
		else if(health > window.getProfile().getHealingHealth())
		{
			healSelf();
		}
		else
		{
			return false;
		}
		return true;
	}
}
