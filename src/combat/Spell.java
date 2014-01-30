package combat;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * List of all spells used by characters
 * 
 * @author Seokho
 *
 */
public enum Spell 
{
	CLEAVE(SpellType.CASTED),
	HEAVY_STRIKE(SpellType.CASTED),
	SHIELD_CHARGE(SpellType.CASTED),
	MOLTEN_SHIELD(SpellType.CASTED),
	LIGHTNING_ARROW(SpellType.CASTED),
	REJUVENATION_TOTEM(SpellType.CASTED),
	POISON_ARROW(SpellType.CASTED),
	ANGER(SpellType.AURA),
	PURITY(SpellType.AURA),
	HASTE(SpellType.AURA),
	WRATH(SpellType.AURA),
	FLAME_TOTEM(SpellType.ATTACK_TOTEM),
	LIGHTNING_TOTEM(SpellType.ATTACK_TOTEM),
	EXPLOSIVE_TOTEM(SpellType.ATTACK_TOTEM),
	DECOY_TOTEM(SpellType.DECOY_TOTEM),
	PORTAL(SpellType.PORTAL),
	EXPLOSIVE_ARROW(SpellType.CASTED),
	ETHEREAL_KNIVES(SpellType.CASTED),
	ENDURING_CRY(SpellType.CASTED),
	DISCHARGE(SpellType.CASTED),
	ICE_NOVA(SpellType.CASTED),
	FREEZING_PULSE(SpellType.CASTED); //??
	
	private SpellType type;
	public SpellType getSpellType() { return type; }
	
	private Spell(SpellType type)
	{
		this.type = type;
	}
	public static Spell fromString(String s)
	{
		for(Spell p : values())
		{
			if(p.toString().equals(s))
			{
				return p;
			}
		}
		System.err.println("Could not convert "+s+" to Spell");
		System.exit(1);
		return null;
	}
	private static final Map<String, Integer> keyMap = new HashMap<String, Integer>();
	static
	{
		keyMap.put("L_BUTTON", MouseEvent.BUTTON1);
		keyMap.put("M_BUTTON", MouseEvent.BUTTON2);
		keyMap.put("R_BUTTON", MouseEvent.BUTTON3);
		keyMap.put("Q", KeyEvent.VK_Q);
		keyMap.put("W", KeyEvent.VK_W);
		keyMap.put("E", KeyEvent.VK_E);
		keyMap.put("R", KeyEvent.VK_R);
		keyMap.put("T", KeyEvent.VK_T);
	}
	public static int keyFromString(String s)
	{
		try
		{
			return keyMap.get(s.trim());
		}
		catch(Exception e) //if it does not contain this key
		{
			System.err.println("Could not convert "+s+" to Spell");
			System.exit(1);
			return -1;
		}
	}
}


