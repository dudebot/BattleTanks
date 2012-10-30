package BattleTanks;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JOptionPane;
import java.util.StringTokenizer;
public class ItemFactory extends EditableElement
{
	String type;
	boolean waiting;
	int seconds;
	int framesPast;//i know this is wrong, but i dont care anymore
    public ItemFactory(World w,Coordinate c, String type, int seconds) 
    {
    	super(w,c,0);
    	this.type=type;
    	waiting=true;
    	this.seconds=seconds;
    	framesPast=10000;
    }
    public boolean edit()
    {
    	//System.out.println("ItemFactory edit() called");

		try{
			String line = JOptionPane.showInputDialog(null,"enter type and time in seconds: 'type t'");
			StringTokenizer st= new StringTokenizer(line);
			String type=(st.nextToken());
			int time=Integer.parseInt(st.nextToken());
			seconds=time;
			this.type=type;
			boolean good=Item.isValid(type);
			if(!good)
				System.out.println("invalid item type");
			return good;
		}catch(Exception e){
			System.out.println("parse error");
			return false;
		}
		
		
		
    	//type seconds
    }
    public int getDelay()
    {
    	return seconds;
    }
    public String getType()
    {
    	return type;
    }
    public void check()
    {
    	if(waiting)
    	{			    		
    		framesPast++;
			if (framesPast/30>seconds)
			{
				waiting=false;
				world.addItem(new Item(world,new Coordinate(location.x(),location.y()),type));
				framesPast=0;
			}
    	}
		else
		{
			boolean closeItem=false;
	    	for(Item i: world.getItems())
	    		if(nextTo(i))
	    			closeItem=true;
	    			
	    	if(!closeItem)
	    	{
	    		waiting=true;
	    		framesPast++;
	    	}
		}
    }
    public void draw(Graphics g,Screen s)
    {
    	int x=s.translateXToScreen(location.xInt());
		int y=s.translateYToScreen(location.yInt());
		if(type.equals("nuke"))
		{		g.setColor(Color.yellow);
				g.fillOval(x-5,y-5,screen.zoom()*10,screen.zoom()*10);	
				g.setColor(Color.blue);
				g.drawOval(x-5,y-5,screen.zoom()*10,screen.zoom()*10);
		}else if(type.equals("starburst"))
		{		g.setColor(Color.red);
				g.fillOval(x-5,y-5,screen.zoom()*10,screen.zoom()*10);	
				g.setColor(Color.blue);
				g.drawOval(x-5,y-5,screen.zoom()*10,screen.zoom()*10);
		}else
		{
				g.setColor(Color.black);
				g.fillOval(x-5,y-5,screen.zoom()*10,screen.zoom()*10);
    	}
    }
    
}