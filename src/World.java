
package BattleTanks;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
/*
 *this is: 	1-a list of tanks, solid objects, and Projectiles.
 *			2-each element of each array to know where it is in it's list (maybe except for solids).
 *			3-something to add an element to any list
 *			4-something to access any element of any list 
 *			5-something to remove anything to the list while keeping rule #2
 *			6-compile everything needed by clients into a byte list via all the necessary getData methods
 *			7-move everything a multiple of a frame
 *
 *todo:	read the mapName parameter instead of creating an empty world 
 */
public class World
{
	//number of bytes sent before the transmittion of data (metadata)
	public static final int size=7;
	/**
	 *a Coordinate of the bottom right edge of the map
	 */ 
	Coordinate bounds;
	
	/**
	 *a coordinate of 0,0;
	 */ 
	final Coordinate origin;
	ArrayList<Team> teams;
	int numberClients;
	int numberAIs;
	String mapName;
	int tag;
	int version;
	long frames;
	String gameType;
	/**
	 *tanks is ordered by client numbers
	 */
	ArrayList<Tank> tanks;
	ArrayList<Solid> solids;
	ArrayList<Projectile> projectiles;
	ArrayList<Item> items;
	ArrayList<AIPoint> points;
	ArrayList<Integer> tanksToRemove;
	ArrayList<Integer> solidsToRemove;
	ArrayList<Integer> projectilesToRemove;
	ArrayList<Integer> itemsToRemove;

	ArrayList<ItemFactory> itemFactories;
	
	/**
	 *constructs the world including 3 ArrayLists
	 */
	public World(String mapName) 
	{
    	this.mapName=mapName;
    	System.out.println("Creating a world of size: "+bounds);
    	origin=new Coordinate(0,0);
    	teams=new ArrayList<Team>();
    	tanks=new ArrayList<Tank>(); 	
		solids=new ArrayList<Solid>(); 	
		projectiles=new ArrayList<Projectile>();
		items=new ArrayList<Item>();
		points=new ArrayList<AIPoint>();
		tanksToRemove=new ArrayList<Integer>();
		solidsToRemove=new ArrayList<Integer>();
		projectilesToRemove=new ArrayList<Integer>();
		itemsToRemove=new ArrayList<Integer>();
		itemFactories=new ArrayList<ItemFactory>();
		numberAIs=0;
		numberClients=0;
		frames=1;
		
		//stuff to be done by files
		
		Util.loadWorld(this);
		//this.bounds=new Coordinate(1000,1000);
		
		//addTeam(new Team(1,new Coordinate(getBounds().xInt()/4,getBounds().yInt()/2)));
		//addTeam(new Team(2,new Coordinate(getBounds().xInt()*3/4,getBounds().yInt()/2)));	
		//addSolid(new Solid(new Coordinate(1000,200),50,50));
	}
	public long getFrames(){return frames;	}
	public int getVersion(){return version;	}
	public int getTag()	   {return tag;		}
	public synchronized byte[] getData()
	{
		byte[] bytes;
		if(frames%30==0)
			bytes=new byte[items.size()*Item.size+tanks.size()*Tank.size+projectiles.size()*Projectile.size+World.size+4];
		else
			bytes=new byte[items.size()*Item.size+tanks.size()*Tank.size+projectiles.size()*Projectile.size+World.size];
		//int numberTanks=tanks.size();
		//int numberProjectiles=projectiles.size();
		//int numberItems=items.size();
				
		byte[] numTanks=Util.getBytes(tanks.size());
		byte[] numProjectiles=Util.getBytes(projectiles.size());
		byte[] numItems=Util.getBytes(items.size());
		if(frames%30==0)//1 per second
			bytes[0]=(byte)1;
		else
			bytes[0]=(byte)0;
		bytes[1]=numTanks[0];
		bytes[2]=numTanks[1];
		bytes[3]=numProjectiles[0];
		bytes[4]=numProjectiles[1];
		bytes[5]=numItems[0];
		bytes[6]=numItems[1];
		try{
		int previousBytes=size;
		for(int i=0;i<tanks.size();i++)
		{
			byte[] tankBytes=tanks.get(i).getData();
			for(int p=0;p<Tank.size;p++)
				bytes[i*Tank.size+previousBytes+p]=tankBytes[p];
			
		}
		previousBytes+=tanks.size()*Tank.size;
		for(int i=0;i<projectiles.size();i++)
		{
			byte[] projectileBytes=projectiles.get(i).getData();
			for(int p=0;p<Projectile.size;p++)
				bytes[i*Projectile.size+previousBytes+p]=projectileBytes[p];

			
		}
		previousBytes+=Projectile.size*projectiles.size();
		for(int i=0;i<items.size();i++)
		{
			byte[] itemBytes=items.get(i).getData();
			for(int p=0;p<Item.size;p++)
				bytes[i*Item.size+previousBytes+p]=itemBytes[p];
		}
		if(frames%30==0)
		{
			previousBytes+=items.size()*Item.size;
			byte[] btag=Util.getBytes(tag);
			byte[] bver=Util.getBytes(version);
			bytes[previousBytes]=btag[0];
			bytes[previousBytes+1]=btag[1];
			bytes[previousBytes+2]=bver[0];
			bytes[previousBytes+3]=bver[1];
		}
		}catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("World had an issue with sending the byte array:\n");
			System.out.println("original tank number was       "+tanks.size());
			System.out.println("original projectile number was "+projectiles.size());
			System.out.println("original item number was	   "+items.size());
			int total=World.size+tanks.size()*Tank.size+projectiles.size()*Projectile.size+items.size()*Item.size;
			System.out.println("total: "+total+" but the size of the array is: "+bytes.length);
			
			e.printStackTrace();
		}
		
		//solids are drawn with the client
		//they are sent before the client starts playing seperately (being a file or dynamic sending)
		//there is no reason to keep on sending them
		return bytes;
	}
	//public void setType(String type)
	//{
	//	gameType=type;
	//}
	public String mapName() { 	return mapName;			}
	public int getAISize(){		return numberAIs;		}	
	public int getClientSize(){	return numberClients;	}
	public void setSize(int x,int y)
	{
		bounds= new Coordinate(x,y);
	}
	public void setTag(int t)
	{
		tag=t;
	}
	public void setVersion(int v)
	{
		version=v;
	}
	public boolean addTeam(Team team)
	{
		addPoint(team);
		return teams.add(team);
	}
	public void resetTeamPlayers()
	{
		//todo?
	}
	public Coordinate getBounds(){ return bounds;	}
	public boolean isValidPoint(Coordinate location)
	{
		return location.insideSquare(origin,bounds);
	}
	private Team findSmallestTeam()
	{
		Team t= teams.get(0);
		for(int i=1;i<teams.size();i++)
		{
			if (teams.get(i).size()<t.size())
				t=teams.get(i);
				
		}
		return t;
	}
	private Team findLargestTeam()
	{
		Team t= teams.get(0);
		for(int i=1;i<teams.size();i++)
		{
			if (teams.get(i).size()>t.size())
				t=teams.get(i);
				
		}
		return t;
	}
	/**
	 *adds a tank object to it's list
	 */
	public synchronized void addTank(Tank t)
	{
    	t.setIndex(tanks.size());
    	if (teams.isEmpty())
    	{
   			t.setTeam(null);
		}
    	else
   		{	
   			Team team = findSmallestTeam();
    		t.setTeam(team);
    		team.incrementPlayers();
   		}
   		if(tanks.isEmpty())
   			tanks.add(t);
   		else
   		{
   			int index=0;
    		while(index<tanks.size()&&t.getClient()>tanks.get(index).getClient())//huzzah for short circuiting
    		{
    			index++;
    		}
    		
    		tanks.add(index,t);  
   		}
   		if (t instanceof AI)
   			numberAIs++;
   		else
   			numberClients++;
   		 //System.out.println(tanks);
    		
	}
	/**
	 *adds a solid object to it's list
	 */
	public synchronized boolean addSolid(Solid s)
	{
	    	s.setIndex(solids.size());	
	    	return solids.add(s);
	}	
	/**
	 *adds a Projectile object to it's list
	 */
	public synchronized boolean addProjectile(Projectile w)
	{    	
		w.setIndex(projectiles.size());
	    	return projectiles.add(w);
	}
	public synchronized boolean addItem(Item i)
	{
		i.setIndex(items.size());
		return items.add(i);
	}
	
	public void addItemFactory(ItemFactory i)
	{
		itemFactories.add(i);
	}
	public void addPoint(AIPoint a)
	{
		if(points.isEmpty())
   			points.add(a);
   		else
   		{
   			int index=0;
    		while(index<points.size()&&a.getIndex()>points.get(index).getIndex())//huzzah for short circuiting
    			index++;
    		
    		
    		points.add(index,a);
   		}
	}
	/**
	 *returns the tank if it exists
	 *if not, then null
	 */
	public Tank getTank(byte client)
	{
		boolean found=false;
    	int index=0;
    	int low=0;
    	int hi=tanks.size()-1;
    	int mid=0;
    	
    	while(low<=hi&&!found)
    	{
    		mid=(low+hi)/2;
    		if (tanks.get(mid).getClient()>client)
    			hi=mid-1;
    		else if (tanks.get(mid).getClient()<client)
    			low=mid+1;
    		else found=true;
    	}
    	if(found)
    		return tanks.get(mid);
    	else
    		return null;
	}
	public void addAI()
	{
		if(points.size()!=0)
		{
			AI ai = new AI(this);
			ai.setAngry(true);
			addTank(ai);
		}
		
		
	}
	public synchronized void removeAI()
	{
		if(tanks.get(0).getClient()==-128)
			tanks.get(0).removeSelfFromWorld();
		/*Team large=findLargestTeam();
		boolean found=false;
		for(Tank t:tanks)
		{
			if(t instanceof AI && t.getTeam().equals(large))
			{
				t.removeSelfFromWorld();
				found=true;
				//System.out.println(t.getClient());
			}
			if (found)
				return;
		}
		*/
	}

	public Tank getTank(int index)		{	return tanks.get(index);	}
	public Solid getSolid(int index)	{	return solids.get(index);	}
	public Projectile getProjectile(int index)	{	return projectiles.get(index);	}
	public Item getItem(int index)		{	return items.get(index);	}
	
	public AIPoint getPoint(int index)
	{
		//System.out.println("looking for " + index);
		boolean found=false;
		int low=0;
		int hi=points.size()-1;
		int mid=0;
		
		while(low<=hi&&!found)
		{
			mid=(low+hi)/2;
			if (points.get(mid).getIndex()>index)
				hi=mid-1;
			else if (points.get(mid).getIndex()<index)
				low=mid+1;
			else found=true;
			//System.out.println(points+" mid is " +mid);
		}
		if(!found)
		{	
			System.out.println("point not found: "+index);
			return null;
		}
		else 
		{
			//System.out.println(points.get(mid).getIndex()+ " found");
			return points.get(mid);
		}
	}
	/**
	 *returns the list of tanks
	 */ 
	public ArrayList<Tank> getTanks()	{	return tanks;	}
	/**
	 *returns the list of solids
	 */ 
	public ArrayList<Solid> getSolids()	{	return solids;	}	
	/**
	 *returns the list of projectiles
	 */ 
	public ArrayList<Projectile> getProjectiles()	{	return projectiles;	}
	/**
	 *returns the list of items
	 */ 
	public ArrayList<Item> getItems() {		return items;	}
	public ArrayList<ItemFactory> getItemFactories() {		return itemFactories;	}
	public ArrayList<AIPoint> getPoints(){	return points;	}
	public ArrayList<Team> getTeams(){	return teams;	}
	/**
	 *automatically finds what type of element this is and calls one of the 3 methods below
	 */
	public void remove(Element e)
	{
		if (e instanceof Tank)
			removeTank(e.getIndex());
		else if(e instanceof Projectile)
			removeProjectile(e.getIndex());
		else if(e instanceof Solid)
			removeSolid(e.getIndex());
		else if(e instanceof Item)
			removeItem(e.getIndex());
		else if(e instanceof AIPoint)
			removePoint((AIPoint)e);
		else if(e instanceof ItemFactory)
			removeItemFactory((ItemFactory)e);
		else
			System.out.println("remove called on a non-removable object - "+e.getClass().getName());
	}
	/**
	 *adds an index into the tanksToRemove list
	 */ 
	private void removeTank(int index)
	{
    	tanksToRemove.add(index);
	}
	/**
	 *adds an index into the solidsToRemove list
	 */ 
	private void removeSolid(int index)
	{
		solidsToRemove.add(index);
	}
	/**
	 *adds an index into the projectilesToRemove list
	 */ 
	private void removeProjectile(int index)
	{
		projectilesToRemove.add(index);    	
	}
	/**
	 *adds an index into the itemsToRemove list
	 */ 
	private void removeItem(int index)
	{
		itemsToRemove.add(index);    	
	}
	private void removeItemFactory(ItemFactory i)
	{
		itemFactories.remove(itemFactories.indexOf(i));
	}
	private void removePoint(AIPoint a)
	{
		points.remove(points.indexOf(a));
	}
	/**
	 *at the end of each world.draw call, indexes are removed based on the indexes stored in the *ToRemove lists from last to first
	 */ 
	public void finalizeRemoves()
	{
		if (!tanksToRemove.isEmpty())
		{
			Tank temp=null;
			Collections.sort(tanksToRemove);
			for(int i=tanksToRemove.size()-1;i>=0;i--)
			{
				temp=tanks.get(tanksToRemove.get(i));
				temp.getTeam().decrementPlayers();
				if (temp instanceof AI)
					numberAIs--;
				else
					numberClients--;
				tanks.remove(tanksToRemove.get(i).intValue());
			}
			fixTanks();
			tanksToRemove.clear();
			
		}
		if (!solidsToRemove.isEmpty())
		{
			Collections.sort(solidsToRemove);
			for(int i=solidsToRemove.size()-1;i>=0;i--)
			{
				//System.out.println("solid removing");
				solids.remove(solidsToRemove.get(i).intValue());
			}
			fixSolids();
			solidsToRemove.clear();
		}
		if (!projectilesToRemove.isEmpty())
		{
			Collections.sort(projectilesToRemove);
			for(int i=projectilesToRemove.size()-1;i>=0;i--)
			{
				projectiles.remove(projectilesToRemove.get(i).intValue());
			}
			fixProjectiles();
			projectilesToRemove.clear();
		}
		if (!itemsToRemove.isEmpty())
		{
			Collections.sort(itemsToRemove);
			for(int i=itemsToRemove.size()-1;i>=0;i--)
			{
				items.remove(itemsToRemove.get(i).intValue());
			}
			fixItems();
			itemsToRemove.clear();
		}
		//System.gc();
	}
	/**
	 *runs through the Tank list, syncronizing their indexes
	 */ 
	private void fixTanks()
    {
    	for(int i=0;i<tanks.size();i++)
    	{
    		//if(tanks.get(i).getIndex()!=i)
    			//System.out.println("fix tanks fixed something");
    		tanks.get(i).setIndex(i);
    	}
    		
    }
    /**
	 *runs through the Solid list, syncronizing their indexes
	 */ 
    private void fixSolids()
    {
    	for(int i=0;i<solids.size();i++)
    	{
    		//if(tanks.get(i).getIndex()!=i)
    			//System.out.println("fix solids fixed something");
    		solids.get(i).setIndex(i);
    	}
    		
    }
    /**
	 *runs through the Projectile list, syncronizing their indexes
	 */ 
    private void fixProjectiles()
    {
    	for(int i=0;i<projectiles.size();i++)
    	{
    		//if(projectiles.get(i).getIndex()!=i)
    			//System.out.println("fix projectiles fixed something");
    		projectiles.get(i).setIndex(i);
    	}   
    }
    private void fixItems()
    {
    	for(int i=0;i<items.size();i++)
    	{
    		//if(items.get(i).getIndex()!=i)
    			//System.out.println("fix items fixed something");
    		items.get(i).setIndex(i);
    	} 
    }


	/**
	 *simply draws all things inside the world
	 */      	
	/*public void draw(Graphics g)
	{
    		for (int i=0;i<projectiles.size();i++) 		
    			projectiles.get(i).draw(g);
    		
    		for (int i=0;i<tanks.size();i++)
    			tanks.get(i).draw(g);
    		
    		for (int i=0;i<solids.size();i++)
    			solids.get(i).draw(g); 
    				
    		  
	} */
		
	/**
	 *moves all the objects in the world a tick 
	 *parameter 1: amount of frames (as a speed multiplier)
	 *parameter 2: ClientDataStore object
	 *
	 *create timeout check
	 */
	public synchronized void move(double multiplier,ClientDataStore cds)
	{	
		Tank temp;
		for (int i=0;i<tanks.size();i++)
		{
			temp=tanks.get(i);
			if (temp.getClient()!=AI.AIClient)
			{
				ClientData tempData=cds.get(temp.getClient());
				if(tempData.exists())
				{
					temp.set(tempData);
					temp.resetLag();
	
				}
				else
					temp.lagged();//basically, if we dont get a packet from them, they are lagging
					//if they lag for too many frames, they are no longer recognised (aka kicked)
			}
			temp.move(multiplier);
			
		}
		while(!cds.isEmpty())
		{
			byte tempclientnum=cds.get().getClient();
			System.out.println("Client "+tempclientnum+" joined the server");
			addTank(new Tank(tempclientnum,this));
		}
	    		
	    		
		for (int i=0;i<projectiles.size();i++)
    		projectiles.get(i).move(multiplier);//cant use for each loop because if a projectile adds a projectile (starburst) it will have a concurrent modification error
    			
    	for(Item i: items)
    		i.checkCollision();   
    	
    	for(ItemFactory i:itemFactories)
    		i.check();		

    	   		
    			
    	finalizeRemoves(); 
    	frames++;
    	if (frames%1000==0)//every 1000 frames, call the garbage collector for great justice.
				System.gc();
	}
	public void draw(Graphics g,Screen s)
	{
		for(Tank t: tanks)
			t.draw(g,s);
		for(Projectile p: projectiles)
			p.draw(g,s);
		for(Solid so: solids)
			so.draw(g,s);
		for(Item i: items)
			i.draw(g,s);
	}
}