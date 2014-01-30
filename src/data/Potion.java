package data;

import java.util.ArrayList;
import java.util.Random;

import window.PWindow;

/**
 * 
 * Potions that a character can carry.
 * 
 * Left as a public enum instead of a private one so character controller classes could access it.
 * 
 * @author Seokho
 *
 */
public enum Potion 
{
	LIFE,
	MANA,
	HYBRID,
	QUICKSILVER,
	NONE;
	
	private static final Random rnd = new Random();
	 
	public static Potion fromString(String s)
	{
		for(Potion p : values())
		{
			if(p.toString().equals(s.trim()))
			{
				return p;
			}
		}
		System.err.println("Could not convert "+s+" to Potion");
		System.exit(1);
		return null;
	}
	public static void usePotion(PWindow window, Potion[] potions, Potion p)
	{
		ArrayList<Integer> matchingPots = new ArrayList<Integer>();
		for(int a = 0; a < potions.length; a++)
		{
			if(potions[a] == p)
			{
				matchingPots.add(a + 1);
			}
		}
		if(matchingPots.size() > 0)
		{
			window.type(matchingPots.get(rnd.nextInt(matchingPots.size())).toString());
		}
	}
	public static void useHealingPotion(PWindow window, Potion[] potions)
	{
		ArrayList<Integer> matchingPots = new ArrayList<Integer>();
		for(int a = 0; a < potions.length; a++)
		{
			if(potions[a] == Potion.LIFE || potions[a] == Potion.HYBRID)
			{
				matchingPots.add(a + 1);
			}
		}
		if(matchingPots.size() > 0)
		{
			window.type(matchingPots.get(rnd.nextInt(matchingPots.size())).toString());
		}
	}
	public static void useManaPotion(PWindow window, Potion[] potions)
	{
		ArrayList<Integer> matchingPots = new ArrayList<Integer>();
		for(int a = 0; a < potions.length; a++)
		{
			if(potions[a] == Potion.MANA || potions[a] == Potion.HYBRID)
			{
				matchingPots.add(a + 1);
			}
		}
		if(matchingPots.size() > 0)
		{
			window.type(matchingPots.get(rnd.nextInt(matchingPots.size())).toString());
		}
	}
	/**
	 * 
	 * Approximate number of milliseconds the Quicksilver potion lasts
	 * 
	 * @return
	 */
	public static int getQuicksilverDuration() { return 4500; }
}
