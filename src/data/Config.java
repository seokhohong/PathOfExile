package data;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import macro.Email;
import macro.Macro;
import control.Computer;
import control.Network;

/**
 * 
 * Loads and stores the configurations of a given launch on a particular machine.
 * 
 * There were other designs for this class but currently its a collection of methods to guarantee only one configuration
 * setting in existence. Singleton design would also work.
 * 
 * @author Seokho
 *
 */
public class Config 
{
	private final String CONFIG_DIR = "config/";		public String dir() { return CONFIG_DIR; }
	private final String CONFIG_EXT = ".conf";			public String ext() { return CONFIG_EXT; }
	
	private final String NOTIFIER_EMAIL = "violetdream111@gmail.com";
	private final String NOTIFIER_PWORD = "\'a;,oq123";
	
	public Config()
	{
		System.out.println("Running on: "+idComputer()); //Identify computer this is operating on
		parseConfig();
	}
	
	private Network network;		public Network getNetwork() { return network; }
	
	private Computer computer;		public Computer getComputer() { return computer; }
	
	private Macro macro;			public Macro getMacro() { return macro; }
	
	//Data fields read to from file
	//All because there can only exist one profile per instance of program
	
	//Number of profiles to run at once
	private int numProfiles;		public int getNumProfiles() { return numProfiles; }
	
	//Location of executable
	private String exeLocation;	public String getExeLocation() { return exeLocation; }
	
	//Number of milliseconds before and after each Native click
	private int clickDelay;		public int getClickDelay() { return clickDelay; }
	
	//Secondary click delay for careful clicks
	private int carefulClickDelay;		public int getCarefulClickDelay() { return carefulClickDelay; }
	
	//Whether Eclipse should be kept open while program is running. Keep true only for logging purposes
	private boolean keepEclipseOpen;		public boolean keepEclipseOpen() { return keepEclipseOpen; } 
	
	//Whether the program will logout of the game upon halting
	private boolean exitOnHalt;			public boolean exitOnHalt() { return exitOnHalt; }
	
	//Who I'm connecting to, if anyone. If nobody, put NONE in text file
	private Computer host;				public Computer getHost() { return host; }
	
	private boolean moveWindow;		public boolean moveWindow() { return moveWindow; }
	
	//Where to send an alert message
	private String alertEmail;
	
	private boolean is64Bit;			public boolean is64bit() { return is64Bit; }
										/** Returns '64' or '32' depending on the architecture of the JVM*/
										public String getBitArchitecture() { return is64Bit? "64" : "32";}
	
	//Gets the identity of the computer running and parses the config file that corresponds to it
	//Somewhat convoluted but decomposed initializer for the class
	private String idComputer()
	{
		String computerName = "Could not find LocalHost";
		try {
			computerName = InetAddress.getLocalHost().getHostName();
			network = new Network(this);
			computer = network.getComputer(computerName);
			loadLocalProfiles();
			findJVMArchitecture(); //32 or 64
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		macro = new Macro(this);
		verifySystemFiles(computerName);
		if(!configExists(computerName))
		{
			System.exit(0);
		}
		parseConfig();
		return computerName;
	}
	
	private void findJVMArchitecture()
	{
		is64Bit = (System.getProperty("sun.arch.data.model").indexOf("64") != -1);
	}
	
	private ArrayList<Profile> localProfiles = new ArrayList<Profile>();			public ArrayList<Profile> getLocalProfiles() { return localProfiles; }
	
	private void loadLocalProfiles()
	{
		for(File f : new File(Profile.PROFILE_DIR + computer.getName() +"\\").listFiles())
		{
			localProfiles.add(new Profile(f));
		}
	}
	
	//Reads the configuration file
	private void parseConfig()
	{
		try
		{
			File configFile = new File(CONFIG_DIR+computer.getName()+CONFIG_EXT);
			Queue<String> lines = new LinkedList<String>();
			Data.readLines(configFile, lines);
			readNumProfiles(lines);
			readPoEDir(lines);
			readClickDelay(lines);
			readCarefulClickDelay(lines);
			readEclipsePref(lines);
			readExitOnHalt(lines);
			readHostName(lines);
			readMoveWindow(lines);
			readAlertEmail(lines);
		}
		catch(Exception e)
		{
			System.err.println("Configuration File not formatted correctly!");
			e.printStackTrace();
		}
	}
	private void readNumProfiles(Queue<String> lines)
	{
		//#
		numProfiles = Integer.parseInt(lines.poll());
	}
	private void readPoEDir(Queue<String> lines)
	{
		//C:\\...
		exeLocation = lines.poll();
	}
	private void readClickDelay(Queue<String> lines)
	{
		//#
		clickDelay = Integer.parseInt(lines.poll());
	}
	private void readCarefulClickDelay(Queue<String> lines)
	{
		//#
		carefulClickDelay = Integer.parseInt(lines.poll());
	}
	private void readEclipsePref(Queue<String> lines)
	{
		//(true, false)
		keepEclipseOpen = Boolean.parseBoolean(lines.poll());
	}
	private void readExitOnHalt(Queue<String> lines)
	{
		//(true, false)
		exitOnHalt = Boolean.parseBoolean(lines.poll());
	}
	private void readHostName(Queue<String> lines)
	{
		host = network.getComputer(lines.poll());
	}
	private void readMoveWindow(Queue<String> lines)
	{
		moveWindow = Boolean.parseBoolean(lines.poll());
	}
	private void readAlertEmail(Queue<String> lines)
	{
		alertEmail = lines.poll();
	}
	
	/** Sends out an email to notify of some event*/
	private static final long MIN_GAP = 5 * 60 * 1000;
	private long lastTime = -1L;
	public void emailNotification(String message)
	{
		if(lastTime == -1 || (System.currentTimeMillis() - lastTime) > MIN_GAP)
		{
			Email.send(NOTIFIER_EMAIL, NOTIFIER_PWORD, alertEmail, message+"\n"+new Date());
			lastTime = System.currentTimeMillis();
		}
	}
	
	
	//Makes sure all accompanying files are in existence
	private void verifySystemFiles(String computerName)
	{
		verifyProfileDir(computerName);
	}
	//Verifies the profile directory exists
	private void verifyProfileDir(String computerName)
	{
		File profileDir = new File(Profile.PROFILE_DIR);
		for(File dirs : profileDir.listFiles())
		{
			if(dirs.getName().equals(computerName))
			{
				return;
			}
		}
		new File(Profile.PROFILE_DIR+computerName).mkdir();
	}
	//Verifies the configuration file exists
	private boolean configExists(String computerName)
	{
		for(File conf : new File(CONFIG_DIR).listFiles())
		{
			if(conf.getName().equals(computerName+CONFIG_EXT))
			{
				return true;
			}
		}
		System.out.println("Detected new computer...");
		makeNewConfigFile(computerName);
		System.out.println("Go update configurations!");
		return false;
	}
	//Makes a new configuration file if it doesn't exist
	private void makeNewConfigFile(String computerName)
	{
		try {
			File newFile = new File(CONFIG_DIR + computerName + CONFIG_EXT);
			newFile.createNewFile();
			System.out.println("Making new configuration files... in "+CONFIG_DIR);
		} catch (IOException e) {
			System.out.print("Failed");
			e.printStackTrace();
		}
	}
}
