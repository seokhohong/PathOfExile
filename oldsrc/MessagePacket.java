package message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import macro.Timer;
import control.Computer;
import data.Config;

/**
 * 
 * Defines the protocols for communicating among computers
 * 
 * Messengers must have the send method invoked
 * 
 * @author Seokho
 *
 */
public class MessagePacket extends Thread
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6299463439153025438L;
	
	private static File messageDir;			public static File messageDir() { return messageDir; }
	private static int messageNumber; //to make sure there are never any duplicate message filenames
	
	static
	{
		verifyMessagesFolder();
	}
	private static void verifyMessagesFolder()
	{
		messageDir = new File(Config.getGoogleDrive());
		if(!messageDir.exists())
		{
			messageDir.mkdir();
		}
		
		messageNumber = messageDir.listFiles().length;
	}
	
	private volatile boolean received = false;
	private volatile boolean processed = false;
	
	private Computer comp;			public Computer getComputer() { return comp; }
	private Message message;		public Message getMessage() { return message; }
	
	/**
	 * 
	 * Sends a message to a particular computer.
	 * The thread will wait until it receives a confirmation that the destination computer received the message 
	 * 
	 */
	public MessagePacket(Computer comp, Message message)
	{
		this.comp = comp;
		this.message = message;
		setName(comp.toString()+" "+message.toString());
	}
	public void send()
	{
		start();
	}
	private static final int MAX_MESSAGE_WAIT = 60000;
	private static final int BIT_LONG = 15000; //may have stalled
	@Override
	public void run() 
	{
		int numRetries = 0;
		Timer msgWait = new Timer(MAX_MESSAGE_WAIT);
		File msgFile = createMessageFile(comp, message);
		while(msgWait.stillWaiting())
		{
			if(msgWait.elapsedTime() > BIT_LONG * (numRetries + 1))
			{
				numRetries ++;
				System.out.println("Trying Again");
				msgFile.delete();
				msgFile = createMessageFile(comp, message);
			}
			if(!msgFile.exists())
			{
				received = true;
				return;
			}
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException e) {}
		}
		processed = true;
		msgFile.delete(); //retract message if it was not received
		System.out.println(comp+" failed to receive message: "+message);
	}
	/**
	 * 
	 * Returns the status of the messenger.
	 * Loop around a wait while the status is UNDECIDED and then proceed after it has been RECEIVED or IGNORED
	 * 
	 */
	public MessageStatus getStatus()
	{
		if(received)
		{
			return MessageStatus.RECEIVED;
		}
		if(processed)
		{
			return MessageStatus.IGNORED;
		}
		return MessageStatus.UNDECIDED;
	}
	
	//Returns the filename
	private static File createMessageFile(Computer comp, Message message)
	{
		while(true)
		{
			try
			{
				//File name is formatted: (Destination computer).(Source computer)(message number)
				File msgFile = new File(MessagePacket.messageDir()+"\\"+ comp.toString() +"."+ Config.getComputer() + messageNumber);
				BufferedWriter buff = new BufferedWriter(new FileWriter(msgFile));
				buff.write(message.toString());
				buff.close();
				if(!msgFile.exists())
				{
					System.out.println("Failed to write message");
				}
				messageNumber ++ ; //another message has been written, so increment the counter to avoid overlaps
				return msgFile;
			}
			catch(IOException e)
			{
				System.out.println("Failed to write message, retrying");
			}
		}
	}

	
	@Override
	public String toString()
	{
		return "Message :"+comp.getName()+" "+message.toString()+" "+getStatus().toString();
	}
}
