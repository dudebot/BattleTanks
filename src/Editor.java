package BattleTanks;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import java.util.ArrayList;
/**
 *this is a mapmaker, have fun
 *
 */
public class Editor extends Frame implements MouseMotionListener,KeyListener,MouseListener,MouseWheelListener
{
	World world;
	Screen screen;
	BufferedImage image,bgimage;
	//Image[] modifyImages;
	Coordinate mouseLocation;
	Graphics buffer,bgbuffer;
	public static final int numberFunctions=6;
	//int lastPoint
	int function;
	
	/*
	 *0=remove
	 *1=add team
	 *2=add solid
	 *3=add item
	 *4=add/select aipoint
	 *5=link points
	 */
	AIPoint selectedPoint;
	
	public Editor(String mapName)
	{
		world=new World(mapName);
		screen=new Screen();
		screen.setCenter(new Coordinate(world.getBounds().x()/2,world.getBounds().y()/2));	
		
		setSize(screen.getWidth(),screen.getWidth());
		setUndecorated(true);
		setVisible(true);
		image=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		buffer=image.createGraphics();
		bgimage=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		bgbuffer=image.createGraphics();
		bgbuffer.setColor(Color.white);
		bgbuffer.fillRect(0,0,getWidth(),getHeight());
		//selectingPoints=false;
		//editElement=false;
		mouseLocation=new Coordinate();
		redraw();
		
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		function=0;
		selectedPoint=null;
	//	for(int i=0;i<numberFunctions;i++)
	//		modifyFunctions[i]=new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
		//Graphics temp;
		//temp=modifyFunctions[0].createGraphics();
		//temp.setColor(Color.white)//crap transparencies suck in java- w/e
	}
	public void mouseMoved(MouseEvent e)
    {
    	//mouseLocation.set(screen.translateXToWorld(e.getX()),screen.translateYToWorld(e.getY()));
    	mouseLocation.set(e.getX(),e.getY());
    	paint(getGraphics());
    }
    public void mouseDragged(MouseEvent e)
    {
    	mouseMoved(e);	
    }
    public void keyPressed(KeyEvent e)
	{
		//System.out.println(e.getKeyCode());
		
		if (e.getKeyCode()==27)
			System.exit(0);
		if (e.getKeyCode()==32&&(JOptionPane.showConfirmDialog(null,"Do you want to save to ver:"+(world.getVersion()+1))==0))
			Util.saveWorld(world);
		//45 61
		if (e.getKeyCode()==45)
		{
			screen.zoom(.9);
			redraw();
		}
		if (e.getKeyCode()==61)
		{
			screen.zoom(10.0/9.0);
			redraw();
		}
	}
	public void keyReleased(KeyEvent e)	{}
	public void keyTyped(KeyEvent e)	{}
	
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){	}
	public void mouseReleased(MouseEvent e)	{}
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
		if(e.getButton()==1)//left click
		{	
			screen.setCenter(new Coordinate(screen.translateXToWorld(e.getX()),screen.translateYToWorld(e.getY())));
		}
		else if(e.getButton()==3)//right (delete,add)
		{
			/*
			 *0=remove
			 *1=add team
			 *2=add solid
			 *3=add item
			 *4=add/select aipoint
			 *5=toggle link points
			 */
			Element element=getNearObject();
			//System.out.println(element);
			if(element==null)
			{
				switch (function)
				{	case 1:
						Team t=new Team(world,mouseLocation.getWorldCoordinate(screen),0);
						if(t.edit())
							world.addTeam(t);
						break;
					case 2:
						Solid s= new Solid(world,mouseLocation.getWorldCoordinate(screen),100,100);
						if(s.edit())
							world.addSolid(s);
						break;
					case 3://itemfactory
						ItemFactory i= new ItemFactory(world,mouseLocation.getWorldCoordinate(screen),"",10);
						if(i.edit())
							world.addItemFactory(i);
						break;
					case 4: //aipoint
						AIPoint a = new AIPoint(world,mouseLocation.getWorldCoordinate(screen),0);
						if(a.edit())
						{
							world.addPoint(a);
							selectedPoint=a;
						}
				}
			}
			else
			{
				switch(function)
				{
					case 0:
						element.removeSelfFromWorld();
						world.finalizeRemoves();//pretty much only for solid (omg this is getting pretty ghetto)
						break;
					case 4:
						if(element instanceof AIPoint)
							selectedPoint=(AIPoint)element;
						break;
					case 5:
						if(element instanceof AIPoint&&selectedPoint!=null&&element!=selectedPoint)
							selectedPoint.doubleLink((AIPoint)element);	
						else if(element instanceof AIPoint&&selectedPoint==null)
							selectedPoint=(AIPoint)element;
						
				}
			}
			
		}
		else if(e.getButton()==2)//middle click (modify)
		{
			EditableElement element=getNearObject();
			if(element!=null)
				element.edit();
		}
		//System.out.println(e.getButton());
		
		redraw();
		
	}
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if(e.getWheelRotation()>0)
			function++;
		else
			function--;
			
		if(function<0)
			function=numberFunctions-1;
		if(function>=numberFunctions)
			function=0;
			//MouseWheelEvent.WHEEL_BLOCK_SCROLL)	
		//System.out.println(e.getWheelRotation());
		paint(getGraphics());
			
	}
	private EditableElement getNearObject()
	{
		Coordinate mouse=mouseLocation.getWorldCoordinate(screen);
		ArrayList<EditableElement> objects= new ArrayList<EditableElement>();
		for(AIPoint a:world.getPoints())//includes teams
			if(a.nextTo(mouse,50))
				objects.add(a);
		for(Solid s:world.getSolids())//includes teams
			if(s.collides(mouse,10)[0])//lolwut
				objects.add(s);
		for(ItemFactory i:world.getItemFactories())//includes teams
			if(i.nextTo(mouse,50))
				objects.add(i);
		if(objects.isEmpty())
			return null;
		else if(objects.size()==1)
			return objects.get(0);
		else
		{
			EditableElement temp=objects.get(0);
			double tempdist1=temp.getLocation().getDistance(mouseLocation);
			double tempdist2;
			for(int i=1;i<objects.size();i++)
				if ((tempdist2=objects.get(i).getLocation().getDistance(mouseLocation))<tempdist1)
				{
					tempdist1=tempdist2;
					temp=objects.get(i);
				}
			return temp;
		}
		
	}
	private void redraw()
	{
		buffer.drawImage(bgimage,0,0,this);
		buffer.setColor(Color.white);
		buffer.fillRect(0,0,getWidth(),getHeight());
		buffer.setColor(Color.gray);
			int line=(int)(500*screen.getZoom());
			int tempX=(int)((screen.getCenterX()*screen.getZoom())%line);
			int tempY=(int)((screen.getCenterY()*screen.getZoom())%line);
			
			//int count=0;
			//for(int i=(int)(-screen.getZoom()*tempX);i<screen.halfX();i+=screen.getZoom()*line)
			for(int i=-1;i<(int)(screen.getWidth()/line/screen.getZoom());i++)
			{
				buffer.fillRect(screen.halfX()+i*line-tempX,0,(int)(25*screen.getZoom()),getHeight());
				buffer.fillRect(screen.halfX()-i*line-tempX,0,(int)(25*screen.getZoom()),getHeight());
				//buffer.drawString(""+count,screen.halfX()+i*line-tempX,30);
				//buffer.drawString(""+count,screen.halfY()-i*line-tempY,30);
				//count++;
			}
			//count=0;
			for(int i=-1;i<(int)(screen.getHeight()/line/screen.getZoom());i++)
			{
				buffer.fillRect(0,screen.halfY()+i*line-tempY,getWidth(),(int)(25*screen.getZoom()));
				buffer.fillRect(0,screen.halfY()-i*line-tempY,getWidth(),(int)(25*screen.getZoom()));
				//buffer.drawString(""+count,30,screen.halfY()+i*line-tempY);
				//buffer.drawString(""+count,30,screen.halfY()-i*line-tempY);
				//count++;
			}
		for(Solid s:world.getSolids())
			s.draw(buffer,screen);
		for(ItemFactory i:world.getItemFactories())
			i.draw(buffer,screen);
		for(AIPoint a: world.getPoints())
			a.draw(buffer,screen);
		
		paint(getGraphics());
	}
	public void paint(Graphics g)
	{
		g.drawImage(image,0,0,this);
		g.setColor(Color.red);
		//g.fillRect(screen.translateXToScreen(screen.translateXToWorld(mouseLocation.xInt())),
		//			screen.translateYToScreen(screen.translateYToWorld(mouseLocation.yInt())),100,100);
		
		switch(function)
		{
			case 0:
				g.drawLine(mouseLocation.xInt()+10,mouseLocation.yInt()-70,mouseLocation.xInt()+70,mouseLocation.yInt()-10);
				g.drawLine(mouseLocation.xInt()+10,mouseLocation.yInt()-10,mouseLocation.xInt()+70,mouseLocation.yInt()-70);
				break;
			case 1:
				g.drawString("Team",mouseLocation.xInt()+10,mouseLocation.yInt()-10);
				break;
			case 2:
				g.drawString("Solid",mouseLocation.xInt()+10,mouseLocation.yInt()-10);
				break;
			case 3:
				g.drawString("Item",mouseLocation.xInt()+10,mouseLocation.yInt()-10);
				break;
			case 4:
				if(selectedPoint==null)
					g.drawString("AIPoint (no selected point)",mouseLocation.xInt()+10,mouseLocation.yInt()-10);	
				else
					g.drawString("AIPoint (point: "+selectedPoint.getIndex()+" selected)",mouseLocation.xInt()+10,mouseLocation.yInt()-10);	
				break;		
			case 5:
				if(selectedPoint==null)
					g.drawString("Linker (no selected point)",mouseLocation.xInt()+10,mouseLocation.yInt()-10);
				else
					g.drawString("Linker (point: "+selectedPoint.getIndex()+" selected)",mouseLocation.xInt()+10,mouseLocation.yInt()-10);
				break;
				
		}		
		
			
	}


}