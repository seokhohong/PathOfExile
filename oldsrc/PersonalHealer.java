package tools;

import java.util.Random;

import window.PWindow;
import window.ScreenRegion;

import img.BinaryImage;
import img.FilterType;
import img.ImageLibrary;
import img.IntBitmap;
import img.RatioFilter;

import macro.LogoutException;
import macro.Macro;

/**
 * 
 * Reads the health bar of the character and uses the appropriate potions to stay alive.
 * Upon threat of death, it will throw a LogoutException
 * 
 * Will press 1-5 to heal (Will press continuously unless controlled)
 * 
 * @author Seokho
 *
 */
public class PersonalHealer 
{
	private static final int NUM_POTIONS = 5;
	private static final int HEAL_WAIT = 4000;
	//Number of health pixels in bar
	private int health;

	private double lastHealed = 0;
	
	public PersonalHealer(PWindow window)
	{
		while(true)
		{
			IntBitmap hpImg = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.LIFE_RECT));
			if(hpImg.findImage(ImageLibrary.MY_HEALTH_ICON.get()) != null)
			{
				RatioFilter.maintainRatio(hpImg, FilterType.LIFE); //Extracts reddish pixels that correspond to life
				//simple conversion to a binary
				BinaryImage hpBinary = hpImg.toGreyscale().doubleCutoff(1);
				//counts the number of white pixels, effectively counting red pixels
				health = hpBinary.countWhite();
				try {
					stayAlive();
				} catch (LogoutException e) {
					window.logout();
				}
			}
		}
	}
	//very safe
	private static final int NEED_HEALING = 4500;
	private static final int CRITICAL = 2500;
	public void healSelf() throws LogoutException
	{
		System.out.println("Healing");
		int index = new Random().nextInt(NUM_POTIONS);
		Macro.macro.type(Character.toChars(index + 1 + '0')[0]);
	}
	//Kept outside constructor because of LogoutException
	//Returns whether it ordered healing or not
	public void stayAlive() throws LogoutException
	{
		if(health < NEED_HEALING && System.currentTimeMillis() - lastHealed > HEAL_WAIT)
		{
			healSelf();
			lastHealed = System.currentTimeMillis();
		}
		if(health < CRITICAL)
		{
			throw new LogoutException("Dying");
		}
	}
}
