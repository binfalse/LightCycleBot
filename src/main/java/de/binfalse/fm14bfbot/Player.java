/**
 * 
 */
package de.binfalse.fm14bfbot;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Martin Scharm
 *
 */
public class Player
{
	private int num;
	private List<Integer> pos;
	private List<Integer> dir;
	
	private boolean in;
	
	public Player (int num)
	{
		this.num = num;
		pos = new ArrayList<Integer> ();
		dir = new ArrayList<Integer> ();
		in = true;
	}
	
	public void setOut ()
	{
		in = false;
	}
	
	public int getId ()
	{
		return num;
	}
	
	public boolean isIn ()
	{
		return in;
	}
	
	public boolean sameCompartment (GameMap map, Player p)
	{
		return map.sameCompartment (getPosition (), p.getPosition ());
	}
	
	/*public void setPosition (int idx)
	{
		pos.add (idx);
	}*/
	
	public int getPosition ()
	{
		return pos.get (pos.size () - 1);
	}
	
	public Integer getDirection ()
	{
		return dir.get (dir.size () - 1);
	}
	
	
	public void update (GameMap map, String coords, String direction)
	{
		if (direction.startsWith ("E"))
			dir.add (Direction.EAST);
		else if (direction.startsWith ("S"))
			dir.add (Direction.SOUTH);
		else if (direction.startsWith ("W"))
			dir.add (Direction.WEST);
		else if (direction.startsWith ("N"))
			dir.add (Direction.NORTH);
		else
			throw new RuntimeException ("direction " + direction + " unknown");
		
		int comma = coords.indexOf (",");
		pos.add (map.getIdx (Integer.parseInt (coords.substring (0, comma)) - 1, Integer.parseInt (coords.substring (comma + 1)) - 1));
	}
}
