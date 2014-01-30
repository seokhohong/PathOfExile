package message;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class OurClient implements RelayProtocol 
{
	private Socket socket;
	
	public static void main(String args[])
	{
		new OurClient().go();
	}
	private void go()
	{
		connect();
		RelayThread rt = new RelayThread(this, socket);
		rt.start();
		rt.send(makeMessage());
		
	}
	@Override
	public Message processInput(Message message) 
	{
		System.out.println(message+" "+System.currentTimeMillis());
		//Macro.macro.sleep(1000);
		return makeMessage();
	}
	
	private Message makeMessage()
	{
		ArrayList<String> params = new ArrayList<String>();
		params.add("Jamison-Laptop");
		return Instruction.HALT.createMessage(params);
	}
	
	public void connect()
	{
	    try {
	        socket = new Socket(OurServer.getHost(), OurServer.getPort());
	    } catch (UnknownHostException e) {
	        System.err.println("Don't know about host: "+OurServer.getHost()+".");
	        System.exit(1);
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for the connection to: "+OurServer.getHost()+".");
	        System.exit(1);
	    }
	}
}
