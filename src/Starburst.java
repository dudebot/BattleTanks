package BattleTanks;
import java.awt.*;
/**
 *A fun little projectile that sends lesser StarBurstProjectiles once every frame it explodes
 *There is no limit to the levels that can be done
 *radius of levels and speed from original explosion might need tweaking
 */
public class Starburst extends Projectile
{
	int level;
	double distance,maxDistance;
    public Starburst(Weapon maker,Coordinate c,int levels) 
    {
    	super(maker,c);
    	distance=0;
    	maxDistance=maker.getLocation().getDistance(c);
    	level=levels;
    	if (maker instanceof Starburst)
    	{
    		this.maker=((Projectile)maker).getMaker();
    		speed=1.5;
    	}
    		
    	
    }
    public void move(double multiplier)
    {
    	if (distance>=maxDistance)
    		alive=false;
    	distance+=speed*multiplier;
    	super.move(multiplier);
    	
    }
    public void explode()
    {
    	if(level>0)
    	{
    		world.addProjectile(new Starburst(this,location.random(30),level-1));   
    	}
    	super.explode();
    }
	public void bla2()
	 {
	 	System.out.println("starburst called");
	 }
	public String toString()
	{
		return "Level: " + level+" Starburst-"+super.toString();
	}
    
}