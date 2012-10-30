package BattleTanks;

import java.net.*;
import java.util.ArrayList;
import java.io.IOException;
/*
 *A simple threaded Object that connects to a single "channel" 
 *and can be checked for the last set of bytes on the channel.
 *
 *
 */
public class MulticastListener extends Thread
{
	
	public static final int INCOMING_BUFFER_LENGTH = 10000;//10000 bytes max per packet
	private InetAddress group;
	private MulticastSocket socket;
	private DatagramPacket incoming;
	private boolean hasData;
	private int channel,port;
	ArrayList<Byte> bytes = new ArrayList<Byte>();
	
	public MulticastListener(int channel,int port)
	{
		this.channel=channel;
		this.port=port;
		hasData=false;
		try {
		group = InetAddress.getByName("224.0.0."+channel);	

		socket = new MulticastSocket(port);	 
		socket.setTimeToLive(1); // stay within local network
		socket.joinGroup(group);
	 
		
	
		byte[] incomingBuffer = new byte[INCOMING_BUFFER_LENGTH];
		incoming = new DatagramPacket(incomingBuffer, INCOMING_BUFFER_LENGTH);

            

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public int getChannel()
	{
		return channel;
	}
	public int getPort()
	{
		return port;
	}
	
   	public void run() 
   	{
		incoming.setLength(INCOMING_BUFFER_LENGTH);
		while (true) 
		{	
			try
			{
				socket.receive(incoming);
				//hasData=false;//prevent race condition with listeners that are continuously reading while this is writing
				byte[] byteData = incoming.getData();
				int size=incoming.getLength();
				for (int i=0;i<size;i++)
					bytes.add(byteData[i]);
				
				//System.out.println("recieved " + size +" bytes from the datagram packet");
				hasData=true;
			}catch(SocketException e)
			{
				//do nothing, this only happens when the socket is closed while still listening
			}catch(IOException e)
			{
					e.printStackTrace();
			}
			
		}
   	}	
   		
	public void close()
	{
		socket.close();
	}
			
	
    public boolean hasBytes()
    {
    	return hasData;
    }
    public byte[] getBytes()
    {
    	byte[] list= new byte[bytes.size()];
    	try{
	    	for(int i = 0; i<list.length;i++)
	    	list[i]=bytes.get(i);
	 
	    	bytes.clear();
	    	//System.out.println("returning "+list.length+" bytes from arraylist in listener");
	    	hasData=false;
    	}catch (NullPointerException e)
    	{
    		System.out.println("nullpointer experienced while retrieving network data from list");
    	}
    	return list;
    	
    }
    
    
}