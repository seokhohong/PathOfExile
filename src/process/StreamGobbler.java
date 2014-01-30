package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class StreamGobbler implements Runnable
{
    private InputStream is;
    
    private Thread thread;
    
    private ArrayList<String> output = new ArrayList<String>();		public ArrayList<String> getOutput() { return output; }
    
    StreamGobbler(InputStream is)
    {
        this.is = is;
		thread = new Thread(this, "StreamGobbler");
		thread.start();
    }
	public void stop()
	{
		System.out.println("Halting "+thread);
		thread = null;
	}
    @Override
	public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while (thread!=null && (line = br.readLine()) != null)
            {
            	//System.out.println("Gobbled "+line);
            	output.add(line);
            } 
        }
        catch (IOException e)
        {
        	e.printStackTrace();  
        }
    }
}