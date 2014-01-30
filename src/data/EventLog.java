package data;

import java.util.ArrayList;

public class EventLog 
{
	private ArrayList<ProfileEvent> events = new ArrayList<ProfileEvent>();
	public EventLog()
	{
		
	}
	public void addEvent(EventType event)
	{
		events.add(new ProfileEvent(event));
	}
	public int numberOfEvents(EventType evert, long timeInMillis)
	{
		int numEvents = 0;
		for(int a = events.size() - 1; a > 0; a--)
		{
			if(events.get(a).type() == EventType.LOGOUT)
			{
				numEvents ++;
			}
			if(System.currentTimeMillis() - events.get(a).getTime() > timeInMillis)
			{
				break;
			}
		}
		return numEvents;
	}
}
