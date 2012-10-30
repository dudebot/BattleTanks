package BattleTanks;
/**
 *A simple class to hangle angles
 *angles are always going to be: Math.PI >= angle > -Math.PI
 *need to double check the automatic check system, because there ARE bugs hidden somewhere
 */
public class Angle 
{

	private double angle;
	public Angle()
	{
		angle=0;
	}
    public Angle(double angle) 
    {
    	
    	if (Math.abs(angle)>Math.PI*2)
    		angle%=Math.PI*2;//reduce to +-2PI
    	if (angle>Math.PI)
    		angle-=Math.PI*2;//send to negative angle
    	else if (angle<-Math.PI)
    		angle+=Math.PI*2;//send to positive angle
    	//now we are at +-PI
    	this.angle=angle;
    	

    }
    public Angle(Angle that) 
    {
    	this.angle=that.angle;
    }
    public byte getByte()
    {
    	return (byte)(Math.round((angle+Math.PI)/Math.PI*64)); //returns 0-127 range 
    } 
    public static double getDouble(byte angle)
    {
    	return ((double)angle)*Math.PI/64-Math.PI; //returns +-PI range
    }
    public void add(double change)
    {
    	set(angle+change);
    }
    public void set(double newVal)
    {
    	angle=newVal;
    	angle%=Math.PI*2;
    	if(angle<-Math.PI)
			angle+=Math.PI*2;
		else if(angle>Math.PI)
			angle-=Math.PI*2;
    }
    public void set(Angle that)
    {
    	this.angle=that.angle;
    }
    public Angle getDifference(Angle that)
    {
    	return new Angle(this.angle-that.angle);
    }
    public double toDegrees() 	{	return Math.toDegrees(angle);	}
    public double getValue()    {   return angle;	}
    public String toString()	{	return ""+angle;}	
    
    
    
    
}