package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import data.Profile;

/**
 * 
 * Logs the amount of time a profile was active for
 * 
 * @author Seokho
 *
 */
public class TimeLog 
{	
	private static final String EXT = ".log";
	
	private ArrayList<TimeInterval> intervals = new ArrayList<TimeInterval>();
	
	private File logFile;
	
	static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24; 
	static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;
	
	//in the last 24-hours
	private long uptime;		public long getUptime() { return uptime; } 
	
	public TimeLog(Profile p)
	{
		logFile = new File(MustaphaMond.getInfoDir()+"\\"+p.getName()+EXT);
		if(logFile.exists())
		{
			readLog();
			calculateUptime();
		}
	}
	
	public void add(long start, long finish)
	{
		intervals.add(new TimeInterval(start, finish));
		//System.out.println(this+" Adding new interval (with finish: "+finish+") gives "+intervals.size()+" intervals.");
	}
	
	public long idleTime()
	{
		if(intervals.isEmpty())
		{
			return DAY_IN_MILLIS;
		}
		//Now - last finish (the intervals should be in chronological order)
		//System.out.println(this+" Last interval is interval.get("+(intervals.size() - 1)+") which finished "+intervals.get(intervals.size() - 1).getFinish());
		return System.currentTimeMillis() - intervals.get(intervals.size() - 1).getFinish();
	}
	
	private void readLog()
	{
		try
		{
			intervals.clear();
			BufferedReader buff = new BufferedReader(new FileReader(logFile));
			String line;
			while( (line = buff.readLine()) != null)
			{
				TimeInterval ti = TimeInterval.fromString(line);
				if(!ti.olderThan(WEEK_IN_MILLIS))
				{
					intervals.add(ti);
				}
			}
			buff.close();
		}
		catch(IOException e)
		{
			
		}
	}
	
	private void calculateUptime()
	{
		uptime = 0L;
		for(TimeInterval ti : intervals)
		{
			uptime += ti.getInterval();
		}
	}
	
	public void save()
	{
		writeLog();
	}
	
	private void writeLog()
	{
		try
		{
			BufferedWriter buff = new BufferedWriter(new FileWriter(logFile));
			for(TimeInterval ti : intervals)
			{
				buff.write(ti.toString());
				buff.newLine();
			}
			buff.close();
		}
		catch(IOException e)
		{
			
		}
	}
}
class TimeInterval
{
	
	private long start;				long getStart() { return start; }
	private long finish; 			long getFinish() { return finish; }
									long getInterval() { return finish - start; }
									
	TimeInterval(long start, long finish)
	{
		this.start = start;
		this.finish = finish;
	}
	
	static TimeInterval fromString(String s)
	{
		String[] split = s.split("\\.");
		return new TimeInterval(Long.parseLong(split[0]), Long.parseLong(split[1]));
	}
	
	@Override
	public String toString()
	{
		return start+"."+finish;
	}
	
	//As long as the finish time is within the range of current time - (time) parameter, the whole interval is counted
	//as being part of the time interval
	public boolean olderThan(long time)
	{
		return System.currentTimeMillis() - finish > time;
	}
}