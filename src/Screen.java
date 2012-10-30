package BattleTanks;

import java.awt.*;
/**
 *this is a pretty wimpy class, but it does simplify things a bit
 */
public class Screen 
{
	Coordinate center;
	Coordinate screenSize;
	double zoom;
    public Screen() 
    {
    	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize= new Coordinate(dim.getWidth(),dim.getHeight());
		System.out.println("Screen size is: "+screenSize);
    	center= new Coordinate();
    	zoom=1;
    }
    public void setCenter(Coordinate c)
    {
    	center=c;
    }
    public Coordinate getCenter()
    {
    	return center;
    }
    public void zoom(double multiply)
    {
    	zoom*=multiply;
    	System.out.println(zoom);
    }
    public double getZoom()
    {
    	return zoom;
    }
    public int getCenterX()
    {
    	return center.xInt();
    }
	public int getCenterY()
	{
   		return center.yInt();
   	}
    public int getWidth()
    {
    	return screenSize.xInt();
    }
    public int getHeight()
    {
    	return screenSize.yInt();
    }
    public int halfX()
    {
    	return screenSize.xInt()/2;
    }
    public int halfY()
    {
    	return screenSize.yInt()/2;
    }
    public int translateXToScreen(int x)
    {
    	//return x;
    	return (int)(halfX()+zoom*(x-center.xInt()));
    }
    public int translateYToScreen(int y)
    {
    	//return y;
    	return (int)(halfY()+zoom*(y-center.yInt()));
    }
    public int translateXToWorld(int x)
    {
    	//return x;
    	return (int)(center.xInt()+(x-halfX())/zoom);
    }
    public int translateYToWorld(int y)
    {
    	//return y;
    	return (int)(center.yInt()+(y-halfY())/zoom);
    }
    
    
    
}