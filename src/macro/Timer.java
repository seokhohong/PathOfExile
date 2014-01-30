package macro;

/**
 * 
 * Used to check if a certain amount of time has elapsed
 * 
 * @author HONG
 *
 */
public class Timer 
{
	private int duration;
	private long startTime;
	
	public Timer(int duration)
	{
		startTime = System.currentTimeMillis();
		this.duration = duration;
	}
	/**
	 * 
	 * Resets the timer with a new duration
	 * 
	 * @param newDuration	: Amount of time before the Timer expires. Future calls to reset() also use this value
	 */
	public void reset(int newDuration)
	{
		duration = newDuration;
		startTime = System.currentTimeMillis();
	}
	/**
	 * 
	 * Resets the timer with the previously established duration
	 * 
	 */
	public void reset()
	{
		
	}
	public long elapsedTime()
	{
		return System.currentTimeMillis() - startTime;
	}
	public boolean hasExpired()
	{
		return System.currentTimeMillis() - startTime > duration;
	}
	public boolean stillWaiting()
	{
		return System.currentTimeMillis() - startTime < duration;
	}
}
