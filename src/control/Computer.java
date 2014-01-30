package control;

import java.io.Serializable;

public class Computer implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3017749800750581166L;
	private String name;			public String getName() { return name; }
	private ComputerStatus status;	public ComputerStatus getStatus() { return status; } 
									public void setStatus(ComputerStatus status) { this.status = status; }
									
	/* Represents a profile, but cannot actually store the whole profile because it would be difficult to serialize */
	private String prof; 			public String getProfileName() { return prof;}
									public void setProfile(String prof) { this.prof = prof; }
									
	Computer(String name)
	{
		this.name = name;
		status = ComputerStatus.OFF;
	}
	
	//Additional information about the state of the computer
	String getDescription()
	{
		if(status == ComputerStatus.BUSY && prof != null)
		{
			return "("+prof+")";
		}
		return "";
	}
	
	@Override 
	public boolean equals(Object o)
	{
		if(o instanceof Computer)
		{
			Computer other = (Computer) o;
			if(other.name.equals(name))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
