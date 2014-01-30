package process;

public interface Quittable 
{
	/**
	 * 
	 * The program will not quit on its own, but upon invoking the correct input sequence that the ExitHook looks for, exitProgram will be called
	 * 
	 */
	public void exitProgram();
}
