package message;

import java.net.Socket;

public interface RelayProtocol<T>
{
	/** Defines how messages should be processed */
	public T processInput(T message);
	
	/** Called when the other end disconnects from the socket */
	public void disconnected(Socket socket);
}
