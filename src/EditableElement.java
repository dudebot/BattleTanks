package BattleTanks;

abstract class EditableElement extends Element
{

    public EditableElement(World w, Coordinate c,int index) 
    {
    	super(w,c,index);
    }
    /**
     *return false if editing goes wrong
     */
    abstract boolean edit();
    
    
}