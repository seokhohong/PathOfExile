package message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import data.Config;
import macro.Macro;

public class Communications extends Thread 
{
	
	private static final int CHECK_DELAY = 500;
	
	private boolean halt;
	
	private Communicator com;
	
	private ArrayList<MessagePacket> messengers = new ArrayList<MessagePacket>();
	
	public Communications(Communicator com)
	{
		clearAllMessages();
		this.com = com;
		start();
	}
	
	@Override
	public void run()
	{
		while(!halt)
		{
			Macro.macro.sleep(CHECK_DELAY);
			checkMessages();
			checkMessengers();
		}
	}
	
	private void checkMessages()
	{
		Queue<Message> messages = readMessages();
		while(!messages.isEmpty())
		{
			com.processMessage(messages.poll());
		}
	}
	
	private static Queue<Message> readMessages()
	{
		Queue<Message> messages = new LinkedList<Message>();
		for(File f : MessagePacket.messageDir().listFiles())
		{
			if(f.getName().startsWith(Config.getComputer().getName()))
			{
				Message msg = readMessage(f);
				if(msg != null) //successful read?
				{
					messages.offer(msg);
				}
			}
		}
		return messages;
	}
	private static Message readMessage(File f)
	{
		while(true)
		{
			try
			{
				BufferedReader buff = new BufferedReader(new FileReader(f));
				String msg = buff.readLine(); //Messages are one line
				buff.close();
				while(!f.delete()) 
				{
					System.out.println("Failed to Delete");
				}
				return new Message(msg);
			}
			catch(FileNotFoundException e)
			{
				System.out.println("Message no longer exists");
				return null;
			}
			catch(IOException e)
			{
				
			}
		}
	}
	
	
	/**
	 * 
	 * Clears all messages destined for this particular machine
	 * 
	 */
	public static void clearAllMessages()
	{
		for(File f : MessagePacket.messageDir().listFiles())
		{
			if(f.getName().startsWith(Config.getComputer().getName()))
			{
				while(!f.delete()) {}
			}
		}
	}
	
	//Outgoing messengers
	void checkMessengers()
	{
		Iterator<MessagePacket> iter = messengers.iterator();
		while(iter.hasNext())
		{
			MessagePacket m = iter.next();
			switch(m.getStatus())
			{
				case RECEIVED : 
				{
					iter.remove();
					com.messageDelivered(m); 
					break;
				}
				case IGNORED : 
				{
					iter.remove(); 
					com.messageIgnored(m);
					break;
				}
				case UNDECIDED: break;
			}
		}
	}
	
	public void sendMessage(MessagePacket m)
	{
		m.send();
		messengers.add(m);
	}
	
	public void halt()
	{
		halt = true;
	}
	
}
