package control;

/**
 * 
 * Thrown if a command is improperly formatted
 * 
 * @author Seokho
 *
 */
@SuppressWarnings("serial")
public class CmdException extends Exception
{
	private String error;
	
	public CmdException(String error)
	{
		this.error = error;
	}
	
	@Override
	public String toString()
	{
		return error;
	}
}
