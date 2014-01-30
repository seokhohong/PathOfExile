package macro;

/**
 * 
 * Useful for faking multithreaded environments that require a certain amount of waiting after a given task
 * 
 * @author HONG
 *
 */
public class Waiter 
{
	private double duration;
	private double time;
	public Waiter(double duration)
	{
		this.duration = duration;
		time = System.currentTimeMillis();
	}
	/**
	 * 
	 * If not enough time has passed between the construction of this object and the time at which this is called, it will continue to wait
	 * 
	 */
	public void waitFully()
	{
		double timePassed = System.currentTimeMillis() - time;
		if(timePassed < duration)
		{
			Macro.sleep((int) (duration - timePassed));
		}
	}
}
