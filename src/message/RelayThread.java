package message;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import macro.Macro;

public class RelayThread<T> extends Thread 
{
	private Socket socket = null;
    
    private boolean halt = false;
    
    private RelayProtocol<T> protocol;
    
    private ObjectOutputStream objOStream = null;
    
    private String connectedComputer = "";				public String getComputer() { return connectedComputer; }

    public RelayThread(RelayProtocol<T> protocol, Socket socket) 
    {
    	this.socket = socket;
    	try
    	{
    		OutputStream os = socket.getOutputStream();
    		objOStream = new ObjectOutputStream(new BufferedOutputStream(os));
    	}
    	catch(IOException e) {}
    	this.protocol = protocol;
    }
    //Returns whether a message was sent
    public boolean send(T message)
    {
    	while(true)
    	{
	    	try
	    	{
	    		objOStream.writeObject(message);
	    		objOStream.flush();
	    		return true;
	    	}
	    	catch(SocketException e)
	    	{
	    		System.out.println("Couldn't send message");
	    		return false;
	    	}
	    	catch(IOException e)
	    	{
	    		e.printStackTrace();
	    		Macro.sleep(5000);
	    	}
    	}
    }
    @Override
	public void run() 
    {
		try 
		{
			InputStream is = socket.getInputStream();

			objOStream.flush();		//flush header
			
			ObjectInputStream objIStream = new ObjectInputStream(is);
			
			while(!halt)
			{
				@SuppressWarnings("unchecked")
				T message = (T) objIStream.readObject();
				T response = protocol.processInput(message);
				if(response != null)
				{
					objOStream.writeObject(response);
					objOStream.flush();
				}
			}
			is.close();
			objIStream.close();
		    socket.close();
	
		}
		catch (SocketException e)
		{
			protocol.disconnected(socket);
		}
		catch (IOException e) {
		    e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    public void halt()
    {
    	halt = true;
    }
}