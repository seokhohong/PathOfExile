package message;

public interface Communicator 
{
	/**
	 * Called when this computer receives a message
	 */
	public void processMessage(Message m);
	
	/**
	 * Sends a packet to a computer
	 */
	public void sendMessage(MessagePacket mp);
	
	/**
	 * Called when a message reaches its destination
	 */
	public void messageDelivered(MessagePacket m);
	
	/**
	 * Called if a message fails to reach its destination
	 */
	public void messageIgnored(MessagePacket m);
}
