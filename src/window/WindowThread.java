package window;

import macro.*;
import message.Message;
import message.RelayThread;
import arena.Arena;
import control.Computer;
import control.Network;
import data.Config;
import data.Profile;

/**
 * 
 * Represents the thread working on a particular PWindow
 * 
 * @author HONG
 *
 */
public class WindowThread implements Runnable
{
	//Parent of sorts
	private WindowManager winMgr;
	
	private volatile Thread thread;
	
	private PWindow window;						public PWindow getWindow() { return window; }
	private Profile profile;					public Profile getProfile() { return profile; }
	
	//Whether this thread should halt
	private boolean halt = false;				public boolean hasHalted() { return halt; }
	
	//Number of consecutive failures to login
	private int numConsecutiveFails = 0;		public void loginFailure() { numConsecutiveFails ++ ; }
												public void loginSuccess() { numConsecutiveFails = 0; } 	//resets consecutive failurs
												
	private static final int TOO_MANY_FAILURES = 2;
	
	private Config config;						public Config getConfig() { return config; }
												public Network getNetwork() { return config.getNetwork(); }
	
	//Whether the thread should try to logout upon halting
	private boolean logout;
	
	private RelayThread<Message> relay;
	
	public WindowThread(Config config, WindowManager winMgr, RelayThread<Message> relay, PWindow window)
	{
		this.config = config;
		this.relay = relay;
		this.window = window;
		this.profile = window.getProfile();
		this.winMgr = winMgr;
		this.logout = config.exitOnHalt();
		thread = new Thread(this);
		setName(window);
	}
	public void start()
	{
		thread.start();
	}
	public void realignPWindow(PWindow newWindow)
	{
		window = newWindow;
		setName(newWindow);
	}
	/**
	 * 
	 * Returns whether this thread has a PWindow at all
	 * 
	 * @return
	 */
	public boolean hasPWindow()
	{
		for(PWindow window : winMgr.getWindows())
		{
			if(hasPWindow(window))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * Returns whether testWindow is linked with this thread
	 * 
	 * @param testWindow
	 * @return
	 */
	public boolean hasPWindow(PWindow testWindow)
	{
		return testWindow.getX() == window.getX() && testWindow.getY() == window.getY();
	}
	
	public void sendMessage(Message message)
	{
		if(relay != null) //might be running off-network
		{
			relay.send(message);
		}
	}
	
	public void stop(boolean logout)
	{
		System.out.println("Halting "+thread);
		this.logout = logout;
		halt = true;
	}
	public boolean checkHalt() throws HaltThread
	{
		if(halt) 
		{
			System.out.println(thread+" Throwing HaltThread");
			throw new HaltThread();
		}
		return halt;
	}
	@Override
	public void run()
	{
		while(!halt)
		{
			System.out.println("In Main Loop");
			try
			{
				Arena toPlay = getProfile().getAvailableArena();
				toPlay.login(this);
				while(true)
				{
					toPlay.openArena(this);
					if(!toPlay.clearArena(this))
					{
						break;
					}
				}
				window.logout();
				System.out.println("Next Arena");
			}
			catch(LogoutException e) //thrown to log out or in confusion
			{
				handleLogoutException(e);
			} 
			catch(WindowException e) //pretty sure something is wrong
			{
				handleTooManyFailures(); 
			}
			catch(HaltThread t) //manual halt
			{
				if(window.myHealthVisible() && logout)
				{
					window.logout();
				}
				return;
			}
		}
		System.out.println("Halted");
	}
	private void handleLogoutException(LogoutException e)
	{
		System.out.println(thread+" Caught LogoutException "+e.getMessage());
		if(e.getMessage().equals("Could not find Labels"))
		{
			e.printStackTrace();
		}
		window.logout();
		handleWindowFailure();
	}
	private void handleWindowFailure()
	{
		if(tooManyFailures()) //give it a few chances
		{
			handleTooManyFailures();
			return;
		}
	}
	private void handleTooManyFailures()
	{
		winMgr.moveWindows(); //fixes PoE
		config.emailNotification(buildNotification());
	}
	private String buildNotification()
	{
		Profile p = window.getProfile();
		Computer c = config.getComputer();
		return c+" may be experiencing problems with "+p.getName();
	}
	private boolean tooManyFailures()
	{
		if(numConsecutiveFails > TOO_MANY_FAILURES)
		{
			numConsecutiveFails = 0;
			System.err.println("Too Many Failures");
			return true;
		}
		return false;
	}
	private void setName(PWindow window)
	{
		thread.setName("WindowThread("+window.getX()+","+window.getY()+")");
	}
	@Override
	public String toString()
	{
		return thread.toString();
	}
}
