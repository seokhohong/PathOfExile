package control;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import macro.Macro;
import message.Instruction;
import message.Message;
import message.RelayProtocol;
import message.RelayThread;
import process.AHKBridge;
import process.Quittable;
import data.Config;
import data.Profile;

public class Client implements RelayProtocol<Message>, Quittable
{
	private BotThread workingThread = null;
	
	private RelayThread<Message> relay;
	
	private boolean registered = false;
	
	private Config config;					public Config getConfig() { return config; }
	
	public static void main(String[] args)
	{
		new Client().go();
	}
	private void go()
	{
		config = new Config();
		AHKBridge.runExitHook(this, config);
		//Connect to MustaphaMond
		relay = new RelayThread<Message>(this, getConnection());
		relay.start();
		sendReady();
		
	}
	private void sendReady()
	{
		ArrayList<String> computerInfo = new ArrayList<String>();
		try 
		{
			computerInfo.add(InetAddress.getLocalHost().getHostAddress());
			computerInfo.add(config.getComputer().getName());
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		relay.send(new Message(config.getComputer(), config.getHost(), Instruction.READY, computerInfo));
		while(!registered) //wait to receive the message
		{
			Macro.sleep(1000);
		}
	}
	@Override
	public Message processInput(Message msg) 
	{
		System.out.println("Received Message: "+msg);
		switch(msg.getInstruction())
		{
		case IDLE :
		case HALT : haltWork(); break;
		case RUN : run(msg); break;
		default : break;
		}
		return null;
	}
	
	private void run(Message msg)
	{
		haltWork(); //clear out anything
		for(Profile p : config.getNetwork().getProfiles())
		{
			if(p.getName().equals(msg.getParameters().get(0)))
			{
				workingThread = new BotThread(config, p, relay);
				workingThread.start();
				return;
			}
		}
	}
	
	@Override
	public void exitProgram() 
	{
		haltWork();
	}
	private void haltWork()
	{
		if(workingThread!=null)
		{
			workingThread.exitProgram(true);
			workingThread = null;
		}
	}
	public Socket getConnection()
	{
	    try {
	        return new Socket(config.getHost().toString(), MustaphaMond.getPort());
	    } catch (UnknownHostException e) {
	        System.err.println("Don't know about host: "+config.getHost()+".");
	        System.exit(1);
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for the connection to: "+config.getHost()+".");
	        System.exit(1);
	    }
	    System.err.println("Unknown Error");
	    return null;
	}

	@Override
	public void disconnected(Socket socket) 
	{
		System.out.println("Server Disconnected");
	}
}
