package BattleTanks;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
/**
 *This is something that does something automatically
 */
public class AI extends Tank
{
	public static final byte AIClient = (byte)-128;
	AIPoint last,goal;

	
	//double accuracy;
	double range;//maximum distance they can fire
	boolean angry;
	
    public AI(World world)
    {
    	super(AIClient,world);
    	
    	goal=last=null;
    	//generateGoal();
    	toggleMove(1);
    	angry=false;
    	//accuracy=1;
    	range=600;
    	
    }
    
    public void move(double multiplier)
    {
    	super.move(multiplier);
    	Tank tempTank=getClosestEnemy();
    	if(tempTank!=null)
    	{
    		sightAngle=getLocation().getAngle(tempTank.getLocation());
    		if (isAngry())
    		{
    			if(Math.random()<0.02)//how often per frames it will shoot
    			{	
    				fireAt(tempTank);
    			}
    		}
    	}
    	else
    		sightAngle=angle;
    	if(goal==null)
    	{
    		if(team==null)
    			goal=getClosestAIPoint();
    		else
    			goal=team;
    	}
    	if(nextTo(goal))
    	{
    		//System.out.println(index+" regenerated a new goal");
    		generateGoal();
    	}
    	
    	
    		
    	toggleRotate(new Angle(location.getAngle(goal.getLocation()).getDifference(angle)).getValue());
    	//System.out.println(this.location.getAngle(goal)-angle.getValue());
    	
    }
    public void setAngry(boolean value)
    {
    	angry=value;
    }
    public boolean isAngry()
    {
    	return angry;
    }
    private void generateGoal()
    {	
    	AIPoint temp=goal;
    	
    	
    	{
    		boolean cutoff=false;
    		int numPoints=goal.getPoints().size();//this isnt foolproof, but it should work
    		int index=0;
    		for(int i=0;i<numPoints&&!cutoff;i++)
    		{
    			index=(int)(Math.random()*numPoints);
    			if(goal.getPoints().get(index)!=last)
    				cutoff=true;
    		}
    		if(cutoff)
    			goal=goal.getPoints().get(index);
    		else
    			goal=last;
    	}
    	last=temp;
    	/*boolean goodGoal;
    	do{
    		goodGoal=true;
    		goal=(new Coordinate(rnd.nextDouble()*world.getBounds().x(),rnd.nextDouble()*world.getBounds().y()));
    		//System.out.println(goal);
    		for(Solid s:world.getSolids())
    		{
    			if(s.collides(goal,20)[0])
    			{
    				goodGoal=false;
    				//System.out.println("goal failure");
    			}
    		}
    	}while(!goodGoal);*/
    }
    
    private AIPoint getClosestAIPoint()
    {
    	AIPoint point=null;
    	double distance=-1;
    	for(AIPoint a:world.getPoints())
    	{
    		double tempDist=this.location.getDistance(a.getLocation());
    		if(tempDist<distance||distance==-1)
    		{
    			point=a;
    			distance=tempDist;
    		}
    			
    	}
    	return point;
    	
    }
    private Tank getClosestEnemy()
    {
    	Tank tank=null;//it's going to be null if not found
    	//double distance=world.getBounds().getDistance(new Coordinate(0,0));//cant be farther than the maximum corners of map
    	double distance = 500;
    	for(Tank t:world.getTanks())
    	{
    		double tempDist=this.location.getDistance(t.getLocation());
    		if (tempDist<distance&&t!=this&&t.isAlive())
    		{
    			if(team==null||t.getTeam()==null||!team.equals(t.getTeam()))
    			{
    				tank=t;
    				distance=tempDist;	
    			}
    		}
    			
    	}
    	return tank;
    }
    public void fireAt(Tank t)
    {
    	Tank tempTank = t.copy();
    	double frames=0;
    	double frameMultiplier=10;
    	boolean gettingCloser=false;
    	double distance;
    	
    	do
    	{
    		//distance is the absolute value of the difference beween how far away the object is with how far a projectile could be.
    		distance=Math.abs(location.getDistance(tempTank.getLocation())-frames*Projectile.getDefaultSpeed());
    		tempTank.move(frameMultiplier);
    		frames+=frameMultiplier;
    		double newDistance=Math.abs(location.getDistance(tempTank.getLocation())-frames*Projectile.getDefaultSpeed());
    		gettingCloser=newDistance<distance;
    		if(!gettingCloser)
    			frameMultiplier=-frameMultiplier/2;
    		distance=newDistance;
    		//System.out.println(distance);
    			
    	}while(Math.abs(frameMultiplier)>0.02);//&&distance>accuracy);
    	//System.out.println(frameMultiplier+ " " + distance);
    	shoot(tempTank.getLocation());
    	//sightAngle.set(getLocation().getAngle(t.getLocation()));
    }
    public void resetLocation()
    {
    	goal=team;
    	last=null;
    	super.resetLocation();
    }

    public void draw(Graphics g)
    {
    	super.draw(g);
    	//g.setColor(team.getColor());
    	//g.fillOval(goal.xInt()-5,(int)goal.yInt()-5,10,10);
    	//g.drawString(location.getAngle(goal)+" and "+angle,location.xInt(),(int)location.yInt()-20);
    }
    
}