package BattleTanks;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
/*
 *A simple Object that connects to a single "channel" 
 *and can be used to send data on the channel.
 */
class MulticastSender 
{
	private static InetAddress group;
	private static MulticastSocket socket;
	private int channel,port;
	private static DatagramPacket outgoing;
	
	public MulticastSender(int channel,int port)
	{
		this.channel=channel;
		this.port=port;
		try{
			group = InetAddress.getByName("224.0.0."+channel);
		}catch (UnknownHostException e)		{
			System.out.println(e.getMessage());
	 		e.printStackTrace();
		}
		try {
			socket = new MulticastSocket(port);	 
			socket.setTimeToLive(1); // stay within local network
			socket.joinGroup(group);
	 
		// initial byte array and length of 1 will be ignored
			outgoing = new DatagramPacket(new byte[1], 1, group, port);
		} catch (IOException e) {
			System.out.println(e.getMessage());
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
	
	public void send(byte[] utf)
   	{
	    outgoing.setData(utf);
	    outgoing.setLength(utf.length);
	    try{
	    	socket.send(outgoing);
	    	//System.out.println("yo dog, i sent " + utf.length +" bytes");
	    }catch (IOException e) {
	 		System.out.println(e.getMessage());
	 		e.printStackTrace();
      	}
	    
	}
	public void close()
	{
		socket.close();
	}
}