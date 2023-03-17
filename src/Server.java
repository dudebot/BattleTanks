package BattleTanks;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

/**
 *server end of the system :D
 *todo:	send a mapName parameter to World to load it instead of randomly creating an empty world (World issue) 
 *		listen for client data
 *
 */
public class Server extends Frame implements KeyListener// implements Runnable// implements Invoker
{
	World world;
	MulticastSender sender;
	MulticastListener listener;
	MulticastSender specialSender;
	MulticastListener specialListener;
	boolean canAdvanceFrame;

	
	public final static long timePerFrame=(long)(1000000000/30);//last number is the maximum fps before the anti lag system starts
	//final static long normalTimePerFrame=1000000000/30;//is the world's modifier
	public final static boolean manualFrames=false;

	public Server(int channel, String mapName)
	{
		//addKeyListener(this);
		//setUndecorated(true);
		setTitle("BattleTanks Server Console");
		setVisible(true);
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					System.exit(0);	}});
		
		addKeyListener(this);
		sender = new MulticastSender(channel,Util.servSend);
		listener= new MulticastListener(channel,Util.servListen);
		listener.start();
		System.out.println("Connected to channel #: "+channel);
		
		//specialSender = new MulticastSender(channel,Util.servSendSpec);
		//specialListener= new MulticastListener(channel,Util.servListenSpec);
		//specialListener.start();
		
		setSize(400,300);
		
		
		
		
		
		world = new World(mapName);
		//world = new World(new Coordinate(800,600));
		
		
		
	

		
			
		
		
		//MidiPlayer sound=new MidiPlayer("Loops_Of_Fury.mid");
		//new Thread(sound).start();
		
	}
	/**
	 *a simi-infinite loop that calls analyzeKeys, world.draw, a simple gui, and draws the buffer
	 */
	public void run()
	{
		System.out.println("Initializing main server loop");
		Graphics buffer,bgBuffer;
		BufferedImage image, bgImage;
		image=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		buffer=image.createGraphics();
		bgImage=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		bgBuffer=bgImage.createGraphics();
		bgBuffer.setColor(Color.RED);
		bgBuffer.fillRect(0,0,getWidth(),getHeight());
		
		long startTime=System.nanoTime(),startFrameTime=startTime,frameTime=1,totalFrameTimes=1;
 		double multiplier=1;
		boolean normalFPS=true;
		byte[] clientData;
		ClientDataStore cds= new ClientDataStore();
		
		canAdvanceFrame=!manualFrames;//for testing purposes
		
		System.out.println("Server started");
		while(true)
		{
			////////////////START COUNTING//////////////
			startFrameTime=System.nanoTime();
			
					
			/////////////////GUI////////////////////////
			buffer.drawImage(bgImage,0,0,null);
			buffer.drawString(String.valueOf((long)(1000000000/frameTime)),30,100);
			buffer.drawString("FPS",60,100);
			buffer.drawString(String.valueOf(world.getTanks().size())+" tanks",30,130);
			
			buffer.drawString(String.valueOf(world.getClientSize())+" Clients",120,130);
			buffer.drawString(String.valueOf(world.getAISize())+" AIs",200,130);
			
			buffer.drawString(String.valueOf(world.getProjectiles().size())+" projectiles",30,160);
			getGraphics().drawImage(image,0,0,null);
			if(listener.hasBytes())
				cds.add(listener.getBytes());
	
					
				
				
				
			
			//move everything
			if (normalFPS)
			{
				world.move(1,cds);
				buffer.drawString(String.valueOf(multiplier),100,20);
			}
			else
			{
				
				multiplier=(double)frameTime/timePerFrame;
				world.move(multiplier,cds);
				buffer.drawString(String.valueOf(multiplier),100,20);
				//buffer.dSystem.out.println(multiplier);
				
			}

			/*if (frameTime<=timePerFrame)
				buffer.drawString("FPS : "+1000000000l/frameTime+" (capped)",100,50);
			else
				buffer.drawString("FPS : "+1000000000l/frameTime,100,50);
			//end gui
			
			buffer.drawString(" avg shown fps: "+1000000000l/((System.nanoTime()-startTime)/frames)+"     theoretical fps:" +1000000000l/(totalFrameTimes/frames)+"     last multiplier: "+multiplier,300,50);
			drawGraph(buffer,5,5);
			
			*/
			
			//frames++;
			//if (frames%1000==0)//every 1000 frames, call the garbage collector for great justice.
				//System.gc();
				
			sender.send(world.getData());
				
				
			
			//draw entire frame/ modify here to make resizable window?
			//getGraphics().drawImage(image,0,0,this);
			totalFrameTimes+=frameTime;
			frameTime=(System.nanoTime()-startFrameTime);
			//System.out.println(frameTime);
			
			try{
				Thread.sleep((timePerFrame-frameTime)/1000000l,(int)(frameTime%1000000));//sleep the remainder of the frame
				normalFPS=true;//this line would be impossible to get to with an exception(i think)
				//System.out.println("normal");
			}catch (Exception e){
				//System.out.println("Warning: this frame was "+(33-timeout)+" milliseconds... removing half of dumbness, with "+world.getProjectiles().size()+" projectiles");
				//world.killHalf();
				normalFPS=false;
				//System.out.println("slow");
				
			}
			while(!canAdvanceFrame);//either test mode is off or spacebar makes true
			if(manualFrames)
				canAdvanceFrame=false;			
		}
				
	}

	private void drawGraph(Graphics2D g,int x,int y)
	{
		g.setColor(Color.white);
		g.fillRect(x,y,x+world.getTanks().size(),5);
		g.fillRect(x,y+10,x+world.getProjectiles().size(),5);
		if(world.getProjectiles().size()>1000)
			g.drawString(""+world.getProjectiles().size(),x+1100,y+30);
		g.setColor(Color.black);
		g.drawLine(x+500,y,x+500,y+15);
		g.drawString("500",x+500,y+25);
		g.drawLine(x+1000,y,x+1000,y+20);
		g.drawString("1000",x+1000,y+30);
		
	}


	public void keyPressed(KeyEvent e)
	{
		//System.out.println(e.getKeyCode());
		if(manualFrames)
			canAdvanceFrame=true;
			
		//61=+
		//45=-
		if (e.getKeyCode()==61)
			world.addAI();
		if (e.getKeyCode()==45)
			world.removeAI();
	}
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e)
	{
		
	}
	

	

	public String toString()
	{
		return ""+world;
	}	


	public static void main(String[] args) 
    {
        Server game= new Server(2,"defaultmap");
        game.run();

    }
    
}


