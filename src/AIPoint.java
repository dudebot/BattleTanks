package BattleTanks;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JOptionPane;
import java.util.StringTokenizer;//pretty worthless in this case, but this is java, and it's crap

public class AIPoint extends EditableElement
{
	private ArrayList<AIPoint> points;
	
    public AIPoint(World w, Coordinate c,int index) 
    {
    	super(w,c,0);
    	setIndex(index);
    	points = new ArrayList<AIPoint>();
    }
    public void removeSelfFromWorld()
    {
    	super.removeSelfFromWorld();
    	for(AIPoint a: points)
    	{
    		a.removeLink(this);//dont keep reference to something that doesnt exist
    	}
    }
    public boolean edit()
    {
    	//	System.out.println("AIPoint edit() called");
    
    	AIPoint collision=null;
		
		try{
			String line = JOptionPane.showInputDialog(null,"enter an index in format: 'number'\ncurrent is: "+getIndex());
			StringTokenizer st= new StringTokenizer(line);
			int index=Integer.parseInt(st.nextToken());
			setIndex(index);
			if(world.getPoint(index)!=null)
			{	
				System.out.println("that point already exists");
				return false;
			}
			
				
		}catch(Exception e){
			System.out.println("parse error");
			return false;
		}
		return true;
			//if(collision!=null)
				//collision.edit();//mutual recurstion with the user to finish it
    	//check for collision, if there is, then call that one's edit until happy
	}
    
    public void linkPoint(AIPoint a)
    {
    	if(points.contains(a))
    		removeLink(a);
    	else
    		points.add(a);
    	//a.points.add(this);
    }
    public void doubleLink(AIPoint a)
    {
    	linkPoint(a);
    	a.linkPoint(this);
    }
    public void removeLink(AIPoint a)
    {
    	points.remove(points.indexOf(a));
    }
    public ArrayList<AIPoint> getPoints()
    {
    	return points;
    }
    public void draw(Graphics g, Screen s)
    {
    	if(this instanceof Team)
    	{
    		g.setColor(Color.green);
    		g.fillOval(s.translateXToScreen(location.xInt()-10),s.translateYToScreen(location.yInt()-10),screen.zoom()*20,screen.zoom()*20);
    	}
    	g.setColor(Color.yellow);
    	g.fillOval(s.translateXToScreen(location.xInt())-5,s.translateYToScreen(location.yInt()-5),screen.zoom()*10,screen.zoom()*10);
    	for(AIPoint a:points)
    	{
    		double angle=location.getAngle(a.getLocation()).getValue();
    		//double angle1=location.getAngle(a.getLocation()).getValue()-Math.PI*9/8;
    		g.setColor(Color.green);
    		g.drawLine(s.translateXToScreen(location.xInt()),s.translateYToScreen(location.yInt()),s.translateXToScreen(a.location.xInt()),s.translateYToScreen(a.location.yInt()));
    		g.setColor(Color.red);
    		g.drawLine(s.translateXToScreen(a.location.xInt()),s.translateYToScreen(a.location.yInt()),
    			s.translateXToScreen(a.location.xInt()+(int)(Math.cos(angle)*40)),
    			s.translateYToScreen(a.location.yInt()+(int)(Math.sin(angle)*40)));
    	}
    	g.setColor(Color.black);
    	g.drawString(String.valueOf(getIndex()),s.translateXToScreen(location.xInt())+20,s.translateYToScreen(location.yInt()));
    }
    public String toString()
    {
    	return "AIPoint @ "+getLocation()+" index: "+getIndex();
    }
    
    
}