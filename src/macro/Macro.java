package macro;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import window.PWindow;
import data.Config;

public class Macro 
{
	
    private static Robot robot;
    
    static
    {
    	try
    	{
    		robot = new Robot();
    	}
    	catch(AWTException e) {}
    }
	
	private int preclickDelay;
	private int postclickDelay;
	private int carefulClickDelay;
	
	private NativeMacro nativeMacro;
	
	public Macro(Config config) 
	{
		preclickDelay = config.getClickDelay();
		postclickDelay = config.getClickDelay();
		carefulClickDelay = config.getClickDelay();
		nativeMacro = new NativeMacro(config);
	}

    //private static final int SLOW_FACTOR = 10;
    public synchronized void click(int x, int y)
    {
    	/*
    	logThreadAction("Clicks Left "+x+" "+y);
    	for(StackTraceElement e : Thread.currentThread().getStackTrace())
    	{
    		System.out.println(e);
    	}
    	*/
		mouseMove(x, y);
		syncSleep(preclickDelay);
		nativeMacro.leftClick(x, y);		//Native code makes it so much faster
		syncSleep(postclickDelay);
    }
    
    //Designed for selecting waypoint
    public synchronized void clickCarefully(int x, int y)
    {
    	//logThreadAction("Clicks Left");
		mouseMove(x, y);
		syncSleep(carefulClickDelay / 4);
		mouseMove(x + 2, y);
		syncSleep(carefulClickDelay / 4);
		mouseMove(x + 4, y);
		syncSleep(carefulClickDelay / 4);
		mouseMove(x + 2, y);
		syncSleep(carefulClickDelay / 4);
		nativeMacro.leftClick(x, y);
		mouseMove(x + 2, y);
		syncSleep(carefulClickDelay / 4);
		mouseMove(x + 4, y);
		syncSleep(carefulClickDelay / 4);
		mouseMove(x + 2, y);
		syncSleep(carefulClickDelay / 4);
    }
    //Not clean, could have WinAPI send a Double Click
    public synchronized void doubleClick(int x, int y)
    {
		click(x, y);
		syncSleep(100);
		click(x, y);	
    }
    
    public synchronized void eraseField()
    {
    	robot.keyPress(KeyEvent.VK_CONTROL);
    	robot.keyPress(KeyEvent.VK_A);
    	robot.keyRelease(KeyEvent.VK_A);
    	robot.keyRelease(KeyEvent.VK_CONTROL);
    	robot.keyPress(KeyEvent.VK_DELETE);
    	robot.keyRelease(KeyEvent.VK_DELETE);
    }
    public synchronized void drag(Point orig, Point dest)
    {
    	mouseMove(orig.x, orig.y);
    	robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    	robot.mouseMove(dest.x, dest.y);
    	robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    public synchronized void rightClick(int x, int y)
    {
    	//logThreadAction("Clicks Right");
		mouseMove(x, y);
		syncSleep(preclickDelay);
		nativeMacro.rightClick(x, y);	
		syncSleep(postclickDelay);
    }
    public synchronized void middleClick(int x, int y)
    {
    	//logThreadAction("Clicks Middle");
		mouseMove(x, y);
		syncSleep(preclickDelay);
		nativeMacro.middleClick(x, y);	
		syncSleep(postclickDelay);
    }
    public synchronized void scrollDown()
    {
    	robot.mouseWheel(100); //Native method does exist
    }
	public synchronized void selectWindow(PWindow window)
	{
		for(int a = 0; a < 2; a++)
		{
			//mouseMove(window.getX() + PWindow.getWidth() / 2, window.getY() + PWindow.getHeight() - 10);
			click(window.getX() + PWindow.getWidth() / 2, window.getY() + PWindow.getHeight() - 10); //select the window
		}
	}
    //Immobilizes the macro entirely, not just a particular thread
	private synchronized void syncSleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch(InterruptedException e) {}
	}
    /**
     * 
     * Stops the entire Macro class from functioning.
     * 
     * @param millis
     */
	public synchronized void blockMacro(int millis)
	{
		System.err.println("Macro Blocked");
		try
		{
			Thread.sleep(millis);
		} catch(InterruptedException e) {}
	}
	public static void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch(InterruptedException e) {}
	}
    public synchronized void type(PWindow window, CharSequence characters) 
    {
    	//Window should already be selected
    	//selectWindow(window);
    	//System.out.println(Thread.currentThread()+" types "+characters);
    	for(int a = 0; a < characters.length(); a++)
    	{
    		nativeMacro.typeChar(characters.charAt(a), window.getX(), window.getY());
    	}
    	syncSleep(postclickDelay);
    }
    public synchronized void typeNormally(PWindow window, CharSequence characters)
    {
    	int length = characters.length();
    	for (int i = 0; i < length; i++) {
    		char character = characters.charAt(i);
    		type(character);
    	}
    }
    public synchronized void typeInField(PWindow window, CharSequence characters, Point fieldLocation) 
    {
    	window.leftClick(fieldLocation);
    	syncSleep(200);
    	window.leftClick(fieldLocation);
    	typeNormally(window, characters);
    }
    
    public synchronized void type(char character) {
    	switch (character) {
    	case 'a': doType(KeyEvent.VK_A); break;
    	case 'b': doType(KeyEvent.VK_B); break;
    	case 'c': doType(KeyEvent.VK_C); break;
    	case 'd': doType(KeyEvent.VK_D); break;
    	case 'e': doType(KeyEvent.VK_E); break;
    	case 'f': doType(KeyEvent.VK_F); break;
    	case 'g': doType(KeyEvent.VK_G); break;
    	case 'h': doType(KeyEvent.VK_H); break;
    	case 'i': doType(KeyEvent.VK_I); break;
    	case 'j': doType(KeyEvent.VK_J); break;
    	case 'k': doType(KeyEvent.VK_K); break;
    	case 'l': doType(KeyEvent.VK_L); break;
    	case 'm': doType(KeyEvent.VK_M); break;
    	case 'n': doType(KeyEvent.VK_N); break;
    	case 'o': doType(KeyEvent.VK_O); break;
    	case 'p': doType(KeyEvent.VK_P); break;
    	case 'q': doType(KeyEvent.VK_Q); break;
    	case 'r': doType(KeyEvent.VK_R); break;
    	case 's': doType(KeyEvent.VK_S); break;
    	case 't': doType(KeyEvent.VK_T); break;
    	case 'u': doType(KeyEvent.VK_U); break;
    	case 'v': doType(KeyEvent.VK_V); break;
    	case 'w': doType(KeyEvent.VK_W); break;
    	case 'x': doType(KeyEvent.VK_X); break;
    	case 'y': doType(KeyEvent.VK_Y); break;
    	case 'z': doType(KeyEvent.VK_Z); break;
    	case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
    	case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
    	case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
    	case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
    	case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
    	case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
    	case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
    	case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
    	case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
    	case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
    	case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
    	case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
    	case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
    	case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
    	case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
    	case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
    	case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
    	case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
    	case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
    	case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
    	case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
    	case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
    	case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
    	case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
    	case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
    	case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
    	case '`': doType(KeyEvent.VK_BACK_QUOTE); break;
    	case '0': doType(KeyEvent.VK_0); break;
    	case '1': doType(KeyEvent.VK_1); break;
    	case '2': doType(KeyEvent.VK_2); break;
    	case '3': doType(KeyEvent.VK_3); break;
    	case '4': doType(KeyEvent.VK_4); break;
    	case '5': doType(KeyEvent.VK_5); break;
    	case '6': doType(KeyEvent.VK_6); break;
    	case '7': doType(KeyEvent.VK_7); break;
    	case '8': doType(KeyEvent.VK_8); break;
    	case '9': doType(KeyEvent.VK_9); break;
    	case '-': doType(KeyEvent.VK_MINUS); break;
    	case '=': doType(KeyEvent.VK_EQUALS); break;
    	case '~': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
    	case '!': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_1); break;
    	case '@': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_2); break; //java is dumb
    	case '#': doType(KeyEvent.VK_NUMBER_SIGN); break;
    	case '$': doType(KeyEvent.VK_DOLLAR); break;
    	case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
    	case '^': doType(KeyEvent.VK_CIRCUMFLEX); break;
    	case '&': doType(KeyEvent.VK_AMPERSAND); break;
    	case '*': doType(KeyEvent.VK_ASTERISK); break;
    	case '(': doType(KeyEvent.VK_LEFT_PARENTHESIS); break;
    	case ')': doType(KeyEvent.VK_RIGHT_PARENTHESIS); break;
    	case '_': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS); break;
    	case '+': doType(KeyEvent.VK_PLUS); break;
    	case '\t': doType(KeyEvent.VK_TAB); break;
    	case '\n': doType(KeyEvent.VK_ENTER); break;
    	case '[': doType(KeyEvent.VK_OPEN_BRACKET); break;
    	case ']': doType(KeyEvent.VK_CLOSE_BRACKET); break;
    	case '\\': doType(KeyEvent.VK_BACK_SLASH); break;
    	case '{': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
    	case '}': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
    	case '|': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
    	case ';': doType(KeyEvent.VK_SEMICOLON); break;
    	case ':': doType(KeyEvent.VK_COLON); break;
    	case '\'': doType(KeyEvent.VK_QUOTE); break;
    	case '"': doType(KeyEvent.VK_QUOTEDBL); break;
    	case ',': doType(KeyEvent.VK_COMMA); break;
    	case '<': doType(KeyEvent.VK_LESS); break;
    	case '.': doType(KeyEvent.VK_PERIOD); break;
    	case '>': doType(KeyEvent.VK_GREATER); break;
    	case '/': doType(KeyEvent.VK_SLASH); break;
    	case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
    	case ' ': doType(KeyEvent.VK_SPACE); break;
    	default:
    		throw new IllegalArgumentException("Cannot type character " + character);
    	}
    }

    private synchronized void doType(int... keyCodes) {
    	doType(keyCodes, 0, keyCodes.length);
    }

    private synchronized void doType(int[] keyCodes, int offset, int length) {
    	if (length == 0) {
    		return;
    	}
    	robot.keyPress(keyCodes[offset]);
    	doType(keyCodes, offset + 1, length - 1);
    	robot.keyRelease(keyCodes[offset]);
    }
    /**
     * Selects the window and presses escape
     * @param window
     */
    public synchronized void pressEscape(PWindow window)
    {
    	selectWindow(window);
    	robot.keyPress(KeyEvent.VK_ESCAPE);
    	syncSleep(100);
    	robot.keyRelease(KeyEvent.VK_ESCAPE);
    }
    public synchronized void pressControl()
    {
    	robot.keyPress(KeyEvent.VK_CONTROL);
    }
    public synchronized void releaseControl()
    {
    	robot.keyRelease(KeyEvent.VK_CONTROL);
    }
    public synchronized void pressShift()
    {
    	robot.keyPress(KeyEvent.VK_SHIFT);
    }
    public synchronized void releaseShift()
    {
    	robot.keyRelease(KeyEvent.VK_SHIFT);
    }
	public void mouseMove(int x, int y) 
	{
		/*
		if(x < 200)
		{
			System.out.println(x+" "+y);
			for(StackTraceElement s : Thread.currentThread().getStackTrace())
			{
				System.out.println(s);
			}
		}
		*/
		robot.mouseMove(x, y);
	}
	public void logThreadAction(String action)
	{
		System.out.println(Thread.currentThread() + " performs "+ action+" at "+System.currentTimeMillis());
	}
}