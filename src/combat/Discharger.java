package combat;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import macro.LogoutException;
import macro.Macro;
import macro.PoEMacro;
import macro.Waiter;
import window.HaltThread;
import window.PWindow;
import window.WindowThread;
import arena.Arena;
import data.Potion;

/**
 * 
 * Required Enduring Cry / Discharge / A Standard Attack
 * 
 * @author HONG
 *
 */
public class Discharger extends CombatStyle
{
	private static final Random rnd = new Random();
	public Discharger(ArrayList<SpellInstance> spells) 
	{
		super(spells);
	}
	private static final int SPRAY_DIST = 50;
	//Fires default Attack, returns cooldown time
	private double shootDefault(PWindow window)
	{
		SpellInstance.mainSpell(getSpells()).cast(window, PWindow.getWindowCenter());
		return SpellInstance.mainSpell(getSpells()).getCooldown(); 	//Waits for casting to be done
	}
	@Override
	public void shootEverywhere(PWindow window, Healer healer) throws LogoutException
	{
		shootDefault(window);
	}

	@Override
	public void move(PWindow window, double angle, int dist, int numMoves)
	{
		Macro.sleep((int) (shootDefault(window) * 1000d));
		PoEMacro.moveHero(window, angle, dist);
		PoEMacro.moveHero(window, angle, dist); //And then moves
	}
	
	private static final int HP_BARS_THRESHOLD = 2; //with HP_BARS_THRESHOLD or more hp bars visible, she will fight
	private static final int BARS_SCALING = 5; //how fast the number of HP bars required goes up per attack
	//Returns whether fighting actually occurred
	private static final double FIRE_DIST = 150d;
	private static final int LONG_COMBAT = 3; //Number of attacks to count a combat round as "Long"
	@Override
	public boolean fight(PWindow window, Arena arena, WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		boolean hasFought = false;
		Waiter waiter = null;
		int numAttacks = 0;	//Number of attacks in this fighting sequence
		while(true) //if in combat, keep fighting
		{
			healer.checkHealth();
			mana(window); //won't do anything if character doesn't have mana
			thread.checkHalt();
			HealthBarFinder hpBars = new HealthBarFinder(window);
			ArrayList<Point> bars = hpBars.getHealthBars();
			if(keepFighting(window, numAttacks, bars.size()))
			{
				double a = hpBars.getShootingAngle(); //gets the optimal shooting angle from method in HealthBarFinder
				Point p = new Point();
				p.x = (int) (FIRE_DIST * Math.cos(a));
				p.y = (int) (FIRE_DIST * Math.sin(a));
				if(waiter!=null)
				{
					waiter.waitFully();
				}
				waiter = new Waiter(attack(window, healer) * 1000d);
				hasFought = true;
			}
			else
			{
				break;
			}
			numAttacks ++; 
		}
		if(numAttacks > LONG_COMBAT) //combat lasted long enough
		{
			window.getProfile().usePotion(window, Potion.QUICKSILVER);
		}
		return hasFought;
	}
	private boolean keepFighting(PWindow window, int numAttacks, int numBars)
	{
		return numBars > HP_BARS_THRESHOLD + numAttacks * BARS_SCALING;
	}
	public double attack(PWindow window, Healer healer) throws LogoutException
	{
		ArrayList<SpellInstance> cries = new ArrayList<SpellInstance>();
		
		SpellInstance discharge = getSpellInstanceOf(Spell.DISCHARGE);
				
		for(SpellInstance cry : getSpells())
		{
			if(cry.getSpell() == Spell.ENDURING_CRY)
			{
				cries.add(cry);
			}
		}
		for(SpellInstance cry : cries)
		{
			cry.cast(window, PWindow.selectPoint());
			Macro.sleep((int) (cry.getCooldown() * 1000d));
			healer.checkHealth();
			discharge.cast(window, PWindow.selectPoint());
			Macro.sleep((int) (discharge.getCooldown() * 1000d));
			healer.checkHealth();
		}
		healer.checkHealth();
		return 0d;
	}
}
