package BattleTanks;
/**
 *A class to handle things done with points
 */
public class Coordinate {
	private double x,y;

    public Coordinate(double x,double y) 
    {
    	this.x=x;
    	this.y=y;
    }
    public Coordinate(Coordinate c)
    {
    	this.x=c.x;
    	this.y=c.y;
    }
    public Coordinate(){}
    public Coordinate getWorldCoordinate(Screen s)
    {
    	return new Coordinate(s.translateXToWorld(xInt()),s.translateYToWorld(yInt()));
    }
    public double x(){return x; }
    public double y(){return y; }
    public int  xInt(){return (int)x; }
    public int  yInt(){return (int)y; }
	public void set(double x,double y){this.x=x;this.y=y;	}
	public void set(int x,int y){this.x=(double)x;this.y=(double)y;	}
    public void addX(double x){this.x+=x; }
    public void addY(double y){this.y+=y; }
    /**
     *copies the values from another Coordinate
     */
    public void set(Coordinate that)
    {
    	this.x=that.x;
    	this.y=that.y;
    }
    public byte[] getBytes()
    {

    	byte[] xBytes=Util.getBytes(xInt());
		byte[] yBytes=Util.getBytes(yInt());
		
		return new byte[] {
			xBytes[0],//0
			xBytes[1],//1
			yBytes[0],//2
			yBytes[1]};//3
    }
    /**
     *returns an Angle that is the difference between right and to the parameter's position
     */
    public Angle getAngle(Coordinate that)
    {
   		return new Angle(Math.atan2(that.y-this.y,that.x-this.x));
    }
    public Angle getAngle(double x, double y)
    {
   		return new Angle(Math.atan2(y-this.y,x-this.x));
    }
    /**
     *returns the distance beween this Coordinate, and the parameter's
     */
    public double getDistance(Coordinate that)
    {
    	return (Math.sqrt(Math.pow(this.y-that.y,2)+Math.pow(this.x-that.x,2)));
    }
    public double getDistance(double x, double y)
    {
    	return (Math.sqrt(Math.pow(this.y-y,2)+Math.pow(this.x-x,2)));
    }
    /**
     *returns a Coordinate that is beween 0,0 and this object's x and y
     */
    public Coordinate random()
    {
    	return new Coordinate(Math.random()*x,Math.random()*y);
    }
     /**
     *returns a Coordinate that is on a circle of the radius
     */
    public Coordinate random(double radius)
    {
    	double angle=Math.random()*Math.PI*2;
    	return new Coordinate(Math.cos(angle)*radius+x,Math.sin(angle)*radius+y);
    }
    public boolean insideSquare(Coordinate corner1,Coordinate corner2)
    {
    	if (corner1.x<corner2.x)
    	{
    		if (!(x>=corner1.x&&x<=corner2.x))
    			return false;
    	}
    	else
    	{
    		if (!(x>=corner2.x&&x<=corner1.x))
    			return false;
    	}
    	if (corner1.y<corner2.y)
    	{
    		if (!(y>=corner1.y&&y<=corner2.y))
    			return false;
    	}
    	else
    	{
    		if (!(y>=corner2.y&&y<=corner1.y))
    			return false;
    	}
    	return true;
    }
    public String toString()
    {
    	return "("+(int)x+","+(int)y+")";
    }
    
    
    
}