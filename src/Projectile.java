package BattleTanks;
import java.awt.*;
/**
 *something that is projected from a weapon (it is a weapon itself too)
 *there is something terribly wrong with explode and remove self, somehow it ends with removing the last index
 */
public class Projectile extends Weapon
{
	public static final int TYPE=0;
	public static final int size=6;
	//inherits angle,location,index, and world and a few methods from Weapon, 
	//so please study the javadocs
	double speed,damage;//this has a crapload of inherited things
	protected int explodeFrame;
	//Color color;
	Weapon maker;
	Coordinate finishLoc;
	public Projectile(Weapon maker,Coordinate c)//,Color color)
	{
		super(maker.getWorld(),5);
		this.maker=maker;
		speed=10;
		finishLoc=c;
		//distance=0;
		//maxDistance=maker.getLocation().getDistance(c);//hypotenuse
		location.set(maker.getLocation());
		angle=maker.getLocation().getAngle(c);
		//this.color=color;
		
	}
	public byte[] getData()
	{
		byte[] coord=location.getBytes();
		
		return new byte[] {
			coord[0],
			coord[1],
			coord[2],
			coord[3],
			getState(),
			angle.getByte()};
	}
	public static void draw(Graphics g, byte[] data,Screen s)
	{
		int x=s.translateXToScreen(Util.getInt(data[0],data[1]));
		int y=s.translateYToScreen(Util.getInt(data[2],data[3]));
		int state=(int)data[4];
		if(state==Laser.TYPE)
			Laser.draw(g,x,y,state,Angle.getDouble(data[5]));
		else if(state==Mine.TYPE);//heh dont hack this :D
			//Mine.draw(g,x,y,state,Angle.getDouble(data[5]));
		else if(state==Seeker.TYPE)
			Seeker.draw(g,x,y,state,Angle.getDouble(data[5]));
		else
			draw(g,x,y,state,Angle.getDouble(data[5]));
	}
	public void draw(Graphics g,Screen s)
	{
		int x=s.translateXToScreen(location.xInt());
		int y=s.translateYToScreen(location.yInt());
		int state=(int)getState();
		if(state==Laser.TYPE)
			Laser.draw(g,x,y,state,angle.getValue());
		else
			draw(g,x,y,state,angle.getValue());
			
	}
	public static void draw(Graphics g,int x,int y,int state,double angle)
	{
		if(state==0)
		{
			g.setColor(Color.black);
			g.fillOval(x-5,y-5,10,10);
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
	
	public double getSpeed()
	{
		return speed;
	}
	public Weapon getMaker()
	{
		return maker;
	}
	public static double getDefaultSpeed()
	{
		return 10;
	}
	public void move(double multiplier)
	{		
		
		if (alive)
		{
			super.move(speed*multiplier);			
		}
		else
			explode();
		checkCollision();
			
		
			
	}
	protected void checkCollision()
	{	
		for(Tank t: world.getTanks())
	 	{
	 		if(nextTo(t)&&t.isAlive()&&t!=maker)
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
	 	for(Solid s: world.getSolids())
	 	{
	 		if (s.collides(this)[0])
	 			alive=false;
	 	}
	 	if (!world.isValidPoint(location))
	 		removeSelfFromWorld();
	}
	/*public void draw(Graphics g)
	{
		super.draw(g);
		g.setColor(color);
		
		if (!alive)//exploding
		{
			g.setColor(Color.red);
			g.fillOval(location.xInt()-5-2*explodeFrame,location.yInt()-5-2*explodeFrame,10+4*explodeFrame,10+4*explodeFrame);
		}
		else
			g.fillOval((int)location.x()-5,(int)location.y()-5,10,10);
		
		//System.out.println(location.x()+location.y());
		
		
	}
	*/
	/**
	 *whatever happens when the projectile has reached it's limit
	 */
	 public void explode()
	 {
	 	alive=false;
	 	explodeFrame++;
	 	//System.out.println(this+" exploded");
	 	if (explodeFrame>=5)
	 	{
	 		removeSelfFromWorld();
	 	}

	 }

	 public void bla()
	 {
	 	System.out.println("projectile called");
	 	bla2();
	 }
	 public void bla2()
	 {
	 	System.out.println("projectile called");
	 }
	 public String toString()
	 {
	 	return "Projectile @ "+location;
	 }

	 
	
	
}
