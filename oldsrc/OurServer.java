package message;

import java.util.ArrayList;

public class OurServer implements RelayProtocol
{
	private static final String HOST = "Seokho-HP";		public static String getHost() { return HOST; }
	private static final int PORT = 4444;				public static int getPort() { return PORT; }
	
	public static void main(String[] args)
	{
		new OurServer();
	}
	
	public OurServer() 
	{
		new Server(this, PORT).start();
	}

	@Override
	public Message processInput(Message message) 
	{
		System.out.println(message);
		ArrayList<String> params = new ArrayList<String>();
		params.add("Seokho-HP");
		return Instruction.HALT.createMessage(params);
	}
}
