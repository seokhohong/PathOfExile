package macro;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class EmailReader 
{
	private static String getHost(String email)
	{
		if(email.contains("hotmail.com"))
		{
			return "pop3.live.com";
		}
		if(email.contains("gmail.com"))
		{
			return "imap.gmail.com";
		}
		System.err.println("Cannot find host of "+email);
		System.exit(0);
		return "";
	}
	private static final int DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
	private static final int MINUTE_IN_MILLIS = 1000 * 60;
	private static boolean messageIsOld(Message m, int time) throws MessagingException
	{
		return m.getSentDate().getTime() < System.currentTimeMillis() - time;
	}
	private static boolean hasReceivedRecently(Folder folder, int numMillis) throws MessagingException
	{
		for(Message m : folder.getMessages())
		{
			if(m.getSentDate() != null && !messageIsOld(m, numMillis))
			{
				return true;
			}
		}
		return false;
	}
	private static void deleteOldMessages(Folder folder) throws MessagingException, IOException
	{
		for(Message m : folder.getMessages())
		{
			if(m.getSentDate() != null && messageIsOld(m, DAY_IN_MILLIS))
			{
				m.setFlag(Flags.Flag.DELETED, true);
			}
		}
	}
	private static String readMostRecentMessage(Folder folder) throws MessagingException, IOException
	{
		Message mostRecent = null;
		for(Message m : folder.getMessages())
		{
			if(!m.isExpunged())
			{				
				if(mostRecent == null 
						|| (m.getSentDate()!=null && m.getSentDate().after(mostRecent.getSentDate())))
				{
					if(m.getContent() != null && m.getSubject().contains("Unlock Code"))
					{
						mostRecent = m;
					}
				}
			}
		}
		return (String) mostRecent.getContent();
	}
	private static final String codeToken = "after logging in:"; //what we're looking for
	private static final int CODE_LENGTH = 14; //including whitespaces
	private static String parseMessage(String message)
	{
		String code = message.substring(message.indexOf(codeToken) + codeToken.length());
		return code.substring(0, CODE_LENGTH).trim();
	}
	private static final int MAX_WAIT_FOR_EMAIL = 20000;
	private static final int UNUSUAL_NUMBER_CHECKS = 5;
	public static String getUnlockCode(String email, String password)
	{
		String host = getHost(email);

		Properties pop3Props = new Properties();
		pop3Props.setProperty("mail.pop3s.port",  "995");

		Session session = Session.getInstance(pop3Props, null);
		try 
		{
			Store store = session.getStore("pop3s");
			store.connect(host, 995, email, password);
			System.out.println("Connected to "+email);
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			
			Timer emailWait = new Timer(MAX_WAIT_FOR_EMAIL);
			int numChecks = 0;
			//wait for the message
			while(emailWait.stillWaiting() && !hasReceivedRecently(folder, MINUTE_IN_MILLIS))
			{
				numChecks ++;
				if(numChecks == UNUSUAL_NUMBER_CHECKS)
				{
					System.out.println("Taking an unusually long time to receive unlock email...");
				}
				try { Thread.sleep(1000); } catch(InterruptedException e) {}
			}
			if(emailWait.hasExpired())
			{
				System.err.println("No Unlock Email Received");
			}
			deleteOldMessages(folder);
				
			String code = parseMessage(readMostRecentMessage(folder));
			folder.close(true);	//expunges all messages to be deleted
			return code;
		} 
		catch (MessagingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return "Could not find code";
	}
}
