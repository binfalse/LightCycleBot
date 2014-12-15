/**
 * 
 */
package de.binfalse.fm14bfbot;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Martin Scharm
 *
 */
public class TestCommunication
{
	@Test
	public void positionLineTest ()
	{
  	List<String> map = new ArrayList<String> ();
  	map.add ("#.........");
  	map.add ("..........");
  	map.add ("....#.....");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("......#...");
  	map.add (".........#");
  	
  	
  	GameMap gm = new GameMap (map);
  	
  	
  	
  	
		String [] line = "POS 1 2,5 EAST".split (" ");
		
		Player p = new Player (1);
		

		line = "POS 1 2,5 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		assertEquals ("unexpected player position", 41, p.getPosition ());
		assertEquals ("unexpected player direction", Direction.EAST, (int) p.getDirection ());
		

		line = "POS 1 7,5 NORTH".split (" ");
		p.update (gm, line[2], line[3]);
		assertEquals ("unexpected player position", 46, p.getPosition ());
		assertEquals ("unexpected player direction", Direction.NORTH, (int) p.getDirection ());
		

		line = "POS 1 8,1 SOUTH".split (" ");
		p.update (gm, line[2], line[3]);
		assertEquals ("unexpected player position", 7, p.getPosition ());
		assertEquals ("unexpected player direction", Direction.SOUTH, (int) p.getDirection ());
		

		line = "POS 1 10,9 WEST".split (" ");
		p.update (gm, line[2], line[3]);
		assertEquals ("unexpected player position", gm.getWidth () * gm.getHeight () - 1, p.getPosition ());
		assertEquals ("unexpected player direction", Direction.WEST, (int) p.getDirection ());
		
		
		
	}
	
}
