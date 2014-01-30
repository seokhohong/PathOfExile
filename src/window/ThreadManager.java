package window;

import java.util.ArrayList;

import macro.Macro;
import message.Message;
import message.RelayThread;
import data.Config;
import data.Profile;

/**
 * 
 * Manages the WindowThreads of the program
 * 
 * Does not ever kill the threads unless the program is instructed to halt
 * 
 * @author HONG
 *
 */
public class ThreadManager 
{
	private WindowManager winMgr;
	
	private ArrayList<WindowThread> threads = new ArrayList<WindowThread>();
	private ArrayList<PWindow> windows = new ArrayList<PWindow>();
	
	private Config config;
	
	private boolean halt = false;
	
	private ArrayList<Profile> profiles = null; //predesignated list of profiles
	
	private RelayThread<Message> relay = null;
	
	public ThreadManager(Config config)
	{
		this.config = config;
		this.winMgr = new WindowManager(config, threads);
		windows = winMgr.getWindows();
		this.profiles = config.getLocalProfiles();
	}
	
	public ThreadManager(Config config, ArrayList<Profile> profiles, RelayThread<Message> relay)
	{
		this.config = config;
		this.relay = relay;
		this.winMgr = new WindowManager(config, threads);
		windows = winMgr.getWindows();
		this.profiles = profiles;
	}
	
	public void kill(boolean logout)
	{
		halt = true;
		killThreads(logout);
	}
	
	private void killThreads(boolean logout)
	{
		for(WindowThread t : threads)
		{
			t.stop(logout);
		}
		threads.clear();
	}
	public void run()
	{
		winMgr.moveWindows();
		buildThreads(profiles.size());
		winMgr.alignWindows();
		startThreads();
	}
	private void buildThreads(int numPrograms)
	{
		if(!halt)
		{
			if(profiles.isEmpty())
			{
				System.err.println("No Profiles to Run!");
				Macro.sleep(1000); //for the error message to display on command
				System.exit(0);
			}
			windows = winMgr.getWindows();
			for(int a = 0; a < numPrograms; a++)
			{
				windows.get(a).setProfile(profiles.get(a));
				threads.add(new WindowThread(config, winMgr, relay, windows.get(a)));
			}
		}
	}
	private void startThreads()
	{
		for(WindowThread thread : threads)
		{
			thread.start();
		}
	}
}
