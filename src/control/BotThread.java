package control;

import java.util.ArrayList;

import macro.Macro;
import memo.InternalMemo;
import message.Message;
import message.RelayThread;
import window.ThreadManager;
import data.Config;
import data.Profile;

/**
 * 
 * Running Bot:
 * Any Path of Exile windows must be on login screen
 * Make sure there is nothing visible except eclipse and the PoE windows
 * 
 * Only supports one bot
 * 
 * Last Updated 6/26/2013
 * 
 * 
 * 
 * 
 * @author HONG
 *
 */
public class BotThread extends Thread// implements Quittable
{
	private ArrayList<Profile> profs = new ArrayList<Profile>(); 

	private boolean exitProgram = false;												
	
	private ThreadManager thrdMgr;			
	
	private boolean logout; //log out on halt?
	
	private Profile currProf; 		public Profile getProfile() { return currProf; }
	
	private Config config;
	
	private RelayThread<Message> relay;
	
	BotThread(Config config, Profile prof, RelayThread<Message> relay)
	{
		this.config = config;
		this.relay = relay;
		currProf = prof;
		profs.add(prof);
	}
	
	//Returns whether to continue execution
	@Override
	public void run()
	{
		//AHKBridge.runExitHook(this);	//Allows (relatively) quick exit
		thrdMgr = new ThreadManager(config, profs, relay);
		thrdMgr.run();
		while(!exitProgram) //avoid busylooping?
		{
			Macro.sleep(1 * 1000);		//Main thread has no reason to be very busy
		}
		thrdMgr.kill(logout);
	}
	
	public void sendMemo(InternalMemo memo)
	{
		currProf.receiveMemo(memo);
	}
	
	/**
	 * 
	 * Halts the thread
	 * 
	 * @param logout	: Should the bot log out?
	 */
	public void exitProgram(boolean logout)
	{
		this.logout = logout;
		exitProgram = true;
	}
}
