package combat;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import macro.Macro;
import window.PWindow;

/**
 * 
 * Defines a particular spell associated with a particular character
 * 
 * @author Seokho
 *
 */
public class SpellInstance 
{
	private Spell spell;			public Spell getSpell() { return spell; }
	private int key;				public int getKey() { return key; }
	private double frequency;		public double getFrequency() { return frequency; }
	private double cooldown; 		public double getCooldown() { return cooldown; }
	public SpellInstance(String[] spellData)
	{
		this.spell = Spell.fromString(spellData[0]);
		this.key = Spell.keyFromString(spellData[1]);
		this.frequency = spellData.length >=3 ? Double.parseDouble(spellData[2]) : 0;
		this.cooldown = spellData.length >=4 ? Double.parseDouble(spellData[3]) : 1.0d;
	}
	
	private static HashMap<Integer, String> keyToString = new HashMap<Integer, String>();
	static
	{
		keyToString.put(KeyEvent.VK_Q, "q");
		keyToString.put(KeyEvent.VK_W, "w");
		keyToString.put(KeyEvent.VK_E, "e");
		keyToString.put(KeyEvent.VK_R, "r");
		keyToString.put(KeyEvent.VK_T, "t");
	}
	
	/**
	 * 
	 * Casts a spell. Beware of frequently casting with keys.
	 * 
	 * @param window
	 */
	
	private static final int NUM_PRESSES = 1; 
	public void cast(PWindow window)
	{
		for(int a = 0; a < NUM_PRESSES; a++)
		{
			int x = PWindow.selectPoint().x;
			int y = PWindow.selectPoint().y;
			switch(key)
			{
			case MouseEvent.BUTTON1 : window.leftClick(new Point(x, y)); break;
			case MouseEvent.BUTTON2 : window.middleClick(new Point(x, y)); break;
			case MouseEvent.BUTTON3 : window.rightClick(new Point(x, y)); break;
			case KeyEvent.VK_Q : window.typeInField("q", PWindow.selectPoint()); break;
			case KeyEvent.VK_W : window.typeInField("w", PWindow.selectPoint()); break;
			case KeyEvent.VK_E : window.typeInField("e", PWindow.selectPoint()); break;
			case KeyEvent.VK_R : window.typeInField("r", PWindow.selectPoint()); break;
			case KeyEvent.VK_T : window.typeInField("t", PWindow.selectPoint()); break;
			default : System.err.println(key + " is not a valid action");
			}
		}
	}
	/**
	 * 
	 * Casts a spell, aimed at a certain point. Beware of frequently casting with keys.
	 * 
	 * @param window
	 */
	public void cast(PWindow window, Point p)
	{
		for(int a = 0; a < NUM_PRESSES; a++)
		{
			window.mouseMove(p);
			Macro.sleep(20);
			switch(key)
			{
			case MouseEvent.BUTTON1 : window.leftClick(p); break;
			case MouseEvent.BUTTON2 : window.middleClick(p); break;
			case MouseEvent.BUTTON3 : window.rightClick(p); break;
			case KeyEvent.VK_Q : window.type("q"); break;
			case KeyEvent.VK_W : window.type("w"); break;
			case KeyEvent.VK_E : window.type("e"); break;
			case KeyEvent.VK_R : window.type("r"); break;
			case KeyEvent.VK_T : window.type("t"); break;
			default : System.err.println(key + " is not a valid action");
			}
		}
	}
	/**
	 * 
	 * Sleeps the thread until the spell has been casted
	 * 
	 */
	public void waitCastDelay()
	{
		Macro.sleep((int) (cooldown * 1000d)); //convert to seconds
	}
	public static SpellInstance mainSpell(ArrayList<SpellInstance> spells)
	{
		if(spells.size() == 0) 
		{
			System.err.println("No Spells");
			return null; //issue here
		}
		
		SpellInstance mostFrequent = spells.get(0);
		for(SpellInstance i : spells)
		{
			if(i.frequency > mostFrequent.frequency) //thus avoids using spells with no frequency
			{
				mostFrequent = i;
			}
		}
		return mostFrequent;
	}
}
