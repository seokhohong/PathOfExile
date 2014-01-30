package message;

/**
 * 
 * Thrown if a bad string is passed to a message parser
 * 
 * @author Seokho
 *
 */
public class MessageException extends Exception
{
	private String error;
	
	public MessageException(String error)
	{
		this.error = error;
	}
	
	@Override
	public String toString()
	{
		return error;
	}
}
