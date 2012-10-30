package BattleTanks;
/**
 *A simple parser for the bytes sent by client to the server
 *This class is created from the divided up stream of bytes from
 *the clients to the server (Client.size number of bytes)
 *and then sent to the server's world's clientdatastore
 *there, the cds is sent to world.move, and for each clientData
 *object there is, the tank is set to copy whatever the client does
 *
 *all the booleans are smooshed into 1 byte by util.i forgot
 */
public class ClientData
{
	
	byte clientNum;
	boolean exists;
	boolean forward;
	boolean backward;
	boolean left;
	boolean right;
	boolean shoot;
	boolean specialShoot;
	Coordinate mouseLoc;

	
	public ClientData(byte[] data)
	{
		exists=true;
		clientNum=data[0];
		boolean[] boolData= Util.getBooleans(data[1]);
		forward=boolData[0];
		backward=boolData[1];
		left=boolData[2];
		right=boolData[3];
		shoot=boolData[4];
		specialShoot=boolData[5];
		mouseLoc=new Coordinate(Util.getInt(data[2],data[3]),Util.getInt(data[4],data[5]));
	}
	public ClientData()
	{
		exists=false;
	}
	public byte getClient(){	return clientNum;	}
	public boolean exists(){	return exists;		}
	public boolean forward(){	return forward;		}
	public boolean backward(){	return backward;	}
	public boolean left(){		return left;		}
	public boolean right(){		return right;		}
	public boolean shoot(){		return shoot;		}
	public boolean specialShoot(){return specialShoot;	}
	public Coordinate mouseLoc()
	{
		return mouseLoc;
	}
	
	public String toString()
	{
		return "ClienData: "+clientNum+" exists:"+exists+" mouseLoc:"+mouseLoc;
	}
}