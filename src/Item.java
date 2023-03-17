package BattleTanks;
/**
 *items are a bit wonky, when taken up by a tank, world says: hey, go away you hobo
 *and a nice philantropic tank takes the item unter it's arms
 *when the tank dies, it calls world.addItem(it's item) in it's last location
 */
 import java.awt.*;
public class Item extends Element
{
	public static final int size=5;
	public static final String[] types={"nuke","starburst","laser","mine","seeker"	};

	String type;
	Tank owner;
    public Item(World w, Coordinate c, String type) 
    {
    	super(w,c,10);
    	owner=null;
    	this.type=type;
    	
    }
    public static boolean isValid(String type)
    {
    	boolean found=false;
    	for(int i=0;i<types.length&&!found;i++)
    		found=types[i].equals(type);
    	if(!found)
    		System.out.println(type+ " not found as an item type");
    	return found;
    	
    }
    public void setOwner(Tank t)
    {
    	//System.out.println("tank got an item");
    	owner=t;
    	t.setItem(this);
    	removeSelfFromWorld();
    }
    public void reset()
    {
    	//System.out.println("item reset");
       	owner.setItem(null);    	
    	location.set(owner.getLocation());
    	world.addItem(this);
    	owner=null;
    }
    public void draw(Graphics g,Screen s)
    {
    	draw(g,getData(),s);
    }
    public static void draw(Graphics g, int x, int y, int type,Screen s)
    {
    	switch(type)
		{
			case 0: //nuke
				g.setColor(Color.yellow);
				g.fillOval(x-5,y-5,10,10);	
				g.setColor(Color.blue);
				g.drawOval(x-5,y-5,10,10);
				break;
			case 1://starburst
				g.setColor(Color.red);
				g.fillOval(x-5,y-5,10,10);	
				g.setColor(Color.blue);
				g.drawOval(x-5,y-5,10,10);
				break;
			case 2://laser
				g.setColor(Color.green);
				g.drawOval(x-5,y-5,10,10);
				g.setColor(Color.blue);
				g.fillOval(x-5,y-5,10,10);				
				break;
			case 3: //mine
				//break;
			default:
				g.setColor(Color.black);
				g.fillOval(x-5,y-5,10,10);
    	}
    }
    public static void draw(Graphics g,byte[] data,Screen s)
    {
    	int x=s.translateXToScreen(Util.getInt(data[0],data[1]));
		int y=s.translateYToScreen(Util.getInt(data[2],data[3]));
		draw(g,x,y,(int)(data[4]),s);
    }
    public void checkCollision()
    {
    	for(Tank t: world.getTanks())
    	{
    		if(nextTo(t)&&!t.hasItem())
    		{
    			setOwner(t);
    			return;
    		}
    	}
    }
    public byte[] getData()
    {
    	byte[] coord=location.getBytes();
    	byte t;
		if(type.equals("nuke"))
			t=0;
		else if(type.equals("starburst"))
			t=1;
		else if(type.equals("laser"))
			t=2;
		else if(type.equals("mine"))
			t=3;
		else t=-1;
			
		return new byte[] {
			coord[0],
			coord[1],
			coord[2],
			coord[3],
			t};
    }
    public void edit(){}
    public String getType()
    {
    	return type;
    }
    
    
}