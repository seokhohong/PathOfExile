package main;

import macro.Macro;
import process.AHKBridge;
import process.Quittable;
import window.ThreadManager;
import data.Config;

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
public class Main implements Quittable
{
	private boolean exitProgram = false;												
	
	private ThreadManager thrdMgr;
	
	private Config config;
	
	public static void main(String[] args)
	{
		new Main().go();
	}
	//Returns whether to continue execution
	private void go()
	{
		config = new Config();
		AHKBridge.runExitHook(this, config);	//Allows (relatively) quick exit		
		
		thrdMgr = new ThreadManager(config);
		thrdMgr.run();
		while(!exitProgram) //avoid busylooping?
		{
			Macro.sleep(1 * 1000);		//Main thread has no reason to be very busy
		}
		thrdMgr.kill(config.exitOnHalt());
	}
	@Override
	public void exitProgram() 
	{ 
		System.out.println("Exit Command Received"); 
		thrdMgr.kill(config.exitOnHalt()); 
		exitProgram = true; 
	}
}
