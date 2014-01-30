package control;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;

import macro.Macro;
import message.Instruction;
import message.Message;
import message.RelayProtocol;
import message.RelayThread;
import message.Server;
import data.Config;
import data.Profile;

public class MustaphaMond implements RelayProtocol<Message>
{
	private static final boolean AUTOSTART = false;
	
	private static final File infoDir = new File("cmd");						public static final File getInfoDir() { return infoDir; }
	private static final File prioritiesFile = new File("cmd\\priorities");		public static final File getPrioritiesFile() { return prioritiesFile; }
	
	//Host is read from Config
	private static final int PORT = 4444;				public static int getPort() { return PORT; }
	
	private Scheduler scheduler = new Scheduler(this);
	
	private MainFrame window;
	
	private Server<Message> server;
	
	private Config config;				public Config getConfig() { return config; }
										public Network getNetwork() { return config.getNetwork(); }
	
	public static void main(String[] args)
	{
		new MustaphaMond().run();
	}
	private void run()
	{
		config = new Config();
		server = new Server<Message>(this, PORT);
		server.start();
		window = new MainFrame(this);
		while(true)
		{
			window.update();
			scheduler.work();
			Macro.sleep(1000);
		}
	}
	
	void updateConsole(String s)
	{
		window.updateConsole(s);
	}
	//Returns whether the message was sent successfully
	boolean sendMessage(Message message)
	{
		RelayThread<Message> connection = server.getConnectionTo(message.getDestination().getName());
		if(connection != null)
		{
			if(connection.send(message))
			{
				switch(message.getInstruction())
				{
				case RUN : handleSentRun(message); break;
				case WAITER : handleSentWaiter(message); break;
				case HALT : handleSentHalt(message); break;
				case HALT_ALL : handleSentHalt(message); break; //because its as though each computer received its own halt message
				case IDLE : handleSentIdle(message); break;
				case PORTAL_OPENED : System.out.println("Relaying Portal Opened"); break;
				default : break;
				}
				return true;
			}
			else
			{
				return false;
			}
		}
		return false;
	}
	
	@Override
	public Message processInput(Message message) 
	{
		//source is that other computer
		//destination is me
		System.out.println("Received "+message);
		switch(message.getInstruction())
		{
			case READY : handleReceivedReady(message); break;
			case PORTAL_OPENED : handlePortalOpened(message); break;
			default : break;
		}
		return null;
	}
	
	private void handlePortalOpened(Message message)
	{
		Computer source = getNetwork().getEquivalent(message.getSource());
		while(true)
		{
			Profile opener = getNetwork().getProfile(source.getProfileName());
			if(opener != null && opener.getFriend() != null)
			{
				sendMessage(new Message(config.getHost(), opener.getFriend().getComputer(), Instruction.PORTAL_OPENED, new ArrayList<String>()));
				break;
			}
		}
	}
	
	private void handleReceivedReady(Message message)
	{
		Computer source = getNetwork().getEquivalent(message.getSource());
		String ip = message.getParameters().get(0);
		String name = message.getParameters().get(1);
		server.updateConnectionEnd(ip, name);
		if(source.getProfileName() != null)
		{
			getNetwork().getProfile(source.getProfileName()).setStatus(ProfileStatus.IDLE);;
		}
		source.setStatus(AUTOSTART ? ComputerStatus.IDLE : ComputerStatus.HALTED);
		while(window == null)
		{
			Macro.sleep(100);
		}
		window.updateConsole(source.getName() + " is Ready");
		window.update();
	}
	
	private void handleSentWaiter(Message message)
	{
		Computer destination = getNetwork().getEquivalent(message.getDestination());
		Profile otherProfile = config.getNetwork().getProfile(message.getParameters().get(0));
		boolean isToInviter = message.getParameters().get(2).equals("INVITER");
		if(otherProfile == null)
		{
			System.out.println("Inviter/Waiter has no computer?");
		}
		//Delivered to inviter
		Profile dest = getNetwork().getProfile(destination.getProfileName());
		if(isToInviter)
		{
			updateConsole("Inviter "+destination+" received "+otherProfile);
			dest.setFriend(otherProfile);
		}
		else
		{
			updateConsole("Waiter "+destination+" has connected to "+otherProfile);			
			//dest.setFriend(otherProfile);
		}
	}
	
	private void handleSentRun(Message message)
	{
		Profile runningProfile = config.getNetwork().getProfile(message.getParameters().get(0));
		Computer dest = getNetwork().getEquivalent(message.getDestination());
		window.updateConsole(dest+" is now running "+runningProfile);
		dest.setStatus(ComputerStatus.IDLE);	//send notification that the current profile will idle
		if(dest.getProfileName() != null)
		{
			getNetwork().getProfile(dest.getProfileName()).setStatus(ProfileStatus.IDLE);
		}
		dest.setStatus(ComputerStatus.BUSY);
		dest.setProfile(runningProfile.getName());
		runningProfile.setStatus(ProfileStatus.BUSY);
		runningProfile.setComputer(dest);
	}
	
	private void handleSentHalt(Message message)
	{
		Computer destination = getNetwork().getEquivalent(message.getDestination());
		window.updateConsole("Halted "+destination);
		destination.setStatus(ComputerStatus.HALTED);
		if(destination.getProfileName() != null && getNetwork().getProfile(destination.getProfileName()) != null)
		{
			getNetwork().getProfile(destination.getProfileName()).setStatus(ProfileStatus.HALTED);
			getNetwork().getProfile(destination.getProfileName()).setComputer(null);
			destination.setProfile(null);
		}
	}
	
	private void handleSentIdle(Message message)
	{
		Computer destination = getNetwork().getEquivalent(message.getDestination());
		window.updateConsole("Idled "+destination);
		destination.setStatus(ComputerStatus.IDLE);
		if(destination.getProfileName() != null && getNetwork().getProfile(destination.getProfileName()) != null)
		{
			getNetwork().getProfile(destination.getProfileName()).setStatus(ProfileStatus.IDLE);
			getNetwork().getProfile(destination.getProfileName()).setComputer(null);
			destination.setProfile(null);
		}
	}
	@Override
	public void disconnected(Socket socket) 
	{
		System.out.println(server.getNameByIp(socket.getInetAddress().getHostAddress()) +" Disconnected");
	}
}
