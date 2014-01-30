package main;

import message.Message;

public class TestRelay implements Communicator
{
	private Communications com;
	public static void main(String[] args)
	{
		new TestRelay().go();
	}
	private void go()
	{
		com = new Communications(this);
	}
	@Override
	public void processMessage(Message message) 
	{
		System.out.println("Received");
	}
	@Override
	public void sendMessage(MessagePacket mp) 
	{
		System.out.println("Sending ...");
		com.sendMessage(mp);	
	}
	@Override
	public void messageDelivered(MessagePacket m) 
	{
		System.out.println("Delivered "+m.getMessage());	
	}
	@Override
	public void messageIgnored(MessagePacket m) 
	{
		System.out.println("Ignored "+m.getMessage());
	}
}
