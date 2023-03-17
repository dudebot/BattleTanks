package BattleTanks;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 *this class is the client section of the system.
 *it gets information from the server (list of projectiles/tanks)
 *finds which tank this client is controlling, and then finally
 *draws all of the world based off where that tank is.
 *
 *the second threaded task of this class is to send the server
 *what keys are being pressed that the server needs to know
 *ie: wasd, clicking, where the mouse is on the world.
 *
 *in a communication sense, the class that represends the opposite
 *(server sending) is Tank
 *any changes to what this class sends (number of bytes), Client.size
 *NEEDS to change and the next byte needs to be implemented in Client.getData
 *and the ClientData constructor (also other methods to get that data)
 *and finally in world, do something with that data in World.move
 *
 *
 *currently working on making the view dynamic:
 *	change where everything is drawn
 *	change where the mouse location is based on where the tank is
 *		(relative change)
 *	zoom maybe?
 *
 *also working on:
 *	automatic client selection wooo!
 */
public class Client extends Frame implements MouseMotionListener,KeyListener,MouseListener
{
	public static final int size=6; //number of bytes being sent to the server
	MulticastListener listener;
	MulticastSender sender;
	MulticastSender specialSender;
	MulticastListener specialListener;
	//Config config;
	byte client;//what THIS client is
	byte team;
	Coordinate mouseLocation;
	Screen screen;
	boolean forward;
	boolean backward;
	boolean left;
	boolean right;
	boolean shoot,specialShoot;
	
	ArrayList<Integer> keys;
	ArrayList<Solid> solids;
	
	public Client(int channel)
	{
		//read this from server :/
		//String mapName="defaultmap";
		
		keys=new ArrayList<Integer>();
		
		
		listener = new MulticastListener(channel,Util.servSend);
		sender= new MulticastSender(channel,Util.servListen);
		
		//specialListener = new MulticastListener(channel,Util.servSendSpec);
		//specialSender = new MulticastSender(channel,Util.servListenSpec);
		
		
		mouseLocation=new Coordinate();
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		
		setTitle("BattleTanks Client");
		setUndecorated(true);
		listener.start();
		screen = new Screen();	
		setUndecorated(true);
		setSize(screen.getWidth(),screen.getHeight());
		
		
		team=0;
		
		
		
		//System.out.println("recieved client number of " + client);
		addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					System.exit(0);	}});
	}
	
	public void paint(Graphics g){}
	private void makeBG(Graphics g)
	{
		g.setColor(Color.white);
		g.fillRect(0,0,getWidth(),getHeight());
		/*
		*/
	}


   	public void run() 
   	{
   		Graphics2D buffer,radarBuffer,scoreBuffer, bgBuffer;
		BufferedImage image,radarImage,scoreImage, bgImage;
		image=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		buffer=image.createGraphics();
		//radarImage=new BufferedImage(screen.getWidth()/5,screen.getWidth()/5, BufferedImage.TYPE_INT_ARGB);
		//radarBuffer=radarImage.createGraphics();
		//radarBuffer.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, .7F));
		bgImage=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		bgBuffer=bgImage.createGraphics();
		makeBG(bgBuffer);
		byte[] byteData=Util.waitForBytes(listener,10);
		
		boolean[] clientsTaken=new boolean[128];
		int numTanks=Util.getInt(byteData[1],byteData[2]);
        
		
		//choose a client number
		System.out.println("List of existing clients:");
		for(int i=0;i<numTanks;i++)
		{
			byte temp=byteData[World.size+8+i*Tank.size];
			if(temp!=-128)
			{
				clientsTaken[temp]=true;
				System.out.println(temp);
			}
			
				
		}
		
		//now choose the client
		boolean found=false;
		for(int i=0;i<128&&!found;i++)
		{
			if (!clientsTaken[i])
			{
				client=(byte)i;
				found=true;
			}
				
		}
		if(!found)
		{
			JOptionPane.showMessageDialog(null,"The server is full, try getting one of the 128 people off the server");
			System.exit(0);
		}
		//while((byteData=Util.waitForBytes(listener,10))[0]!=(byte)1);//heh
		
		//wait for server to pop out map info
		while(byteData[0]!=(byte)1)
			byteData=Util.waitForBytes(listener,10);
		numTanks=Util.getInt(byteData[1],byteData[2]);
		int numProjectiles=Util.getInt(byteData[3],byteData[4]);
		int numItems=Util.getInt(byteData[5],byteData[6]);
		int previousBytes=numTanks*Tank.size+
						numProjectiles*Projectile.size+
						numItems*Item.size+
						World.size;
		
		solids= Util.getSolids(Util.getInt(byteData[previousBytes],byteData[previousBytes+1]),
							   Util.getInt(byteData[previousBytes+2],byteData[previousBytes+3]));
		if (solids==null)
			System.exit(1);
		setVisible(true);
		while (true) 
		{
			

			
			byteData=Util.waitForBytes(listener,10);
			
			//System.out.println("byte data is " + byteData.length+ " bytes from listener in client");
			numTanks=Util.getInt(byteData[1],byteData[2]);
            numProjectiles=Util.getInt(byteData[3],byteData[4]);
			numItems=Util.getInt(byteData[5],byteData[6]);
			
			Coordinate center = new Coordinate();
			//***************************************** START DRAWING
			buffer.drawImage(bgImage,0,0,this);
			buffer.setColor(Color.gray);
			int line=(int)(500*screen.getZoom());
			int tempX=(int)((screen.getCenterX()*screen.getZoom())%line);
			int tempY=(int)((screen.getCenterY()*screen.getZoom())%line);
			
			
			//for(int i=(int)(-screen.getZoom()*tempX);i<screen.halfX();i+=screen.getZoom()*line)
			for(int i=-1;i<(int)(screen.getWidth()/line/screen.getZoom());i++)
			{
				buffer.fillRect(screen.halfX()+i*line-tempX,0,(int)(25*screen.getZoom()),getHeight());
				buffer.fillRect(screen.halfX()-i*line-tempX,0,(int)(25*screen.getZoom()),getHeight());
			
			}
			for(int i=-1;i<(int)(screen.getHeight()/line/screen.getZoom());i++)
			{
				buffer.fillRect(0,screen.halfY()+i*line-tempY,getWidth(),(int)(25*screen.getZoom()));
				buffer.fillRect(0,screen.halfY()-i*line-tempY,getWidth(),(int)(25*screen.getZoom()));
			}
			
			for(Solid s:solids)
			{
				s.draw(buffer,screen);
			}

				
			try{
			//radarBuffer.setColor(Color.white);
			//radarBuffer.fillOval(0,0,screen.getWidth()/5,screen.getWidth()/5);
			buffer.setColor(new Color(70,70,70,70));
			buffer.fillOval(0,0,screen.getWidth()/5,screen.getWidth()/5);
			previousBytes=World.size;
			for(int i=0;i<numTanks;i++)//ignore first 4 bytes because those are the number tanks and projectiles 
			{
				byte[] tank = new byte[Tank.size];
				for(int p=0;p<Tank.size;p++)
					tank[p]=byteData[i*Tank.size+previousBytes+p];				
					
				int[] data=Tank.draw(buffer,tank,screen);
				if (tank[8]==client)
				{
					team=tank[7];
					center.set(Util.getInt(tank[0],tank[1]),Util.getInt(tank[2],tank[3]));
				}
					
				if(screen.getCenter().getDistance(data[1],data[2])<screen.getWidth()/2*3)
				{
					if(data[4]!=team)
						buffer.setColor(Color.red);
					else if (tank[8]!=client)
						buffer.setColor(Color.green);
					else
						buffer.setColor(Color.blue);
					buffer.fillOval(screen.halfX()/5+(data[1]-screen.getCenterX())/(3*5)-3,screen.halfX()/5+(data[2]-screen.getCenterY())/(3*5)-3,6,6);
						
				}
				
			}
			
			previousBytes+=numTanks*Tank.size;
			for(int i=0;i<numProjectiles;i++)
			{
				byte[] projectile=new byte[Projectile.size];
				for(int p=0;p<Projectile.size;p++)
					projectile[p]=byteData[i*Projectile.size+previousBytes+p];
				Projectile.draw(buffer,projectile,screen);
			}
			previousBytes+=numProjectiles*Projectile.size;
			for(int i=0;i<numItems;i++)
			{
				byte[] item=new byte[Item.size];
				for(int p=0;p<Item.size;p++)
					item[p]=byteData[i*Item.size+previousBytes+p];
				Item.draw(buffer,item,screen);
			}
			}catch(IndexOutOfBoundsException e)
			{
				System.out.println("failed to properly draw world because of stream corruption");
			}
			//set the center from that found in tank loop
			screen.setCenter(center);
			//buffer.drawImage(radarImage,0,0,Color.white,this);
			getGraphics().drawImage(image,0,0,this);
			
			try{
			passiveAnalyzeKeys();
			sender.send(getData());
			}catch(Exception e)
			{
				System.out.println("goodbye");
				return;
			}
			
		}
		
			

	}
	private byte[] getData()
	{
		byte[] x=Util.getBytes(screen.translateXToWorld(mouseLocation.xInt()));
		byte[] y=Util.getBytes(screen.translateYToWorld(mouseLocation.yInt()));
		byte[] bytes= new byte[size];
		bytes[0]=client;		
		bytes[1]=Util.getByte(forward,backward,left,right,shoot,specialShoot,false,false);
		bytes[2]=x[0];
		bytes[3]=x[1];
		bytes[4]=y[0];
		bytes[5]=y[1];
		shoot=false;//dont repeat :)
		specialShoot=false;
		return bytes;
	}


	public void mouseMoved(MouseEvent e)
    {
    	//mouseLocation.set(screen.translateXToWorld(e.getX()),screen.translateYToWorld(e.getY()));
    	mouseLocation.set(e.getX(),e.getY());
    }
    public void mouseDragged(MouseEvent e)
    {
    	mouseMoved(e);	
    }
    public void keyPressed(KeyEvent e)
	{
		//System.out.println(e.getKeyCode());
		int lastKey=e.getKeyCode();
		if (!keys.contains(lastKey))
			keys.add(lastKey);
		activeAnalyzeKeys();
	
	}
	public void keyReleased(KeyEvent e)
	{
		keys.remove((Object)e.getKeyCode());
	}
	public void keyTyped(KeyEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){	}
	public void mouseReleased(MouseEvent e)	{}
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{	
		if(e.getButton()==1)
			shoot=true;
		else if(e.getButton()==3)
			specialShoot=true;
	}
	
	
	/**
	 *this is called at the end of every frame
	 */
    public void passiveAnalyzeKeys()
	{
		
			
		forward=keys.contains(87);
		backward=keys.contains(83);
			
		
		if(forward&&backward)
		{
			backward=false;
			forward=false;
		}
		
		right=keys.contains(68);
		left=keys.contains(65);

		if(right&&left)
		{
			left=false;
			right=false;
		}
		/*boolean in,out;//super secret dont look
		in=keys.contains(45);
		out=keys.contains(61);
		
		if(in^out)
		{
			if(in)
				screen.zoom(0.8);
			else
				screen.zoom(1.25);
		}*/
		
		
	}
	/**
	 *this method is called after every key pressing event
	 */
	private void activeAnalyzeKeys()
	{
		if(keys.contains(27))
			exitServer();
	}
	private void exitServer()
	{
		//Util.sendDisconnect(client,sender);
		listener.close();
		sender.close();
		System.exit(0);
	}
	public static void main(String[] args)
	{
		Client f= new Client(2);
	
		f.run();
		
		
		
	}
	


}
