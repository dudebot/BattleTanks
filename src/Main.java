package BattleTanks;
import javax.swing.*;//JOptionPane;

public class Main 
{
	private static int getChannel()
	{
			try{
				Object[] list = {"0","1","2","3","4","custom"};
        		String answer =(String)(JOptionPane.showInputDialog(null,"Pick a channel","Hello",JOptionPane.QUESTION_MESSAGE,null,list,list[0]));
        		if(answer.equals("custom"))
        			answer=JOptionPane.showInputDialog(null,"Enter number");
        		return Integer.parseInt(answer);
			}catch(Exception e)
			{}	
		return 0;
		
	}
    public static void main(String[] args) 
    {
    	Object[] list = {"Server","Client","Editor" 	};
        String answer =(String)(JOptionPane.showInputDialog(null,"Pick an option","Hello",JOptionPane.QUESTION_MESSAGE,null,list,list[0]));
        if (answer==null)
        	answer="";
        if (answer.equals(list[0]))
        {
        	Server s;
        	String map=JOptionPane.showInputDialog(null,"Pick a mapname (this are the .txt files)\nBlank picks default");
        	if (map.equals(""))
        		s = new Server(getChannel(),"defaultmap");
        	else	
        		s = new Server(getChannel(),map);
        	s.run();
        }
        else if(answer.equals(list[1]))
        {
        	Client c = new Client(getChannel());
        	c.run();
        }
        else if(answer.equals(list[2]))
        {
        	Editor e;
        	String map=JOptionPane.showInputDialog(null,"Pick a mapname (this are the .txt files)\nBlank picks default");
        	if(map.equals(""))
        		e = new Editor("defaultmap");
        	else
        		e = new Editor(map);
        	
        }
        	
        
    }
}
