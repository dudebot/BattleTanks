package BattleTanks;
/**
 * @(#)Team.java
 *
 *
 * @author 
 * @version 1.00 2009/1/17
 */

import java.awt.*;
public class Team extends AIPoint
{
	//Coordinate spawn;
	byte teamNumber;
	int players;
    public Team(World w,Coordinate spawn,int teamNum) 
    {
    	super(w,spawn,teamNum);
    	//teamColor=new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
    	//this.spawn=spawn;
    	teamNumber=(byte)teamNum;
    }
    /*public Team(int teamNum,Coordinate spawn,Color color) 
    {
    	//teamColor=color;
    	this.spawn=spawn;
    	teamNumber=(byte)teamNum;
    }*/
    public byte getByte()
    {
    	return teamNumber;
    }
    /*public Color getColor()
    {
    	return teamColor;
    }*/
    public int size()
    {
    	return players;
    }
    /*public Coordinate getSpawn()
    {
    	return spawn;
    }*/
    public void incrementPlayers()
    {
    	players++;
    }
    public void decrementPlayers()
    {
    	players--;
    }
    public boolean equals(Team other)
    {
    	return teamNumber==other.teamNumber;
    }
    
    
}