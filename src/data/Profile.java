package data;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import macro.LogoutException;
import memo.InternalMemo;
import memo.MemoProcessor;
import party.SocialMacro;
import window.HaltThread;
import window.PWindow;
import window.WindowThread;
import arena.Arena;

import combat.CombatStyle;
import combat.Healer;
import combat.SpellInstance;

import control.Computer;
import control.MustaphaMond;
import control.Network;
import control.ProfileStatus;
import control.TimeLog;

public class Profile implements Comparable<Profile>
{
	static final String PROFILE_DIR = "profile/"; 		public static String dir() { return PROFILE_DIR; }
	
	//Maintains a list of all local profiles read from file
	static final ArrayList<Profile> localProfiles = new ArrayList<Profile>();		
	
	public static ArrayList<Profile> getLocalProfiles(Config config) 
	{ 
		if(localProfiles.isEmpty())
		{
			String s = PROFILE_DIR + config.getComputer()+"/";
			for(File f : new File(s).listFiles())
			{
				localProfiles.add(new Profile(f));
			}
		}
		return localProfiles; 
	}
	
	private static final int VERSION = 7; 
	
	private String name;							public String getName() { return name; }
	
	private String poeName;							public String getPoeName() { return poeName; }
	
	//Log in information
	private String email;							public String getEmail() { return email; }
	private String password;						public String getPassword() { return password; }
	
	//Character class
	private CombatStyle combatStyle;				public CombatStyle getCombatStyle() { return combatStyle; }
	
	//When logged in, which character position it is in on the list (0-offset)
	private int indexOnList;						public int getIndexOnList() { return indexOnList; }
	
	//Character level
	private int level; 								public int getLevel() { return level; }
	
	private Point twilightSellPoint;				public Point getTwilightSellPoint() { return twilightSellPoint; }
	
	//What potions this character holds (only type is significant)
	private static final int NUM_POTIONS = 5;			public int getNumPotions() { return NUM_POTIONS; }
	
	private Potion[] potions = new Potion[NUM_POTIONS];	public Potion getPotions(int index) { return potions[index]; }
	
	public void usePotion(PWindow window, Potion p) { Potion.usePotion(window, potions, p); }
	public void useHealingPotion(PWindow window) { Potion.useHealingPotion(window, potions); }
	public void useManaPotion(PWindow window) { Potion.useManaPotion(window, potions); }
	
	/**
	 * 
	 * Returns the indices of all potions of a certain type
	 * 
	 * @param type
	 * @return
	 */
	public ArrayList<Integer> getPotions(Potion type)
	{
		ArrayList<Integer> matchingPots = new ArrayList<Integer>();
		for(int a = 0; a < potions.length; a++)
		{
			if(potions[a] == type)
			{
				matchingPots.add(a + 1);
			}
		}
		return matchingPots;
	}
	
	private int healingHealth;		public int getHealingHealth() { return healingHealth; }
	private int criticalHealth;		public int getCriticalHealth() { return criticalHealth; }
	
	private static final long NO_START = -1L;
	private long startTime = NO_START; //since it has yet to start
	
	
	private static final long MAX_PLAYTIME_PER_DAY = 1000 * 60 * 60 * 12; //Change to 6 later
	
	public boolean isAvailable() { System.out.println("Status for "+name+" is "+status); return status == ProfileStatus.IDLE; } 
	/** @return : 	Time the profile has been active */
	public long sessionTime() { return startTime == NO_START ? 0 : System.currentTimeMillis() - startTime; }
	
	private EventLog eventLog = new EventLog();
	public void logsOut(Config config)
	{
		eventLog.addEvent(EventType.LOGOUT);
		if(eventLog.numberOfEvents(EventType.LOGOUT, 5 * 60 * 1000) > 15)
		{
			System.err.println("Logging out Frequently");
			config.emailNotification("Logging Out Frequently");
		}
	}
	
	private ProfileStatus status = ProfileStatus.IDLE;		public ProfileStatus getStatus() { return status; }
	
	public void setStatus(ProfileStatus status) 
	{
		this.status = status; 
	}
	
	private Computer comp = null;							public Computer getComputer() { return comp; } //what computer this is running on, if any
															public void setComputer(Computer comp) { this.comp = comp; }
															
	//Lower number is higher priority to run
	private int priority; 									public int getPriority() { return priority; }
															public void setPriority(int priority) { this.priority = priority; } //could use a more robust api
															
														
	public static void savePriorities(Network network)
	{
		try
		{
			BufferedWriter buff = new BufferedWriter(new FileWriter(MustaphaMond.getPrioritiesFile()));
			Vector<Profile> profiles = network.getProfiles();
			Collections.sort(profiles);
			for(Profile prof : profiles)
			{
				buff.write(prof.name);
				buff.newLine();
			}
			buff.close();
		}
		catch(IOException e)
		{
			
		}
	}
	
	private ArrayList<InternalMemo> memos = new ArrayList<InternalMemo>();
	public void receiveMemo(InternalMemo im) { memos.add(im); }
	
	public void processMemos(Network network, PWindow window) 
	{
		for(InternalMemo memo : memos)
		{
			MemoProcessor.process(network, window, this, memo);
		}
		memos.clear();
	}
	
	private Profile friend = null;
	
	public void setFriend(Profile friend) { System.out.println("Setting friend of "+name+" to "+friend.name); this.friend = friend; }
	public Profile getFriend() { return friend; }
	
	public String getDescription()
	{
		if(friend != null)
		{
			return "(Partied With: "+friend+")";
		}
		return "";
	}
	
	public void reparty(PWindow window)
	{
		if(friend != null)
		{
			SocialMacro.inviteToParty(window, friend);
		}
	}
	/*
	//Used for determining whether this character should go into the portal (as a waiter)
	private boolean portalOpen = false;						public void markPortalOpen() { portalOpen = true; }
															public void markPortalClosed() { portalOpen = false; }
															public boolean isPortalOpen() { return portalOpen; }
															*/
	
	/** Clears out all existing arenas and replaces it with this one */
	public void changeArena(final Arena arena)
	{
		arenas.clear();
		arenas.add(arena);
	}

	
	//What Arenas this character is allowed to have access to
	private ArrayList<Arena> arenas = new ArrayList<Arena>();
	
	private boolean hasMana = false;						public void foundManaBar() { hasMana = true; }
															public boolean hasMana() { return hasMana; }

	/**
	 * 
	 * Gets an available arena based on when it was last visited
	 * If no arenas are available, it will hang until there is one available
	 * 
	 * @return
	 */
	public Arena getAvailableArena()
	{
		int rndIndex = new Random().nextInt(arenas.size());
		return arenas.get(rndIndex);
	}

	@Override
	public String toString()
	{
		return name;
	}
	
	public boolean fight(PWindow window, Arena arena, WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		return combatStyle.fight(window, arena, thread, healer);
	}
	public void useAuras(PWindow window)
	{
		combatStyle.useAuras(window);
	}
	/*
	public void turnOffAuras(PWindow window)
	{
		combatStyle.turnOffAuras(window);
	}
	*/
	public void pickUpItems(PWindow window, WindowThread thread, Healer healer) throws LogoutException, HaltThread
	{
		combatStyle.pickUpItems(window, thread, healer);
	}
	public void shootEverywhere(PWindow window, Healer healer) throws LogoutException
	{
		combatStyle.shootEverywhere(window, healer);
	}
	public void castDecoyTotem(PWindow window)
	{
		combatStyle.castDecoyTotem(window);
	}
	public void castAttackTotem(PWindow window)
	{
		combatStyle.castAttackTotem(window);
	}
	public boolean castPortal(PWindow window)
	{
		return combatStyle.castPortal(window);
	}
	public double attack(PWindow window, Point p)
	{
		return combatStyle.attack(window, p);
	}
	//Loads a profile from the file
	public Profile(File f)
	{
		readName(f);	//based on filename
		Queue<String> lines = new LinkedList<String>(); 
		Data.readLines(f, lines);
		readVersion(f, lines);
		readPoeName(lines);
		readLoginInfo(lines); 	//email and password
		readIndexOnList(lines);
		readLevel(lines);
		readPotions(lines);
		
		ArrayList<SpellInstance> spells = new ArrayList<SpellInstance>();	
		readSpells(lines, spells);
		readClass(lines, spells);
		
		readArenas(lines);
		readHealing(lines);
		readTwilightSellPoint(lines);
		//readHome(lines); //unused at the moment
	}
	private void readName(File f)
	{
		name = f.getName().split("\\.")[0];
	}
	private void readPoeName(Queue<String> lines)
	{
		poeName = lines.poll();
	}
	//Each read method dequeues as it processes
	private void readVersion(File f, Queue<String> lines)
	{
		//#
		int version = Integer.parseInt(lines.poll());
		if(version != VERSION)
		{
			System.err.println("File "+f.getName()+" is of version "+version+". Update to Version "+VERSION+".");
			System.exit(1);
		}
	}
	private void readLoginInfo(Queue<String> lines)
	{
		email = lines.poll();
		password = lines.poll();
	}
	private void readClass(Queue<String> lines, ArrayList<SpellInstance> spells)
	{
		combatStyle = CombatStyle.fromString(lines.poll(), spells);
	}
	private void readIndexOnList(Queue<String> lines)
	{
		//#
		indexOnList = Integer.parseInt(lines.poll());
	}
	private void readLevel(Queue<String> lines)
	{
		//#
		level = Integer.parseInt(lines.poll());
	}
	private void readPotions(Queue<String> lines)
	{
		//{Potion}, {Potion}, etc.
		String[] potions = lines.poll().split(",");
		for(int a = 0; a < NUM_POTIONS; a++)
		{
			this.potions[a] = Potion.fromString(potions[a]);
		}
	}
	private void readSpells(Queue<String> lines, ArrayList<SpellInstance> spells)
	{
		//Spell:Key:Freq, (decimal)
		//Spell:Key:Freq,
		//...
		//Spell:Key:Freq;
		boolean isTerminal = false;
		while(!isTerminal)
		{
			String line = lines.poll();
			isTerminal = line.endsWith(";");
			line = line.substring(0, line.length() - 1); //cut off last character
			String[] components = line.split(":");
			for(int a = 0; a < components.length; a++)
			{
				components[a] = components[a].trim();
			}
			if(components[0].endsWith("+")) //Marks it to use
			{
				components[0] = components[0].substring(0, components[0].length() - 1);
				spells.add(new SpellInstance(components));
			}
		}
	}
	private void readArenas(Queue<String> lines)
	{
		//#represents level of difficulty, 0-2
		//Arena#,
		//Arena#,
		//...
		//Arena#;
		while(true)
		{
			String line = lines.poll();
			boolean isTerminal = line.endsWith(";");
			line = line.substring(0, line.length() - 1); //cut off last character
			arenas.add(Arena.fromString(line)); //assume we have yet to visit each location
			if(isTerminal) break;
		}
	}
	private void readHealing(Queue<String> lines)
	{
		//# Healing followed by Critical (Lower is more health)
		//#
		//# 
		healingHealth = Integer.parseInt(lines.poll());
		criticalHealth = Integer.parseInt(lines.poll());
	}
	
	private void readTwilightSellPoint(Queue<String> lines)
	{
		//# Healing followed by Critical (Lower is more health)
		//#
		//# 
		twilightSellPoint = new Point(PWindow.getWidth() / 2, Integer.parseInt(lines.poll()));
	}
	
	@Override
	public int compareTo(Profile otherProfile) 
	{
		return (priority - otherProfile.priority);
	}
	
}
