package party;

import img.BinaryImage;
import img.ImageLibrary;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;

import java.awt.Point;
import java.util.ArrayList;

import arena.Arena;
import macro.Macro;
import macro.Timer;
import window.PWindow;
import window.ScreenRegion;
import data.Profile;

public class SocialMacro 
{
	private static final Point inputField = new Point(226, 497);
	/*
	public static void friend(PWindow window, String name)
	{
		if(SocialMacro.isOpen(window))
		{
			SocialTab.FRIENDS.click(window);
			window.typeInField(name + "\n", inputField);
		}
	}
	*/
	public static void acceptPartyRequests(PWindow window)
	{
		if(!SocialMacro.isOpen(window))
		{
			SocialMacro.open(window);
		}
		if(SocialMacro.isOpen(window))
		{
			SocialTab.PARTY.click(window);
			acceptRequest(window);
		}
		SocialMacro.close(window);
	}
	public static void invite(PWindow window)
	{
		Profile profile = window.getProfile();
		if(profile.getFriend() == null)
		{
			return;
		}
		do
		{
			SocialMacro.open(window);
		}
		while(!SocialMacro.inviteToParty(window, profile.getFriend()));
			
		SocialMacro.close(window);
		//Not the greatest design
		if(profile.getAvailableArena() == Arena.fromString("The Twilight Strand Solo2"))
		{
			profile.changeArena(Arena.fromString("The Twilight Strand2"));
		}
	}
	//Returns whether invite was sent
	public static boolean inviteToParty(PWindow window, Profile friend)
	{
		if(SocialMacro.isOpen(window))
		{
			SocialTab.PARTY.click(window);
			Macro.sleep(200);
			window.typeInField(friend.getPoeName() + "\n", inputField);
			return true;
		}
		return false;
	}
	/*
	private static void setFriendComputerIdle(Profile friend)
	{
		if(friend.getComputer() != null)
		{
			friend.getComputer().setStatus(ComputerStatus.IDLE);
		}
		friend.setStatus(ProfileStatus.IDLE);
	}
	*/
	/*
	//Returns if a friend is online
	private static final int FRIEND_THRESHOLD = 120;
	public static boolean friendOnline(PWindow window)
	{
		IntBitmap region = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.FIRST_FRIEND_RECT));
		MidpassFilter.maintainRanges(region, MidpassFilterType.SOCIAL_GREEN);
		BinaryImage bin = region.toGreyscale().doubleCutoff(30);
		//System.out.println("Friend Whitecount "+bin.countWhite());
		return bin.countWhite() > FRIEND_THRESHOLD;
	}
	*/
	//private static final int GREEN_THRESHOLD = 120;
	private static void acceptRequest(PWindow window)
	{
		IntBitmap panel = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.SOCIAL_PANEL_RECT));
		Point p = panel.findImage(ImageLibrary.ACCEPT_PARTY.get());
		if(p != null)
		{
			window.leftClick(p);
			window.leftClick(p);
		}
		/*
		IntBitmap region = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.PARTY_INVITE_RECT));
		MidpassFilter.maintainRanges(region, MidpassFilterType.SOCIAL_GREEN);
		BinaryImage bin = region.toGreyscale().doubleCutoff(30);
		//Display.showHang(bin);
		//System.out.println(bin.countWhite());
		return bin.countWhite() > GREEN_THRESHOLD;
		*/
	}

	private static final Point FRIENDS_LABEL = new Point(100, 100);			public Point getFriendsLabel() { return FRIENDS_LABEL; }
	private static final Point CURRENT_PARTY_LABEL = new Point(160, 100);	public Point getCurrentPartyLabel() { return CURRENT_PARTY_LABEL; }
	
	private static final int PANEL_THRESHOLD = 300;
	public static boolean isOpen(PWindow window)
	{
		IntBitmap region = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.STASH_OPEN_RECT)); //same label
		MidpassFilter.maintainRanges(region, MidpassFilterType.STASH_OPEN);
		BinaryImage bin = region.toGreyscale().doubleCutoff(20);
		bin.killLoners(2, true);
		//Display.showHang(bin);
		return bin.countWhite() > PANEL_THRESHOLD; //Doesn't suck and it works
	}
	public static void open(PWindow window)
	{
		Timer socialTimer = new Timer(2000);
		while(!SocialMacro.isOpen(window) && socialTimer.stillWaiting())
		{
			window.type("s");
			Macro.sleep(500);
		}
	}
	public static void close(PWindow window)
	{
		Timer socialTimer = new Timer(2000);
		while(SocialMacro.isOpen(window) && socialTimer.stillWaiting())
		{
			window.type("s");
			Macro.sleep(500);
		}
	}
	public static void unfriendAll(PWindow window)
	{
		SocialMacro.open(window);
		
		Timer maxUnfriending = new Timer(15000);
		while(maxUnfriending.stillWaiting())
		{
			IntBitmap panelImg = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.SOCIAL_PANEL_RECT));
			ArrayList<Point> btns = panelImg.findImages(ImageLibrary.FRIEND_OPTION_BUTTON.get());
			if(btns.isEmpty())
			{
				break;
			}
			window.leftClick(btns.get(0));
			processOption(window);
		}
		SocialMacro.close(window);
		System.out.println();
	}
	private static void processOption(PWindow window)
	{
		IntBitmap panelImg = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.SOCIAL_PANEL_RECT));
		Timer unfriendTimer = new Timer(3000);
		while(unfriendTimer.stillWaiting())
		{
			ArrayList<Point> unfriends = panelImg.findImages(ImageLibrary.UNFRIEND_ONLINE.get());
			unfriends.addAll(panelImg.findImages(ImageLibrary.UNFRIEND_OFFLINE.get(), 20));
			for(Point unfriend : unfriends)
			{
				window.leftClick(unfriend);
				Timer confirmTimer = new Timer(1000);
				while(confirmTimer.stillWaiting())
				{
					Point ok = panelImg.findImage(ImageLibrary.REMOVE_FRIEND_OK.get());
					if(ok != null)
					{
						window.leftClick(ok);
						Macro.sleep(500); //wait for game to process
						return;
					}
					panelImg = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.SOCIAL_PANEL_RECT));
				}
				return;
			}
			panelImg = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.SOCIAL_PANEL_RECT));
		}
	}
}
