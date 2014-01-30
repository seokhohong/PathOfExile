package window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import macro.Macro;
import macro.PoEMacro;
import macro.Timer;
import process.AHKBridge;
import data.Config;

public class WindowManager 
{
	
	private volatile ArrayList<WindowThread> threads;
	
	private Config config;
	
	public WindowManager(Config config, ArrayList<WindowThread> threads)
	{
		this.config = config;
		this.threads = threads;
	}
	public WindowManager(Config config)
	{
		this.config = config;
		this.threads = new ArrayList<WindowThread>();
	}
	/**
	 * 
	 * Rearranges all PoE windows
	 * 
	 * @return
	 */
	private static final int TOP_OF_SCREEN = 31; //WARNING: may depend on the machine
	public void moveWindows()
	{
		repairWindows();
		while(true)
		{
			ArrayList<PWindow> windows = getWindows();
			for(PWindow window : windows)
			{
				if(window.getY() != TOP_OF_SCREEN)	//then something is displaced
				{
					repairWindows();
					continue;
				}
			}
			if(windows.size() >= config.getNumProfiles()) //We have all our windows
			{
				break;
			}
			else
			{
				while(!openAndWaitPoE())
				{
					
				}
			}
		}	
	}
	private boolean openAndWaitPoE()
	{
		PoEMacro.openPoE(config);
		Timer waitForPoE = new Timer(3 * 60 * 1000);
		while(waitForPoE.stillWaiting())
		{
			System.out.println("Waiting for PoE");
			Macro.sleep(1000);
			if(getWindows().size() >= config.getNumProfiles())
			{
				return true;
			}
		}
		return false;
	}
	private void repairWindows()
	{
		if(config.moveWindow())
		{
			if(config.keepEclipseOpen())
			{
				AHKBridge.runScript("ahk\\MoveWindowsKeepEclipse"+config.getBitArchitecture()+".exe");
			}
			else
			{
				AHKBridge.runScript("ahk\\MoveWindows"+config.getBitArchitecture()+".exe");
			}
		}
		align();
	}
	public void alignWindows()
	{
		align();
	}
	/**
	 * 
	 * Retrieves the locations of all PoE windows
	 * 
	 * @return
	 */
	public ArrayList<PWindow> getWindows()
	{
		ArrayList<PWindow> windows = new ArrayList<PWindow>();
		Set<PWindow> occupiedWindows = new HashSet<PWindow>();
	    try
	    {
	    	ArrayList<String> output = null;
	    	output = AHKBridge.runScript("ahk\\FindWindows"+config.getBitArchitecture()+".exe");

	    	for(String s : output)	//The AHK script will nicely format its output
	    	{
	    		occupiedWindows.add(PWindow.fromString(config, s)); //Avoid duplicating
	    	}
	    } 
	    catch(Exception e) 
	    {
	    	System.out.println(e);
	    }
	    windows.addAll(occupiedWindows);
	    return windows;
	}
	
	/**
	 * 
	 * Alignment Methods
	 * 
	 */
	private void align()
	{
		ArrayList<PWindow> unmatchedWindows = findUnmatchedWindows();
		ArrayList<WindowThread> unmatchedThreads = findUnmatchedThreads();
		for(int a = 0; a < Math.min(unmatchedWindows.size(), unmatchedThreads.size()); a++)
		{
			System.out.println("Realigning "+unmatchedThreads.get(a)+" with "+unmatchedWindows.get(a));
			unmatchedThreads.get(a).realignPWindow(unmatchedWindows.get(a));
		}
	}
	//Finds PWindow that should exist, but are not matched to a WindowThread
	private ArrayList<PWindow> findUnmatchedWindows()
	{
		ArrayList<PWindow> unoccupiedWindows = new ArrayList<PWindow>();
		for(PWindow pwind : getWindows())
		{
			boolean occupied = false;
			for(WindowThread thread : threads)
			{
				if(thread.hasPWindow(pwind))
				{
					occupied = true;
				}
			}
			if(!occupied)
			{
				unoccupiedWindows.add(pwind);
			}
		}
		return unoccupiedWindows;
	}
	//Finds WindowThreads that occupy nonexistent windows
	private ArrayList<WindowThread> findUnmatchedThreads()
	{
		ArrayList<WindowThread> unoccupiedThreads = new ArrayList<WindowThread>();
		for(WindowThread thread : threads)
		{
			boolean occupied = false;
			for(PWindow pwind : getWindows())
			{
				if(thread.hasPWindow(pwind))
				{
					occupied = true;
				}
			}
			if(!occupied)
			{
				System.out.println(thread+" Is Unmatched with a PWindow");
				unoccupiedThreads.add(thread);
			}
		}
		return unoccupiedThreads;
	}
}
