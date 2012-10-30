package BattleTanks;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;
import java.io.*;
import java.util.NoSuchElementException;
import javax.swing.JOptionPane;
/**
 *todo:	
 *		test the boolean[] <-> byte sys
 *		askForClient (make both listener and sender just for the purpose)
 */
 
public class Util 
{
	/**
	 *port for sending
	 */
	public static final int servSend=3000;
	public static final int servListen=3001;
	public static final int servSendSpec=3002;
	public static final int servListenSpec=3003;
	
	/**
	 *this function is only necessary if the int is more than 127, so this allows up to 65000 or so
	 *anything under 127 can simply be done with typecasting
	 */
	public static int getInt(byte byte1, byte byte2)
	{
		return ((byte1 & 0xFF) << 8)
            +  	(byte2 & 0xFF);
	}   
	/**
	 *returns an array of 2 bytes for convenience
	 */
	public static byte[] getBytes(int number)
	{
		//if(number<0)
			//throw new IllegalArgumentException("Number is negative, translation impossible");
		return new byte[] {
                (byte)(number >>> 8),
                (byte)(number)};
	} 
	/**
	 *translates 8 bools into 1 byte for ease in translating keys
	 */
	public static byte getByte(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h)
	{
		byte data=0;
		if (a)
			data+=1;
		if (b)
			data+=2;
		if (c)
			data+=4;
		if (d)
			data+=8;
		if (e)
			data+=16;
		if (f)
			data+=32;
		if (g)
			data+=64;//max 127
		if (h&&data==0)
			data=-128;
		else if(h)
			data*=-1;
		return data;
			
	}
	public static boolean[] getBooleans(byte b)
	{
		boolean[] bools = new boolean[8];
		if (b==-128)
		{
			bools[7]=true;
			return bools;
		}
		if(b<0)
		{
			bools[7]=true;
			b*=-1;
		}
		if (b>=64)
		{
			bools[6]=true;
			b-=64;
		}
		if (b>=32)
		{
			bools[5]=true;
			b-=32;
		}
		if (b>=16)
		{
			bools[4]=true;
			b-=16;
		}
		if (b>=8)
		{
			bools[3]=true;
			b-=8;
		}
		if (b>=4)
		{
			bools[2]=true;
			b-=4;
		}
		if (b>=2)
		{
			bools[1]=true;
			b-=2;
		}
		if (b==1)
		{
			bools[0]=true;
		}
		return bools;
	}


	public static ArrayList<Solid> getSolids(int tag,int ver)
	{
		//find map file with that tag
		File[] files = new File (".").listFiles(new FileExtentionFilter("txt"));
		boolean found=false;
		File f=null;
		for(int i=0;i<files.length&&!found;i++)
		{
			
			try{
			BufferedReader r=new BufferedReader(new FileReader(files[i]));
			String line=r.readLine();
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			int testTag=Integer.parseInt(line.substring(4,line.length()));
			line=r.readLine();
			int testVer=Integer.parseInt(line.substring(4,line.length()));
			if(testTag==tag)
			{
				System.out.println("found tag: checking with version, name is: "+files[i].getName());
				if(testVer==ver)
				{
					f=files[i];
					found=true;
					System.out.println("\tversion match!" );
				}
				else
					System.out.println("\tversion mismatch" );
			}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
			
		if(!found)
		{
			System.out.println("map not found with version: "+ver);
			System.out.println("ask the server host for the latest version");
			return null;
		}
		
		
		
		String line;
		ArrayList<Solid> solids=new ArrayList<Solid>();
		try{
		FileReader fr= new FileReader(f);
		BufferedReader br= new BufferedReader(fr);
		
		
		
		while((line=br.readLine())!=null)
		{
			try{
			StringTokenizer st = new StringTokenizer(line);
			if(st.nextToken().equals("solid"))
			{
				//System.out.println("reading line");
				
				
				if (st.countTokens()!=4)
					System.out.println("malformed line: "+line);
				else
					solids.add(new Solid(null,new Coordinate(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())));
					
			}
			}catch(NoSuchElementException e)
			{
				//e.printStackTrace();
				//System.out.println(line);
			}
		}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return solids;
	}
	/**
	 *used by world to load everything from textfile
	 */
	public static void loadWorld(World w)
	{
		String line;
		ArrayList<Solid> solids=new ArrayList<Solid>();
		try{
		FileReader f= new FileReader(w.mapName()+".txt");
		BufferedReader br= new BufferedReader(f);
		
		
		
		while((line=br.readLine())!=null)
		{
			try{
			StringTokenizer st = new StringTokenizer(line);
			String first = st.nextToken();
			if(first.equals("solid"))
			{
				System.out.println("reading line: "+line);
				if (st.countTokens()!=4)
					System.out.println("malformed line,needs 4 vars!!");
				else
					w.addSolid(new Solid(w,new Coordinate(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())));
					
			}
			else if(first.equals("size"))
			{
				System.out.println("reading line: "+line);
				if (st.countTokens()!=2)
					System.out.println("malformed line,needs 2 vars!!");
				else
					w.setSize(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));
				
			}
			else if(first.equals("spawn"))
			{
				System.out.println("reading line: "+line);
				if (st.countTokens()!=3)
					System.out.println("malformed line,needs 3 vars!!");
				else
					w.addTeam(new Team(w,new Coordinate(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())),Integer.parseInt(st.nextToken())));
				
			}
			else if(first.equals("tag"))
			{
				System.out.println("reading line: "+line);
				if (st.countTokens()!=1)
					System.out.println("malformed line,needs 1 var!!");
				else
					w.setTag(Integer.parseInt(st.nextToken()));
				
			}
			else if(first.equals("ver"))
			{
				System.out.println("reading line: "+line);
				if (st.countTokens()!=1)
					System.out.println("malformed line,needs 1 var!!");
				else
					w.setVersion(Integer.parseInt(st.nextToken()));
				
			}
			else if(first.equals("ai"))
			{
				System.out.println("reading line: "+line);
				if (st.countTokens()!=1)
					System.out.println("malformed line, needs 1 var!!");
				else
				{
					for(int i=Integer.parseInt(st.nextToken());i>0;i--)
					{
						w.addAI();
						System.out.println("\tadding new ai");
					}
				}
				
			}
			else if(first.equals("aipoint"))
			{
				System.out.println("reading line: "+line);
				if (st.countTokens()!=3)
					System.out.println("malformed line, needs 3 vars!!");
				else
				{
					w.addPoint(new AIPoint(w,new Coordinate(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())),Integer.parseInt(st.nextToken())));
				}
				
			}
			else if(first.equals("linkpoint"))
			{
				System.out.println("reading line: "+line);
				//if (st.countTokens()!=1)
					//System.out.println("malformed line, needs 1 var!!");
				AIPoint temp1,temp2;
				temp1=w.getPoint(Integer.parseInt(st.nextToken()));
				//System.out.println(temp1==null);
				
				while (st.hasMoreTokens())
				{
					//System.out.println("linking");
					int index=Integer.parseInt(st.nextToken());
					//System.out.println("test");
					temp2=w.getPoint(index);
					//System.out.println(index+" is null: "+temp2==null);
					
					temp1.linkPoint(temp2);
				} 
					


				
			}
			else if(first.equals("item"))
			{
				System.out.println("reading line: "+line);
				if (st.countTokens()!=4)
					System.out.println("malformed line, needs 4 vars!!");
				else
					w.addItemFactory(new ItemFactory(w,new Coordinate(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())),st.nextToken(),Integer.parseInt(st.nextToken())));
				
			}
			else
				System.out.println("ignoring line: "+line);
			}catch(NoSuchElementException e)//blank line so ignore it
			{
				//e.printStackTrace();
				//System.out.println(line);
			}
			catch(NumberFormatException e)
			{
				System.out.println("warning: non-number found when parsing for number:");
				//System.out.println(line);
			}
			
		}
		br.close();
		f.close();
		
		}
		catch (FileNotFoundException e)
		{
			System.out.println("file does not exist, creating new");
			File f= new File(w.mapName()+".txt");
			try{
				f.createNewFile();
				//FileWriter fw=new FileWriter(f);
				int x=Integer.parseInt(JOptionPane.showInputDialog("how wide?"));
				int y=Integer.parseInt(JOptionPane.showInputDialog("how tall?"));
				//fw.write("size "+ x +" " +y);
				w.setSize(x,y);
				w.addSolid(new Solid(w,new Coordinate(x/2,0),x,50));
				w.addSolid(new Solid(w,new Coordinate(x/2,y),x,50));
				w.addSolid(new Solid(w,new Coordinate(0,y/2),50,y));
				w.addSolid(new Solid(w,new Coordinate(x,y/2),50,y));
				saveWorld(w);
			}catch (IOException e2){e2.printStackTrace();};
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//for(AIPoint a: w.getPoints())
				//System.out.println(a);
	}
	public static void saveWorld(World w)
	{
		w.setVersion(w.getVersion()+1);
		File f=new File(w.mapName()+".txt");
		System.out.println("writing world to file");
		try{
		BufferedWriter fw=new BufferedWriter(new FileWriter(f));
		if(f.exists())
			f.delete();
		f.createNewFile();
		fw.write("tag "+w.getTag());
		fw.newLine();
		fw.write("ver "+w.getVersion());
		fw.newLine();
		//size
		fw.write("size "+ w.getBounds().xInt()+ " " + w.getBounds().yInt());
		fw.newLine();
		//spawn
		for(Team t: w.getTeams())
		{	
			fw.write("spawn "+ t.getLocation().xInt()+ " " + t.getLocation().yInt()+ " "+ t.getIndex());
			fw.newLine();
		}
		//point
		for(AIPoint a: w.getPoints())
		{
			if(!(a instanceof Team))
			{
				fw.write("aipoint "+a.getLocation().xInt()+ " " +a.getLocation().yInt()+ " "+ a.getIndex());
				fw.newLine();
			}
		}
		//link
		for(AIPoint a1: w.getPoints())
		{
			fw.write("linkpoint "+a1.getIndex());
			for(AIPoint a2: a1.getPoints())
			{
				
				fw.write(" " +a2.getIndex());
			}
			fw.newLine();
		}				
		//solid
		for(Solid s: w.getSolids())
		{
			fw.write("solid "+ s.getLocation().xInt()+ " " + s.getLocation().yInt()+ " "+s.getWidth()+" "+ s.getHeight());
			fw.newLine();
		}
		//item
		for(ItemFactory i: w.getItemFactories())
		{
			fw.write("item "+i.getLocation().xInt()+ " " + i.getLocation().yInt()+ " " + i.getType()+ " " + i.getDelay());
			fw.newLine();
		}
		//ai number*/
		fw.write("ai 0");
		fw.close();
		//f.close();
		System.out.println("success!");
		}catch(IOException e){
			System.out.println("failure, deleting");
			f.delete();
		}
		
		
	}
	public static byte[] waitForBytes(MulticastListener l,int time)
	{
	
		while (true)
		{
			try{
				Thread.sleep(time);
				if (l.hasBytes())
				{
					return l.getBytes();
				}
				
			} catch(InterruptedException e)	{}
			
		}
	}
	
	/**
	 *simple test cases
	 */
	public static void main (String[] args)
	{
		byte b = getByte(true,true,true,true,false,true,true,false);
		boolean[] bool=getBooleans(b);
		System.out.println(bool[0]);
		System.out.println(bool[1]);
		System.out.println(bool[2]);
		System.out.println(bool[3]);
		System.out.println(bool[4]);
		System.out.println(bool[5]);
		System.out.println(bool[6]);
		System.out.println(bool[7]);
	}
}