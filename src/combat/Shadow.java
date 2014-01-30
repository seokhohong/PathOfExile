package combat;

import geom.CoordToolkit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import macro.LogoutException;
import macro.PoEMacro;
import window.HaltThread;
import window.PWindow;
import window.WindowThread;
import arena.Arena;


//Is there a better design for this?
public class Shadow extends CombatStyle
{
	private Random rnd = new Random();
	Shadow(ArrayList<SpellInstance> spells) 
	{
		super(spells);
	}
	private static final int SPRAY_DIST = 40;
	private static final double TURN_BACK = 0.2;
	@Override
	public void move(PWindow window, double angle, int dist, int numMoves)
	{
		SpellInstance.mainSpell(getSpells()).cast(window, PoEMacro.rectFromPolar(angle, SPRAY_DIST));
		SpellInstance.mainSpell(getSpells()).waitCastDelay(); 	//Waits for casting to be done
		if(rnd.nextDouble() < TURN_BACK) //cast the other way
		{
			SpellInstance.mainSpell(getSpells()).cast(window, PoEMacro.rectFromPolar(angle + Math.PI, SPRAY_DIST));
			SpellInstance.mainSpell(getSpells()).waitCastDelay();
		}
		PoEMacro.moveHero(window, angle, dist);
	}
	
	private static final int HP_BARS_THRESHOLD = 3; //with HP_BARS_THRESHOLD or more hp bars visible, he will fight
	//Returns whether fighting actually occurred
	private static final double FIRE_DIST = 150d;
	@Override
	public boolean fight(PWindow window, Arena arena, WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		boolean hasFought = false;
		while(true) //if in combat, keep fighting
		{
			healer.checkHealth();
			mana(window); //won't do anything if character doesn't have mana
			HealthBarFinder hpBars = new HealthBarFinder(window);
			ArrayList<Point> bars = hpBars.getHealthBars();
			if(bars.size() > HP_BARS_THRESHOLD)
			{
				double a = hpBars.getShootingAngle();
				Point p = new Point();
				p.x = (int) (FIRE_DIST * Math.cos(a));
				p.y = (int) (FIRE_DIST * Math.sin(a));
				attack(window, CoordToolkit.toUncentered(p, PWindow.getWidth(), PWindow.getHeight()));
				hasFought = true;
			}
			else
			{
				break;
			}
		}
		return hasFought;
	}
	@Override
	public double attack(PWindow window, Point p)
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
