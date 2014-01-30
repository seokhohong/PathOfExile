package process;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import data.Config;

public class AHKBridge 
{

	/**
	 * 
	 * Runs a script
	 * 
	 * @param filename
	 * @return		: Returns an ArrayList<String> representing stdout
	 * @throws IOException 
	 * @throws ExecuteException 
	 */
	private static ArrayList<String> runProcess(String filename) throws ExecuteException, IOException
	{
		CommandLine cmdLine = new CommandLine(filename);

		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

		ExecuteWatchdog watchdog = new ExecuteWatchdog(10 * 1000); //kills runaway/stalled processes
		Executor executor = new DefaultExecutor();
		
		LogStream stdout = new LogStream();
		PumpStreamHandler psh = new PumpStreamHandler(stdout);
		
		executor.setExitValue(1);
		executor.setStreamHandler(psh);
		executor.setWatchdog(watchdog);
		executor.execute(cmdLine, resultHandler);

		// some time later the result handler callback was invoked so we
		// can safely request the exit value
		try 
		{
			resultHandler.waitFor();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		return stdout.getLines();
	}
	public static ArrayList<String> runScript(String filename)
	{
		try {
			return runProcess(filename);
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(1);
		return null;
	}
	/**
	 * 
	 * Press ALT-A to exit the program
	 * 
	 * Takes an ArrayList<WindowThread> to know what threads to terminate before program exits
	 * 
	 */
	public static void runExitHook(Quittable program, Config config)
	{
		new ExitHook(program, config).start();
    	System.out.println("(Press Alt + A to exit the program)");
	}
}
