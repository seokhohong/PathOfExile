
public class NativeMacro
{
	public native void leftClick(int x, int y);
	public native void leftDoubleClick(int x, int y);
	public native void rightClick(int x, int y);
	public native void middleClick(int x, int y);

	/*
	* Coordinates identify a point within the window that is to accept this input
	*/
	public native void typeChar(char c, int x, int y);
	public native void typeEscape(int x, int y);
	public native void scrollDown(int x, int y, int clicks);
	static 
	{
		System.loadLibrary("NativeMacro");
		nativeMacro = new NativeMacro();
	}
	private NativeMacro() {}
	public static NativeMacro nativeMacro; //Static native methods don't work, so this is the singleton pattern
	public static void main(String[] args)
	{
		System.out.println("Loaded Library");
		nativeMacro.typeChar('A', 30, 0);
		System.out.println("Done");
	}
}