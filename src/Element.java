package BattleTanks;
/*
 *	just something i made so it looks pretty
 */
import java.awt.Graphics;
abstract class Element
{
	protected Coordinate location;
	protected World world;
	protected int radius;
	private int index;
	public Element(World w, Coordinate c,int r)
	{
		world=w;
		location=c;
		radius=r;
	}
	public void setIndex(int index)
	{
		this.index=index;
	}
	public int getIndex()
	{
		return index;
	}
		public boolean nextTo(Element e,int distance)
	{
		return distance>=(Math.sqrt(Math.pow(e.location.x()-location.x(),2)+Math.pow(e.location.y()-location.y(),2)));
	}
	public boolean nextTo(Element e)
	{
		return radius+e.getRadius()>=this.location.getDistance(e.location);
	}
	public boolean nextTo(Coordinate c)
	{
		return radius>=this.location.getDistance(c);
	}
	public boolean nextTo(Coordinate c,int radius)
	{
		return radius+this.radius>this.location.getDistance(c);
	}
	public int getRadius(){	return radius;	}
	public Coordinate getLocation()	{	return location;	}
	public World getWorld()	{	return world;	}
	
	public void removeSelfFromWorld()
	{
		//System.out.println("removing self");
		world.remove(this);
	}
	
	//abstract byte[] getData();
}