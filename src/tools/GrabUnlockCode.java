package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import macro.Email;

public class GrabUnlockCode 
{
	private boolean quit = false;
	public static void main(String[] args)
	{
		new GrabUnlockCode().go();
	}
	private void go()
	{
		while(!quit)
		{
			String email = null;
			String password = null;
			System.out.println("Enter an email");
			try
			{
			    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			    String s = bufferRead.readLine();
			    if(!s.equals(null))
			    {
			    	email = s;
			    }
			    System.out.println("Enter the password");
			    s = bufferRead.readLine();
			    if(!s.equals(null))
			    {
			    	if(s.equals("dvorak password"))
			    	{
			    		password = "'a;,oq123";
			    	}
			    	else
			    	{
			    		password = s;
			    	}
			    }
			    System.out.println(Email.getUnlockCode(email, password));
			    System.out.println("Would you like to get another code? (yes/no)");
			    s = bufferRead.readLine();
			    if(!s.equals(null))
			    {
			    	if(s.equals("no"))
			    	{
			    		quit = true;
			    	}
			    }
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
