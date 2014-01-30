package map;

import java.util.LinkedList;

/**
 * 
 * Can add elements to the front or the back, but does not have to start from zero.
 * Backed by a LinkedList
 * 
 * @author HONG
 *
 */
public class MapRow 
{
	private LinkedList<Tile> list;
	
	private int offset; //The linkedlist will always start from zero, but to make referencing elements in a grid nicer without padding, this integer will pad
	
	public MapRow()
	{
		
	}
	
	public void addFirst(Tile t)
	{
		offset -- ;
		list.addFirst(t);
	}
	public void addLast(Tile t)
	{
		offset ++ ;
		list.addLast(t);
	}
}
