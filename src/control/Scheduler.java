package control;

import java.util.ArrayList;
import java.util.Random;

import message.Instruction;
import message.Message;
import data.Profile;
import macro.Macro;


public class Scheduler 
{
	private static final int SCHEDULER_DELAY = 2000;		
	
	/**
	 * Call work around a busy loop, waiting this delay period each time.
	 * @return
	 */
	public static int getDelay() { return SCHEDULER_DELAY; }
	
	private static final Random rnd = new Random();
	
	private MustaphaMond mm;
	
	public Scheduler(MustaphaMond mm)
	{
		this.mm = mm;
	}
	void work()
	{
		idleComputers();
	}
	private static final long MIN_SESSION_IN_MILLIS = 1000 * 60 * 30;
	private static final double CHANCE_QUIT = 0.0005d; //On average, will quit within SCHEDULER DELAY / CHANCE_QUIT millis
	//Computers (and Profiles) that have been active too long are idled
	private void idleComputers()
	{
		for(Computer c : mm.getNetwork().getComputers())
		{
			if(c.getStatus() == ComputerStatus.BUSY && c.getProfileName() != null)
			{
				checkIdle(c);
			}
			if(c.getStatus() == ComputerStatus.IDLE)
			{
				giveTask(c);
			}
		}
	}
	//If this computer should halt
	private void checkIdle(Computer c)
	{
		if(mm.getNetwork().getProfile(c.getProfileName()).sessionTime() > MIN_SESSION_IN_MILLIS)
		{
			if(rnd.nextDouble() < CHANCE_QUIT)
			{
				mm.sendMessage(new Message(mm.getConfig().getComputer(), c, Instruction.IDLE, new ArrayList<String>()));
			}
		}
	}

	private static final int NUM_ATTEMPTS = 10; //number of random attempts to find an empty profile
	//Give this computer something to run
	private void giveTask(Computer c)
	{
		mm.getNetwork().sortProfiles(); //actually the bots might just be... random priority?
		//for(Profile p : mm.getNetwork().getProfiles())
		/*
		for(Profile p : mm.getConfig().getLocalProfiles())
		{
			//If the profile is available and its been idle for how long this random number generator wants it to be
			if(p.isAvailable() && rnd.nextInt((int) SESSION_GAP) < p.idleTime())
			{
				System.out.println(p.getName()+" has had "+p.idleTime()+" idle time.");
				System.out.println("Giving "+c.getName()+" "+p.getName());
				ArrayList<String> profile = new ArrayList<String>();
				profile.add(p.getName());
				mm.updateConsole("AutoOrder: "+c.getName()+" "+p.getName());
				mm.sendMessage(new Message(mm.getConfig().getComputer(), c, Instruction.RUN, profile));
				return;
			}
		}
		*/
		ArrayList<Profile> localProfiles = mm.getConfig().getLocalProfiles();
		recheckAvailability();
		boolean found = false;
		for(int a = 0; a < NUM_ATTEMPTS; a++)
		{
			Profile p = localProfiles.get(new Random().nextInt(localProfiles.size()));
			if(p.isAvailable())
			{
				p.setStatus(ProfileStatus.BUSY);
				System.out.println("Giving "+c.getName()+" "+p.getName());
				ArrayList<String> profile = new ArrayList<String>();
				profile.add(p.getName());
				mm.updateConsole("AutoOrder: "+c.getName()+" "+p.getName());
				mm.sendMessage(new Message(mm.getConfig().getComputer(), c, Instruction.RUN, profile));
				found = true;
			}
		}
		if(!found)
		{
			Macro.sleep(5000); //don't die looking for an empty profile to run
		}
	}
	private void recheckAvailability()
	{
		for(Profile p : mm.getConfig().getLocalProfiles())
		{
			p.setStatus(ProfileStatus.IDLE);
			p.setComputer(null);
		}
		for(Computer c : mm.getNetwork().getComputers())
		{
			if(mm.sendMessage(new Message(mm.getConfig().getComputer(), c, Instruction.ECHO, null)))
			{
				for(Profile p : mm.getConfig().getLocalProfiles())
				{
					if(c.getProfileName() != null && c.getProfileName().equals(p.getName()))
					{
						p.setStatus(ProfileStatus.BUSY);
						p.setComputer(c);
					}
				}
			}
		}
	}
}
