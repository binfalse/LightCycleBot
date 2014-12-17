/**
 * 
 */
package de.binfalse.fm14bfbot;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.binfalse.bflog.LOGGER;


/**
 * @author Martin Scharm
 *
 */
public class TestFight
{

	
	@Test
	public void testFight ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
		List<String> map = new ArrayList<String> ();
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		
		GameMap gm = new GameMap (map);
  	
  	List<Player> enemies = new ArrayList<Player> ();
  	Player p = new Player (3);
		String [] line = "POS 3 1,2 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		enemies.add(p);
	  	
	  	p = new Player (2);
		line = "POS 2 5,5 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		enemies.add(p);
	  	
	  	p = new Player (1);
		line = "POS 1 9,8 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		
		gm.setMe(p);
		gm.fight (enemies);
	}

	
	
	@Test
	public void testGoodnessOfFloods ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
		List<String> map = new ArrayList<String> ();
		map.add ("..........");
		map.add ("..........");
		map.add (".########.");
		map.add ("......#...");
		map.add ("......#...");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		
		GameMap gm = new GameMap (map);
		
		
		int [] me = {1,2,3,4};
		List<List<int[]>> theirFloods = new ArrayList<List<int[]>> ();
		for (int i = 0; i < 3; i++)
			theirFloods.add(new ArrayList<int[]> ());
		
		// player 1
		theirFloods.get(0).add(new int [] {5,3,5,5});
		theirFloods.get(0).add(new int [] {5,1,5,5});
		// player 2
		theirFloods.get(1).add(new int [] {5,5,5,5});
		theirFloods.get(1).add(new int [] {4,5,5,2});
		theirFloods.get(1).add(new int [] {5,7,5,5});
		// player 3
		theirFloods.get(2).add(new int [] {5,5,2,5});
	
		LOGGER.debug(gm.goodnessOfFlood(me, theirFloods));
		LOGGER.setMinLevel (LOGGER.WARN);
		

		assertEquals ("goodness of floods seems to be wrong", 13, gm.goodnessOfFlood(me, theirFloods));
		
		

		me = new int [] {1,2,3,4};
		theirFloods = new ArrayList<List<int[]>> ();
		for (int i = 0; i < 1; i++)
			theirFloods.add(new ArrayList<int[]> ());
		
		// player 1
		theirFloods.get(0).add(new int [] {5,3,2,5});
		theirFloods.get(0).add(new int [] {5,1,5,4});
	
		LOGGER.debug(gm.goodnessOfFlood(me, theirFloods));
		LOGGER.setMinLevel (LOGGER.WARN);
		assertEquals ("goodness of floods seems to be wrong", 5, gm.goodnessOfFlood(me, theirFloods));
		
	}
}
