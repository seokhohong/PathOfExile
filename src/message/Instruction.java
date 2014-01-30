package message;

import java.io.Serializable;

/**
 * 
 * Commands that can be represented in a Message
 * 
 * @author Seokho
 *
 */
public enum Instruction implements Serializable
{
	//There will be one extra usage parameter for the computer
	HALT(1, "halt (computer name)"),					//HALT : just halts the current program
	HALT_ALL(0, "haltAll"),
	RUN(1, "run (computer name) (account email)"),		//RUN (Profile Email) : Runs the specified profile
	RESUME(1, "pause (computer name OR account name)"),
	WAITER(2, "waiter (account to party name) (account waiting)"), //See Note 1
	PORTAL_OPENED(0, ""),
	TRADE(2, "trade (source account) (destination account)"),
	ECHO(0, ""), 										// Pings a computer 
	IDLE(1, ""),										// *Internally transmitted message, not accessible by console
	READY(2, "");										// *Automated message when Client is run
	
	/*
	 * Note 1: The two parameters that will be sent to each computer:
	 * Waiter : Inviter INVITER		(the capitals indicate a literal string)
	 * Inviter : Waiter WAITER
	 */
	
	
	private int numParams;	//Number of parameters in message, not in console
	
	private String usage; 			public String getUsage() { return usage; }
	
	private Instruction(int numParams, String usage)
	{
		this.numParams = numParams;
	}
	
	public Instruction fromString(String instr)
	{
		for(Instruction i : Instruction.values())
		{
			if(i.toString().equals(instr))
			{
				return i;
			}
		}
		System.out.println(instr+" is not a valid instruction");
		return null;
	}
	
	//Halts the program if there is an error
	public static Instruction parseInstruction(String s)
	{
		String[] split = s.split(" ");
		for(Instruction i : Instruction.values())
		{
			if(i.toString().equals(split[0].toUpperCase()))
			{
				return i;
			}
		}
		System.err.println("\""+s+"\" Does not include a valid Instruction");
		System.exit(1);
		return null;
	}
	//Use for console
	public static boolean hasValidInstruction(String s)
	{
		String[] split = s.split(" ");
		for(Instruction i : Instruction.values())
		{
			if(i.toString().equals(split[0].toUpperCase()))
			{
				return true;
			}
		}
		return false;
	}
}
