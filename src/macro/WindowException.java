package macro;

//Thrown if the window is no longer visible
public class WindowException extends Throwable
{
	String message;
	public WindowException(String message)
	{
		this.message = message;
	}
	@Override
	public String toString()
	{
		return message + "\n\n" + super.toString();
	}
	@Override
	public String getMessage()
	{
		return message;
	}
	private static final long serialVersionUID = 1L;
}
