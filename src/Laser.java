package BattleTanks;
import java.awt.Graphics;
import java.awt.Color;

public class Laser extends Projectile 
{
	public static final int TYPE=121;
	Projectile[] detect;
	public Laser(Weapon maker, Coordinate c)
	{
		super(maker,c);
		double angle=maker.getLocation().getAngle(c).getValue();
		detect= new Projectile[10];
		Weapon temp= maker;
		for(int i=-5;i<5;i++)
		{
			detect[i+5]=new Projectile(temp,c);
			detect[i+5].move(1);
			temp=detect[i+5];
		}
		speed=100;
	}
	public static void draw(Graphics g,int x,int y,int state,double angle)
	{
		double angle90=angle+Math.PI/2;
		if(state==TYPE)
		{
			g.setColor(Color.blue);
			for(double i=-4;i<4;i+=.5)
			{
				g.drawLine(	(int)(x-Math.cos(angle)*50+Math.cos(angle90)*i),
							(int)(y-Math.sin(angle)*50+Math.sin(angle90)*i),
							(int)(x+Math.cos(angle)*50+Math.cos(angle90)*i),
							(int)(y+Math.sin(angle)*50+Math.sin(angle90)*i));
			}
		}
		else
		{
			g.setColor(Color.red);
			g.fillOval(x-5-2*state,y-5-2*state,10+4*state,10+4*state);
		}
	}
	/**
	 *i hate how i have to repeat this method for each subclass (against polymorphism)
	 *im pretty sure this is only because i use a static variable (	TYPE) for the difference
	 *also, type needs to be static because draw is in static reference
	 *there is a way around it, but WAY around it
	 */
    protected byte getState()
	{
		if (alive)
			return (byte)TYPE;
		else
			return (byte)explodeFrame;
	}
    protected void checkCollision()
	{	
		for(Tank t: world.getTanks())
	 	{
	 		for(Projectile p: detect)
	 		{
	 			if(p.nextTo(t)&&t.isAlive()&&t!=maker)
		 		{
		 			alive=false;
		 			t.explode();
		 			if (maker instanceof Tank)
		 			{
		 				if(t.getTeam()==(((Tank)maker).getTeam())&&t.getTeam()!=null)
		 					((Tank)maker).decrementKills();
		 				else
		 					((Tank)maker).incrementKills();
		 			}
		 		}
	 		}	 				 	
	 	}
	 	for(Solid s: world.getSolids())
	 	{
	 		for(Projectile p: detect)
	 		{	
	 			if (s.collides(p)[0])
	 				alive=false;
	 		}
	 	}
	 	if (!world.isValidPoint(location))
	 		removeSelfFromWorld();
	}
	public void bla2()
	 {
	 	System.out.println("laser called");
	 }
	public String toString()
	{
		return "Laser-"+super.toString();
	}
}