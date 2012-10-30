package BattleTanks;
import java.awt.*;


class Nuke extends Projectile
{
	
	public Nuke(Weapon w,Coordinate c)
	{
		super(w,c);
	}
	
	
	/**
	 *thanks alot chris, you made this superghetto
	 */
	 public void explode()
	 {
	 	for(Tank t: world.getTanks())
	 	{
	 		if (location.getDistance(t.getLocation())<130&&t.isAlive())
	 		{
	 			t.explode();
	 			if(t.getTeam()==(((Tank)maker).getTeam())&&t.getTeam()!=null)
	 					((Tank)maker).decrementKills();
	 				else
	 					((Tank)maker).incrementKills();
	 		}
	 			
	 	}
	 	alive=false;
	 	explodeFrame+=10;
	  	if (explodeFrame>=65)
	 	{
	 		removeSelfFromWorld();
	 	}

	 }
	 public void bla2()
	 {
	 	System.out.println("nuke called");
	 }
	 public String toString()
	 {
	 	return "Nuke-"+super.toString();
	 }
}
