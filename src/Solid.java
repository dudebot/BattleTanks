package BattleTanks;
import java.awt.*;
import javax.swing.JOptionPane;
import java.util.StringTokenizer;
/**
 *Im not really sure what this will do, but it would be nice if it blocked thinks from passing
 *maybe return an Angle so that tanks can move still if hitting it or a wall
 *maybe make the edges of the world automatically solids
 */
public class Solid extends EditableElement
{
	//AffineTransform yay; how about no
	//Image image;
	//Angle angle;//screw this :)
	int width,height;
	public Solid(World w,Coordinate c,int width,int height)
	{
		super(w,c,0);
		this.height=height;
		this.width=width;
		//angle=a;
	}
	public boolean edit()
	{
		//System.out.println("Solid edit() called");

		
		try{
			String line = JOptionPane.showInputDialog(null,"enter width and height with format: 'x y'");
			StringTokenizer st= new StringTokenizer(line);
			int x=Integer.parseInt(st.nextToken());
			int y=Integer.parseInt(st.nextToken());
			width=x;
			height=y;
			return true;
		}catch(Exception e){
			System.out.println("parse error");
			return false;
		}
		
		
		
	}
	public int getWidth(){return width;	}
	public int getHeight(){return height;	}
	public void draw(Graphics g,Screen s)
	{
		//if (image)!=null
		g.setColor(Color.blue);
		g.fillRect(s.translateXToScreen(location.xInt()-width/2),s.translateYToScreen(location.yInt()-height/2),(int)(s.getZoom()*width),(int)(s.getZoom()*height));
	}
	
	public boolean[] collides(Weapon w)
	{
		return collides(w.getLocation(),w.getRadius());
	}
	public boolean[] collides(Coordinate c, int radius)
	{
		//w.getRadius();
		
			
		boolean insideXLeft  =c.x()<location.x()+width/2+radius;
		boolean insideXRight =c.x()>location.x()-width/2-radius;
		boolean insideYLeft  =c.y()<location.y()+height/2+radius;
		boolean insideYRight =c.y()>location.y()-height/2-radius;
		boolean withinX=(insideXLeft&&insideXRight);
		boolean withinY=(insideYLeft&&insideYRight);
		boolean collision=  withinX&&withinY;
							
							
		boolean top=false,bottom=false,left=false,right=false;
		if(collision)
		{
			top=withinX&&c.y()<location.y();
			bottom=withinX&&c.y()>location.y();
			left=withinY&&c.x()<location.x();
			right=withinY&&c.x()>location.x();
			
			/*if (top)
				System.out.println("collision: top");
			if (bottom)
				System.out.println("collision: bottom");
			if (left)
				System.out.println("collision: left");
			if (right)
				System.out.println("collision: right");
			*/
		}
		boolean[] bools={collision,top,bottom,left,right};
		return bools;
		
	}

	
	/*
	private double farthestDistance(Coordinate c,boolean ws)
	{
		return 0;
	}
	public boolean widthSide(Angle a)
	{
		return height/width<Math.tan(a.getValue());//if this works, cudos
	}
	private Coordinate closestPoint(Weapon w,boolean ws,Angle relative)
	{
		if (ws)
		{
				
		}
		else
		{
			
		}
		return new Coordinate(w.getLocation().x()+w.getRadius()*Math.cos(relative),)
	}
	*/
		


}