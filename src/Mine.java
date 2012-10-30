package BattleTanks;

public class Mine extends Projectile
{
	public static final int TYPE=122;
	private int count;
	private boolean counting;
    public Mine(Weapon maker) 
    {
    	super(maker,maker.getLocation());
    	count=0;
    	counting=true;
    	
    }
    /**
	 *i hate how i have to repeat this method for each subclass (against polymorphism)
	 *im pretty sure this is only because i use a static variable (	TYPE) for the difference
	 *also, type needs to be static because draw is in static reference
	 *there is a way around it, but WAY around it
	 */
    protected byte getState()
	{
		if (counting)
			return (byte)Projectile.TYPE;
		else if (alive)
			return (byte)TYPE;
		else
			return (byte)explodeFrame;
	}
	/*public static void draw(Graphics g,int x,int y,int state,double angle)
	{
		if(counting)
			Projectile.draw(g,x,y,state,angle);
	}*/
    public void move(double multiplier)
    {
    	if(!counting)
    		count++;
    	else if(count>50)
    		counting=false;
    	if(!alive)
    		explode();
    	checkCollision();
    }
    
    
}