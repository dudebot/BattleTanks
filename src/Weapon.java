package BattleTanks;
import java.awt.*;
/**
 *an abstract class that pretty much does anything that anything that moves can do
 */
abstract class Weapon extends Element//,Moveable
{
	protected Angle angle;

	
	/**
	 *this value relfects whatever index it is in it's world,
	 * so any modifications to world must fix all indexes
	 */
	boolean alive;
	protected int radius;
	public Weapon(World world)
	{
		super(world,new Coordinate(),5);
		angle=new Angle();
		alive=true;

	}
	public Weapon(World world,int radius)
	{
		super(world,new Coordinate(),radius);
		angle=new Angle();
		alive=true;
	}

	
	
	public Angle getAngle()	{	return angle;	}
	public boolean isAlive()
	{
		return alive;
	}
	

	
	public void rotate(double change)
	{
		angle.add(change);
	}
	public void move(double speed)
	{		
		location.addX(Math.cos(angle.getValue())*speed);
		location.addY(Math.sin(angle.getValue())*speed);
		//check walls, maybe make it repeating some way
	}
	abstract double getSpeed();
	/**
	 *whatever the weapon looks like
	 *keep in mind this needs to reflect the explosion detection system/what the f did i mean by this?
	 */
	public void draw(Graphics g)
	{
		//g.drawImage(image,50,50,null);
		g.setColor(Color.white);
		g.drawString(toString(),location.xInt(),location.yInt()-20);//this is basically only used for debug
		//g.drawString(""+angle.getValue(),location.xInt(),location.yInt()-20);
	}
	abstract void explode();
	public String toString()
	{
		return getClass().getName()+" at ("+location.xInt()+", "+location.yInt()+") "+getSpeed()+" pixels/move and "+(int)angle.toDegrees()+" degrees";		
	}

}