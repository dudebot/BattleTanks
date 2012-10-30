package BattleTanks;
import java.util.ArrayList;


public class InheritanceTest 
{

    public static void main(String[] args) 
    {
    	ArrayList<Projectile> list= new ArrayList<Projectile>();
    	Tank t=new Tank((byte)0,null);
    	list.add(new Projectile(t,new Coordinate()));
    	list.add(new Starburst(t,new Coordinate(),2));
    	list.add(new Nuke(t,new Coordinate()));
    	list.add(new Laser(t,new Coordinate())); 
    	for(Projectile p: list)
    	{
    		System.out.println(p.getClass().getName());
    		System.out.println(p);
    		p.bla();
    		
    	}   
    }
}
