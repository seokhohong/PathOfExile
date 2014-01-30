package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import macro.Macro;

//Static class
public class Data 
{
	private Data() { throw new AssertionError("Do Not Instantiate Me"); }
	//Reads a file and puts it into the Collection
	public static void readLines(File f, Collection<String> strings)
	{
		while(true)
		{
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String line;
				while((line = reader.readLine())!=null)
				{
					//ignore comments and newlines
					if(!line.startsWith("#") && !line.isEmpty())
					{
						strings.add(line);
					}
				}
				reader.close();
				break;
			}
			catch(IOException e)
			{
				System.err.println("Error reading file "+f.getName()+".... attempting Again");
				Macro.sleep(1000);
			}
		}
	}
}
