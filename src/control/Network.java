package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import data.Config;
import data.Profile;

/**
 * 
 * Loads and maintains information about the Vectorup of the Path of Exile network
 * 
 * 
 * @author Seokho
 *
 */
public class Network 
{
	private Vector<Computer> computers = new Vector<Computer>();	
	private Vector<Profile> profiles = new Vector<Profile>();
	
	private Config config;
	
	public Network(Config config)
	{
		this.config = config;
		configureNetwork();
	}
	Vector<Computer> getComputers()
	{
		return computers;
	}
	public Vector<Profile> getProfiles()
	{
		return profiles;
	}
	
	public Computer getEquivalent(Computer comp)
	{
		for(Computer c : computers)
		{
			if(c.equals(comp))
			{
				return c;
			}
		}
		return null;
	}
	
	/** Gets a Computer by name */
	public Computer getComputer(String s)
	{
		for(Computer c : computers)
		{
			if(c.getName().equals(s))
			{
				return c;
			}
		}
		return null;
	}
	
	/** Gets a Profile by name */
	public Profile getProfile(String s)
	{
		for(Profile p : profiles)
		{
			if(p.getName().equals(s))
			{
				return p;
			}
		}
		return null;
	}
	
	void refresh()	//if settings have been changed in the files
	{
		configureNetwork();
	}
	private void configureNetwork()
	{
		findAllConfiguredComputers();
		profiles = findAllProfiles(new File(Profile.dir()));
		setPriorities();
	}
	private void findAllConfiguredComputers()
	{
		computers.clear();
		for(File f : new File(config.dir()).listFiles())
		{
			String compName;
			int indexOfExt = f.getName().indexOf(config.ext());
			compName = f.getName().substring(0, indexOfExt);
			computers.add(new Computer(compName));
		}
	}
	private Vector<Profile> findAllProfiles(File dir)
	{
		Vector<Profile> profiles = new Vector<Profile>();
		for(File f : dir.listFiles())
		{
			if(f.isDirectory())
			{
				profiles.addAll(findAllProfiles(f));
			}
			else
			{
				profiles.add(new Profile(f));
			}
		}
		return profiles;
	}
	public void sortProfiles()
	{
		Collections.sort(profiles);
	}
	//Call after loading profiles
	private void setPriorities()
	{
		File priorities = MustaphaMond.getPrioritiesFile();
		if(priorities.exists())
		{
			readPriorities();
		}
		else
		{
			buildPrioritiesFile();
		}
		Collections.sort(profiles);
	}
	private void readPriorities()
	{
		try
		{
			BufferedReader buff = new BufferedReader(new FileReader(MustaphaMond.getPrioritiesFile()));
			String line;
			int priority = 0;
			while((line = buff.readLine()) != null)
			{
				for(Profile p : profiles)
				{
					if(p.getName().equals(line))
					{
						p.setPriority(priority);
						priority++;
					}
				}
			}
			buff.close();
		}
		catch(IOException e)
		{
			
		}
	}
	private void buildPrioritiesFile()
	{
		for(int a = 0; a < profiles.size(); a++)
		{
			profiles.get(a).setPriority(a);
		}
		Profile.savePriorities(this);
	}
}
