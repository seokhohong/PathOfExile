package process;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import macro.Macro;
import data.Config;
import data.Data;

public class ExitHook extends Thread
{
	
	private String quitFilename;
	
	private Process p;
	private Quittable program;
	private ArrayList<StreamGobbler> gobblers = new ArrayList<StreamGobbler>();
	private File quitFile;
	
	private Config config;
	
	public ExitHook(Quittable program, Config config)
	{
		this.config = config;
		quitFilename = "hook\\Quit"+config.getComputer()+".txt";
		quitFile = new File(quitFilename);
		clearQuitFile();
		launchProcess();
		this.program = program;
	}
	private void launchProcess()
	{
    	try {
			p = Runtime.getRuntime().exec("hook\\CheckExit"+config.getComputer()+".exe", null, new File("hook\\"));
			StreamGobbler stdout = new StreamGobbler(p.getInputStream());
			StreamGobbler stderr = new StreamGobbler(p.getErrorStream());
			gobblers.add(stdout);
			gobblers.add(stderr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    @Override
	public void run()
    {
    	while(true)
        {
    		Queue<String> quitMessage = new LinkedList<String>();
    		Data.readLines(quitFile, quitMessage);
        	if(!quitMessage.isEmpty() && quitMessage.contains("Quit"))
        	{
        		System.out.println("Detected Quit");
        		killThreads();
        		program.exitProgram();
        		return; //kills this thread too
        	}
        	Macro.sleep(100);
        }
    }
    
    //Wipes the contents of the QuitFile
    private void clearQuitFile()
    {
    	try
    	{
    		PrintWriter writer = new PrintWriter(quitFile);
    		writer.print("");
    		writer.close();
    	}
    	catch(IOException e) //Error clearing the quitFile
    	{ 
    		e.printStackTrace(); 
    		System.exit(1); 
    	}
    }
    private void killThreads()
    {
    	for(StreamGobbler sg : gobblers)
    	{
    		sg.stop();
    	}
    }
}