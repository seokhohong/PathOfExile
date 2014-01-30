package message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server<T> extends Thread
{	
	private RelayProtocol<T> protocol;

	private boolean halt = false;
	
	private int port;
	
	private HashMap<String, String> ipToName = new HashMap<String, String>();		public String getNameByIp(String ip) { return ipToName.get(ip); }
	
	private HashMap<String, RelayThread<T>> connections = new HashMap<String, RelayThread<T>>();
	
	public Server(RelayProtocol<T> protocol, int port)
	{
		this.protocol = protocol;
		this.port = port;
	}
	
	@Override
	public void run()
	{
        ServerSocket serverSocket = null;
        try 
        {
            serverSocket = new ServerSocket(port);
            while(!halt)
            {
            	Socket socket = serverSocket.accept();
            	RelayThread<T> rt = new RelayThread<T>(protocol, socket);
            	connections.put(socket.getInetAddress().getHostAddress(), rt);
            	rt.start();
            }
            serverSocket.close();
        } 
        catch (IOException e) 
        {
            System.err.println("Could not listen on port: "+port+".");
            System.exit(-1);
        }
	}
	
	/** Unfortunately, it is not possible to extract a computer name from a socket connection, so the receiver should call this
	 *  method to update the HashMap with the computer name rather than the ip
	 */
	public void updateConnectionEnd(String ip, String computerName)
	{
		ipToName.put(ip, computerName);
		HashMap<String, RelayThread<T>> newConnections = new HashMap<String, RelayThread<T>>();
		RelayThread<T> rt = connections.get(ip);
		for(String s : connections.keySet())
		{
			if(!s.equals(ip))
			{
				newConnections.put(s, connections.get(s));
			}
		}
		connections = newConnections;
		connections.put(computerName, rt);
	}
	
	public RelayThread<T> getConnectionTo(String computer)
	{
		return connections.get(computer);
	}
	
	public void halt()
	{
		halt = true;
	}
	
}
