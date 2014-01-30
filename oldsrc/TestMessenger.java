package main;

import java.util.ArrayList;

import control.Computer;
import macro.Macro;
import message.Communications;
import message.Communicator;
import message.Instruction;
import message.Message;
import message.MessagePacket;

public class TestMessenger implements Communicator
{
	private Communications com;
	private boolean ready = true;
	
	private long time = 0;
	
	public static void main(String[] args)
	{
		new TestMessenger().go();
	}
	private void go()
	{
		com = new Communications(this);
		for(int a = 0; a < 100; a++)
		{
			while(!ready)
			{
				Macro.macro.sleep(500);
			}
			ArrayList<String> comp = new ArrayList<String>();
			comp.add("Seokho-HP");
			sendMessage(new MessagePacket(new Computer("Seokho-HP"), Instruction.HALT.createMessage(comp)));
			time = System.currentTimeMillis();
			ready = false;
		}
	}
	@Override
	public void processMessage(Message message) 
	{
		/*
		System.out.println("Received "+message);
		ArrayList<String> email = new ArrayList<String>();
		email.add("Seokho-HP");
		Message msg = Instruction.HALT.createMessage(email);
		MessagePacket send = new MessagePacket(new Computer("Seokho-HP"), msg);
		sendMessage(send);
		*/
	}
	@Override
	public void sendMessage(MessagePacket mp) 
	{
		System.out.println("Sending... ");
		com.sendMessage(mp);	
	}
	@Override
	public void messageDelivered(MessagePacket m) 
	{
		System.out.println(System.currentTimeMillis() - time);
		ready = true;
		System.out.println("Delivered "+m.getMessage());	
	}
	@Override
	public void messageIgnored(MessagePacket m) 
	{
		System.out.println("Ignored "+m.getMessage());
	}
}
