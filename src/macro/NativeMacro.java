package macro;

import data.Config;

public class NativeMacro 
{
	public native void leftClick(int x, int y);
	public native void rightClick(int x, int y);
	public native void middleClick(int x, int y);

	/*
	* Coordinates identify a point within the window that is to accept this input
	*/
	public native void typeChar(char c, int x, int y);
	public native void typeEscape(int x, int y);
	public native void scrollDown(int x, int y, int clicks);
	
	public NativeMacro(Config config) 
	{
		System.loadLibrary("macro_NativeMacro"+config.getComputer());
	}
}
