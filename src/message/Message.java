package message;

import java.io.Serializable;
import java.util.ArrayList;

import control.Computer;
import data.Config;

/**
 * 
 * Message format:
 * (Source) (Destination) (Instruction) (Parameters)
 * 
 * @author Seokho
 *
 */
public class Message implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5534494043560715088L;
	private Instruction instr;												public Instruction getInstruction() { return instr; }
	private Computer source;												public Computer getSource() { return source; }
	private Computer destination;											public Computer getDestination() { return destination; }
	private ArrayList<String> params = new ArrayList<String>();				public ArrayList<String> getParameters() { return params; }
	
	//Separates all components on a Message by this delimiter
	private static final String delim = " ";		public static String delim() { return delim; }
	
	public Message(Computer source, Computer destination, Instruction instr, ArrayList<String> params)
	{
		this.source = source;
		this.destination = destination;
		this.instr = instr;
		this.params = params;
	}
	
	public Message(Config config, String wholeMessage)
	{
		parseMessage(config, wholeMessage);
	}
	
	private void parseMessage(Config config, String msg)
	{
		String[] split = msg.split(" ");
		source = config.getNetwork().getComputer(split[0]);
		destination = config.getNetwork().getComputer(split[1]);
		instr = Instruction.parseInstruction(split[2]);
		for(int a = 3; a < split.length; a++)
		{
			params.add(split[a]);
		}
	}
	
	@Override
	public String toString()
	{
		return "Source "+source+" Destination "+destination+" : "+instr+" "+params;
	}
	
	/*
	Message(Instruction instr)
	{
		this.instr = instr;
	}
	Message(Instruction instr, ArrayList<String> params)
	{
		this.instr = instr;
		this.params = params;
	}
	public Message(String wholeMessage)
	{
		parseMessage(wholeMessage);
	}
	private void parseParameters(String[] split)
	{
		for(int a = 1 ; a < split.length; a++)
		{
			params.add(split[a]);
		}
	}
	private void parseInstruction(String[] split)
	{
		for(Instruction i : Instruction.values())
		{
			if(i.toString().equals(split[0]))
			{
				instr = i;
				return;
			}
		}
		System.err.println(instr+" is an invalid Instruction");
	}
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(instr.toString()+" ");
		for(String param : params)
		{
			builder.append(param+delim);
		}
		return builder.toString();
	}
	*/
}
