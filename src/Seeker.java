package BattleTanks;
import java.awt.Graphics;
public class Seeker extends Projectile
{
	public static final int TYPE=123;
    public Seeker(Weapon maker, Coordinate c) 
    {
    	super(maker,c);
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
	public static void draw(Graphics g,int x,int y,int state,double angle)
	{
		Projectile.draw(g,x,y,state,angle);
	}
    
}