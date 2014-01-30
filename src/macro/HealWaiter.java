package macro;

import combat.Healer;

/**
 * 
 * Checks the healer while waiting
 * 
 * 
 * @author HONG
 *
 */
public class HealWaiter
{
	private double duration;
	private double time;
	
	private Healer healer;
	
	public HealWaiter(double duration, Healer healer)
	{
		this.duration = duration;
		time = System.currentTimeMillis();
	}
	/**
	 * 
	 * If not enough time has passed between the construction of this object and the time at which this is called, it will continue to wait
	 * @throws LogoutException 
	 * 
	 */
	public void waitFully() throws LogoutException
	{
		double timePassed = System.currentTimeMillis() - time;
		if(timePassed < duration)
		{
			Timer waiter = new Timer((int) (duration - timePassed));
			while(waiter.stillWaiting())
			{
				if(healer!=null)
				{
					healer.checkHealth();
				}
				Macro.sleep(100);
			}
		}
	}
}
