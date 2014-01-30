package main;

import window.PWindow;
import window.WindowManager;
import data.Config;

public class TestPanel 
{
	public static void main(String args[])
	{
		new TestPanel().go();
	}
	private void go()
	{
		Config config = new Config();
		PWindow window = new WindowManager(config).getWindows().get(0);
		window.select();
		SocialPanel.open(window);
		Friender.acceptPartyRequests(window);
		/*
		SocialPanel.open(window);
		Friender.friend(window, "GreenHeaven");
		Friender.inviteToParty(window, config.getNetwork().getProfile("GreenHeaven"));
		SocialPanel.close(window);
		 */
		//Unfriender.unfriendAll(window);
	}
}
