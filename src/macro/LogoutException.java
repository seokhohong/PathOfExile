package macro;

//Thrown to make the program log out
public class LogoutException extends Throwable
{
	String message;
	public LogoutException(String message)
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
