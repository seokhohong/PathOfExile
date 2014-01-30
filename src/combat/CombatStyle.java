package combat;

import img.IntBitmap;
import items.Item;
import items.ItemFinder;
import items.ItemType;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import macro.LogoutException;
import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;
import arena.Arena;
import data.Potion;
import macro.Hotkey;


public abstract class CombatStyle 
{
	static final Random rnd = new Random();
	private ArrayList<SpellInstance> spells;		ArrayList<SpellInstance> getSpells() { return spells; }

	CombatStyle(ArrayList<SpellInstance> spells)
	{
		this.spells = spells;
	}
	public static CombatStyle fromString(String s, ArrayList<SpellInstance> spells)
	{
		switch(s)
		{
		case "MARAUDER": return new Marauder(spells);
		case "RANGER": return new Ranger(spells);
		case "SHADOW": return new Shadow(spells);
		case "DISCHARGER": return new Discharger(spells);
		default : System.err.println("Could not convert "+s+" to a CombatStyle"); System.exit(0); return null;
		}
	}
	boolean hasSpell(Spell spell)
	{
		for(SpellInstance s : spells)
		{
			if(s.getSpell() == spell)
			{
				return true;
			}
		}
		return false;
	}
	SpellInstance getSpellInstanceOf(Spell spell)
	{
		for(SpellInstance s : spells)
		{
			if(s.getSpell() == spell)
			{
				return s;
			}
		}
		return null;
	}
	public double attack(PWindow window, Point p)
	{
		double atkChoice = rnd.nextDouble();
		for(SpellInstance s : spells)
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
	
	/**
	 * 
	 * 
	 * @param window
	 * @param angle		: angle in radians
	 * @param dist
	 */
	public void move(PWindow window, double angle, int dist, int numMoves)
	{
		PoEMacro.moveHero(window, angle, dist);
	}
	public void rush(PWindow window, double angle, int dist)
	{
		PoEMacro.moveHero(window, angle, dist);
	}
	public void shootEverywhere(PWindow window, Healer healer) throws LogoutException
	{
		
	}
	private static final double CHANCE_QUICKSILVER = 0.15d; //per quicksilver
	//Has a chance to use a Quicksilver potion
	void useQuicksilver(PWindow window)
	{
		if(rnd.nextDouble() < CHANCE_QUICKSILVER * window.getProfile().getPotions(Potion.QUICKSILVER).size())
		{
			window.getProfile().usePotion(window, Potion.QUICKSILVER);
		}
	}
	//Returns whether fighting actually occurred
	public boolean fight(PWindow window, Arena arena, WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		/*
		boolean hasFought = false;
		Blotch prevClicked = null; //Keeps track of the last position we clicked so that we can check that first
		while(System.currentTimeMillis()- startTime < arena.getMaxLevelTime())
		{
			window.heal();
			window.mana(); //won't do anything if character doesn't have mana
			Combat c = new Combat(window);
			ArrayList<Blotch> blotches =  c.getBlotches();
			//Insert the last successful blotch coordinate as the first candidate
			if(prevClicked!=null) PWindow.insertFront(blotches, prevClicked);
			boolean clicked = false;
			//If we just got out of a battle, make sure we're completely out
			int numCheck = hasFought ? 10 : 5;
			for(int a = 0; a < Math.min(blotches.size(), numCheck); a++)
			{
				Blotch b = blotches.get(a);
				Macro.macro.sleep(35);
				if(window.existsEnemyBar())
				{
					prevClicked = b;
					attack(window, new Point(b.getX(), b.getY()));
					if(rnd.nextFloat() < 0.2f)
					{
						window.findItems();
					}
					clicked = true;
					hasFought = true;
					break;
				}
			}
			if(!clicked)
			{
				break;
			}
		}
		return hasFought;
		*/
		System.exit(10);
		System.out.println("Deprecated Fight method");
		return false;
	}
	public void useAuras(PWindow window)
	{
		for(SpellInstance s : getSpells())
		{
			if(s.getSpell().getSpellType() == SpellType.AURA && !PoEMacro.auraOn(window, Hotkey.fromKeyEvent(s.getKey())))
			{
				//Presses the key associated with this spell
				s.cast(window);
				Macro.sleep((int) (s.getCooldown() * 1000d));
			}
		}
	}
	/*
	public void turnOffAuras(PWindow window)
	{
		for(SpellInstance s : getSpells())
		{
			if(s.getSpell().getSpellType() == SpellType.AURA)
			{
				//System.out.println("Decasting "+s);
				//Presses the key associated with this spell
				s.cast(window, PWindow.selectPoint());
				Macro.sleep(300);
			}
		}
	}
	*/
	private static final int TOTEM_SLEEP = 100;
	public void castAttackTotem(PWindow window)
	{
		castTotem(window, SpellType.ATTACK_TOTEM);
	}
	public void castDecoyTotem(PWindow window)
	{
		castTotem(window, SpellType.DECOY_TOTEM);
	}
	private static final int TOTEM_ADJUST_X = 10;
	private static final int TOTEM_ADJUST_Y = -10;
	public void castTotem(PWindow window, SpellType spellType)
	{
		for(SpellInstance s : getSpells())
		{
			if(s.getSpell().getSpellType() == spellType)
			{
				//Totems should not be casted at precisely the center, but a bit to the up and right because of occasional placement issues
				//This is for Crossroads totems
				Point p = new Point(PWindow.getWindowCenter().x + TOTEM_ADJUST_X, PWindow.getWindowCenter().y + TOTEM_ADJUST_Y);
				switch(s.getKey())
				{
				case MouseEvent.BUTTON1 : window.mouseMove(p); Macro.sleep(TOTEM_SLEEP); window.leftClick(p); break;
				case MouseEvent.BUTTON2 : window.mouseMove(p); Macro.sleep(TOTEM_SLEEP); window.middleClick(p); break;
				case MouseEvent.BUTTON3 : window.mouseMove(p); Macro.sleep(TOTEM_SLEEP); window.rightClick(p); break;
				case KeyEvent.VK_Q : window.mouseMove(p); Macro.sleep(TOTEM_SLEEP); window.type("q"); break;
				case KeyEvent.VK_W : window.mouseMove(p); Macro.sleep(TOTEM_SLEEP); window.type("w"); break;
				case KeyEvent.VK_E : window.mouseMove(p); Macro.sleep(TOTEM_SLEEP); window.type("e"); break;
				case KeyEvent.VK_R : window.mouseMove(p); Macro.sleep(TOTEM_SLEEP); window.type("r"); break;
				case KeyEvent.VK_T : window.mouseMove(p); Macro.sleep(TOTEM_SLEEP); window.type("t"); break;
				default : System.err.println(s.getKey()+ " is not a valid action");
				}
				Macro.sleep((int) (s.getCooldown() * 1000d));
			}
		}
	}
	private static final int WAIT_FOR_PORTAL_LABEL = 0;
	public boolean castPortal(PWindow window)
	{
		for(SpellInstance s : getSpells())
		{
			if(s.getSpell().getSpellType() == SpellType.PORTAL)
			{
				s.cast(window, PWindow.getWindowCenter());
				Macro.sleep((int) (s.getCooldown() * 1000d) + WAIT_FOR_PORTAL_LABEL);
				return true;
			}
		}
		return false;
	}

	private static final int ITEM_ATTEMPT_TIME = 20000;
	private static final double MVMT_SPEED = 1.8d; //How much to multiply the distance to the item by to get millisecond to wait
	private static final int TOO_MANY_CLICKS = 20;
	public void pickUpItems(PWindow window, WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		
		ArrayList<ItemType> types = new ArrayList<ItemType>();
		types.add(ItemType.CURRENCY);
		types.add(ItemType.GEM);
		//types.add(ItemType.RARE);
		types.add(ItemType.UNIQUE);
		
		int numClicks = 0;
		Timer itemAttemptTimer = new Timer(ITEM_ATTEMPT_TIME);
		while(itemAttemptTimer.stillWaiting()) //while not taking too long . . .
		{
			ArrayList<Item> items = ItemFinder.findItems(window, ScreenRegion.ITEM_SCAN_RECT, types);
			if(items.isEmpty())
			{
				break;
			}
			double distToItem = items.get(0).toPoint().distance(new Point(PWindow.getWindowCenter()));	//how far away the item is
			window.leftClick(items.get(0).getX() + items.get(0).getWidth() / 4, items.get(0).getY() + 4); //don't click on the top left corner!
			numClicks++;
			if(numClicks == TOO_MANY_CLICKS)
			{
				throw new LogoutException("Inventory Full (Probably)");
			}
			Timer walkTime = new Timer((int) (distToItem * MVMT_SPEED));
			while(walkTime.stillWaiting())
			{
				HealthBarFinder hpBars = new HealthBarFinder(window);
				if(items.size() == 0 || !hpBars.isSafe()) 
				{
					return;
				}
				if(healer.checkHealth())
				{
					return;
				}
				Macro.sleep(50); //some breathing room here
			}
		}
	}
	public void mana(PWindow window) throws LogoutException
	{
		if(window.getProfile()!=null && window.getProfile().hasMana())
		{
			new Mana(window).keepFilled();
		}
	}
}
