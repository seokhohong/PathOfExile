package macro;

import img.BinaryImage;
import img.ImageLibrary;
import img.IntBitmap;
import img.MidpassFilter;
import img.MidpassFilterType;

import data.Config;

import java.awt.Point;

import window.HaltThread;
import window.PWindow;
import window.ScreenRegion;
import window.WindowThread;

public class AccountNavigator 
{
	private Config config;
	private PWindow window;

	public AccountNavigator(Config config, PWindow window)
	{
		this.config = config;
		this.window = window;
	}
	/**
	 * 
	 * Presses escape and clicks the logout button
	 * 
	 */
	private static final int LOGOUT_SEARCH_TIME = 4000;
	private static final int LOGIN_SEARCH_TIME = 30000;
	private static final int CHAR_SELECT_WAIT = 8000;
	
	//Assumes that Login button can be seen
	private static final int LOGIN_BUTTON_X = 390;
	private static final int LOGIN_BUTTON_Y = 390;
	
	public void logout()
	{
		System.out.println(Thread.currentThread() + " Attempting to Log Out");
		window.pressEscape();
		Timer logoutSearchTimer = new Timer(LOGOUT_SEARCH_TIME);
		while(logoutSearchTimer.stillWaiting() && !window.logoutVisible())
		{
			Macro.sleep(50);
		}
		while(logoutSearchTimer.stillWaiting() && window.logoutVisible())
		{
			window.leftClickCarefully(new Point(400, 240));
			Macro.sleep(100);
		}
		window.getProfile().logsOut(config);
	}
	//Often, the bot will think itself on the login screen when it is in fact, not, and this method handles
	//some of the more common locations it gets stuck
	private void getUnstuck()
	{
		if(window.destroyItemVisible())
		{
			window.leftClick(ScreenRegion.DESTROY_ITEM_RECT.getCenter());
		}
		if(window.myHealthVisible())
		{
			window.logout();
		}
		Macro.sleep(1000);
	}

	public void login(WindowThread thread) throws LogoutException, HaltThread, WindowException
	{

		Timer loginSearchTimer = new Timer(LOGIN_SEARCH_TIME);
		Timer reallyLongTimer = new Timer(120 * 1000);			//If the game is loading
		Timer takingAWhile = new Timer(2 * 1000); 				//Longer than expected
		boolean clicked = false;
		while(loginSearchTimer.stillWaiting() || (!window.hasSeenLogin() && reallyLongTimer.stillWaiting()))
		{
			if(takingAWhile.hasExpired())
			{
				if(!thread.hasPWindow())
				{
					throw new WindowException("No PWindow");
				}
			}
			if(loginSearchTimer.elapsedTime() > 2000)
			{
				getUnstuck();
			}
			System.out.println(Thread.currentThread()+" Looping Within Login for "+window.getProfile().getEmail());
			thread.checkHalt();
			if(window.loginVisible())
			{
				typeLoginInfo();
				Macro.sleep(50);
				window.clickOnImageMatch(ImageLibrary.LOGIN_BUTTON.get(), ScreenRegion.LOGIN_RECT, PWindow.getLoginErrorThreshold());	//Sees the log in button, and clicks it
				System.out.println(Thread.currentThread() + " Saw Login");
				window.sawLogin(); //PoE must be done loading
				clicked = true;
			}
			else if(clicked && !window.loginVisible()) //If its clicked the button and can't see the Login Button
			{
				//System.out.println(Thread.currentThread() + " Left Login Loop");
				thread.loginSuccess();
				return; //if we've clicked and can't find the login button, then good to go
			}
			if(window.imageExists(ImageLibrary.LOGIN_ERROR.get())) //Some side menu bar open?
			{
				window.pressEscape();
			}
			Macro.sleep(500);
		}
		thread.loginFailure();
		throw new LogoutException("Failed to Log In");
	}

	private boolean typeLoginInfo()
	{
		if(window.loginVisible() || window.existsImageMatch(ImageLibrary.LOGIN_BUTTON.get(), ScreenRegion.LOGIN_RECT, PWindow.getLoginErrorThreshold())) //Will only type if login button is visible
		{
			window.leftClick(LOGIN_BUTTON_X, LOGIN_BUTTON_Y);
			Macro.sleep(100);
			window.eraseField();
			Macro.sleep(100);
			window.typeInField(window.getProfile().getEmail()+"\t"+window.getProfile().getPassword(), new Point(LOGIN_BUTTON_X, LOGIN_BUTTON_Y));
			return true;
		}
		return false;
	}
	//Login screen can fail in many different ways. This method is designed to handle such failures
	public void resolveLoginScreenError(IntBitmap screen, WindowThread thread) throws LogoutException, HaltThread, WindowException
	{
		if(window.charSelectionVisible()) //Onto character selection then
		{
			return; 
		}
		if(window.imageExists(ImageLibrary.LOGIN_ERROR.get()))	//Has a side menu open
		{
			window.pressEscape();
		}
		Point ok;
		if((ok = screen.findImage(ImageLibrary.LOGIN_OK.get())) != null) //some login failure that brings up the OK button
		{
			window.leftClick(ok);
			//System.out.println(Thread.currentThread() + " Typing in login info");
			Timer okTimer = new Timer(4000);
			while(!window.loginVisible()) 	//While the OK button continues to shroud the login button
			{
				//System.out.println(Thread.currentThread()+" Is there an OK button?");
				window.leftClick(ok);
				if(okTimer.hasExpired())	//Taking too long for this to be the correct solution
				{
					throw new LogoutException("Login Screen Error");
				}
				Macro.sleep(1000);
			} 
			//loop for login button
			//System.out.println(Thread.currentThread()+" Logging In After Handling OK error");
			login(thread);
		}
	}
	//Ensures that the character selection menu is up, otherwise goes back to original screen
	private void ensureCharSelectionAvailable(WindowThread thread) throws LogoutException, HaltThread, WindowException
	{
		Timer charSelectTimer = new Timer(CHAR_SELECT_WAIT);
		while(true) //wait until I can select a character
		{
			IntBitmap screen = IntBitmap.getInstance(window.takeScreenshot());
			resolveLoginScreenError(screen, thread);
			//System.out.println(Thread.currentThread()+" Resolved all Login Errors");
			//Is it still on login screen?
			if(charSelectTimer.stillWaiting())
			{
				if(window.loginVisible())
				{
					System.out.println(Thread.currentThread() + " Trying to log in again");
					login(thread);
				}
				else
				{
					System.out.println(Thread.currentThread()+" Ready to select a Character");
					return; //Ready to select a character
				}
			}
			else
			{
				throw new LogoutException("Failed to select a character");
			}
			System.out.println(Thread.currentThread()+" Could not possibly be looping through this much");
		}
	}
	/**
	 * 
	 * Selects a character from the list of characters visible on the screen
	 * 
	 * @param index				: which character on the list (probably 1st)
	 * @throws LogoutException	: if there is some error
	 * @throws WindowException	: if there is some serious error including a non-visible window
	 * @throws HaltThread 
	 */

	public void selectCharacter(int index, WindowThread thread) throws LogoutException, WindowException, HaltThread
	{
		ensureCharSelectionAvailable(thread);
		Timer charSelectWait = new Timer(3000);
		while(!window.charSelectionVisible() && charSelectWait.stillWaiting()) {}
		while(window.charSelectionVisible())
		{
			thread.checkHalt();
			System.out.println(Thread.currentThread()+" Character Selection Loop");
			for(int a = 0; a < 2; a++)	//Select the character at the given index
			{
				window.doubleClick(new Point(600, 90 + 42 * index));
				//System.out.println(Thread.currentThread()+" Selects Character at index "+index);
			}
			if(optionsOpen(window))
			{
				window.pressEscape();
			}
			Macro.sleep(500);
			if(!window.charSelectionVisible()) 	//If the character selection menu has disappeared
			{
				System.out.println(Thread.currentThread() + " No Character Selection Menu");
				return;
			}
			if(charSelectWait.hasExpired())
			{
				throw new LogoutException("Failed to select Character");
			}
		}
	}
	private static final int OPTIONS_THRESHOLD = 300;
	private boolean optionsOpen(PWindow window)
	{
		IntBitmap region = IntBitmap.getInstance(window.takeScreenshot(ScreenRegion.STASH_OPEN_RECT));
		MidpassFilter.maintainRanges(region, MidpassFilterType.STASH_OPEN);
		BinaryImage bin = region.toGreyscale().doubleCutoff(20);
		bin.killLoners(2, true);
		return bin.countWhite() > OPTIONS_THRESHOLD; //Doesn't suck and it works
	}
}
