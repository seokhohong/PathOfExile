package combat;

import geom.CoordToolkit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import macro.HealWaiter;
import macro.LogoutException;
import macro.Macro;
import macro.PoEMacro;
import macro.Waiter;
import window.HaltThread;
import window.PWindow;
import window.WindowThread;
import arena.Arena;
import data.Potion;


public class Ranger extends CombatStyle
{
	private static final Random rnd = new Random();
	public Ranger(ArrayList<SpellInstance> spells) 
	{
		super(spells);
	}
	private static final int SPRAY_DIST = 50;
	private static final double FIRE_BACKWARD = 3; //Every three moves, the character fires backward
	//Fires default Attack, returns cooldown time
	private double shootDefault(PWindow window, double angle)
	{
		SpellInstance.mainSpell(getSpells()).cast(window, PoEMacro.rectFromPolar(angle, SPRAY_DIST));
		return SpellInstance.mainSpell(getSpells()).getCooldown(); 	//Waits for casting to be done
	}
	@Override
	public void shootEverywhere(PWindow window, Healer healer) throws LogoutException
	{
		System.out.println(Thread.currentThread()+" Shoots Everywhere");
		shootDefault(window, 0);
		healer.checkHealth();
		Macro.sleep(50);
		shootDefault(window, 180);
	}

	@Override
	public void move(PWindow window, double angle, int dist, int numMoves)
	{
		Macro.sleep((int) (shootDefault(window, angle) * 1000d));
		//System.out.println("Sleeping here");
		//Macro.sleep(1000);
		/*
		if(numMoves % FIRE_BACKWARD == 0) //shoot in opposite direction
		{
			Macro.sleep(50); //a little more maybe
			Macro.sleep((int) (shootDefault(window, angle + Math.PI) * 1000d));
			//window.getProfile().usePotion(window, Potion.QUICKSILVER);
		}
		*/
		PoEMacro.moveHeroCarefully(window, angle, dist); //And then moves
	}
	
	private static final int HP_BARS_THRESHOLD = 1; //with HP_BARS_THRESHOLD or more hp bars visible, she will fight
	//Returns whether fighting actually occurred
	private static final double FIRE_DIST = 150d;
	private static final int LONG_COMBAT = 3; //Number of attacks to count a combat round as "Long"
	@Override
	public boolean fight(PWindow window, Arena arena, WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		boolean hasFought = false;
		HealWaiter waiter = null;
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
				waiter = new HealWaiter(attack(window, CoordToolkit.toUncentered(p, PWindow.getWidth(), PWindow.getHeight()), numAttacks, bars.size()) * 1000d, healer);
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
		if(window.getProfile().getPotions(Potion.QUICKSILVER).size() > 1)
		{
			//If this character is geared for xp
			return numBars > HP_BARS_THRESHOLD + numAttacks;
		}
		else
		{
			return numBars > HP_BARS_THRESHOLD;
		}
	}
	public double attack(PWindow window, Point p, int numAttacks, int numBars)
	{
		if(hasSpell(Spell.LIGHTNING_ARROW) && hasSpell(Spell.POISON_ARROW))
		{
			return attackLA(window, p, numAttacks, numBars);
		}
		else
		{
			return genericAttack(window, p);
		}
	}
	//Optimized for characters who have lighting and poison arrows
	//Lighting is better for larger bunches, poison, for smaller
	private double attackLA(PWindow window, Point p, int numAttacks, int numBars)
	{
		SpellInstance lightning = getSpellInstanceOf(Spell.LIGHTNING_ARROW);
		SpellInstance poison = getSpellInstanceOf(Spell.POISON_ARROW);
		if(numBars > 5)
		{
			switch(numAttacks)
			{
				case 0: lightning.cast(window, p); return lightning.getCooldown();
				case 1: lightning.cast(window, p); return lightning.getCooldown();
				case 2: poison.cast(window, p); return poison.getCooldown();
				case 3: lightning.cast(window, p); return lightning.getCooldown();
				case 4: lightning.cast(window, p); return lightning.getCooldown();
				default: //alternate between the two
				{
					if(numAttacks % 2 == 0)
					{
						poison.cast(window, p); return poison.getCooldown();
					}
					else
					{
						lightning.cast(window, p); return lightning.getCooldown();
					}
				}
			}
		}
		else
		{
			if(numAttacks % 3 == 0)
			{
				lightning.cast(window, p); return lightning.getCooldown();
			}
			else
			{
				poison.cast(window, p); return poison.getCooldown();
			}
		}
	}
	private double genericAttack(PWindow window, Point p)
	{
		double atkChoice = rnd.nextDouble();		
		for(SpellInstance s : getSpells())
		{
			if(atkChoice < s.getFrequency())
			{
				s.cast(window, p);
				return s.getCooldown();
			}
			else
			{
				atkChoice -= s.getFrequency();
			}
		}
		return 0d;
	}
}
