package BattleTanks;
import java.awt.*;
/**
 *a weapon that moves in ways a user can move it
 *also it does some magic with sending and parsing it's own data
 *if this data is modified, change the number of bytes to match Tank.size
 */
public class Tank extends Weapon
{
	public static final int size=11;
	//inherits angle,location,index, and world and a few methods from Weapon
	protected double speed,maxRotateSpeed,rotateToggle;

	protected Angle sightAngle;
	protected int speedToggle,ghostCount,shootTime,kills;
	byte client;
	//Color color;
	protected Team team;
	private int lagFrames;
	private Item item;
	private boolean deathWait;
	private int deathWaitCount;
	
	public Tank(byte client,World world)
	{
		super(world,10);
		this.client=client;
		angle.set(Math.random()*2*Math.PI);
		sightAngle= new Angle(angle);
		//color=Color.white;
		speed=5;
		maxRotateSpeed=.1;
		speedToggle=0;
		kills=0;
		lagFrames=0;
		item=null;
		deathWait=false;
		deathWaitCount=0;
	}
	public boolean hasItem()
	{
		return item!=null;
	}
	public void setItem(Item i)
	{
		item=i;
	}
	public byte[] getData()
	{
		byte[] coord=location.getBytes();
		byte[] bytekills = Util.getBytes(kills);
		byte teamByte;
		if(team==null)
			teamByte=(byte)-1;
		else
			teamByte=team.getByte();
		return new byte[] {
			coord[0],
			coord[1],
			coord[2],
			coord[3],
			angle.getByte(),
			sightAngle.getByte(),
			getState(),
			teamByte,
			client,
			bytekills[0],
			bytekills[1]};
			

	}
	public void lagged()
	{
		lagFrames++;
		if(lagFrames>300)//10 seconds
		{
			System.out.println("Client "+ client + " lagged out");
			removeSelfFromWorld();
			
		}
		
		
			
	}
	public void resetLag()
	{
		lagFrames=0;
	}
	public void set(ClientData cd)
	{
		//System.out.println(cd);
		if(cd.left())
			toggleRotate(-1);
		else if(cd.right())
			toggleRotate(1);
		else
			toggleRotate(0);
		if (cd.forward())
			toggleMove(1);
		else if(cd.backward())
			toggleMove(-1);
		else
			toggleMove(0);
		//if(cd.mouseLoc()!=null)//i have no idea why this is null sometimes, but this works
	//	{
		if(cd.shoot())
			shoot(cd.mouseLoc());
		else if(cd.specialShoot())
			specialShoot(cd.mouseLoc());			
		lookAt(cd.mouseLoc());	
	//	}
		
	}
	
	public byte getClient(){return client;	}
	private byte getState()
	{
		if (alive)
			return (byte)0;
		else if(!deathWait)
			return (byte)1;
		else
			return (byte)deathWaitCount;
	}
	public static int[] draw(Graphics g, byte[] data,Screen s)
	{
		int client=(int)data[8];
		int realX=Util.getInt(data[0],data[1]);
		int realY=Util.getInt(data[2],data[3]);
		int x=s.translateXToScreen(realX);
		int y=s.translateYToScreen(realY);
		int kills=Util.getInt(data[9],data[10]);
		draw(g,x,y,Angle.getDouble(data[4]),Angle.getDouble(data[5]),(int)data[6],(int)data[7],kills);
		int[] retur={client,realX,realY,kills,(int)data[7]};
		
		return retur;
	}
	public void draw(Graphics g,Screen s)
	{
		int x=s.translateXToScreen(location.xInt());
		int y=s.translateYToScreen(location.yInt());
		if(team!=null)
			draw(g,x,y,angle.getValue(),sightAngle.getValue(),1,(int)(team.getByte()),kills);
		else
			draw(g,x,y,angle.getValue(),sightAngle.getValue(),1,-1,kills);
	}
	public static void draw(Graphics g,int x,int y,double angle1,double angle2,
								int state,int team,int kills)
	{
		if (state==0||Math.random()>.85)
		{
			if(team!=-1)//no team
			{
				switch(team)
				{
					case 0: g.setColor(Color.yellow); break;
					case 1: g.setColor(Color.green); break;
					case 2: g.setColor(Color.blue); break;
					case 3: g.setColor(Color.red); break;
					default: g.setColor(Color.black);
				}
			}
			else
				g.setColor(Color.lightGray);
		}	 
		else
			g.setColor(Color.gray);
		drawPolygon(g,x,y,4,angle1);
		g.setColor(Color.black);
		g.drawString(""+kills,x,y-20);
		g.setColor(Color.red);
		double sightAngle = angle2;
		g.drawLine(x,y,x+(int)(10*Math.cos(sightAngle)),y+(int)(10*Math.sin(sightAngle)));
		//System.out.println(Util.getInt(data[0],data[1])+ ", "+ Util.getInt(data[2],data[3]));
		
		if(state>1)
		{
			g.setColor(Color.red);
			g.fillOval(x-state,y-state,2*state,2*state);
		}
	}
	public void setTeam(Team team)
	{		
		this.team=team;
		if(hasItem())
			item.reset();
		resetLocation();

		//if (team!=null)
			//color=team.getColor();	
	
	}
	public Team getTeam() { return team;}
	//public void setColor(Color color){	this.color=color;}
	public Tank copy()
	{
		Tank tank = new Tank(client,world);
		tank.angle.set(angle);
		tank.speed=speed;
		tank.location.set(getLocation());
		tank.speedToggle=speedToggle;
		tank.rotateToggle=rotateToggle;
		return tank;
	}
	public void incrementKills()	{		kills++;	}
	public void decrementKills()
	{		
		if (kills>0)
			kills--;
	}
	/*public void draw(Graphics g)
	{
		if(alive||ghostCount%2==0)
		{
			super.draw(g);
			g.setColor(color);
			drawPolygon(g,location.xInt(),location.yInt(),4,angle.getValue());
			g.setColor(Color.white);
			g.drawLine(location.xInt(),location.yInt(),(int)(location.x()+10*Math.cos(sightAngle.getValue())),(int)(location.y()+10*Math.sin(sightAngle.getValue())));//add 90
		}
		

		
		
	}*/
	private static void drawPolygon(Graphics g,int centerX, int centerY,int sides, double theta)
	{
    	int[] x=new int[sides];
    	int[] y=new int[sides];
    	int index=0;
    	theta+=Math.PI/4;
    	
    	while (index<sides)
    	{
    		
    		//System.out.println("theta is "+theta+" with sides: "+sides);
    		x[index]=(int)(centerX+10*Math.cos(theta));
    		y[index]=(int)(centerY+10*Math.sin(theta));
    		index++;
    		theta+=2*Math.PI/sides;
    		
    	}
    	g.fillPolygon(x,y,sides);
	}
	public void toggleRotate(double move)//find a way to make these 2 methods permanent
	{	
		if(Math.abs(move)<=maxRotateSpeed)
		{
			rotateToggle=move;
		}
		else
		{
			if (move>0)
				rotateToggle=1;//move cw
			else if (move<0)
				rotateToggle=-1;//move ccw
			else
				rotateToggle=0;
		}
		
	}
	public void toggleMove(double move)
	{
		if (move>0)
			speedToggle=1;//move forward
		else if (move<0)
			speedToggle=-1;//move backward
		else
			speedToggle=0;
	}

	public void move(double multiplier)		
	{
		shootTime++;
		if(!alive)
		{
			if(deathWait)
			{
				deathWaitCount++;
				if(deathWaitCount>50)
				{
					deathWait=false;
					if(hasItem())
						item.reset();
					resetLocation();
				}
					
			}
			else
			{
				ghostCount++;
				if (ghostCount>=100)
				alive=true;
			}
		}
			
			
		if(!deathWait)
		{	
			double x=Math.cos(angle.getValue())*speed*speedToggle*multiplier;
			double y=Math.sin(angle.getValue())*speed*speedToggle*multiplier;
			for(Solid s: world.getSolids())
			{
				boolean[] bools=s.collides(this);
				if(bools[0])
				{
					if ((y>0&&bools[1])||(y<0&&bools[2]))
						y=0;
					if ((x>0&&bools[3])||(x<0&&bools[4]))
						x=0;
					
				}
				
					
			}
			
			
			//super.move(speed*multiplier*speedToggle);
			location.addX(x);
			location.addY(y);
			
			if(rotateToggle!=0)
			{
				if(Math.abs(rotateToggle)<maxRotateSpeed)
					rotate(rotateToggle);//go exact remainder of angle
				else
					rotate(rotateToggle*multiplier*maxRotateSpeed);
					
			}
		}
	}
	public double getSpeed()
	{
		return speed*speedToggle;
	}
	public void shoot(Coordinate explodeCoordinate)
	{
		if(shootTime>=10&&alive)
		{
			world.addProjectile(new Projectile(this,explodeCoordinate));
			shootTime=0;
		}
	}
	public void specialShoot(Coordinate explodeCoordinate)
	{
		
		if(alive&&hasItem())//no time limit
		{
			if(item.getType().equals("nuke"))
			{
				world.addProjectile(new Nuke(this,explodeCoordinate));
				item=null;
			}
			else if(item.getType().equals("starburst"))
			{
				world.addProjectile(new Starburst(this,explodeCoordinate,2));
				item=null;
			}
			else if(item.getType().equals("laser"))
			{
				world.addProjectile(new Laser(this,explodeCoordinate));
				item=null;
			}
			/*else if(item.getType().equals("mine"))
			{
				world.addProjectile(new StarburstProjectile(this,explodeCoordinate,2));
				item=null;
			}*/
			else
			{
				
				System.out.println("unrecognised item shot: deleting...");
				item=null;
			}
		}
			
		
		
		//,color));
			//System.out.println("tank shot");
			
			
		
			
		
	}
	
	public void resetLocation()
	{
		if (team!=null)
			location.set(team.getLocation());
		else
			location.set(world.getBounds().random());
	}

	public double getDistance(Tank that)
	{
		Coordinate other= that.getLocation();
		return Math.sqrt(Math.pow(other.x()-location.x(),2)+Math.pow(other.x()-location.y(),2));
	}
	public void lookAt(Coordinate c)
	{
		sightAngle.set(location.getAngle(c));
	}
	public double getDirection(Tank that)
	{
		Coordinate other= that.getLocation();
		return Math.atan2(other.y()-location.y(),other.x()-location.x());
	}

	public boolean obstructedFrom(Element that)
	{
		return false;
	}
	/*private int[] cleanIntArray(int[] array, int length)
	{
		int[] result= new int[length];
		for(int i =0; i<length;i++)
			result[i]=array[i];
		return result;			
	}*/

	public void explode()
	{
		deathWait=true;
		deathWaitCount=0;
		//System.out.println("Client "+client+" exploded!");
		alive=false;
		ghostCount=0;
		//terminate();
	}
	public String toString()
	{
		return String.valueOf(client);
	}
	     

	
}