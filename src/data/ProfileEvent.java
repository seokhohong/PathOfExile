package data;

public class ProfileEvent 
{
	private EventType eventType;									public EventType type() { return eventType; }
	private final long timeStamp = System.currentTimeMillis();		public long getTime() { return timeStamp; }
	public ProfileEvent(EventType type)
	{
		eventType = type;
	}
}
