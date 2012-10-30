package BattleTanks;
import java.util.ArrayList;
/**
 *this class automatically sends a list of bytes to a
 *self sorting arraylist of ClientData objects
 *
 *
 *changes in the amount of bytes that are in Client should
 *NOT change the operation of this class because the constructor
 *of ClientData takes care of it
 *
 *the arraylist of nodes is kept in order to enable the use of a binary search
 */
public class ClientDataStore {
	ArrayList<ClientData> datanodes;
	
	public ClientDataStore()
	{
		datanodes= new ArrayList<ClientData>();
	}
    public void add(byte[] list) 
    {
    	//datanodes.clear();//BAD IDEA
    	int numberNodes=list.length/Client.size;
    	byte[] data = new byte[Client.size];
    	for(int i = 0; i<numberNodes;i++)
    	{
    		for(int j=0;j<Client.size;j++)
    			data[j]=list[i*Client.size+j];

    		add(new ClientData(data));
    	}
    }
    
    /**
     *keeps the list in order
     *mostly copypasta'd from world.addTank
     */
    private void add(ClientData cd)
    {
    	//System.out.println("Adding new ClientData to CDS");
    	//System.out.println(cd);
    	
    	if(datanodes.isEmpty())
   			datanodes.add(cd);
   		else
   		{
   			int index=0;
    		while(index<datanodes.size()&&cd.getClient()>datanodes.get(index).getClient())//huzzah for short circuiting
    		{
    			index++;
    		}
    		//untested algorithm, get on it
    		//if this works improperly, it will have two instances of the same client,
    		// so check for it in world
    		get(cd.getClient());//removes last reference of that one (race conditions of: 	two clients with same number
    																					//	client sent 2 packets??
    		datanodes.add(index,cd);  //replace
   		}
    }
    
    /**
     *binary search for client data
     *warning, this method is self destructive so make sure the only time you use it, you keep a copy of that object or somehting
     */
    public ClientData get(byte client)
    {
		boolean found=false;
		int index=0;
		int low=0;
		int hi=datanodes.size()-1;
		int mid=0;
		
		while(low<=hi&&!found)
		{
			mid=(low+hi)/2;
			if (datanodes.get(mid).getClient()>client)
				hi=mid-1;
			else if (datanodes.get(mid).getClient()<client)
				low=mid+1;
			else found=true;
		}
		if(found)
		{
			//System.out.println("Found CD in CDS: returning object of values:");
			ClientData temp = datanodes.remove(mid);
			//System.out.println(temp);
			
			return temp;
		}
		else
		{
			//System.out.println("Failed to fine CD in CDS: returning default");
			return new ClientData();
		}
			
			
    }
    public boolean isEmpty()
    {
    	return datanodes.isEmpty();
    }
    /**
     *returns and removes next one on the list 
     *the purpose of this is to be used after world consumes all the tanks it thinks exists
     *if there is more data (as returned by this method:
     *world should add another tank based off this data
     */
    public ClientData get()
    {
    	return datanodes.remove(0);
    }
}